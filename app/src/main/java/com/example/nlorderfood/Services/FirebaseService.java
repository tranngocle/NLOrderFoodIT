package com.example.nlorderfood.Services;

import com.example.nlorderfood.Models.Order;
import com.example.nlorderfood.Models.User;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseService {
    private FirebaseDatabase firebaseDatabase;
    Order order;
    User user;

    public FirebaseService() {
        firebaseDatabase=FirebaseDatabase.getInstance();

    }

    public void saveUser(User user){


    }




}
