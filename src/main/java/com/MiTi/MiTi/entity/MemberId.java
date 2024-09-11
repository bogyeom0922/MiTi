package com.MiTi.MiTi.entity;

import com.MiTi.MiTi.OAuth2.OAuth2Provider;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberId implements Serializable {

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;

    private String providerId;
}