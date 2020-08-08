package com.xml.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
    PasswordEncoder passwordEncoder;
 
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		 auth.inMemoryAuthentication()
	        .passwordEncoder(passwordEncoder)
	        .withUser("admin").password(passwordEncoder.encode("admin")).roles("ADMIN");
	}

	  @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	  
	@Override
	public void configure(HttpSecurity http) throws Exception {
		 http.authorizeRequests()
	        .antMatchers("/login")
	            .permitAll()
	        .antMatchers("/**")
	            .hasAnyRole("ADMIN")
	        .and()
	            .formLogin()
	            .loginPage("/login")
	            .defaultSuccessUrl("/dashboard")
	            .failureUrl("/login?error=true")
	            .permitAll()
	        .and()
	            .logout()
	            .logoutSuccessUrl("/login?logout=true")
	            .invalidateHttpSession(true)
	            .permitAll()
	        .and()
	            .csrf()
	            .disable();

	}
}
