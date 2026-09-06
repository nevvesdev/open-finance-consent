package br.com.nevvesdev.openfinance.domain;

import br.com.nevvesdev.openfinance.domain.consent.Consent;
import br.com.nevvesdev.openfinance.domain.consent.ConsentPermission;
import br.com.nevvesdev.openfinance.domain.consent.ConsentStatus;
import br.com.nevvesdev.openfinance.domain.consent.exception.ConsentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class ConsentTest {

    private static final String CPF = "12345678901";
    private static final String CNPJ = "12345678000195";
    private static final Set<ConsentPermission> PERMISSIONS =
            Set.of(ConsentPermission.ACCOUNTS_READ, ConsentPermission.ACCOUNTS_BALANCES_READ);
    private static final OffsetDateTime FUTURE = OffsetDateTime.now().plusDays(30);

    @Nested
    @DisplayName("Criação")
    class Creation {

        @Test
        @DisplayName("deve criar com status AWAITING_AUTHORISATION")
        void shouldCreateWithAwaitingStatus() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);

            assertThat(consent.getStatus()).isEqualTo(ConsentStatus.AWAITING_AUTHORISATION);
            assertThat(consent.getId()).isNotNull();
            assertThat(consent.getLoggedUserCpf()).isEqualTo(CPF);
        }

        @Test
        @DisplayName("deve registrar evento de criação")
        void shouldRegisterCreationEvent() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            var events = consent.pullDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0).newStatus()).isEqualTo(ConsentStatus.AWAITING_AUTHORISATION);
        }

        @Test
        @DisplayName("deve falhar se permissions vazia")
        void shouldFailIfPermissionsEmpty() {
            assertThatThrownBy(() -> Consent.create(CPF, CNPJ, Set.of(), FUTURE))
                    .isInstanceOf(ConsentException.class)
                    .hasMessageContaining("Permissions cannot be empty");
        }

        @Test
        @DisplayName("deve falhar se expiração no passado")
        void shouldFailIfExpirationInPast() {
            var past = OffsetDateTime.now().minusDays(1);
            assertThatThrownBy(() -> Consent.create(CPF, CNPJ, PERMISSIONS, past))
                    .isInstanceOf(ConsentException.class)
                    .hasMessageContaining("Expiration date must be in the future");
        }
    }

    @Nested
    @DisplayName("Máquina de estados")
    class StateMachine {

        @Test
        @DisplayName("deve autorizar a partir de AWAITING_AUTHORISATION")
        void shouldAuthorise() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.pullDomainEvents(); // limpa eventos de criação

            consent.authorise();

            assertThat(consent.getStatus()).isEqualTo(ConsentStatus.AUTHORISED);
            assertThat(consent.pullDomainEvents()).hasSize(1);
        }

        @Test
        @DisplayName("não deve autorizar se já AUTHORISED")
        void shouldNotAuthoriseIfAlreadyAuthorised() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.authorise();

            assertThatThrownBy(consent::authorise)
                    .isInstanceOf(ConsentException.class);
        }

        @Test
        @DisplayName("deve rejeitar a partir de AWAITING_AUTHORISATION")
        void shouldReject() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.reject("User denied");

            assertThat(consent.getStatus()).isEqualTo(ConsentStatus.REJECTED);
        }

        @Test
        @DisplayName("deve revogar a partir de AUTHORISED")
        void shouldRevokeFromAuthorised() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.authorise();
            consent.revoke("User requested");

            assertThat(consent.getStatus()).isEqualTo(ConsentStatus.REVOKED);
        }

        @Test
        @DisplayName("deve revogar a partir de AWAITING_AUTHORISATION")
        void shouldRevokeFromAwaiting() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.revoke("Cancelled before auth");

            assertThat(consent.getStatus()).isEqualTo(ConsentStatus.REVOKED);
        }

        @Test
        @DisplayName("não deve revogar se já REVOKED")
        void shouldNotRevokeIfAlreadyRevoked() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.revoke("First revoke");

            assertThatThrownBy(() -> consent.revoke("Second revoke"))
                    .isInstanceOf(ConsentException.class);
        }

        @Test
        @DisplayName("deve expirar e registrar evento")
        void shouldExpire() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.authorise();
            consent.pullDomainEvents();

            consent.expire();

            assertThat(consent.getStatus()).isEqualTo(ConsentStatus.EXPIRED);
            assertThat(consent.pullDomainEvents()).hasSize(1);
        }

        @Test
        @DisplayName("deve estender expiração se AUTHORISED")
        void shouldExtendExpiration() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);
            consent.authorise();
            var newExpiration = FUTURE.plusDays(60);

            consent.extend(newExpiration);

            assertThat(consent.getExpirationDateTime()).isEqualTo(newExpiration);
        }

        @Test
        @DisplayName("não deve estender se não AUTHORISED")
        void shouldNotExtendIfNotAuthorised() {
            var consent = Consent.create(CPF, CNPJ, PERMISSIONS, FUTURE);

            assertThatThrownBy(() -> consent.extend(FUTURE.plusDays(60)))
                    .isInstanceOf(ConsentException.class);
        }
    }
}