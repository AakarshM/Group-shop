package aakarsh.familyshop;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterLoginActivity extends AppCompatActivity {

    Button login, signup;
    EditText email, pass, displayField;
    public FirebaseAuth auth;
    ProgressBar bar;
    public static String emailID, passID, displayName;
    public static String uid;
    public FirebaseAuth Auth = FirebaseAuth.getInstance();
    public FirebaseAuth.AuthStateListener authlistener;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference leaders = root.child("Leaders");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        signup = (Button)findViewById(R.id.signup);
        login.setOnClickListener(loginListener);
        displayField = (EditText)findViewById(R.id.displayField);
        signup.setOnClickListener(signupListener);
         bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.INVISIBLE);

    }


    public View.OnClickListener loginListener = new View.OnClickListener() {
        public void onClick (View view){

            logIn(email.getText().toString(), pass.getText().toString());

        }};
    public View.OnClickListener signupListener = new View.OnClickListener() {
        public void onClick (View view){
            emailID = email.getText().toString();
            passID = pass.getText().toString();
            displayName = displayField.getText().toString();
           System.out.println(emailID + passID + displayName);
            if(emailID == "" || passID == "" || displayName == ""){
                Toast.makeText(getApplicationContext(), "Error, one of the fields is empty", Toast.LENGTH_SHORT).show();

            }
        else {
                createUser(emailID, passID, displayName);

                //Else creates a user
            }



        }};

    public void createUser(String email, String pass, final String displayName){

        bar.setVisibility(View.VISIBLE);

        Auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(RegisterLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            logInUserforDB(emailID, passID, displayName);
                        } else{
                            Toast.makeText(getApplicationContext(), "Failed, Email is probably taken or pass was < 6 char.", Toast.LENGTH_LONG).show();
                            bar.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }

    public void logInUserforDB(String email, String pass, final String displayName){
        Auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            uid = Auth.getCurrentUser().getUid().toString();
                            createDB(Auth.getCurrentUser().getUid().toString(), displayName);
                           // sgnOutNow();
                            bar.setVisibility(View.INVISIBLE);

                        }
                        else {
                            bar.setVisibility(View.INVISIBLE);

                        }
                    }
                });

    }



    public void createDB(String userID, final String displayName){


        //The app begins with creating a Database entry to server as an example.

        createTheLeaderList(userID, displayName);

        //Creation of a space in leaders
        leaders.child(userID).setValue(displayName);
        //Get reference to leaderName
        DatabaseReference leaderName = leaders.child(userID);
        //Create a database reference for the groups this specific user is invovled in.
        leaderName.child("INVOLVEDIN").child("Sample Group").setValue("Sample Group");

        leaderName.child("OWNEDGROUPS").child("Sample Group").setValue("Sample Group"); //Create an own section
        // for owned groups.
        //Create a group
        leaderName.child("Sample Group").setValue("Created a sample group");
        //Get reference to "Sample group"
        DatabaseReference sampleGroupRef = leaderName.child("Sample Group");

     /*
        //ADD GROUP NAME
        sampleGroupRef.child("GROUPNAME").setValue("Sample Group");

     */
        // Create ALLOWED, BLACKLIST, DATA, REQUESTS
        sampleGroupRef.child("ALLOWED").setValue("");
        sampleGroupRef.child("BLACKLIST").setValue("");
        sampleGroupRef.child("DATA").setValue("");
        sampleGroupRef.child("REQUESTS").setValue("");
        //Get reference to "ALLOWED" and add a fake user
        DatabaseReference ALLOWEDREF = sampleGroupRef.child("ALLOWED");
        ALLOWEDREF.child("Child1").setValue("");
        // blacklist
        DatabaseReference BLACKLISTREF = sampleGroupRef.child("BLACKLIST");
        BLACKLISTREF.child("Child2").setValue("");
        //DATA
        DatabaseReference DATAREF = sampleGroupRef.child("DATA");
        DATAREF.child("Data").setValue("Fake data content");
        //REQUESTS
        DatabaseReference REQUESTSREF = sampleGroupRef.child("REQUESTS");
        REQUESTSREF.child("FakeReq").setValue("");




        //Assign the display name below:
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("Display name changed to", displayName);
                        }
                    }
                });
        //CREATE THE USER PROFILE

        leaderName.child("USERPROFILE");
        DatabaseReference profileRef = leaderName.child("USERPROFILE");
        profileRef.child("DISPNAME").setValue(displayName);
        Intent MainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(MainActivityIntent);


    }

    public void createTheLeaderList(String uid, String displayName){


        DatabaseReference leaderSectionNameRef = root.child("Names");
        //CREATE DB REFERENCES FOR "LEADERNAMES" list, just the list with the leaders UID's and disp names.
        leaderSectionNameRef.child(uid).setValue(displayName);
        //This actions adds the key as the uid and the value of the dispname.




    }



    public void logIn(String email, String pass){

        Auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //Notify listeners
                            bar.setVisibility(View.INVISIBLE);
                            Intent startActivityForMain = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(startActivityForMain);
                            //This starts the Intent Activity.

                        }
                        else {

                            Toast.makeText(RegisterLoginActivity.this, "There was an error in logging in", Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.INVISIBLE);
                        }
                    }
                });





    }


}
