package com.dev.ssms.model;

import com.dev.ssms.component.util.UtilService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "authentication")
public class Authentication extends SharedModule {
    @Id
    @Column(name = "authentication_seq")
    private String authenticationSeq;
    @Column(name = "password")
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_seq", referencedColumnName = "user_seq")
    private User user;

    public static Authentication initFrom(String password) {
        Authentication authentication = new Authentication();
        authentication.setPassword(UtilService.bCryptPassword(password));
        return authentication;
    }
}
