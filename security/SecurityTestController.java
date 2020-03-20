package com.smoothstack.lms.common.security;

import com.smoothstack.lms.common.jwt.JsonWebToken;
import com.smoothstack.lms.common.jwt.JwtServices;
import com.smoothstack.lms.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/security-test")
public class SecurityTestController {
    @Autowired
    private JwtServices jwtServices;
    /**
     * With correct configuration, only request with proper JWT will be able to access this resource
     * @param principal Spring boot principal
     * @param servletRequest Java Servlet Request
     * @return Information about request
     */

    @RequestMapping(path = {"/administrator","/librarian","/borrower"}  )
    public ResponseEntity testJwtAccessControl(Principal principal, HttpServletRequest servletRequest) {
        Response response = new Response();
        response.getPayload().setStatus(HttpStatus.OK);
        response.getPayload().getRequest().put("principal", principal);

        response.getPayload().getRequest().put("HttpServletRequest.getMethod()", servletRequest.getMethod());
        response.getPayload().getRequest().put("HttpServletRequest.getRequestURI()", servletRequest.getRequestURI());

        response.getPayload().getRequest().put("HttpServletRequest.isUserInRole(''TEST_ADMIN'')",
                                servletRequest.isUserInRole("TEST_ADMIN"));
        response.getPayload().getRequest().put("HttpServletRequest.isUserInRole(''TEST_LIBRARIAN'')",
                servletRequest.isUserInRole("TEST_LIBRARIAN"));
        response.getPayload().getRequest().put("HttpServletRequest.isUserInRole(''TEST_BORROWER'')",
                servletRequest.isUserInRole("TEST_BORROWER"));

        response.getPayload().getResponse().put("message", "Success!");

        return response.buildResponseEntity();
    }

    @RequestMapping (
            method = RequestMethod.POST,
            path = {"/generate-test-token"}
    )
    public ResponseEntity generateTestToken(
            @RequestParam(defaultValue = "test") String username,
            @RequestParam(required = false) List<String> authorities) {
        try {

        User.UserBuilder userBuilder = User.withUsername(username);
        userBuilder.password("");
        if (authorities != null)
            userBuilder.authorities(
                    authorities.stream()
                    .map(s->String.format("TEST_%s", s.trim().toUpperCase()))
                    .toArray(String[]::new));
        else
            userBuilder.authorities(Collections.emptyList());

        JsonWebToken token = jwtServices.getJasonWebToken(userBuilder.build());

        Response response = Response.code(HttpStatus.OK);

        response.getPayload().getRequest().put("userDetails", userBuilder.build());

        response.getPayload().getResponse().put("jws", token);

        return response.buildResponseEntity();

        } catch (Exception e) {
            Response response = Response.code(HttpStatus.BAD_REQUEST);
            response.getPayload().getResponse().put("message", e.getMessage());
            response.getPayload().getResponse().put("exception", e);
            return response.buildResponseEntity();
        }

    }

    @RequestMapping (
            method = RequestMethod.GET,
            path = {"/claim-test-token"},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Object> claimTestToken(@RequestHeader("Proxy-Authorization") String proxyAuthorization) {
        if (null == proxyAuthorization) {
            return ResponseEntity.badRequest().body("Header missing 'Proxy-Authorization: JWT <jwt-token>'");
        }
        if (!proxyAuthorization.startsWith("JWT ")) {
            return ResponseEntity.badRequest().body("Token not start with 'JWT' string.");
        }

        JsonWebToken token = new JsonWebToken(proxyAuthorization.substring(4));

        return ResponseEntity.ok(jwtServices.getUserDetails(token, JwtServices::extractFromTrustedSource));
    }
}
