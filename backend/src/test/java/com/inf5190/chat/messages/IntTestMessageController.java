package com.inf5190.chat.messages;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
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

    private final FirestoreMessage message1 = new FirestoreMessage("u1", Timestamp.now(), "t1", null);
    private final FirestoreMessage message2 = new FirestoreMessage("u2", Timestamp.now(), "t2", null);

    @Value("${firebase.project.id}")
    private String firebaseProjectId;

    @Value("${firebase.emulator.port}")
    private String emulatorPort;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Firestore firestore;

    private String messagesEndpointUrl;
    private String loginEndpointUrl;

    @InjectMocks
    private MessageController messageController; // Le contrôleur à testerS

    @BeforeAll
    public static void checkRunAgainstEmulator() {

        checkEmulators();
    }

    @BeforeEach
    public void setup() throws InterruptedException, ExecutionException {
        MockitoAnnotations.openMocks(this);

        // this.restTemplate = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);
        // Utilisation du TestRestTemplate sans cookie, uniquement avec JWT
        this.restTemplate = new TestRestTemplate();
        this.messagesEndpointUrl = "http://localhost:" + port + "/auth/chat";
        this.loginEndpointUrl = "http://localhost:" + port + "/auth/login";

        // Vérifier que les URLs ne sont pas nulles
        assertThat(this.firestore).isNotNull();
        assertThat(this.messagesEndpointUrl).isNotNull();
        assertThat(this.loginEndpointUrl).isNotNull();

        // Pour ajouter deux message dans firestore au début de chaque test.
        this.firestore.collection("messages").document("1").create(this.message1).get();
        this.firestore.collection("messages").document("2").create(this.message2).get();
    }

    @AfterEach
    public void testDown() {
        // Pour effacer le contenu de l'émulateur entre chaque test.
        this.restTemplate.delete("http://localhost:" + this.emulatorPort + "/emulator/v1/projects/"
                + this.firebaseProjectId + "/databases/(default)/documents");
    }

    @SuppressWarnings("deprecation")
    @Test
    public void getMessageNotLoggedIn() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(this.messagesEndpointUrl, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    @Test
    public void getMessages() throws InterruptedException, ExecutionException {
        final String jwtToken = this.login(); // Obtenir le JWT

        // 2. Préparer les en-têtes HTTP avec le JWT
        final HttpHeaders headers = this.createHeadersWithJwt(jwtToken);
        headers.set("Authorization", "Bearer" + jwtToken);
        final HttpEntity<Object> request = new HttpEntity<>(headers);

        // Appeler l'API via le TestRestTemplate pour récupérer les messages
        final ResponseEntity<Message[]> response = this.restTemplate.exchange(
                this.messagesEndpointUrl, HttpMethod.GET, request, Message[].class);

        // Vérifications
        assertThat(response).isNotNull(); // La réponse ne doit pas être nulle
        assertThat(response.getBody()).isNotNull(); // Le corps de la réponse ne doit pas être nul
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2); // Vérifier qu'il y a au moins 2 messages
    }

    // Test pour GET /messages sans jeton valide
    @SuppressWarnings("deprecation")
    @Test
    public void getMessagesWithoutValidToken() {
        ResponseEntity<String> response = this.restTemplate.getForEntity(this.messagesEndpointUrl, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    // Test pour POST /messages sans jeton valide
    @SuppressWarnings("deprecation")
    @Test
    public void postMessageWithoutValidToken() {
        NewMessageRequest messageRequest = new NewMessageRequest();
        ResponseEntity<String> response = this.restTemplate.postForEntity(this.messagesEndpointUrl, messageRequest,
                String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(403);
    }

    // Test pour GET /messages avec paramètre fromId
    @SuppressWarnings("deprecation")
    @Test

    public void getMessagesWithFromId() {
        final String jwtToken = this.login();

        // 1. Préparer les en-têtes HTTP avec le JWT
        HttpHeaders headers = this.createHeadersWithJwt(jwtToken);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        // 2. Préparer l'URL avec le paramètre 'fromId'
        String urlWithFromId = this.messagesEndpointUrl + "?fromId=1"; // Assurez-vous que '10' est un ID valide

        // 3. Appeler l'API avec le paramètre 'fromId'
        ResponseEntity<Message[]> response = this.restTemplate.exchange(urlWithFromId, HttpMethod.GET, request,
                Message[].class);

        // 4. Vérifications
        assertThat(response.getStatusCodeValue()).isEqualTo(200); // Vérifier le code de statut
        assertThat(response.getBody()).isNotNull(); // Vérifier que le corps de la réponse n'est pas nul
        assertThat(response.getBody().length).isGreaterThan(0); // Vérifier qu'il y a au moins un message
    }

    // Test pour GET /messages sans paramètre fromId

    @SuppressWarnings("deprecation")

    @Test
    public void getMessagesWithoutFromId() {
        final String jwtToken = this.login(); // Obtenir le JWT

        // 1. Préparer les en-têtes HTTP avec le JWT
        HttpHeaders headers = this.createHeadersWithJwt(jwtToken);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        // 2. Appeler l'API sans le paramètre 'fromId'
        ResponseEntity<Message[]> response = this.restTemplate.exchange(
                this.messagesEndpointUrl, HttpMethod.GET, request, Message[].class);

        // 3. Vérifications
        assertThat(response.getStatusCodeValue()).isEqualTo(200); // Vérifier le code de statut
        assertThat(response.getBody()).isNotNull(); // Vérifier que le corps de la réponse n'est pas nul
        assertThat(response.getBody().length).isGreaterThan(0); // Vérifier qu'il y a au moins un message

    }

    // Test pour le cas où il y a plus de 20 messages

    @SuppressWarnings("deprecation")
    @Test
    public void getMessagesMoreThan20() throws InterruptedException, ExecutionException {
        // 1. Simuler la connexion pour obtenir le JWT
        final String jwtToken = this.login(); // Utiliser la méthode login pour obtenir un jeton JWT

        // 2. Préparer les en-têtes HTTP avec le JWT
        HttpHeaders headers = this.createHeadersWithJwt(jwtToken);
        HttpEntity<Object> request = new HttpEntity<>(headers); // Création de l'entité HTTP avec les en-têtes

        // Ajouter plus de 20 messages pour les tests
        // 3. Ajouter plus de 20 messages pour les tests
        for (int i = 3; i <= 25; i++) { // Ajout de messages supplémentaires
            FirestoreMessage message = new FirestoreMessage(
                    "user" + i,
                    Timestamp.now(),
                    "Message " + i,
                    null);
            this.firestore.collection("messages").document(String.valueOf(i)).create(message).get();
        }
        // 4. Appeler l'API via le TestRestTemplate pour récupérer les messages
        ResponseEntity<Message[]> response = this.restTemplate.exchange(
                this.messagesEndpointUrl, HttpMethod.GET, request, Message[].class);

        // 4. Vérifier que la réponse est correcte

        // Vérifier que la réponse contient au maximum 20 messages
        Message[] messages = response.getBody();
        assertThat(messages.length).isGreaterThanOrEqualTo(20);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void getMessagesWithInvalidFromId() throws Exception {
        // 1. Préparer le JWT
        final String jwtToken = this.login();

        // 2. Créer les en-têtes avec le JWT
        HttpHeaders headers = this.createHeadersWithJwt(jwtToken);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        // 3. Utiliser un `fromId` invalide (par exemple, un ID qui n'existe pas dans la
        // base de données)
        String urlWithInvalidFromId = this.messagesEndpointUrl + "?fromId=999"; // ID 999 est invalide

        // 4. Effectuer la requête GET avec le `fromId` invalide
        ResponseEntity<Message[]> response = this.restTemplate.exchange(
                urlWithInvalidFromId, HttpMethod.GET, request, Message[].class);

        // 5. Vérifier que le code HTTP est 404
        assertThat(response.getStatusCode()).isEqualTo(404); // 404 attendu

        // 6. Vérifier que le corps de la réponse est nul ou vide
        assertThat(response.getBody()).isNull();
    }

    // Test pour fromId invalide

    /**
     * Se connecte et retourne le cookie de session.
     * 
     * @return le JWT de session.
     */
    private String login() {
        // Effectue la requête pour se connecter
        ResponseEntity<LoginResponse> response = this.restTemplate.postForEntity(this.loginEndpointUrl,
                new LoginRequest("username", "password"), LoginResponse.class);

        // Récupère le JWT depuis l'en-tête Authorization
        String jwtToken = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (jwtToken == null || !jwtToken.startsWith("Bearer")) {
            throw new IllegalStateException("JWT non trouvé dans l'en-tête Authorization.");
        }
        // Retourne uniquement le token sans le préfixe "Bearer"
        return jwtToken.substring("Bearer".length()).trim();
    }

    private HttpEntity<NewMessageRequest> createRequestEntityWithJwt(
            NewMessageRequest messageRequest, String jwtToken) {
        HttpHeaders header = this.createHeadersWithJwt(jwtToken);
        return new HttpEntity<>(messageRequest, header);
    }

    private HttpHeaders createHeadersWithJwt(String jwtToken) {
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.AUTHORIZATION, "Bearer" + jwtToken);
        return header;
    }

    @AfterEach
    public void cleanup() throws InterruptedException, ExecutionException {
        // Suppression des messages après chaque test pour nettoyer la base de données
        this.firestore.collection("messages").get().get().forEach(doc -> {
            try {
                doc.getReference().delete().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace(); // Log si un message ne peut pas être supprimé
            }
        });
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