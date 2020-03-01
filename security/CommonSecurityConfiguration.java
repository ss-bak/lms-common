package com.smoothstack.lms.common.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
public class CommonSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Value("${spring.h2.console.path:/h2-console}")
    private String h2ConsolePath;

    @Autowired
    private CommonJwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("u1").password("p1").roles("ADMIN");
        auth.inMemoryAuthentication().withUser("u2").password("p2").roles("USER");
        auth.inMemoryAuthentication().withUser("u3").password("p3").roles("USER");

    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeRequests()
                .antMatchers(

                        "/**"
                )
                .permitAll();

        if (h2ConsoleEnabled) {
            httpSecurity.authorizeRequests().antMatchers(h2ConsolePath + "/**").permitAll();

            httpSecurity.csrf().disable();

            httpSecurity.headers().frameOptions().disable();
        }
//
//        httpSecurity.authorizeRequests()
//                .antMatchers(
//                        "/blogEntry/edit/**",
//                        "/blogEntry/edit/process",
//                        "/blogEntry/delete/**")
//                .access("hasAnyRole('USER', 'ADMIN')");

//        httpSecurity    .authorizeRequests()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin();

        httpSecurity.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin().permitAll();

//        httpSecurity.authorizeRequests()
//                .and()
//                .exceptionHandling()
//                .accessDeniedPage("/403");

        httpSecurity.logout();


//        http.authorizeRequests().and().formLogin()//
//                // Submit URL of login page.
//                .loginProcessingUrl("/j_spring_security_check") // Submit URL
//                .loginPage("/login")//
//                .defaultSuccessUrl("/userAccountInfo")//
//                .failureUrl("/login?error=true")//
//                .usernameParameter("username")//
//                .passwordParameter("password")
//                // Config for Logout Page
//                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/logoutSuccessful");

    }


    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
