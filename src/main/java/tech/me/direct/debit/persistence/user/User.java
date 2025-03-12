package tech.me.direct.debit.persistence.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"oauth_provider", "oauth_user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends tech.me.direct.debit.persistence.Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "national_id", nullable = false, length = 10)
    private String nationalId;

    @Column(name = "phone_number", nullable = false, length = 12)
    private String phoneNumber;
}