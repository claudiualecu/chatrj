package com.rj.chatrj;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@SpringBootApplication
public class ChatrjApplication {

	@Autowired
	private DataInitializer dataInitializer;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(ChatrjApplication.class, args);
	}

	@EnableWebSecurity
	@Configuration
	class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
					.addFilterAfter(new JWTAuthorizationFilter(jdbcTemplate), UsernamePasswordAuthenticationFilter.class)
					.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/user").permitAll()
					.antMatchers("/chat").permitAll()
					.antMatchers("/**/*.js", "/**/*.css", "/**/*.scss", "/**/*.html").permitAll()
					.anyRequest().authenticated();
		}

		@EventListener({ApplicationReadyEvent.class})
		public void applicationReadyEvent() {
			System.out.println("Initializing core classes ...");
			dataInitializer.initializeSystemProperties();
			dataInitializer.initializeApplicationProperties();
		}
	}
}
