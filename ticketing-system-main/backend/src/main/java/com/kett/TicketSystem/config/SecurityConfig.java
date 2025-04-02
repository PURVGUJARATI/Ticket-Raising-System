// package com.kett.TicketSystem.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// // import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import com.kett.TicketSystem.authentication.domain.jwt.JwtAuthenticationFilter;
// import com.kett.TicketSystem.authentication.domain.jwt.JwtTokenProvider;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     private final UserDetailsService userDetailsService;
//     private final JwtAuthenticationFilter jwtAuthenticationFilter;
//     private final JwtTokenProvider jwtTokenProvider;

//     public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, JwtTokenProvider jwtTokenProvider) {
//         this.userDetailsService = userDetailsService;
//         this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//         this.jwtTokenProvider = jwtTokenProvider;
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .cors().and().csrf().disable()
//             .authorizeRequests()
//                 .antMatchers("/api/auth/**").permitAll()  // Allow authentication APIs
//                 .antMatchers("/api/analysis/**").authenticated()  // Secure analysis APIs
//                 .anyRequest().authenticated()
//             .and()
//             .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//             .and()
//             .authenticationProvider(authenticationProvider())
//             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

//     @Bean
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//         authProvider.setUserDetailsService(userDetailsService);
//         authProvider.setPasswordEncoder(passwordEncoder());
//         return authProvider;
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }
// }
