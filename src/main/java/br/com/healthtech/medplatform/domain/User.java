package br.com.healthtech.medplatform.domain;

import br.com.healthtech.medplatform.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tb_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "seq_user_id", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Override
    @NullMarked
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Medic has access as a medic and also as a client
        if (this.role == UserRole.MEDIC) {
            return List.of(new SimpleGrantedAuthority("ROLE_MEDIC"), new SimpleGrantedAuthority("ROLE_CLIENT"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENT"));
    }

    /// Establishes which attribute/field will identify the user for logins. In this case, the login is made with the email.
    @Override
    @NullMarked
    public String getUsername() {
        return this.email;
    }

    /// Establishes which attribute/field will represent the account (User) password (credentials).
    @Override
    public String getPassword() {
        return this.password;
    }

    /// Indicates if thw user account has expired.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /// Indicates if the user account has been blocked.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /// Indicates if the user password has expired by time.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /// Indicates if the account is currently active.
    @Override
    public boolean isEnabled() {
        return true;
    }




    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
