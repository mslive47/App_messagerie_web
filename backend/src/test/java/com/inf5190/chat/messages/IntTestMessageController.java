package com.inf5190.chat.messages;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.inf5190.chat.auth.model.LoginRequest;
import com.inf5190.chat.auth.model.LoginResponse;
import com.inf5190.chat.messages.model.Message;
import com.inf5190.chat.messages.model.NewMessageRequest;
import com.inf5190.chat.messages.repository.FirestoreMessage;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:firebase.properties")
public class IntTestMessageController {
    private final FirestoreMessage message1 =
            new FirestoreMessage("u1", Timestamp.now(), "t1", null);
    private final FirestoreMessage message2 =
            new FirestoreMessage("u2", Timestamp.now(), "t2", null);

    @Value("${firebase.project.id}")
    private String firebaseProjectId;

    @Value("${firebase.emulator.port}")
    private String emulatorPort;

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private Firestore firestore;

    private String messagesEndpointUrl;
    private String loginEndpointUrl;

    @BeforeAll
    public static void checkRunAgainstEmulator() {
        checkEmulators();
    }

    @BeforeEach
    public void setup() throws InterruptedException, ExecutionException {
        this.restTemplate = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
        this.messagesEndpointUrl = "http://localhost:" + port + "/auth/chat";
        this.loginEndpointUrl = "http://localhost:" + port + "/auth/login";

        this.firestore.collection("messages").document("1").create(this.message1).get();
        this.firestore.collection("messages").document("2").create(this.message2).get();

    }

    @AfterEach
    public void testDown() {
        // Pour effacer le contenu de l'émulateur entre chaque test.
        this.restTemplate.delete("http://localhost:" + this.emulatorPort + "/emulator/v1/projects/"
                + this.firebaseProjectId + "/databases/(default)/documents");
    }

