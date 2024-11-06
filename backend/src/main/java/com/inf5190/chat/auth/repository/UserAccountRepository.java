package com.inf5190.chat.auth.repository;

import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import org.springframework.stereotype.Repository;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

@Repository
public class UserAccountRepository {
    private static final String COLLECTION_NAME = "userAccounts";
    private final Firestore firestore = FirestoreClient.getFirestore();
    public FirestoreUserAccount getUserAccount(String username) throws
            InterruptedException, ExecutionException {
        //throw new UnsupportedOperationException("A faire");
        // Obtenir une référence au document dans la collection "userAccounts" avec le nom d'utilisateur comme ID
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(username);

        // Lire le document Firestore
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        // Vérifier si le document existe
        if (!document.exists()) {
            return null; // Retourner null si le compte utilisateur n'existe pas
        }

        // Convertir le document en un objet FirestoreUserAccount et le retourner
        return document.toObject(FirestoreUserAccount.class);
    }
    public void createUserAccount(FirestoreUserAccount userAccount) throws
            InterruptedException, ExecutionException {
        // Obtenir une référence à la collection "userAccounts"
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userAccount.getUsername());

        // Utiliser la méthode set() pour enregistrer le compte utilisateur dans Firestore
        ApiFuture<WriteResult> future = docRef.set(userAccount);

        // Attendre l'achèvement de l'opération d'écriture (optionnel)
        WriteResult result = future.get();
        System.out.println("User account created at: " + result.getUpdateTime());
        //throw new UnsupportedOperationException("A faire");
    }
}
