package com.krishagni.catissueplus.core.auth.services.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.bind.annotation.RequestMethod;

import com.krishagni.catissueplus.core.administrative.domain.User;
import com.krishagni.catissueplus.core.auth.SamlBootstrap;
import com.krishagni.catissueplus.core.auth.domain.AuthCredential;
import com.krishagni.catissueplus.core.auth.events.LoginDetail;
import com.krishagni.catissueplus.core.auth.services.AuthenticationService;
import com.krishagni.catissueplus.core.biospecimen.repository.DaoFactory;
import com.krishagni.catissueplus.core.common.PlusTransactional;
import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
import com.krishagni.catissueplus.core.common.util.AuthUtil;
import com.krishagni.catissueplus.core.common.util.Utility;

@Configurable
public class SamlAuthenticationServiceImpl extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationService {
	@Autowired
	private UserAuthenticationServiceImpl userAuthService;

	@Autowired
	private DaoFactory daoFactory;

	public SamlAuthenticationServiceImpl() {
		
	}
	
	public SamlAuthenticationServiceImpl(Map<String, String> props) {
		SamlBootstrap samlBootStrap = new SamlBootstrap(this, props);

		//calling initialize after all beans are injected
		samlBootStrap.initialize();
	}
	
	@Override
	public void authenticate(LoginDetail loginDetail) {
		throw OpenSpecimenException.serverError(new UnsupportedOperationException("Not supported for this implementation"));
	}

	@Override
	@PlusTransactional
	public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth)
	throws IOException {
		User user = (User) auth.getPrincipal();

		LoginDetail loginDetail = new LoginDetail();
		loginDetail.setIpAddress(Utility.getRemoteAddress(req));
		loginDetail.setApiUrl(req.getRequestURI());
		loginDetail.setRequestMethod(RequestMethod.POST.name());

		String encodedToken = userAuthService.generateToken(user, loginDetail);
		AuthUtil.setTokenCookie(req, resp, encodedToken);
		getRedirectStrategy().sendRedirect(req, resp, getDefaultTargetUrl());

		AuthCredential credential = new AuthCredential();
		credential.setToken(AuthUtil.decodeToken(encodedToken));
		credential.setCredential(auth.getCredentials());
		daoFactory.getAuthDao().saveCredentials(credential);
	}
}