    @Test
    public void getMessageNotLoggedIn() {
        ResponseEntity<String> response =
                this.restTemplate.getForEntity(this.messagesEndpointUrl, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void getMessages() {
        final String jwtToken = this.login();

        final HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.AUTHORIZATION, "Bearer" + jwtToken);
        final HttpEntity<Object> headers = new HttpEntity<>(header);

        final ResponseEntity<Message[]> response = this.restTemplate.exchange(
                this.messagesEndpointUrl,
                HttpMethod.GET,
                headers,
                Message[].class
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);

        // Log des messages pour inspection
        for (Message message : response.getBody()) {
            System.out.println(message);
        }
    }

    /**
     * Test GET message avec parametre fromId
     */
    @Test
    public void getMessagesWithFromId() {
        final String jwtToken = login();

        HttpHeaders headers = createHeadersWithJwtToken(jwtToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String urlWithFromId = messagesEndpointUrl + "?fromId=1";
        ResponseEntity<Message[]> response = restTemplate.exchange(urlWithFromId, HttpMethod.GET, requestEntity, Message[].class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0); // Vérifiez qu'il y a des messages.
    }

    /**
     * Test GET message sans parametre fromId
     */
    @Test
    public void getMessagesWithoutFromId() {
        final String jwtToken = login();

        HttpHeaders headers = createHeadersWithJwtToken(jwtToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Message[]> response = restTemplate.exchange(messagesEndpointUrl, HttpMethod.GET, requestEntity, Message[].class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2); // Vérifiez qu'il y a au moins 2 messages.
    }

    /**
     * Test GET message avec plus de 20 messages
     */
    @Test
    public void getMessagesMoreThan20() throws ExecutionException, InterruptedException {

        for (int i = 3; i <= 22; i++) {
            FirestoreMessage message = new FirestoreMessage(
                    "user" + i,
                    Timestamp.now(),
                    "text" + i,
                    null
            );
            this.firestore.collection("messages").document(String.valueOf(i)).create(message).get();
        }

        final String jwtToken = this.login();
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer" + jwtToken);
        final HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<LinkedHashMap<String, Object>>> response = this.restTemplate.exchange(
                this.messagesEndpointUrl,
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<LinkedHashMap<String, Object>>>() {}
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(20); // Vérifiez que 20 messages sont renvoyés

        List<Message> messages = response.getBody().stream()
                .map(this::convertToMessage)
                .toList();

        assertThat(messages).isNotEmpty();
        messages.forEach(message -> {
            assertThat(message.id()).isNotNull();
            assertThat(message.username()).isNotNull();
            assertThat(message.timestamp()).isNotNull();
            assertThat(message.text()).isNotNull();
        });
        messages.forEach(System.out::println);
    }

    /**
     * Test GET message avec un fromId invalide
     */
    @Test
    public void getMessagesWithInvalidFromId() {
        final String jwtToken = login();

        HttpHeaders headers = createHeadersWithJwtToken(jwtToken);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String urlWithInvalidFromId = messagesEndpointUrl + "?fromId=999";
        ResponseEntity<String> response = restTemplate.exchange(urlWithInvalidFromId, HttpMethod.GET, requestEntity, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404); // Vérifiez qu'un ID invalide retourne 404.
    }

    /**
     * Test POST message avec un token invalide
     */
    @Test
    public void postMessageWithInvalidToken() {
        NewMessageRequest messageRequest = new NewMessageRequest();
        messageRequest.setUsername("user1");
        messageRequest.setText("Message avec un token invalide");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer invalidToken");
        HttpEntity<NewMessageRequest> requestEntity = new HttpEntity<>(messageRequest, headers);

        ResponseEntity<String> response = this.restTemplate.postForEntity(
                this.messagesEndpointUrl,
                requestEntity,
                String.class
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("\"status\":403");
        assertThat(response.getBody()).contains("\"error\":\"Forbidden\"");
    }

    /**
     * Test GET message avec un token invalide
     */
    @Test
    public void getMessagesWithoutValidToken() {
        // Préparez une requête GET sans en-tête Authorization
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        // Effectuez la requête GET
        ResponseEntity<String> response = this.restTemplate.exchange(
                this.messagesEndpointUrl,
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // Vérifiez que le statut HTTP est 403 (interdit)
        assertThat(response.getStatusCodeValue()).isEqualTo(403);

        // Vérifiez que le corps de la réponse contient des informations pertinentes
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("\"status\":403");
        assertThat(response.getBody()).contains("\"error\":\"Forbidden\"");
    }

    /**
     * Se connecte et retourne le cookie de session.
     *
     * @return le cookie de session.
     */
    private String login() {
        ResponseEntity<LoginResponse> response =
                this.restTemplate.postForEntity(this.loginEndpointUrl,
                        new LoginRequest("username", "password"), LoginResponse.class);

        String authHeader = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            throw new IllegalStateException("JWT token not returned by login endpoint.");
        }

        return authHeader.substring(6); // Remove "Bearer " prefix
    }

    private HttpHeaders createHeadersWithJwtToken(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer" + jwtToken);
        return headers;
    }

    private Message convertToMessage(LinkedHashMap<String, Object> rawMessage) {
        return new Message(
                (String) rawMessage.get("id"),
                (String) rawMessage.get("username"),
                ((Number) rawMessage.get("timestamp")).longValue(),
                (String) rawMessage.get("text"),
                (String) rawMessage.get("imageUrl")
        );
    }

    private static void checkEmulators() {
        final String firebaseEmulator = System.getenv().get("FIRESTORE_EMULATOR_HOST");
        if (firebaseEmulator == null || firebaseEmulator.length() == 0) {
            System.err.println(
                    "**********************************************************************************************************");
            System.err.println(
                    "******** You need to set FIRESTORE_EMULATOR_HOST=localhost:8181 in your system properties. ********");
            System.err.println(
                    "**********************************************************************************************************");
        }
        assertThat(firebaseEmulator).as(
                        "You need to set FIRESTORE_EMULATOR_HOST=localhost:8181 in your system properties.")
                .isNotEmpty();
    }
}