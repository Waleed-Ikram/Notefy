package com.example.notefy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class AuthActivity extends AppCompatActivity {

    public static String UID ;
    public static String NAME  ;
    public static String KEY_UID = "users";


    Button googleSignInButton ;
    Fragment LoginFragment, SignUpFragment,GSignUpFragment;
    View loginView, signupView , GsignupView;
    FragmentManager FragManager ;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    TextView Gtv ;



    Button LbtnLogin, SbtnLogin, LbtnSignup, SbtnSignup,GbtnSignup;
    TextInputEditText SetName,Setemail,Setphone, SetPassword, SetCPassword, LetPassword , Letemail ,GetPassword  ,Getphone;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkIfUserExists(mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getEmail());
                                mAuth = FirebaseAuth.getInstance();
//                                Glide.with(AuthActivity.this).load(Objects.requireNonNull(mAuth.getCurrentUser()).getPhotoUrl()).into(imageView);
                                FragManager.beginTransaction()
                                        .hide(LoginFragment)
                                        .hide(SignUpFragment)
                                        .show(GSignUpFragment)
                                        .commit();
                                Gtv.setText("Hey " + Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName() + " ,");
                                Toast.makeText(AuthActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AuthActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void checkIfUserExists(String name,String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query emailQuery = usersRef.orderByChild("email").equalTo(email);
        ((Query) emailQuery).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getKey();
                    NAME = name ;
                    UID = uid ;
                    startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                    finish();  //
                    Toast.makeText(AuthActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    // User does not exist, perform signup process
                    // Implement your signup logic here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AuthActivity.this, "Failed to check user existence: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        init();
//        Intent i = new Intent(AuthActivity.this, HomeActivity.class);
//        startActivity(i);
        FragManager.beginTransaction()
                .hide(GSignUpFragment)
                .hide(LoginFragment)
                .show(SignUpFragment)
                .commit();


        SbtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = SetName.getText().toString().trim();
                String email = Setemail.getText().toString().trim();
                String phone = Setphone.getText().toString().trim();
                String password= SetPassword.getText().toString().trim();
                String cpassword= SetCPassword.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Name is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Phone number is required", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password can't be Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() <= 5) {
                    Toast.makeText(getApplicationContext(), "Password must be greater than 5 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(cpassword)) {
                    Toast.makeText(getApplicationContext(), "Password is not Same", Toast.LENGTH_SHORT).show();
                    return;
                }



                // Proceed with user creation and database storage
                createUserAndStoreInDatabase(email, phone,password , name);

                // Transition to the LoginFragment
                FragManager.beginTransaction()
                        .show(LoginFragment)
                        .hide(SignUpFragment)
                        .hide(GSignUpFragment)
                        .commit();
            }
        });

        GbtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                String name , email , phone ,password;
                name = String.valueOf(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                email = String.valueOf(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                phone = String.valueOf(Getphone.getText());

                GoogleLogin(name,email,phone);




            }
        });

        LbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Letemail.getText().toString().trim();
                String password = LetPassword.getText().toString().trim();

                // Check if email field is empty
                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if password field is empty
                if (password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the password is greater than 5 characters
                if (password.length() <= 5) {
                    Toast.makeText(getApplicationContext(), "Password must be greater than 5 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed with the login process
                loginUser(email, password);
            }
        });

        LbtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragManager.beginTransaction()
                        .hide(GSignUpFragment)
                        .hide(LoginFragment)
                        .show(SignUpFragment)
                        .commit();
            }
        });

        SbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragManager.beginTransaction()
                        .show(LoginFragment)
                        .hide(SignUpFragment)
                        .hide(GSignUpFragment)
                        .commit();
            }
        });





        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.clientid))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(AuthActivity.this, options);

        mAuth = FirebaseAuth.getInstance();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

    }
    private static final int RC_SIGN_IN = 9001;  // Unique request code for sign-in intent


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            GoogleSignInAccount account = task.getResult();
                            firebaseAuthWithGoogle(account);  // Authenticate with Firebase
                        } else {
                            Log.e("GoogleSignIn", "Sign-In failed", task.getException());
                        }
                    });
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
//                            storeUserDataInDatabase(user);  // Store user data in Firebase
                        }
                    } else {
                        Log.e("FirebaseAuth", "Firebase Authentication failed", task.getException());
                    }
                });
    }

    private void init()
    {
        FragManager = getSupportFragmentManager();
        LoginFragment = FragManager.findFragmentById(R.id.fragLogin);
        SignUpFragment = FragManager.findFragmentById(R.id.fragSignup);
        GSignUpFragment = FragManager.findFragmentById(R.id.fragGSignup);
        loginView = Objects.requireNonNull(FragManager.findFragmentById(R.id.fragLogin)).requireView();
        signupView = Objects.requireNonNull(FragManager.findFragmentById(R.id.fragSignup)).requireView();
        GsignupView = Objects.requireNonNull(FragManager.findFragmentById(R.id.fragGSignup)).requireView();

        LbtnLogin = loginView.findViewById(R.id.btnLLogin);
        LbtnSignup = loginView.findViewById(R.id.btnLsignup);
        SbtnLogin = signupView.findViewById(R.id.btnSlogin);
        SbtnSignup = signupView.findViewById(R.id.btnSsignup);

        SetName = signupView.findViewById(R.id.etsname);
        Setemail = signupView.findViewById(R.id.etsemail);
        SetPassword = signupView.findViewById(R.id.etspassword);
        Setphone = signupView.findViewById(R.id.etsphone);
        SetCPassword = signupView.findViewById(R.id.etscpassword);
        Letemail = loginView.findViewById(R.id.LetUsername);
        LetPassword = loginView.findViewById(R.id.LetPassword);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        googleSignInButton = signupView.findViewById(R.id.btnssignupwithgoogle);

//        GetPassword = GsignupView.findViewById(R.id.etgpassword);
        Getphone = GsignupView.findViewById(R.id.etgphone);
//        GetCPassword = GsignupView.findViewById(R.id.etgcpassword);

        Gtv = GsignupView.findViewById(R.id.gtvname);
        GbtnSignup = GsignupView.findViewById(R.id.btnGsignup);


//        cb1 = signupView.findViewById(R.id.cbS1);  // Reference to your first CheckBox
//        cb2 = signupView.findViewById(R.id.cbS2);


    }
    public void createUserAndStoreInDatabase(String email,String phone, String password, String name) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Successfully created a new user
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            User newUser = new User(uid, name, email,phone);  // No password stored

                            // Store the user information in Firebase Realtime Database or Firestore

                            databaseRef.child("users").child(uid).setValue(newUser)
                                    .addOnCompleteListener(storeTask -> {
                                        if (storeTask.isSuccessful()) {
                                            Log.d("Firebase", "User data stored successfully");
                                        } else {
                                            Log.e("Firebase", "Failed to store user data", storeTask.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e("FirebaseAuth", "User creation failed", task.getException());
                    }
                });
    }

    private void GoogleLogin(String name , String email , String phone)
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            User newUser = new User(uid, name, email,phone);  // No password stored

            // Store the user information in Firebase Realtime Database or Firestore

            databaseRef.child("users").child(uid).setValue(newUser)
                    .addOnCompleteListener(storeTask -> {
                        if (storeTask.isSuccessful()) {

                            Log.d("Firebase", "User data stored successfully");
                            NAME = name ;
                            UID = uid ;
                            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                            finish();  //
                        } else {
                            Log.e("Firebase", "Failed to store user data", storeTask.getException());
                        }
                    });
        }
    }

    public void loginUser(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            // Validate input
//            Toast.makeText(this, "Email and password must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful, retrieve the authenticated FirebaseUser
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            UID = user.getUid();
                            // Do something with the authenticated user
//                            with the authenticated user
                            Toast.makeText(AuthActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(UID);
                            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        NAME = dataSnapshot.child("name").getValue(String.class);
                                        startActivity(new Intent(AuthActivity.this, HomeActivity.class));
                                        finish();  //
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle error
                                }
                            });

                            // You can now navigate to another activity or store user information


                        }
                    } else {
                        // Sign-in failed, handle the error
                        Exception e = task.getException();
                        Toast.makeText(AuthActivity.this, "Login failed: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                    }
                });
    }








}