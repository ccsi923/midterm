package com.ironhack.midterm.security;


import com.ironhack.midterm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UserService userService;
  
  @Bean
  public PasswordEncoder passwordEncoder () {
    return new BCryptPasswordEncoder();
  }
  
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(userService)
      .passwordEncoder(passwordEncoder);
  }

  @Override
  public void configure(HttpSecurity httpSecurity) throws Exception {

    httpSecurity.httpBasic();
    httpSecurity.authorizeRequests().antMatchers(HttpMethod.POST, "/user/thirdparty").hasAuthority("ROLE_ADMIN")
            .antMatchers(HttpMethod.POST, "/debit/thirdparty").hasAuthority("ROLE_THIRDPARTY")
            .antMatchers(HttpMethod.POST, "/credit/thirdparty").hasAuthority("ROLE_THIRDPARTY")
            .antMatchers(HttpMethod.POST, "/transaction").hasAuthority("ROLE_ACCOUNTHOLDER")

            //.antMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
            .antMatchers("/^((?!resources/**).)*$").hasAuthority("ROLE_ADMIN")
            //.antMatchers("/", "/resources/**").permitAll()
            .and().logout().deleteCookies("JSESSIONID");

    httpSecurity.csrf().disable();
  }
}
