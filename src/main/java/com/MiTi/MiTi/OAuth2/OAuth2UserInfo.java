package com.MiTi.MiTi.OAuth2;

import java.util.Map;

//유저 정보 저장
// 공통적인 인터페이스와 기본 동작을 제공
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;
    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    public abstract String getProviderId(); //소셜 식별 값 : 구글 - "sub", 카카오 - "id", 네이버 - "id"
    public abstract OAuth2Provider getProvider();
    public abstract String getEmail();
    public abstract String getName();
    public abstract String getImage();
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}