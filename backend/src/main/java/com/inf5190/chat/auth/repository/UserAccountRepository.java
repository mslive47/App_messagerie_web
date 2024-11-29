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
    private final Firestore firestore;

    public UserAccountRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Cette methode permet d'obtenir un utilisateur de la db
     * @param username le nom de l'utilisateur
     * @return l'utilisateur
     * */
    public FirestoreUserAccount getUserAccount(String username) throws
            InterruptedException, ExecutionException {

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(username);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (!document.exists()) {
            return null;
        }

        return document.toObject(FirestoreUserAccount.class);
    }
    /**
     * Cette methode permet d'enregistrer un utilisateur de la db
     * @param userAccount le compte de l'utilisateur
     * */
    public void createUserAccount(FirestoreUserAccount userAccount) throws
            InterruptedException, ExecutionException {
        FirestoreUserAccount userOnFirestore = this.getUserAccount(userAccount.getUsername());
        if (userOnFirestore == null) {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userAccount.getUsername());
            ApiFuture<WriteResult> future = docRef.set(userAccount);
            WriteResult result = future.get();
            System.out.println("User account created at: " + result.getUpdateTime());
        } else  {
            System.out.println("user already in db");
        }

    }
}
