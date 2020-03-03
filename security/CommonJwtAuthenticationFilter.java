package com.smoothstack.lms.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class CommonJwtAuthenticationFilter extends GenericFilterBean {

	@Autowired
	private CommonIdentityTenantService commonIdentityTenantService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		logger.info("CommonJwtAuthenticationFilter");
		final String requestTokenHeader = ((HttpServletRequest) request).getHeader("Authorization");
		String jwt = null;

		if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
			logger.warn("JWT Token missing or does not begin with Bearer String");
		} else {
			jwt = requestTokenHeader.substring(7);
			logger.info("JWT Token => " + jwt);

			if (SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = null;
				try {
					userDetails = commonIdentityTenantService.getUserDetailsByJwt(jwt);
				} catch (UsernameNotFoundException e) {
					e.printStackTrace();
				}

				if (userDetails != null) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					usernamePasswordAuthenticationToken.setDetails(
							new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));

					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			} else {
				logger.info("Security Context Authentication is not null");
			}
		}
		filterChain.doFilter(request, response);
	}
}