package uk.gov.justice.digital.hmpps.oauth2server.auth.model;

import com.google.common.base.Splitter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "OAUTH_SERVICE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"code"})
public class Service {
    @Id
    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "authorised_roles")
    private String authorisedRoles;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private boolean enabled;

    @Column
    private String email;

    public List<String> getRoles() {
        return StringUtils.isBlank(authorisedRoles) ? Collections.emptyList() : Splitter.on(',').trimResults().splitToList(authorisedRoles);
    }

    public boolean isUrlInsteadOfEmail() {
        return StringUtils.startsWith(email, "http");
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getAuthorisedRoles() {
        return authorisedRoles;
    }

    public String getAuthorisedRolesWithNewlines() {
        return StringUtils.defaultIfBlank(authorisedRoles, "").replaceAll(",", "\n");
    }

    public void setAuthorisedRolesWithNewlines(final String authorisedRolesWithNewlines) {
        authorisedRoles = StringUtils.defaultIfBlank(authorisedRolesWithNewlines, "").replaceAll("\n", ",");
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String getEmail() {
        return this.email;
    }
}
