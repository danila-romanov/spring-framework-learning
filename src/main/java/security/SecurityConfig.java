package security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import security.handlers.ApiAccessDenied;
import security.handlers.ApiEntryPoint;
import security.jwt.JwtAuthenticationFilter;
import security.jwt.JwtAuthorizationFilter;

@EnableWebSecurity
@PropertySource("classpath:/application.properties")
public class SecurityConfig {

    @Value("${jwt.signing.key}")
    private String key;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

//        http.formLogin();

        http.authorizeHttpRequests()
                .requestMatchers("/api/**")
                .authenticated()
                .requestMatchers("/api/version").permitAll()
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/**").permitAll();

        http.exceptionHandling()
                .authenticationEntryPoint(new ApiEntryPoint())
                .accessDeniedHandler(new ApiAccessDenied());

        http.apply(new FilterConfigurer());

        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    public class FilterConfigurer extends AbstractHttpConfigurer<FilterConfigurer, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            super.configure(http);

            AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);

            http.addFilterBefore(new JwtAuthenticationFilter(manager, "/api/login", key),
                    UsernamePasswordAuthenticationFilter.class);
            http.addFilterBefore(new JwtAuthorizationFilter(key), AuthorizationFilter.class);
        }


    }
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                .password("$2a$10$XZmdbP8VWJJ1WD7j1TLTA.lbpLqNvyiZABk3e2007i42UJXEys6Dy")
                .authorities("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("$2a$10$T33Lj5aIdaCQUeubMQWvq.egkzKgtjvNZ51JsBRy.TK4WE1RR1L6y")
                .authorities("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);
        System.out.println(bCryptPasswordEncoder.encode("user"));
        System.out.println(bCryptPasswordEncoder.encode("admin"));
    }
}
