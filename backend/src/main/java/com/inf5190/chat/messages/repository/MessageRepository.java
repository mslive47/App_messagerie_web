package com.inf5190.chat.messages.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.cloud.firestore.*;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.StorageClient;
import com.inf5190.chat.messages.model.NewMessageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import com.inf5190.chat.messages.model.Message;
import org.springframework.web.bind.annotation.RequestParam;


import com.google.cloud.Timestamp;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import org.springframework.stereotype.Repository;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;


/**
 * Classe qui gère la persistence des messages.
 * En mémoire pour le moment.
 */
@Repository
public class MessageRepository {
    //private final List<Message> messages = new ArrayList<Message>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    private static final Storage STORAGE = StorageOptions.getDefaultInstance().getService();
    private static final String BUCKET_NAME = "inf5190-chat-faee1.firebasestorage.app"; // Remplacez par le nom de votre bucket

    private static final String COLLECTION_NAME = "messages";

    private final Firestore firestore;
    private final StorageClient storageClient;

    public MessageRepository(Firestore firestore, StorageClient storageClient) {
        this.firestore = firestore;
        this.storageClient = storageClient;
    }

    /**
     * Cette methode de retourner la liste de message
     * @param fromId, l'id du message
     * @return la liste de messages
     * */
    public List<Message> getMessages(@RequestParam(required = false) String fromId)
            throws ExecutionException, InterruptedException {

        List<Message> messages = new ArrayList<>();

        CollectionReference messagesCollection = firestore.collection(COLLECTION_NAME);
        Query query = messagesCollection.orderBy("timestamp", Query.Direction.ASCENDING).limit(20);

        if (fromId != null) {
            DocumentSnapshot fromSnapshot = messagesCollection.document(fromId).get().get();
            if (fromSnapshot.exists()) {
                query = query.startAfter(fromSnapshot);
            }
        }

        ApiFuture<QuerySnapshot> future = query.get();
        for (DocumentSnapshot document : future.get().getDocuments()) {
            FirestoreMessage firestoreMessage = document.toObject(FirestoreMessage.class);
            if (firestoreMessage != null) {
                messages.add(new Message(
                        document.getId(),
                        firestoreMessage.getUsername(),
                        firestoreMessage.getTimestamp().toDate().getTime(),
                        firestoreMessage.getText(),
                        firestoreMessage.getImageUrl()
                ));
            }
        }

        return messages;

    }

    /**
     * Cette methode permet de créer un message
     * @param message le message à creer
     * @return receiveMessage le message créé
     * */
    public Message createMessage(String username, NewMessageRequest message) throws ExecutionException, InterruptedException, IOException {
        // À faire...
        Message userMessage = null;
        String imageUrl = null;
        if(message.getUsername().equals(username)) {

            Timestamp timestamp = Timestamp.now();
            long timestampLong = timestamp.toDate().getTime();

            FirestoreMessage firestoreMessage = new FirestoreMessage(message.getUsername(), timestamp, message.getText(), null);
            DocumentReference documentReference = firestore.collection(COLLECTION_NAME).add(firestoreMessage).get();

            String generatedId = documentReference.getId();

            if(message.getImageData() != null) {
                imageUrl = this.uploadImage(generatedId, message.getImageData().getData(), message.getImageData().getType());
                System.out.println("image processs");
            }
            documentReference.update("imageUrl", imageUrl);
            userMessage = new Message(generatedId, message.getUsername(), timestampLong, message.getText(), imageUrl);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user dont have the permission");
        }

        return userMessage;
    }

    /**
     * Cette methode permet d'obtenir l'url de l'image
     * @param messageId, l'id du message
     * @param base64Image, la data de l'image
     * @param imageType, le type de l'image
     * @return l'url de limage
     * */
    public String uploadImage(String messageId, String base64Image, String imageType) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        String path = String.format("images/%s.%s", messageId, imageType);
        Bucket bucket = StorageClient.getInstance().bucket();
        bucket.create(path, imageBytes, Bucket.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ));
        return String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, path);
    }


}
