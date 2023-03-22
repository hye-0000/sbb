package com.mysite.sbb.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)  //unique = ture: 유일한 값만 저장할 수 있음, 즉 값을 중복되게 저장할 수 없음
    private String username;
    private String password;

    @Column(unique = true)
    private String email;
}
