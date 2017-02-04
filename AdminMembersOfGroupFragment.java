package aakarsh.familyshop;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.List;

import static aakarsh.familyshop.R.attr.showText;


public class AdminMembersOfGroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<HashMap<String, String>> listofMembersMAP = new ArrayList<>(); //[ (name, uid), (name, uid)]
    //List of userHashMaps
    ArrayList<String> memberList = new ArrayList<>(); //Just names
    ListView lview;
    TextView emptyField;
    HashMap<String, String> memberMap = new HashMap<>();
    FirebaseAuth.AuthStateListener AuthListener;
    ArrayAdapter listAdapter;
    private static String uid;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdminMembersOfGroupFragment() {
        // Required empty public constructor
    }
   private static String nameOfGroupSelected;


    public static AdminMembersOfGroupFragment newInstance(String param1, String param2) {
        AdminMembersOfGroupFragment fragment = new AdminMembersOfGroupFragment();
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
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nameOfGroupSelected = (String) bundle.getSerializable("DBREFGROUP"); //Get group name of which u want to check mem.


        }

        AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    uid = user.getUid().toString();
                    System.out.println("Uid in ADMINEMEMBERSOFGROUPFRAGMENT IS  " + uid);
                     generateListOfMember();


                } else {
                    Log.i("Problem:", "couldn't login.");
                }
            }
        };FirebaseAuth.getInstance().addAuthStateListener(AuthListener);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_members_of_group, container, false);
        lview = (ListView) v.findViewById(R.id.lview);
        emptyField = (TextView) v.findViewById(R.id.EmailField);
        emptyField.setVisibility(View.INVISIBLE);
        return v;
    }


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


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    public void generateListOfMember(){
        DatabaseReference dbRefToAllowedMembers = FirebaseDatabase.getInstance().getReference().child("Leaders")
                .child(uid).child(nameOfGroupSelected).child("ALLOWED");

        dbRefToAllowedMembers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    if(d.getKey().equals("Child1")){
                        //Skip fakechild created.

                    } else {

                        if (getActivity() != null) {
                            memberList.add(d.getValue().toString()); //add to name list
                            HashMap<String, String> memberMap = new HashMap<String, String>();
                            memberMap.put(d.getKey().toString(), d.getValue().toString()); //(name, UID)
                            listofMembersMAP.add(memberMap);
                            System.out.println(memberList.size());


                        }
                    }



                }

                displayMemberList();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void displayMemberList(){
        System.out.println(memberList.size());

        if(memberList.size() == 0){
            emptyField.setVisibility(View.VISIBLE);

        } else {
            listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, memberList);
            lview.setAdapter(listAdapter);
            lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> selectedUserMap = listofMembersMAP.get(position);
                    String nameOfMember = "";
                    String uidOfMember = "";
                    for(String key: selectedUserMap.keySet()){
                        uidOfMember = key;
                    }
                    nameOfMember = selectedUserMap.get(uidOfMember);
                    //Launch a dialog to remove or keep user.
                    openDialog(uidOfMember, nameOfMember);

                }
            });
        }

    }

    public void openDialog(final String uidOfMember, String nameOfMember){

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.dialog_layout, null);
        //final EditText subEditText = (EditText)subView.findViewById(R.id.dialogEditText); <--Removed EditText

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Remove or keep user");
        builder.setMessage("Remove " + nameOfMember + "?");
        builder.setView(subView);
        AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Create a DB reference here.
                removeUser(uidOfMember);



            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_LONG).show();
                //Does nothing.
            }
        });
        builder.show();

    }

    public void removeUser(String uidOfMember){

        DatabaseReference dbRefToAllowed = FirebaseDatabase.getInstance().getReference().child("Leaders")
                .child(uid).child(nameOfGroupSelected).child("ALLOWED").child(uidOfMember);
        dbRefToAllowed.removeValue();

        Fragment goToMemberList = new AdminMembersOfGroupFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent,goToMemberList) //flcontent is the FrameLayout in
                // Main Activity class (the main holder of the frags)
                .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]




    }


}
