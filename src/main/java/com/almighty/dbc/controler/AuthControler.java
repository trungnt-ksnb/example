package com.almighty.dbc.controler;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.almighty.dbc.configuration.JwtAuthenticationResponse;
import com.almighty.dbc.security.JwtTokenProvider;

/**
 * @author trungnt
 *
 */
@RestController
@RequestMapping("/auth")
public class AuthControler {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtTokenProvider tokenProvider;

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public ResponseEntity<?> findUsers(HttpServletRequest request) {
		// Authorization:"Basic
		// dXNlcjpjZDJmMTNiYS0zZWM3LTQ0NjQtODZiNi1hYjgzMmMxNmFiNGM="
		String authorization = request.getHeader("Authorization");
		String username = "";
		String password = "";
		if (authorization != null) {
			authorization = authorization.replace("Basic ", "");
			authorization = new String(Base64.getDecoder().decode(authorization.getBytes()));
			String[] authoInfo = authorization.split(":");
			if (authoInfo != null && authoInfo.length == 2) {
				username = authoInfo[0];
				password = authoInfo[1];
			}
		}

		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken(authentication);

		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

	}

}
