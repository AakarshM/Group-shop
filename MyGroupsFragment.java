package aakarsh.familyshop;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class MyGroupsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<String> groupsInvolvedInList = new ArrayList<>();
    ListView l;
    ArrayAdapter listAdapter;
    ArrayList<HashMap<String, String>> userDetailList = new ArrayList<>(); //[ (name, uid), (name, uid)]
                //List of userHashMaps
    TextView noGroupField;

    public FirebaseAuth.AuthStateListener AuthListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static String uid, displayName, emailID;
    FloatingActionButton fab ;

    private OnFragmentInteractionListener mListener;

    public MyGroupsFragment() {
        // Required empty public constructor
    }

    public static MyGroupsFragment newInstance(String param1, String param2) {
        MyGroupsFragment fragment = new MyGroupsFragment();
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


        AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Handle non-null user action. Fetch all data
                    uid = user.getUid().toString();
                    emailID = user.getEmail().toString();
                    displayName = user.getDisplayName().toString();
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference LeaderNamesAllRef = rootRef.child("Leaders");
                    DatabaseReference UserLeaderRef = LeaderNamesAllRef.child(uid);
                    DatabaseReference InvolvedInRef = UserLeaderRef.child("INVOLVEDIN");


                    InvolvedInRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for(DataSnapshot d: dataSnapshot.getChildren()) {

                                if (d.getKey().equals("Sample Group")) {
                                    //Skip

                                } else {


                                LinkedHashMap<String, String> userMap = new LinkedHashMap<>();
                                userMap.put(d.getKey().toString(), d.getValue().toString()); //(name, uid)
                                userDetailList.add(userMap);
                                groupsInvolvedInList.add(d.getKey().toString()); //Adds group name NOT UID of owner.
                            }

                            }

                           // s.add(dataSnapshot.child(nameOfEntry).getKey().toString()); //Previous entry.
                            onPost();
                        }
                        public void onPost(){


                            if(groupsInvolvedInList.size() == 0){
                                noGroupField.setVisibility(View.VISIBLE);

                            } else {

                                FetchData(emailID, uid, displayName);

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });






                } else {
                    Log.i("Problem:", "No user");
                }
            }
        };FirebaseAuth.getInstance().addAuthStateListener(AuthListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_groups, container, false);
        l = (ListView) v.findViewById(R.id.lview);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(fabListener);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("My Groups");
        noGroupField = (TextView) v.findViewById(R.id.noGroupField);
        noGroupField.setVisibility(View.INVISIBLE);
        return v;
    }

    public View.OnClickListener fabListener = new View.OnClickListener() {
        public void onClick (View view){
            //CREATE GROUP BUTTON ACTION LISTENER.

            System.out.println("Floating action button clicked.");
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View subView = inflater.inflate(R.layout.dialog_layout, null);
            final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Create a group");
            builder.setMessage("Set a (unique) group name");
            builder.setView(subView);
            AlertDialog alertDialog = builder.create();
            builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Create a DB reference here.
                    String nameOfGroupOwnerWantsToCreate = subEditText.getText().toString();
                        createGroup(nameOfGroupOwnerWantsToCreate);

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_LONG).show();
                }
            });
            builder.show();


        }};


    public void createGroup(String nameOfGroupOwnerToCreate){
            DatabaseReference dbRefToLeader = FirebaseDatabase.getInstance().getReference()
                    .child("Leaders").child(uid);
        //CREATED A DB REFERENCE TO THE SPECIFIC LEADER/OWNER.


        //Add the new Database reference to the INVOLVEDIN.
        dbRefToLeader.child("INVOLVEDIN").child(nameOfGroupOwnerToCreate).setValue(uid);
                //Create a DB Reference and add the owner's UID as key and NAMEOFGROUP As value.

        dbRefToLeader.child("OWNEDGROUPS").child(nameOfGroupOwnerToCreate).setValue(nameOfGroupOwnerToCreate);
        // /Create an own section
        // for owned groups.

        //Create a group
        dbRefToLeader.child(nameOfGroupOwnerToCreate).setValue(nameOfGroupOwnerToCreate);




        //Get reference to the NEWLY CREATED GROUP

        DatabaseReference sampleGroupRef = dbRefToLeader.child(nameOfGroupOwnerToCreate);

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


        Fragment MyGroupsFrag = new MyGroupsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,MyGroupsFrag)//flcontent is the FrameLayout in
                // Main Activity class (the main holder of the frags)
                .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]




    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void FetchData(String email, final String uid, String displayName) {

        if (getActivity() != null) {
            listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, groupsInvolvedInList);
            l.setAdapter(listAdapter);
            l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Toast.makeText(getApplicationContext(), "# " + arrayList.get(position), Toast.LENGTH_SHORT).show();
                    System.out.println(userDetailList.get(position));
                    HashMap<String, String> correspondingHashMap = new HashMap<>();
                    correspondingHashMap = userDetailList.get(position); //gets the hashmap corresponding to the #.
                    String nameofGroupClicked = "";
                    String uidOfGroup = "";
                    for (String key : correspondingHashMap.keySet()) {
                        //There's only 1 key in each member of the list.
                        nameofGroupClicked = key;
                        uidOfGroup = correspondingHashMap.get(nameofGroupClicked);
                    }

                    //Go to the next fragment (group view fragment)
                    System.out.println("You cliked name  " + nameofGroupClicked + "  owned by  " + uidOfGroup);
                    goToGroupsMainActivity(nameofGroupClicked, uidOfGroup);

                }
            });

        }
    }
    public void goToGroupsMainActivity(String nameofGroup, String uidOfOwner){
        //Go to the mainActivityGROUP Fragment.


    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
