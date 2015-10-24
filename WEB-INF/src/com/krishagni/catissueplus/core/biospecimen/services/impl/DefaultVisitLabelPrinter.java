package com.krishagni.catissueplus.core.biospecimen.services.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.krishagni.catissueplus.core.administrative.domain.User;
import com.krishagni.catissueplus.core.biospecimen.ConfigParams;
import com.krishagni.catissueplus.core.biospecimen.domain.Visit;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.domain.LabelPrintJob;
import com.krishagni.catissueplus.core.common.domain.LabelPrintJobItem;
import com.krishagni.catissueplus.core.common.domain.LabelPrintJobItem.Status;
import com.krishagni.catissueplus.core.common.domain.LabelTmplToken;
import com.krishagni.catissueplus.core.common.domain.LabelTmplTokenRegistrar;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.service.ConfigChangeListener;
import com.krishagni.catissueplus.core.common.service.ConfigurationService;
import com.krishagni.catissueplus.core.common.util.AuthUtil;

public class DefaultVisitLabelPrinter extends AbstractLabelPrinter<Visit> implements InitializingBean, ConfigChangeListener {
	private static final Logger logger = Logger.getLogger(DefaultVisitLabelPrinter.class);
	
	private List<VisitLabelPrintRule> rules = new ArrayList<VisitLabelPrintRule>();
	
	private DaoFactory daoFactory;
	
	private ConfigurationService cfgSvc;
	
	private LabelTmplTokenRegistrar printLabelTokensRegistrar;
	
	private MessageSource messageSource;

	public void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

	public void setCfgSvc(ConfigurationService cfgSvc) {
		this.cfgSvc = cfgSvc;
	}

	public void setPrintLabelTokensRegistrar(LabelTmplTokenRegistrar printLabelTokensRegistrar) {
		this.printLabelTokensRegistrar = printLabelTokensRegistrar;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public LabelPrintJob print(List<Visit> visits, int numCopies) {		
		try {
			String ipAddr = AuthUtil.getRemoteAddr();
			User currentUser = AuthUtil.getCurrentUser();
			
			LabelPrintJob job = new LabelPrintJob();
			job.setSubmissionDate(Calendar.getInstance().getTime());
			job.setSubmittedBy(currentUser);
			job.setItemType(Visit.getEntityName());
			job.setNumCopies(numCopies <= 0 ? 1 : numCopies);
	
			List<Map<String, Object>> labelDataList = new ArrayList<Map<String,Object>>();
			for (Visit visit : visits) {
				boolean found = false;
				for (VisitLabelPrintRule rule : rules) {
					if (!rule.isApplicableFor(visit, currentUser, ipAddr)) {
						continue;
					}
					
					Map<String, String> labelDataItems = rule.getDataItems(visit);

					LabelPrintJobItem item = new LabelPrintJobItem();
					item.setJob(job);
					item.setPrinterName(rule.getPrinterName());
					item.setItemLabel(visit.getName());
					item.setStatus(Status.QUEUED);
					item.setLabelType(rule.getLabelType());
					item.setData(new ObjectMapper().writeValueAsString(labelDataItems));

					job.getItems().add(item);
					labelDataList.add(makeLabelData(item, rule, labelDataItems));
					found = true;
					break;
				}
				
				if (!found) {
					logger.warn("No print rule matched visit: " + visit.getName());
				}
			}
			
			if (job.getItems().isEmpty()) {
				return null;
			}

			generateCmdFiles(labelDataList);
			daoFactory.getLabelPrintJobDao().saveOrUpdate(job);
			return job;
		} catch (Exception e) {
			e.printStackTrace();
			throw OpenSpecimenException.serverError(e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		reloadRules();
		cfgSvc.registerChangeListener(ConfigParams.MODULE, this);
	}
	
	@Override
	public void onConfigChange(String name, String value) {
		if (!name.equals(ConfigParams.VISIT_LABEL_PRINT_RULES)) {
			return;
		}
		
		reloadRules();
	}		
		
	private void reloadRules() {
		String rulesFile = cfgSvc.getStrSetting(
				ConfigParams.MODULE,
				ConfigParams.VISIT_LABEL_PRINT_RULES, 
				(String)null);
		
		if (StringUtils.isBlank(rulesFile)) {
			return;
		}
		
		boolean classpath = false;
		if (rulesFile.startsWith("classpath:")) {
			rulesFile = rulesFile.substring(10);
			classpath = true;
		}
				
		List<VisitLabelPrintRule> rules = new ArrayList<VisitLabelPrintRule>();
		
		BufferedReader reader = null;
		try {
			if (classpath) {
				reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(rulesFile)));
			} else {
				reader = new BufferedReader(new FileReader(rulesFile));
			}
						
			String ruleLine = null;
			while ((ruleLine = reader.readLine()) != null) {
				VisitLabelPrintRule rule = parseRule(ruleLine);
				if (rule == null) {
					continue;
				}
				
				rules.add(rule);
			}
			
			this.rules = rules;
		} catch (Exception e) {
			logger.error("Error loading rules from file: " + rulesFile, e);
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	//
	// Format of each rule
	// cp_short_title	visit_site	user_login	ip_address	label_type	label_tokens	label_design	printer_name	dir_path
	//
	private VisitLabelPrintRule parseRule(String ruleLine) {
		try {
			if (ruleLine.startsWith("#")) {
				return null;
			}
			
			String[] ruleLineFields = ruleLine.split("\\s+");
			if (ruleLineFields == null || ruleLineFields.length != 10) {
				logger.error("Invalid rule: " + ruleLine);
				return null;
			}
			
			int idx = 0;
			VisitLabelPrintRule rule = new VisitLabelPrintRule();
			rule.setCpShortTitle(ruleLineFields[idx++]);
			rule.setVisitSite(ruleLineFields[idx++]);
			rule.setUserLogin(ruleLineFields[idx++]);
			
			if (!ruleLineFields[idx++].equals("*")) {
				rule.setIpAddressMatcher(new IpAddressMatcher(ruleLineFields[idx - 1]));
			}
			rule.setLabelType(ruleLineFields[idx++]);

			String[] labelTokens = ruleLineFields[idx++].split(",");
			boolean badTokens = false;			
			
			List<LabelTmplToken> tokens = new ArrayList<LabelTmplToken>();
			for (String labelToken : labelTokens) {
				LabelTmplToken token = printLabelTokensRegistrar.getToken(labelToken);
				if (token == null) {
					logger.error("Unknown token: " + token);
					badTokens = true;
					break;
				}
				
				tokens.add(token);
			}
			
			if (badTokens) {
				return null;
			}
			
			rule.setDataTokens(tokens);
			rule.setLabelDesign(ruleLineFields[idx++]);
			rule.setPrinterName(ruleLineFields[idx++]);
			rule.setCmdFilesDir(ruleLineFields[idx++]);

			if (!ruleLineFields[idx++].equals("*")) {
				rule.setCmdFileFmt(ruleLineFields[idx - 1]);
			}

			rule.setMessageSource(messageSource);
			return rule;
		} catch (Exception e) {
			logger.error("Error parsing rule: " + ruleLine, e);
		}
		
		return null;
	}		
}
