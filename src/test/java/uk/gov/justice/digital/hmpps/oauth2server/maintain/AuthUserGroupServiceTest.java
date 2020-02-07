package uk.gov.justice.digital.hmpps.oauth2server.maintain;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.Group;
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.User;
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.GroupRepository;
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.UserRepository;
import uk.gov.justice.digital.hmpps.oauth2server.maintain.AuthUserGroupService.AuthUserGroupException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AuthUserGroupServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private TelemetryClient telemetryClient;

    private AuthUserGroupService service;

    @BeforeEach
    void setUp() {
        service = new AuthUserGroupService(userRepository, groupRepository, telemetryClient);
    }

    @Test
    void addGroup_blank() {
        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.of(User.of("user")));

        assertThatThrownBy(() -> service.addGroup("user", "        ", "admin")).
                isInstanceOf(AuthUserGroupException.class).hasMessage("Add group failed for field group with reason: notfound");
    }

    @Test
    void addGroup_groupAlreadyOnUser() {
        final var user = User.of("user");
        final var group = new Group("GROUP_LICENCE_VARY", "desc");
        user.setGroups(new HashSet<>(List.of(group)));

        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.of(user));
        when(groupRepository.findByGroupCode(anyString())).thenReturn(Optional.of(group));

        assertThatThrownBy(() -> service.addGroup("user", "LICENCE_VARY", "admin")).
                isInstanceOf(AuthUserGroupException.class).hasMessage("Add group failed for field group with reason: exists");
    }

    @Test
    void addGroup_success() throws AuthUserGroupException {
        final var user = User.of("user");
        user.setGroups(new HashSet<>(List.of(new Group("GROUP_JOE", "desc"))));

        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.of(user));

        final var group = new Group("GROUP_LICENCE_VARY", "desc");
        when(groupRepository.findByGroupCode(anyString())).thenReturn(Optional.of(group));

        service.addGroup("user", "GROUP_LICENCE_VARY", "admin");

        assertThat(user.getGroups()).extracting(Group::getGroupCode).containsOnly("GROUP_JOE", "GROUP_LICENCE_VARY");
    }

    @Test
    void removeGroup_groupNotOnUser() {
        final var user = User.of("user");
        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.removeGroup("user", "BOB", "admin")).
                isInstanceOf(AuthUserGroupException.class).hasMessage("Add group failed for field group with reason: missing");
    }

    @Test
    void removeGroup_success() throws AuthUserGroupException {
        final var user = User.of("user");
        user.setGroups(new HashSet<>(List.of(new Group("JOE", "desc"), new Group("LICENCE_VARY", "desc2"))));
        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.of(user));

        service.removeGroup("user", "  licence_vary   ", "admin");

        assertThat(user.getGroups()).extracting(Group::getGroupCode).containsOnly("JOE");
    }

    @Test
    void getAuthUserGroups_notfound() {
        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.empty());
        final var groups = service.getAuthUserGroups(" BOB ");
        assertThat(groups).isNotPresent();
    }

    @Test
    void getAuthUserAssignableGroups_notAdminAndNoUser() {
        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.empty());
        final var groups = service.getAssignableGroups(" BOB ", Set.of());
        assertThat(groups).isEmpty();
    }

    @Test
    void getAuthUserGroups_success() {
        final var user = User.of("user");
        user.setGroups(new HashSet<>(List.of(new Group("JOE", "desc"), new Group("LICENCE_VARY", "desc2"))));
        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.of(user));
        final var groups = service.getAuthUserGroups(" BOB ");
        //noinspection OptionalGetWithoutIsPresent
        assertThat(groups.get()).extracting(Group::getGroupCode).containsOnly("JOE", "LICENCE_VARY");
    }

    @Test
    void getAuthUserAssignableGroups_normalUser() {
        final var user = User.of("user");
        user.setGroups(new HashSet<>(List.of(new Group("JOE", "desc"), new Group("LICENCE_VARY", "desc2"))));
        when(userRepository.findByUsernameAndMasterIsTrue(anyString())).thenReturn(Optional.of(user));
        final var groups = service.getAssignableGroups(" BOB ", Set.of());
        assertThat(groups).extracting(Group::getGroupCode).containsOnly("JOE", "LICENCE_VARY");
    }

    @Test
    void getAuthUserAssignableGroups_superUser() {
        when(groupRepository.findAllByOrderByGroupName()).thenReturn(List.of(new Group("JOE", "desc"), new Group("LICENCE_VARY", "desc2")));
        final var groups = service.getAssignableGroups(" BOB ", Set.of(new SimpleGrantedAuthority("ROLE_MAINTAIN_OAUTH_USERS")));
        assertThat(groups).extracting(Group::getGroupCode).containsOnly("JOE", "LICENCE_VARY");
    }
}
