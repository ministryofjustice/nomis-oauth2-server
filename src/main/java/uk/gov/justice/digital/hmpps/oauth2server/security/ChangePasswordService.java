package uk.gov.justice.digital.hmpps.oauth2server.security;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.security.authentication.LockedException;
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.UserEmail;
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.UserToken;
import uk.gov.justice.digital.hmpps.oauth2server.auth.model.UserToken.TokenType;
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.UserEmailRepository;
import uk.gov.justice.digital.hmpps.oauth2server.auth.repository.UserTokenRepository;
import uk.gov.justice.digital.hmpps.oauth2server.verify.PasswordService;

import java.util.Map;

public abstract class ChangePasswordService implements PasswordService {
    private final UserTokenRepository userTokenRepository;
    private final UserEmailRepository userEmailRepository;
    private final UserService userService;
    private final TelemetryClient telemetryClient;

    protected ChangePasswordService(final UserTokenRepository userTokenRepository,
                                    final UserEmailRepository userEmailRepository,
                                    final UserService userService, final TelemetryClient telemetryClient) {
        this.userTokenRepository = userTokenRepository;
        this.userEmailRepository = userEmailRepository;
        this.userService = userService;
        this.telemetryClient = telemetryClient;
    }

    String createToken(final String username) {
        final var userEmailOptional = userEmailRepository.findById(username);
        final var userEmail = userEmailOptional.orElseGet(() -> {
            final var ue = new UserEmail(username);
            userEmailRepository.save(ue);
            return ue;
        });
        final var userTokenOptional = userTokenRepository.findByTokenTypeAndUserEmail(TokenType.CHANGE, userEmail);
        userTokenOptional.ifPresent(userTokenRepository::delete);

        final var userToken = new UserToken(TokenType.CHANGE, userEmail);
        userTokenRepository.save(userToken);
        telemetryClient.trackEvent("ChangePasswordRequest", Map.of("username", username), null);
        return userToken.getToken();
    }

    public void setPassword(final String token, final String password) {
        final var userToken = userTokenRepository.findById(token).orElseThrow();
        final var userEmail = userToken.getUserEmail();
        final var userOptional = userService.getUserByUsername(userEmail.getUsername());

        // before we set, ensure user allowed to still change their password
        if (userOptional.isEmpty() || userOptional.get().getAccountDetail().getStatus().isLocked()) {
            // failed, so let user know
            throw new LockedException("locked");
        }

        userEmail.setLocked(false);
        changePasswordWithUnlock(userEmail.getUsername(), password);
        userEmailRepository.save(userEmail);
        userTokenRepository.delete(userToken);
    }


    abstract void changePassword(String username, String password);

    public abstract void changePasswordWithUnlock(String username, String password);
}
