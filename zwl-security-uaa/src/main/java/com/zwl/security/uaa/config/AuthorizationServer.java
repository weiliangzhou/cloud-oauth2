package com.zwl.security.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @ClassName AuthorizationServer
 * @Description
 * @Author 二师兄
 * @Date 2020-01-14 14:09
 * @Version V1.0
 **/
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtAccessTokenConverter accessTokenConverter;


    /**
     * 密码编码器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public ClientDetailsService clientDetailsService(DataSource dataSource) {
//        ClientDetailsService clientDetailsService = new JdbcClientDetailsService(dataSource);
//        ((JdbcClientDetailsService) clientDetailsService).setPasswordEncoder(passwordEncoder());
//        return clientDetailsService;
//    }

    /**
     * 1.配置客户端详情信息服务
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        // 使用in‐memory存储
        clients.inMemory()
                //client_id:(必须的)用来标识客户的Id
                .withClient("c1")
                //客户端密钥:(需要值得信任的客户端)客户端安全码，如果有的话。
                .secret(passwordEncoder().encode("secret"))
                //资源列表
                .resourceIds("res1")
                //该client允许的授权类型:此客户端可以使用的授权类型，默认为空。
                .authorizedGrantTypes("authorization_code", "password", "client_credentials", "implicit", "refresh_token")
                //允许的授权范围:用来限制客户端的访问范围，如果为空(默认)的话，那么客户端拥有全部的访问范围。
                .scopes("ROLE_API")
                //登录后绕过批准询问(/oauth/confirm_access)
                .autoApprove(false)
                //加上验证回调地址:授权码模式回调
                .redirectUris("http://www.baidu.com");
        //使用jdbc模式
//        clients.withClientDetails(clientDetailsService);
    }

    /**
     * 2.配置令牌管理服务
     *
     * @return
     */
    @Bean
    public AuthorizationServerTokenServices tokenService() {
        DefaultTokenServices service = new DefaultTokenServices();
        //客户端服务详情
        service.setClientDetailsService(clientDetailsService);
        //支持刷新令牌
        service.setSupportRefreshToken(true);
        //令牌存储策略
        service.setTokenStore(tokenStore);
        //JWT
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
        service.setTokenEnhancer(tokenEnhancerChain);
        //令牌默认有效期2小时
        service.setAccessTokenValiditySeconds(7200);
        //刷新令牌默认有效期3天
        service.setRefreshTokenValiditySeconds(259200);
        return service;
    }


    /**
     * 3.配置令牌(token)的访问端点
     *
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                //密码模式需要
                .authenticationManager(authenticationManager)
                //授权码模式需要
                .authorizationCodeServices(authorizationCodeServices)
                //令牌管理服务
                .tokenServices(tokenService())
                /**
                 pathMapping用来配置端点URL链接，第一个参数是端点URL默认地址，第二个参数是你要替换的URL地址
                 上面的参数都是以“/”开头，框架的URL链接如下：
                 /oauth/authorize：授权端点。----对应的类：AuthorizationEndpoint.java
                 /oauth/token：令牌端点。----对应的类：TokenEndpoint.java
                 /oauth/confirm_access：用户确认授权提交端点。----对应的类：WhitelabelApprovalEndpoint.java
                 /oauth/error：授权服务错误信息端点。
                 /oauth/check_token：用于资源服务访问的令牌解析端点。
                 /oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。
                 */
//                .pathMapping("/oauth/confirm_access", "/custom/confirm_access")
                //允许post提交
                .allowedTokenEndpointRequestMethods(HttpMethod.POST);
    }

    /**
     * 设置授权码模式的授权码如何存取
     * 内存模式
     *
     * @return
     */
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

//    /**
//     * 设置授权码模式的授权码如何存取
//     * jdbc模式
//     *
//     * @return
//     */
//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
//
//        return new JdbcAuthorizationCodeServices(dataSource);
//    }

    /**
     * 4.配置令牌端点(Token Endpoint)的安全约束
     */

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                //oauth/token_key公开
                .tokenKeyAccess("permitAll()")
                //oauth/check_token公开
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

//    public static void main(String[] args) {
//        String s = "secret";
//        String hashpw = BCrypt.hashpw(s, BCrypt.gensalt());
//        System.out.println(hashpw);
//
//    }


}
