package com.empresa.empresa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    /**
     * Configura el filtro de seguridad HTTP
     * @param http configuración de seguridad HTTP
     * @return SecurityFilterChain configurado
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Recursos estáticos accesibles para todos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Página de login y registro accesibles para todos
                .requestMatchers("/login", "/registro").permitAll()
                // Endpoints para reset de contraseña
                .requestMatchers("/olvidaste-contrasena", "/reset-password").permitAll()
                // API REST endpoints - requieren autenticación
                .requestMatchers("/api/**").authenticated()
                // Debug endpoints (considerar deshabilitar en producción)
                .requestMatchers("/debug/**").hasRole("ADMIN")
                // Rutas específicas para roles
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf
                // Deshabilitar CSRF para las APIs REST
                .ignoringRequestMatchers("/api/**", "/debug/**")
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/acceso-denegado")
            );

        return http.build();
    }

    /**
     * Configura el administrador de autenticación
     * @param authConfig configuración de autenticación
     * @return AuthenticationManager configurado
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}