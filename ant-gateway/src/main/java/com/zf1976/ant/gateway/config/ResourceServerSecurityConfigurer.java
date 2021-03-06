package com.zf1976.ant.gateway.config;

import com.zf1976.ant.gateway.filter.GatewayRouteFilter;
import com.zf1976.ant.gateway.manager.GatewayReactiveAuthorizationManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;


/**
 * @author mac
 * @date 2021/2/11
 **/
@Configuration
@EnableWebFluxSecurity
public class ResourceServerSecurityConfigurer {

    private final ReactiveAuthorizationManager<AuthorizationContext> reactiveAuthorizationManager;

    public ResourceServerSecurityConfigurer(GatewayReactiveAuthorizationManager reactiveAuthenticationManager) {
        this.reactiveAuthorizationManager = reactiveAuthenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity.httpBasic().disable()
                    .csrf()
                    // 关闭csrf
                    .disable()
                    // 允许跨域
                    .cors()
                    .and()
                    // 关闭表单登录
                    .formLogin().disable()
                    .authorizeExchange(authorizeExchangeSpec -> {
                        authorizeExchangeSpec.pathMatchers("/actuator/**","/oauth/**").permitAll()
                                             .anyExchange()
                                             // 自定义授权处理
                                             .access(this.reactiveAuthorizationManager);
                    })
                    .oauth2ResourceServer(oAuth2ResourceServerSpec -> {
                        oAuth2ResourceServerSpec.jwt(jwtSpec -> {
                            jwtSpec.jwtAuthenticationConverter(this.jwtConverter())
                                   .jwkSetUri("http://localhost:9000/oauth/token_key");
                        }).bearerTokenConverter(new ServerBearerTokenAuthenticationConverter());
                    })
                    .addFilterBefore(new GatewayRouteFilter(), SecurityWebFiltersOrder.HTTP_BASIC);
        return httpSecurity.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}