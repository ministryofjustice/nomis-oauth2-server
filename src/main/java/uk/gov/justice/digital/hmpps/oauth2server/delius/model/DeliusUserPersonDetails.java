package uk.gov.justice.digital.hmpps.oauth2server.delius.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User;
import uk.gov.justice.digital.hmpps.oauth2server.security.AuthSource;
import uk.gov.justice.digital.hmpps.oauth2server.security.UserPersonDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Builder
@Data
public class DeliusUserPersonDetails implements UserPersonDetails {
    private String surname;
    private String firstName;
    private String username;
    private boolean locked;
    private String email;
    private Collection<? extends GrantedAuthority> roles;

    @Override
    public String getUserId() {
        return username;
    }

    @Override
    public String getName() {
        return String.format("%s %s", firstName, surname);
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public String getAuthSource() {
        return "delius";
    }

    @Override
    public User toUser() {
        return User.builder().username(username).source(AuthSource.delius).email(email).verified(true).build();
    }

    @Override
    public void eraseCredentials() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.concat(roles.stream(), Set.of(new SimpleGrantedAuthority("ROLE_PROBATION")).stream()).collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return "password";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}