package com.example.nlorderfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.nlorderfood.Models.Food;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminAddFoodItemView extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private DatabaseReference firebaseDatabase;
    StorageReference storageReference= FirebaseStorage.getInstance().getReference();
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    Uri imageUri;
    private String imageUrl;

    Toolbar toolbaradd;
    ImageView imageViewFood;
    EditText nameEDT, descriptionEDT,priceEDT;
    Spinner categorySpinner, statusSpinner;
    Button browseBTN,saveBTN;
    String[] categories={"Món Khai Vị","Nước Trái cây","Món Chính","Món Tráng Miệng","Salads","Thức Ăn Nhanh"};
    String[] statuses={"Có Sẵn","Không có sẵn "};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_food_item_view);

        toolbaradd = findViewById(R.id.admin_toolbar_addfood);
       // setSupportActionBar(toolbaradd);

        imageViewFood=findViewById(R.id.imageView_admin_add_food_item);
        nameEDT=findViewById(R.id.edit_text_admin_food_item_name);
        descriptionEDT=findViewById(R.id.edit_text_admin_food_item_description);
        priceEDT=findViewById(R.id.edit_text_admin_food_item_price);
        categorySpinner=findViewById(R.id.spinner_categories);
        statusSpinner=findViewById(R.id.spinner_status);

        progressDialog = new ProgressDialog(this);
        firestore = FirebaseFirestore.getInstance();

        browseBTN=findViewById(R.id.button_browseImage);
        saveBTN=findViewById(R.id.button_save_food_item);

        toolbaradd = findViewById(R.id.admin_toolbar_addfood);
        setSupportActionBar(toolbaradd);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        getSupportActionBar().setTitle("Trang chủ quản lý");

        ArrayAdapter catAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(catAdapter);


        ArrayAdapter statusAdapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,statuses);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        browseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && data != null && data.getData() != null){

            imageUri = data.getData();
            imageViewFood.setImageURI(imageUri);
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }


    //Saving product in the database
    private  void saveFoodItem(String imgUrl, String name, String description, String category, String status, String price){

        progressDialog.setMessage("Thêm món ăn");
        progressDialog.show();
        int p=Integer.parseInt(price);

        //upload Image to firebase storage
        //uploadImage();

        System.out.println("Save 2");
        //Creating the food object
        Food foodOBJ = new Food(imgUrl,name,description,category,status,p);

        System.out.println("The OBJECT URL: "+foodOBJ.getImageUrl());

        firestore.collection("US4U").document("MENU").collection(category).add(foodOBJ)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressDialog.dismiss();
                        System.out.println("Save 3");
                        if (task.isSuccessful()){
                            nameEDT.setText("");
                            descriptionEDT.setText("");
                            priceEDT.setText("");
                            Toast.makeText(AdminAddFoodItemView.this,  foodOBJ.getFoodname() + " Thêm thành công", Toast.LENGTH_LONG).show();
                        }

                        else
                            Toast.makeText(AdminAddFoodItemView.this, foodOBJ.getFoodname() + " Xảy ra lỗi", Toast.LENGTH_LONG).show();

                    }
                });
    }
    //validate userInputs
    private boolean validateUserInput(String name, String description,String category, String status, String price){

        if (name.isEmpty()) {
            nameEDT.setError("Bắt buộc");
            nameEDT.requestFocus();
            return false;
        }
        else if (description.isEmpty()) {
            descriptionEDT.setError("Bắt buộc");
            descriptionEDT.requestFocus();
            return false;
        }
        else if (price.isEmpty()) {
            priceEDT.setError("Bắt buộc");
            priceEDT.requestFocus();
            return false;
        }
        else if (category.isEmpty()) {
            return false;
        }
        else if(status.isEmpty()){
            return false;
        }
        else if(imageViewFood==null)
            return false;
        else
            return true;
    }
    private void uploadImageAndFoodDetails(String name, String description, String category, String status, String price) {
        //progressDialog.setMessage("Adding New Food Item");
        //progressDialog.show();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        String fileName = formatter.format(now);
        StorageReference fileRef=storageReference.child("images/"+fileName);


        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUrl=uri.toString();
                                System.out.println("The Url: "+imageUrl);
                                saveFoodItem(uri.toString(), name,description,category,status,price);
                            }
                        });

                        imageViewFood.setImageURI(null);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progressDialog.dismiss();
                Toast.makeText(AdminAddFoodItemView.this,"Đã xảy ra lỗi",Toast.LENGTH_SHORT).show();


            }
        });

    }
    private void save(){
        String name=nameEDT.getText().toString().trim();
        String description=descriptionEDT.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString().trim();
        String price=priceEDT.getText().toString().trim();
        String status=statusSpinner.getSelectedItem().toString().trim();
        String url="";
        boolean isValid=validateUserInput(name,description,category,status, price);
        if (isValid){
            uploadImageAndFoodDetails(name,description,category,status,price);
            System.out.println("Save 1");
        }
        else
            return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.admin_logout) {
            auth.signOut();
            startActivity(new Intent(AdminAddFoodItemView.this, admin_background.class));
            finish();
        }
        return true;

    }
}