package com.zf1976.ant.gateway.filter;

import com.nimbusds.jose.JWSObject;
import com.power.common.model.CommonResult;
import com.zf1976.ant.common.core.constants.AuthConstants;
import com.zf1976.ant.common.core.util.JSONUtil;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 不支持参数形式access_token访问
 *
 * @author ant
 * Create by Ant on 2021/3/6 8:52 AM
 */
@SuppressWarnings("all")
public class OAuth2TokenAuthenticationFilter implements WebFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Pattern authorizationPattern = Pattern.compile(
            "^Bearer (?<token>[a-zA-Z0-9-._~+/]+)=*$",
            Pattern.CASE_INSENSITIVE);
    private final RestTemplate restTemplate = new RestTemplate();
    private final String jwtCheckUrl;


    public OAuth2TokenAuthenticationFilter(String checkTokenUrl) {
        this.jwtCheckUrl = checkTokenUrl + "?token={value}";
    }

    /**
     * 资源服务器携带token访问过滤
     *
     * @param serverWebExchange exchange
     * @param webFilterChain filter chain
     * @return /
     */
    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange serverWebExchange, @NonNull WebFilterChain webFilterChain) {
        final ServerHttpRequest request = serverWebExchange.getRequest();
        final ServerHttpResponse response = serverWebExchange.getResponse();
        // options请求放行
        if (Objects.requireNonNull(request.getMethod()).matches(HttpMethod.OPTIONS.name())) {
            return webFilterChain.filter(serverWebExchange);
        }
        try {
            String token = this.token(request);
            // 无token放行
            if (StringUtils.isEmpty(token)) {
                return webFilterChain.filter(serverWebExchange);
            }
            if (!this.checkToken(token)) {
                BearerTokenError bearerTokenError = this.bearerTokenError();
                throw new OAuth2AuthenticationException(bearerTokenError);
            }
            serverWebExchange.getAttributes()
                             .put(AuthConstants.OWNER, isOwner(token));
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            return this.exceptionHandler(response, e);
        }
        return webFilterChain.filter(serverWebExchange);
    }


    private Boolean isOwner(String token) {
        try {
            Assert.hasText(token, "token cannot be empty");
            final JWSObject parseToken = JWSObject.parse(token);
            // 提取jwt token 载荷
            final Object o = parseToken.getPayload()
                                       .toJSONObject()
                                       .get(AuthConstants.JWT_AUTHORITIES_KEY);
            if (o instanceof JSONArray) {
                return  ((JSONArray) o).stream()
                                       .map(Object::toString)
                                       .anyMatch(permission -> ObjectUtils.nullSafeEquals(permission, AuthConstants.OWNER));
            }
        } catch (ParseException ignored) {
            BearerTokenError bearerTokenError = this.bearerTokenError();
            throw new OAuth2AuthenticationException(bearerTokenError);
        }
        return Boolean.FALSE;
    }

    @SuppressWarnings("rawtypes")
    private boolean checkToken(String token) {
        try {
            Assert.hasText(token, "token cannot be empty");
            // 向服务端校验token 有效性
            ResponseEntity<Map> responseEntity = this.restTemplate.getForEntity(this.jwtCheckUrl, Map.class, token);
            return responseEntity.getStatusCode().is2xxSuccessful();
        } catch (Exception ignored) {
            return false;
        }
    }

    private String token(ServerHttpRequest request) {
        String authorizationHeaderToken = this.resolveRequestAuthorizationHeader(request.getHeaders());
        String accessToken = request.getQueryParams()
                                    .getFirst("access_token");
        if (authorizationHeaderToken != null) {
            if (accessToken != null) {
                BearerTokenError error = BearerTokenErrors.invalidRequest("Found multiple bearer tokens in the request");
                throw new OAuth2AuthenticationException(error);
            } else {
                return authorizationHeaderToken;
            }
        }
        return accessToken != null && this.isParameterTokenSupportedForRequest(request)? accessToken : null;
    }

    private String resolveRequestAuthorizationHeader(HttpHeaders headers) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.startsWithIgnoreCase(authorization, OAuth2AccessToken.TokenType.BEARER.getValue())) {
            Matcher matcher = authorizationPattern.matcher(authorization);
            if (!matcher.matches()) {
                BearerTokenError bearerTokenError = bearerTokenError();
                throw new OAuth2AuthenticationException(bearerTokenError);
            } else {
                return matcher.group("token");
            }
        } else {
            return null;
        }
    }

    private boolean isParameterTokenSupportedForRequest(ServerHttpRequest request) {
        return false;
    }

    private BearerTokenError bearerTokenError(){
        return BearerTokenErrors.invalidToken("invalid token!");
    }

    @SuppressWarnings("rawtypes")
    private Mono<Void> exceptionHandler(ServerHttpResponse response, AuthenticationException e) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().set("Access-Control-Allow-Origin", "*");
        response.getHeaders().set("Cache-Control", "no-cache");
        CommonResult fail = CommonResult.fail(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                HttpStatus.UNAUTHORIZED.getReasonPhrase());
        String body = JSONUtil.toJsonString(fail);
        DataBuffer buffer = response.bufferFactory()
                                    .wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer))
                       .doOnError(error -> {
                           DataBufferUtils.release(buffer);
                           logger.error(error.getMessage(), e);
                       });

    }
}