package uk.gov.justice.digital.hmpps.oauth2server.config;

import com.microsoft.applicationinsights.TelemetryClient;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.client.RestTemplate;
import uk.gov.justice.digital.hmpps.oauth2server.security.UserContextApprovalHandler;
import uk.gov.justice.digital.hmpps.oauth2server.service.UserContextService;

import javax.sql.DataSource;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;


@SuppressWarnings("deprecation")
@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@Slf4j
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private static final int HOUR_IN_SECS = 60 * 60;
    private final Resource privateKeyPair;
    private final String keystorePassword;
    private final String keystoreAlias;
    private final String keyId;
    private final AuthenticationManager authenticationManager;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final TelemetryClient telemetryClient;
    private final RedirectResolver redirectResolver;
    private final RestTemplate restTemplate;
    private final boolean tokenVerificationEnabled;
    private final TokenVerificationClientCredentials tokenVerificationClientCredentials;
    private final UserContextService userContextService;
    private JdbcClientDetailsService jdbcClientDetailsService;
    private JwtTokenStore tokenStore;
    private DefaultOAuth2RequestFactory oAuth2RequestFactory;

    @Autowired
    public OAuth2AuthorizationServerConfig(@Lazy final AuthenticationManager authenticationManager,
                                           @Value("${jwt.signing.key.pair}") final String privateKeyPair,
                                           @Value("${jwt.keystore.password}") final String keystorePassword,
                                           @Value("${jwt.keystore.alias:elite2api}") final String keystoreAlias,
                                           @Value("${jwt.jwk.key.id}") final String keyId,
                                           @Qualifier("authDataSource") final DataSource dataSource,
                                           @Lazy final RedirectResolver redirectResolver,
                                           final PasswordEncoder passwordEncoder, final TelemetryClient telemetryClient,
                                           @Qualifier("tokenVerificationApiRestTemplate") final RestTemplate restTemplate,
                                           @Value("${tokenverification.enabled:false}") final boolean tokenVerificationEnabled,
                                           final TokenVerificationClientCredentials tokenVerificationClientCredentials,
                                           final UserContextService userContextService) {

        this.privateKeyPair = new ByteArrayResource(Base64.decodeBase64(privateKeyPair));
        this.keystorePassword = keystorePassword;
        this.keystoreAlias = keystoreAlias;
        this.keyId = keyId;
        this.authenticationManager = authenticationManager;
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
        this.telemetryClient = telemetryClient;
        this.redirectResolver = redirectResolver;
        this.restTemplate = restTemplate;
        this.tokenVerificationEnabled = tokenVerificationEnabled;
        this.tokenVerificationClientCredentials = tokenVerificationClientCredentials;
        this.userContextService = userContextService;
    }

    @Bean
    public TokenStore tokenStore() {
        if (tokenStore == null) {
            tokenStore = new JwtTokenStore(accessTokenConverter());
        }
        return tokenStore;
    }

    @Bean
    @Primary
    public JdbcClientDetailsService jdbcClientDetailsService() {
        if (jdbcClientDetailsService == null) {
            jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
            jdbcClientDetailsService.setPasswordEncoder(passwordEncoder);
        }
        return jdbcClientDetailsService;
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(jdbcClientDetailsService());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final var keyStoreKeyFactory = new KeyStoreKeyFactory(privateKeyPair, keystorePassword.toCharArray());
        return new JwtKeyIdHeaderAccessTokenConverter(keyId, keyStoreKeyFactory.getKeyPair(keystoreAlias));

    }

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Bean
    public TokenEnhancer jwtTokenEnhancer() {
        return new JWTTokenEnhancer();
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain())
                .redirectResolver(redirectResolver)
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(new JdbcAuthorizationCodeServices(dataSource))
                .requestFactory(requestFactory())
                .userApprovalHandler(userApprovalHandler())
                .tokenServices(tokenServices());
    }

    private UserApprovalHandler userApprovalHandler() {
        final var approvalHandler = new UserContextApprovalHandler(userContextService);
        approvalHandler.setClientDetailsService(jdbcClientDetailsService());
        approvalHandler.setRequestFactory(requestFactory());
        approvalHandler.setTokenStore(tokenStore());
        return approvalHandler;
    }

    private OAuth2RequestFactory requestFactory() {
        if (oAuth2RequestFactory == null) {
            oAuth2RequestFactory = new DefaultOAuth2RequestFactory(jdbcClientDetailsService());
        }
        return oAuth2RequestFactory;
    }

    @Bean
    public TokenEnhancerChain tokenEnhancerChain() {
        final var tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtTokenEnhancer(), accessTokenConverter()));
        return tokenEnhancerChain;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        final var tokenServices = new TrackingTokenServices(telemetryClient, restTemplate, tokenVerificationClientCredentials, tokenVerificationEnabled);
        tokenServices.setTokenEnhancer(tokenEnhancerChain());
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setAccessTokenValiditySeconds(HOUR_IN_SECS); // default 1 hours
        tokenServices.setRefreshTokenValiditySeconds(HOUR_IN_SECS * 12); // default 12 hours
        tokenServices.setClientDetailsService(jdbcClientDetailsService());
        tokenServices.setAuthenticationManager(authenticationManager);
        return tokenServices;
    }

    @Bean
    public JWKSet jwkSet() {
        final var keyStoreKeyFactory = new KeyStoreKeyFactory(privateKeyPair, keystorePassword.toCharArray());
        final var builder = new RSAKey.Builder((RSAPublicKey) keyStoreKeyFactory.getKeyPair(keystoreAlias).getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(keyId);
        return new JWKSet(builder.build());
    }
}