package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.config.JwtService;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;

/**
 * Qui a le droit de faire quoi : c'est là qu'une erreur coûte le plus cher, et
 * c'est aussi ce qu'une modification anodine peut casser sans bruit. Les règles
 * de Spring Security s'appliquent dans l'ordre où elles sont écrites, donc
 * déplacer une ligne suffit à rouvrir les routes d'administration.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationRulesTest {

    private static final String ADMIN_USERS = "/api/auth/admin/allUsers";
    private static final String ADMIN_REGISTER = "/api/auth/admin/register";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository users;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void clean() {
        users.deleteAll();
    }

    private String tokenOf(String email, String role) {
        User user = User.builder()
                .fullName(email)
                .email(email)
                .role(role)
                .password(passwordEncoder.encode("Passw0rd!"))
                .build();
        return jwtService.generateToken(users.save(user));
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    @DisplayName("L'inscription publique crée toujours un Customer, même si la requête réclame Admin")
    void publicRegistrationAlwaysCreatesACustomer() throws Exception {
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Pirate\",\"email\":\"pirate@evil.com\",\"password\":\"Passw0rd!\",\"role\":\"Admin\"}"))
                .andExpect(status().isOk());

        assertThat(users.findByEmail("pirate@evil.com").orElseThrow().getRole()).isEqualTo("Customer");
    }

    @Test
    @DisplayName("Les routes d'administration sont fermées aux anonymes et aux clients")
    void adminRoutesAreClosedToAnonymousAndCustomers() throws Exception {
        mvc.perform(get(ADMIN_USERS))
                .andExpect(status().isForbidden());

        mvc.perform(get(ADMIN_USERS).header("Authorization", bearer(tokenOf("client@shop.local", "Customer"))))
                .andExpect(status().isForbidden());

        mvc.perform(get(ADMIN_USERS).header("Authorization", bearer(tokenOf("patron@shop.local", "Admin"))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("La liste des utilisateurs ne contient aucun mot de passe")
    void theUserListNeverExposesPasswords() throws Exception {
        tokenOf("client@shop.local", "Customer");
        mvc.perform(get(ADMIN_USERS).header("Authorization", bearer(tokenOf("patron@shop.local", "Admin"))))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("password"))))
                .andExpect(content().string(containsString("client@shop.local")));
    }

    @Test
    @DisplayName("Seul un Admin crée des comptes privilégiés, et les rôles sont limités à une liste connue")
    void onlyAdminsCreateAccountsAndRolesAreLimited() throws Exception {
        String customer = tokenOf("client2@shop.local", "Customer");
        String admin = tokenOf("patron2@shop.local", "Admin");
        String body = "{\"fullName\":\"Nouveau\",\"email\":\"nouveau@shop.local\",\"password\":\"Passw0rd!\",\"role\":\"%s\"}";

        mvc.perform(post(ADMIN_REGISTER).contentType(MediaType.APPLICATION_JSON).content(body.formatted("Admin")))
                .andExpect(status().isForbidden());

        mvc.perform(post(ADMIN_REGISTER).header("Authorization", bearer(customer))
                .contentType(MediaType.APPLICATION_JSON).content(body.formatted("Admin")))
                .andExpect(status().isForbidden());

        mvc.perform(post(ADMIN_REGISTER).header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON).content(body.formatted("SuperAdmin")))
                .andExpect(status().isBadRequest());

        mvc.perform(post(ADMIN_REGISTER).header("Authorization", bearer(admin))
                .contentType(MediaType.APPLICATION_JSON).content(body.formatted("Admin")))
                .andExpect(status().isCreated());

        assertThat(users.findByEmail("nouveau@shop.local").orElseThrow().getRole()).isEqualTo("Admin");
    }
}
