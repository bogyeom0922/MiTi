package com.MiTi.MiTi.OAuth2;

public enum OAuth2Provider {
    SPOTIFY("spotify", "id");

    private final String providerName;
    private final String attributeKey;

    OAuth2Provider(String providerName, String attributeKey) {
        this.providerName = providerName;
        this.attributeKey = attributeKey;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getAttributeKey() {
        return attributeKey;
    }
}

