package uk.gov.justice.digital.hmpps.oauth2server.maintain;

import com.google.common.collect.Sets;
import com.microsoft.applicationinsights.TelemetryClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.Authority;
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.RoleRepository;
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.UserRepository;
import uk.gov.justice.digital.hmpps.oauth2server.security.MaintainUserCheck;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional(transactionManager = "authTransactionManager", readOnly = true)
@AllArgsConstructor
public class AuthUserRoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TelemetryClient telemetryClient;
    private final MaintainUserCheck maintainUserCheck;

    @Transactional(transactionManager = "authTransactionManager")
    public void addRoles(final String username, final List<String> roleCodes, final String loggedInUser, final Collection<? extends GrantedAuthority> authorities) throws AuthUserRoleException, MaintainUserCheck.AuthUserGroupRelationshipException {
        // already checked that user exists
        final var user = userRepository.findByUsernameAndMasterIsTrue(username).orElseThrow();
        maintainUserCheck.ensureUserLoggedInUserRelationship(loggedInUser, authorities, user);

        final var formattedRoles = roleCodes.stream().map(this::formatRole).collect(Collectors.toList());
        final var allAssignableRoles = getAllAssignableRoles(username, authorities);

        for (final var roleCode : formattedRoles) {
            // check that role exists
            final var role = roleRepository.findByRoleCode(roleCode).orElseThrow(() -> new AuthUserRoleException("role", "role.notfound"));

            if (user.getAuthorities().contains(role)) {
                throw new AuthUserRoleExistsException();
            }

            if (!allAssignableRoles.contains(role)) {
                throw new AuthUserRoleException("role", "invalid");
            }
            user.getAuthorities().add(role);
        }
        // now that roles have all been added, then audit the role additions
        formattedRoles.forEach((roleCode) -> {
            telemetryClient.trackEvent("AuthUserRoleAddSuccess", Map.of("username", username, "role", roleCode, "admin", loggedInUser), null);
            log.info("Adding role {} to user {}", roleCode, username);
        });
    }

    @Transactional(transactionManager = "authTransactionManager")
    public void removeRole(final String username, final String roleCode, final String loggedInUser, final Collection<? extends GrantedAuthority> authorities) throws AuthUserRoleException, MaintainUserCheck.AuthUserGroupRelationshipException {
        // already checked that user exists
        final var user = userRepository.findByUsernameAndMasterIsTrue(username).orElseThrow();

        // check that the logged in user has permission to modify user
        maintainUserCheck.ensureUserLoggedInUserRelationship(loggedInUser, authorities, user);

        final var roleFormatted = formatRole(roleCode);
        final var role = roleRepository.findByRoleCode(roleFormatted).orElseThrow(() -> new AuthUserRoleException("role", "role.notfound"));

        if (!user.getAuthorities().contains(role)) {
            throw new AuthUserRoleException("role", "role.missing");
        }

        if (!getAllAssignableRoles(username, authorities).contains(role)) {
            throw new AuthUserRoleException("role", "invalid");
        }

        log.info("Removing role {} from user {}", roleFormatted, username);
        user.getAuthorities().removeIf(role::equals);
        telemetryClient.trackEvent("AuthUserRoleRemoveSuccess", Map.of("username", username, "role", roleFormatted, "admin", loggedInUser), null);
    }

    private String formatRole(final String role) {
        return Authority.removeRolePrefixIfNecessary(StringUtils.upperCase(StringUtils.trim(role)));
    }

    public List<Authority> getAllRoles() {
        return roleRepository.findAllByOrderByRoleName();
    }

    private Set<Authority> getAllAssignableRoles(final String username, final Collection<? extends GrantedAuthority> authorities) {
        if (MaintainUserCheck.canMaintainAuthUsers(authorities)) {
            // only allow oauth admins to see that role
            return getAllRoles().stream().filter(r -> !"OAUTH_ADMIN".equals(r.getRoleCode()) || canAddAuthClients(authorities)).collect(Collectors.toSet());
        }
        // otherwise they can assign all roles that can be assigned to any of their groups
        return roleRepository.findByGroupAssignableRolesForUsername(username);
    }

    public List<Authority> getAssignableRoles(final String username, final Collection<? extends GrantedAuthority> authorities) {
        final var user = userRepository.findByUsernameAndMasterIsTrue(username.toUpperCase()).orElseThrow();
        final var userRoles = user.getAuthorities();

        final var allAssignableRoles = getAllAssignableRoles(username, authorities);

        return Sets.difference(allAssignableRoles, userRoles)
                .stream()
                .sorted(Comparator.comparing(Authority::getRoleName))
                .collect(Collectors.toList());
    }

    public static class AuthUserRoleExistsException extends AuthUserRoleException {
        public AuthUserRoleExistsException() {
            super("role", "role.exists");
        }
    }

    private static boolean canAddAuthClients(final Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_OAUTH_ADMIN"::equals);
    }

    public static class AuthUserRoleException extends Exception {
        private final String errorCode;
        private final String field;

        public AuthUserRoleException(final String field, final String errorCode) {
            super(String.format("Modify role failed for field %s with reason: %s", field, errorCode));

            this.field = field;
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return this.errorCode;
        }

        public String getField() {
            return this.field;
        }
    }
}
