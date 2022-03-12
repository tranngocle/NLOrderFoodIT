package com.example.nlorderfood;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nlorderfood.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {

    EditText fnameEDT, lnameEDT, emailEDT,phoneEDT,passwordEDT,confirmPasswordEDT;
    Button regBTN;
    TextView signInTXT;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private FirebaseFirestore firestore;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        mAuth = FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("USERS");

        signInTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        regBTN=findViewById(R.id.reg_button);
        regBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fname = fnameEDT.getText().toString().trim();
                String lname = lnameEDT.getText().toString().trim();
                String phone = phoneEDT.getText().toString().trim();
                String email = emailEDT.getText().toString().trim();
                String password = passwordEDT.getText().toString().trim();
                String confirmpassword = confirmPasswordEDT.getText().toString().trim();

                boolean isValid = validateUserInputs(fname, lname, phone,email, password, confirmpassword);
                if (isValid) {
                    registerUser(fname, lname, phone,email, password);
                }
                else
                    return;
            }
        });
    }

    private void initViews() {

        fnameEDT=findViewById(R.id.editText_reg_fname);
        lnameEDT=findViewById(R.id.edit_text_reg_lname);
        emailEDT=findViewById(R.id.edit_text_reg_email);
        phoneEDT=findViewById(R.id.editText_reg_phone);
        passwordEDT=findViewById(R.id.editText_reg_password);
        confirmPasswordEDT=findViewById(R.id.edit_text_reg_confirm_password);
        signInTXT=findViewById(R.id.text_view_already_have_account);
    }

    //Validating user inputs
    private boolean validateUserInputs(String fname, String lname, String phone,String email, String password, String confirmpassword) {
        if (email.isEmpty()) {
            emailEDT.setError("Bắt buộc");
            emailEDT.requestFocus();
            return false;
        }
        if (fname.isEmpty()) {
            fnameEDT.setError("Bắt buộc");
            fnameEDT.requestFocus();
            return false;
        }
        if (lname.isEmpty()) {
            lnameEDT.setError("Bắt buộc");
            lnameEDT.requestFocus();
            return false;
        }
        if (phone.isEmpty()) {
            phoneEDT.setError("Số điện thoại là bắt buộc");
            phoneEDT.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passwordEDT.setError("Mật khẩu không được để trống");
            passwordEDT.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEDT.setError("Hãy cung cấp email");
            emailEDT.requestFocus();
            return false;
        }
        if (password.length() < 8) {
            passwordEDT.setError("Độ dài nhỏ nhất của mật khẩu là 8");
            passwordEDT.requestFocus();
            return false;
        }
        if (confirmpassword.length() < 8) {
            confirmPasswordEDT.setError("Độ dài nhỏ nhất của mật khẩu là 8");
            confirmPasswordEDT.requestFocus();
            return false;
        }
        if (!TextUtils.equals(password, confirmpassword)) {
            Toast.makeText(this, "Mật khẩu không đúng. Thử lại", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }
    //Registering user
    private void registerUser(String fname, String lname, String phone,String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                FirebaseUser rUser = mAuth.getCurrentUser();
                assert rUser != null;
                String userId = rUser.getUid();
                User userObj = new User(fname, lname, phone, email);

                //Calling the saveUserFunction that saves the information in the firestore
                saveUserInfo(userId,userObj);


            } else {
                String errMSG = task.getException().getMessage();
                Toast.makeText(this, errMSG, Toast.LENGTH_LONG).show();
            }
        });

    }

    //This method Saves the User information in the realtime database
    private void saveUserInfo(String userId, User user) {
        //Setting up the Progress dialog
        progressDialog.setMessage("Hãy chờ...");
        progressDialog.show();
        //end
        System.out.println("THE USER'S ID: " + userId);

        databaseReference.child(userId).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        clearUserInputs();
                        if (task.isSuccessful()) {
                            //end
                            Toast.makeText(Register.this, "Đã tạo thành công", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Register.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Không tạo thành công", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    //Clearing the Edit text inputs
    private void clearUserInputs(){

        emailEDT.setText("");
        fnameEDT.setText("");
        lnameEDT.setText("");
        phoneEDT.setText("");
        emailEDT.setText("");
        passwordEDT.setText("");
        confirmPasswordEDT.setText("");
    }

}