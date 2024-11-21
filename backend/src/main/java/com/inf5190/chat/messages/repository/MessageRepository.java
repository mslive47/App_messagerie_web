package com.inf5190.chat.messages.repository;

import com.inf5190.chat.messages.model.NewMessageRequest;
import com.inf5190.chat.messages.model.ChatImageData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


import com.google.cloud.firestore.*;
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

import io.jsonwebtoken.io.Decoders;
import java.io.ByteArrayInputStream;
import com.google.cloud.storage.Storage.PredefinedAcl;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.google.cloud.storage.Bucket.BlobTargetOption;




/**
 * Classe qui gère la persistence des messages.
 * En mémoire pour le moment.
 */
@Repository
public class MessageRepository {
    //private final List<Message> messages = new ArrayList<Message>();
    private final AtomicLong idGenerator = new AtomicLong(0);
    private static final String COLLECTION_NAME = "messages";
    private final Firestore firestore = FirestoreClient.getFirestore();
    private static final String BUCKET_NAME = "gs://inf5190-chat-d4bbd.firebasestorage.app";

    /**
     * Cette methode de retourner la liste de message
     * @param fromId, l'id du message
     * @return la liste de messages
     * */
    public List<Message> getMessages(@RequestParam(required = false) String fromId)
            throws ExecutionException, InterruptedException {
        // À faire...

        List<Message> messages = new ArrayList<>();

        CollectionReference messagesCollection = firestore.collection(COLLECTION_NAME);
        Query query = messagesCollection.orderBy("timestamp", Query.Direction.ASCENDING).limit(20);

        if (fromId != null) {
            // Récupérer le document de référence pour fromId
            DocumentSnapshot fromSnapshot = messagesCollection.document(fromId).get().get();

            // Appliquer startAfter seulement si le document existe
            if (fromSnapshot.exists()) {
                query = query.startAfter(fromSnapshot);
            }
        }

        // Exécuter la requête et ajouter les messages dans la liste
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
    public Message createMessage(String username, NewMessageRequest message) throws ExecutionException, InterruptedException {
        // À faire...
        Message userMessage = null;
        if(message.username().equals(username)) {
            // Créer un timestamp côté backend
            Timestamp timestamp = Timestamp.now();
            // Convertir le timestamp en millisecondes
            long timestampLong = timestamp.toDate().getTime();

             // Créer l'objet FirestoreMessage avec les données du message
            FirestoreMessage firestoreMessage = new FirestoreMessage(message.username(), timestamp, message.text(), null);

            // Ajouter le message à Firestore, qui génère un ID unique
            DocumentReference documentReference = firestore.collection(COLLECTION_NAME).add(firestoreMessage).get();

            // Récupérer l'ID Firestore et le timestamp pour retourner un Message au frontend
            String generatedId = documentReference.getId();

            // Initialiser l'URL de l'image à null
            String imageUrl = null;
            if (message.chatImageData() != null) {
                Bucket b = StorageClient.getInstance().bucket(BUCKET_NAME);
                String path = String.format("images/%s.%s", generatedId, message.chatImageData().type());
                b.create(path, Decoders.BASE64.decode(message.chatImageData().data()),
                BlobTargetOption.predefinedAcl(PredefinedAcl.PUBLIC_READ));
                imageUrl = String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, path);
            }
            // Mettre à jour l'URL de l'image dans firestoreMessage si l'image existe
            firestoreMessage.setImageUrl(imageUrl);

            // Enregistrez le message dans Firestore avec l'ID généré
            documentReference.set(firestoreMessage).get();
            
            // Construisez l'objet Message pour le retourner au frontend
            userMessage = new Message(generatedId, message.username(), timestampLong, message.text(), imageUrl);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user dont have the permission");
        }
        return userMessage;
    }
}
