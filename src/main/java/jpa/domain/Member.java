package jpa.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "age")
    private Integer age;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @OneToMany(mappedBy = "member")
    private Set<Favorite> favorites = new HashSet<>();

    public Member(final Integer age, final String email, final String password) {
        this.age = age;
        this.email = email;
        this.password = password;
    }

    public Member changeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("올바른 이메일이 아닙니다");
        }
        this.email = email;
        return this;
    }
}
