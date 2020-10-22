package com.krishagni.catissueplus.core.common.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.util.ReflectionUtils;

import com.krishagni.catissueplus.core.administrative.domain.User;
import com.krishagni.catissueplus.core.common.Pair;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.util.MessageUtil;
import com.krishagni.catissueplus.core.common.util.Utility;

public abstract class LabelPrintRule {
	private static final String DEF_LINE_ENDING = "LF";

	private static final String DEF_FILE_EXTN = "txt";

	public enum CmdFileFmt {
		CSV("csv"),
		KEY_VALUE("key-value"),
		KEY_Q_VALUE("key-q-value");

		private String fmt;

		CmdFileFmt(String fmt) {
			this.fmt = fmt;
		}

		public static CmdFileFmt get(String input) {
			for (CmdFileFmt cfFmt : values()) {
				if (cfFmt.fmt.equals(input)) {
					return cfFmt;
				}
			}

			return null;
		}
	};

	private String labelType;
	
	private IpAddressMatcher ipAddressMatcher;

	private List<User> users = new ArrayList<>();
	
	private String printerName;
	
	private String cmdFilesDir;

	private String labelDesign;

	private List<Pair<LabelTmplToken, List<String>>> dataTokens = new ArrayList<>();

	private CmdFileFmt cmdFileFmt = CmdFileFmt.KEY_VALUE;

	private String lineEnding;

	private String fileExtn;

	public String getLabelType() {
		return labelType;
	}

	public void setLabelType(String labelType) {
		this.labelType = labelType;
	}

	public IpAddressMatcher getIpAddressMatcher() {
		return ipAddressMatcher;
	}

	public void setIpAddressMatcher(IpAddressMatcher ipAddressMatcher) {
		this.ipAddressMatcher = ipAddressMatcher;
	}

