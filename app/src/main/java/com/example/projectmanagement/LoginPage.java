package com.example.projectmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectmanagement.Model.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import  com.example.projectmanagement.Model.Users;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPage extends Fragment implements View.OnClickListener {
    private GoogleSignInClient mSignInClient;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference users;
    private String ACCOUNT_CHILD = "accounts";
    private Button signUp, signIn, signInWithGoogle, resetAccount;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignIn";
    private EditText emailText, passwordText;
    private TextView loginPageMainText;
    private Long time;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginPage.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginPage newInstance(String param1, String param2) {
        LoginPage fragment = new LoginPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        users = mFirestore.collection("Users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_login_page, container, false);
        signInWithGoogle = rootView.findViewById(R.id.signInWithGoogle);
        loginPageMainText = rootView.findViewById(R.id.loginPage_MainText);
        emailText = rootView.findViewById(R.id.emailText);
        passwordText = rootView.findViewById(R.id.passwordText);
        signUp = rootView.findViewById(R.id.signUp);
        signIn = rootView.findViewById(R.id.signIn);
        resetAccount = rootView.findViewById(R.id.resetAccount);
        if (mFirebaseUser == null){
            loginPageMainText.setText("You have not logged in now.");
            signInWithGoogle.setOnClickListener(this);
            signUp.setOnClickListener(this);
            signIn.setOnClickListener(this);
            resetAccount.setOnClickListener(this);
        } else {
            loginPageMainText.setText("You have already logged in.");
            signInWithGoogle.setEnabled(false);
            emailText.setEnabled(false);
            passwordText.setEnabled(false);
            signUp.setEnabled(false);
            signIn.setEnabled(false);
            resetAccount.setEnabled(false);
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        String emailInput = emailText.getText().toString();
        String passwordInput = passwordText.getText().toString();
        switch (v.getId()){
            case R.id.signInWithGoogle:
                signWithGoogle();
                break;
            case R.id.signUp:
                Navigation.findNavController(getActivity(), R.id.signUp).navigate(R.id.signUpPage);
            case R.id.signIn:
                signInWithEmailAndPassword(emailInput, passwordInput);
                break;
            case R.id.resetAccount:
                mAuth.sendPasswordResetEmail(emailInput).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getActivity(), R.string.reset_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent in signIn()
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    private void signWithGoogle() {
        Intent signInIntent = mSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signInWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Navigation.findNavController(getActivity(), R.id.signIn).navigate(R.id.itemsList);
                } else{
                    String exception = task.getException().toString();
                    Toast.makeText(getContext(), exception, Toast.LENGTH_SHORT).show();
                    resetAccount.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                if (task.isSuccessful()){
                    String userName = task.getResult().getUser().getDisplayName();
                    String email = task.getResult().getUser().getEmail();
                    String uid = task.getResult().getUser().getUid();
                    users.document(uid).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Log.d("UserExisted? ", task1.getResult().toObject(Users.class).getUid());
                        } else {
                            Log.d("UserExisted? ", "Not yet existed");
                            createUser(userName, email, uid);
                        }
                    });
                    Navigation.findNavController(getActivity(),R.id.signInWithGoogle).navigate(R.id.itemsList);
                }
            }
        });
    }
    private void createUser(String userName, String userEmail, String uid) {
        Long time = Calendar.getInstance().getTimeInMillis();
        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> tasks = new ArrayList<>();
        Users user = new Users(uid, userName, time, "active", userEmail, activities, tasks);
        users.document(uid).set(user);
    }
}