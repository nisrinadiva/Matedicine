package com.example.matedicine.dao;

import com.example.matedicine.model.Medicine;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MedicineDao {

    private DatabaseReference databaseReference;
    private String firebaseAuth;

    public MedicineDao(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance().getUid();
        databaseReference = db.getReference(firebaseAuth);
    }

    // insert data
    public Task<Void> insert(Medicine medicine){
        return databaseReference.push().setValue(medicine);
    }

    // update data
    public Task<Void> update(String id,Medicine medicine){
        return databaseReference.child(id).setValue(medicine);
    }

    //delete data
    public Task<Void> delete(String id){
        return databaseReference.child(id).removeValue();
    }

    public Query get (){
        return databaseReference.orderByKey();
    }

    public Task<DataSnapshot> getall(String id){
        return databaseReference.child(id).get();
    }
}