	public void setUserLogin(User user) {
		users = new ArrayList<>();
		if (user != null) {
			users.add(user);
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getPrinterName() {
		return printerName;
	}

	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}

	public String getCmdFilesDir() {
		return cmdFilesDir;
	}

	public void setCmdFilesDir(String cmdFilesDir) {
		this.cmdFilesDir = cmdFilesDir;
	}

	public String getLabelDesign() {
		return labelDesign;
	}

	public void setLabelDesign(String labelDesign) {
		this.labelDesign = labelDesign;
	}

	public List<Pair<LabelTmplToken, List<String>>> getDataTokens() {
		return dataTokens;
	}

	public void setDataTokens(List<Pair<LabelTmplToken, List<String>>> dataTokens) {
		this.dataTokens = dataTokens;
	}

	public CmdFileFmt getCmdFileFmt() {
		return cmdFileFmt;
	}

	public void setCmdFileFmt(CmdFileFmt cmdFileFmt) {
		this.cmdFileFmt = cmdFileFmt;
	}

	public void setCmdFileFmt(String fmt) {
		this.cmdFileFmt = CmdFileFmt.get(fmt);
		if (this.cmdFileFmt == null) {
			throw new IllegalArgumentException("Invalid command file format: " + fmt);
		}
	}

	public String getLineEnding() {
		return StringUtils.isNotBlank(lineEnding) ? lineEnding : DEF_LINE_ENDING;
	}

	public void setLineEnding(String lineEnding) {
		this.lineEnding = lineEnding;
	}

	public String getFileExtn() {
		return StringUtils.isNotBlank(fileExtn) ? fileExtn : DEF_FILE_EXTN;
	}

	public void setFileExtn(String fileExtn) {
		this.fileExtn = fileExtn;
	}

	public boolean isApplicableFor(User user, String ipAddr) {
		if (CollectionUtils.isNotEmpty(users) && !users.stream().anyMatch(u -> u.equals(user))) {
			return false;
		}

		if (ipAddressMatcher != null && !ipAddressMatcher.matches(ipAddr)) {
			return false;
		}
		
		return true;
	}
	
	public Map<String, String> getDataItems(PrintItem<?> printItem) {
		try {
			Map<String, String> dataItems = new LinkedHashMap<>();


			if (!isWildCard(labelDesign)) {
				dataItems.put(getMessageStr("LABELDESIGN"), labelDesign);
			}

			if (!isWildCard(labelType)) {
				dataItems.put(getMessageStr("LABELTYPE"), labelType);
			}

			if (!isWildCard(printerName)) {
				dataItems.put(getMessageStr("PRINTER"), printerName);
			}
			
			for (Pair<LabelTmplToken, List<String>> tokenArgs : dataTokens) {
				LabelTmplToken token = tokenArgs.first();
				List<String> args = tokenArgs.second();

				String name = token.getName().toLowerCase();
				if (args.size() > 1 && (name.equals("eval") || name.equals("custom_field"))) {
					name = args.get(0).replaceAll("^\"|\"$", "");
					args = args.subList(1, args.size());
				} else {
					name = getMessageStr(name);
				}

				dataItems.put(name, token.getReplacement(printItem.getObject(), args.toArray(new String[0])));
			}

			return dataItems;
		} catch (Exception e) {
			throw OpenSpecimenException.serverError(e);
		}
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("label design = ").append(getLabelDesign())
			.append(", label type = ").append(getLabelType())
			.append(", user = ").append(getUsersList(true))
			.append(", printer = ").append(getPrinterName())
			.append(", line ending = ").append(getLineEnding())
			.append(", file extension = ").append(getFileExtn());

		String tokens = getDataTokens().stream()
			.map(Pair::first)
			.map(LabelTmplToken::getName)
			.collect(Collectors.joining(";"));
		result.append(", tokens = ").append(tokens);
		return result.toString();
	}

	public Map<String, String> toDefMap() {
		return toDefMap(false);
	}

	public Map<String, String> toDefMap(boolean ufn) {
		try {
			Map<String, String> rule = new HashMap<>();
			rule.put("labelType", getLabelType());
			rule.put("ipAddressMatcher", getIpAddressRange(getIpAddressMatcher()));
			rule.put("users", getUsersList(ufn));
			rule.put("printerName", getPrinterName());
			rule.put("cmdFilesDir", getCmdFilesDir());
			rule.put("labelDesign", getLabelDesign());
			rule.put("dataTokens", getTokens());
			rule.put("cmdFileFmt", getCmdFileFmt().fmt);
			rule.put("lineEnding", getLineEnding());
			rule.put("fileExtn", getFileExtn());
			rule.putAll(getDefMap(ufn));
			return rule;
		} catch (Exception e) {
			throw new RuntimeException("Error in creating map from print rule ", e);
		}
	}

	protected abstract Map<String, String> getDefMap(boolean ufn);

	protected boolean isWildCard(String str) {
		return StringUtils.isBlank(str) || str.trim().equals("*");
	}

	private String getMessageStr(String name) {
		return MessageUtil.getInstance().getMessage("print_" + name, null);
	}

	private String getTokens() {
		StringBuilder tokenStr = new StringBuilder();
		for (Pair<LabelTmplToken, List<String>> tokenArgs : dataTokens) {
			if (tokenStr.length() > 0) {
				tokenStr.append(",");
			}

			LabelTmplToken token = tokenArgs.first();
			List<String> args = tokenArgs.second();

			tokenStr.append(token.getName());
			if (args != null && !args.isEmpty()) {
				tokenStr.append("(").append(String.join(",", args)).append(")");
			}
		}

		return tokenStr.toString();
	}

	private String getIpAddressRange(IpAddressMatcher ipRange) {
		if (ipRange == null) {
			return null;
		}

		String address = getFieldValue(ipAddressMatcher, "requiredAddress").toString();
		address = address.substring(address.indexOf("/") + 1);

		int maskBits = getFieldValue(ipAddressMatcher, "nMaskBits");
		return address + "/" + maskBits;
	}

	private <T> T getFieldValue(Object obj, String fieldName) {
		Field field = ReflectionUtils.findField(obj.getClass(), fieldName);
		field.setAccessible(true);
		return (T)ReflectionUtils.getField(field, obj);
	}

	private String getUsersList(boolean ufn) {
		Function<User, String> mapper = ufn ? (u) -> u.getLoginName() : (u) -> u.getId().toString();
		return Utility.nullSafeStream(getUsers()).map(mapper).collect(Collectors.joining(","));
	}
}
