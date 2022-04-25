package com.smartcoupon.service.usercard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smartcoupon.service.account.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private long id;

    @JsonProperty
    @Column(nullable = false, unique = true, updatable = false)
    private String cardNo;
    @JsonProperty
    private String cardName;
    @JsonProperty
    private long expMonth;
    @JsonProperty
    private long expYear;
    @JsonProperty
    private long cvv;
    @JsonProperty
    private long availablePoints;

    @JsonIgnore
    private boolean isSmart;

    @OneToOne
    @JoinColumn(name = "email",nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private User user;
}
