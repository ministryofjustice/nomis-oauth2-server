package uk.gov.justice.digital.hmpps.oauth2server.nomis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "DBA_USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"username"})
@ToString(of = {"username"})
public class AccountDetail {

    @Id
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "ACCOUNT_STATUS", nullable = false)
    private String accountStatus;

    @Column(name = "PROFILE")
    private String profile;

    @Column(name = "expiry_date")
    private LocalDateTime passwordExpiry;

    public AccountStatus getStatus() {
        return AccountStatus.get(accountStatus);
    }

    public AccountProfile getAccountProfile() {
        return AccountProfile.get(profile);
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccountStatus() {
        return this.accountStatus;
    }

    public String getProfile() {
        return this.profile;
    }

    public LocalDateTime getPasswordExpiry() {
        return this.passwordExpiry;
    }
}
