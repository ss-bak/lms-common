package com.smoothstack.lms.common.security;

import com.smoothstack.lms.borrowermicroservice.Debug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CommonAnonymousIdProvider {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/identityprovider/getuserdetails")
    public ResponseEntity<Object> anonymousIdProvider(HttpServletRequest request,
                                                       @RequestHeader MultiValueMap<String, String> headers) {

        final String requestTokenHeader = request.getHeader("Proxy-Authorization");
        String jwt = null;

        if (requestTokenHeader == null || !requestTokenHeader.startsWith("JWT ")) {
            return ResponseEntity.badRequest().body(requestTokenHeader == null?"<NULL>":request.getHeader("Proxy-Authorization"));
        }
        jwt = requestTokenHeader.substring(4);
        Debug.printf("JWT = %s\n",jwt);

        /* TODO:
                1. Validate JWT
                2. Get subject from JWT, this will be the username (or use JwtUtils)
                3. Query UserDetails from database by username
                4. Construct UserDetails (build) or if UserObject from database
                   is implements UserDetails, just use it as-is
                5. Return UserDetails to IdentityTenant
         */

        User.UserBuilder userBuilder = User.withUsername("anonymous");
        userBuilder.password(jwt);
        userBuilder.roles("ANONYMOUS", "UNCONFIGURED");
        return ResponseEntity.ok().body(userBuilder.build());
    }

}
