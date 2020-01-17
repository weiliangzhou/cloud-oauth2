package com.zwl.security.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @ClassName TokenConfig
 * @Description
 * @Author 二师兄
 * @Date 2020-01-14 14:24
 * @Version V1.0
 **/
@Configuration
public class TokenConfig {

    /**
     * InMemoryTokenStore:token存储在本机的内存之中
     * JdbcTokenStore:token存储在数据库之中
     * JwtTokenStore:token不会存储到任何介质中
     * RedisTokenStore:token存储在Redis数据库之中
     *
     * @return
     */

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /**
     * JWT模式
     */
    private String SIGNING_KEY = "uaa123";

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        //对称秘钥，资源服务器使用该秘钥来验证
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }
}
