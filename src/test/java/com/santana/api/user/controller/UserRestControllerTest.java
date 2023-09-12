package com.santana.api.user.controller;

import com.santana.api.user.domain.User;
import com.santana.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(UserRestController.class)
public class UserRestControllerTest {

    @Autowired
    private ApplicationContext context;

    @MockBean
    private UserService userService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/").build();
    }

    @Test
    public void ensureThat_getUserByIdFromInitialDataModel_returnsUser() throws Exception {
        WebTestClient.ResponseSpec rs = webTestClient.get().uri("/user/1").exchange();

        rs.expectStatus().isOk()
                .expectBody(User.class)
                .consumeWith(result -> {
                    User user = result.getResponseBody();
                    assertThat(user).isNotNull();
                    assertThat(user.getName()).isEqualTo("Fabian Pfaff");
                });
    }

    @Test
    public void getUserById_invalidId_error() throws Exception {
        String id = "1";
        String name = "Fabian Pfaff";
        when(userService.getUserById(id)).thenReturn(Mono.just(User.builder().name(name).build()));


        ResponseSpec rs = webTestClient.get().uri("/user/" + id).exchange();

        rs.expectStatus().isOk().expectBody(User.class).consumeWith(result -> {
            User user = result.getResponseBody();
            assertThat(user).isNotNull();
            assertThat(user.getName()).isEqualTo(name);
        });
    }

    @Test
    public void ensureThat_createUser_withValidUserInput_createsUser() throws Exception {
        var user = User.builder().id("asdf").build();
        user.setName("Jonas Hungershausen");
        user.setEmail("jonas.hungershausen@vogella.com");
        WebTestClient.ResponseSpec rs = webTestClient.post().uri("/user")
                .body(BodyInserters.fromValue(user))
                .exchange();

        rs.expectStatus().isCreated().expectHeader()
                .valueMatches("LOCATION", "^/user/\\d+");
    }

}
