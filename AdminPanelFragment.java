package aakarsh.familyshop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static aakarsh.familyshop.R.id.nameOfGroup;



public class AdminPanelFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DatabaseReference leadRef = FirebaseDatabase.getInstance().getReference().child("Leaders");
    DatabaseReference groupRef;
    Button button; //Friend request manaage button
    Button blacklist, memberButton;
    TextView groupName;
    private static String nameOfCurrentGroup;
    DatabaseReference groupSelectedRef;
    public FirebaseAuth Auth;
    public FirebaseAuth.AuthStateListener AuthListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdminPanelFragment() {
        // Required empty public constructor
    }
    private static  String nameOfGroupSelected;

    public static AdminPanelFragment newInstance(String param1, String param2) {
        AdminPanelFragment fragment = new AdminPanelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static  String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_panel, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Admin Panel");
        groupName = (TextView) v.findViewById(R.id.groupName);
        button = (Button) v.findViewById(R.id.button2);
        memberButton = (Button) v.findViewById(R.id.memberButton);
        blacklist = (Button) v.findViewById(R.id.blacklistButton);
        button.setOnClickListener(reqListener);
        memberButton.setOnClickListener(memListener);
        blacklist.setOnClickListener(blackListener);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nameOfGroupSelected = (String) bundle.getSerializable("DBREFGROUP");

        }
        AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid().toString();
                    groupSelectedRef = leadRef.child(uid).child(nameOfGroupSelected);
                    groupName.setText(nameOfGroupSelected);



                    /*

                        Fragment goToAdminPanel = new AdminPanelFragment();
                Bundle bundle = new Bundle(); //Create a bundle.
                bundle.putSerializable("DBREFGROUP", nameOfGroup); //send DB Ref String to Fragment
                goToAdminPanel.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent,goToAdminPanel) //flcontent is the FrameLayout in
                        // Main Activity class (the main holder of the frags)
                        .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]

                     */

                } else {
                    Log.i("Problem:", "couldn't login.");
                }
            }
        };FirebaseAuth.getInstance().addAuthStateListener(AuthListener);

        return v;
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
//        Auth.removeAuthStateListener(AuthListener);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public View.OnClickListener reqListener = new View.OnClickListener() {
        public void onClick (View view){

            Fragment goToAdminRequests = new AdminRequestsFragment();
            Bundle bundle = new Bundle(); //Create a bundle.
            bundle.putSerializable("DBREFGROUP", nameOfGroupSelected); //send DB Ref String to Fragment
            goToAdminRequests.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent,goToAdminRequests) //flcontent is the FrameLayout in
                    // Main Activity class (the main holder of the frags)
                    .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]



        }};


    public View.OnClickListener memListener = new View.OnClickListener() {
        public void onClick (View view){
            Fragment goToAdminMemberView = new AdminMembersOfGroupFragment();
            Bundle bundle = new Bundle(); //Create a bundle.
            bundle.putSerializable("DBREFGROUP", nameOfGroupSelected); //send DB Ref String to Fragment
            goToAdminMemberView.setArguments(bundle);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent,goToAdminMemberView)//flcontent is the FrameLayout in
                    // Main Activity class (the main holder of the frags)
                    .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]



        }};

    public View.OnClickListener blackListener = new View.OnClickListener() {
        public void onClick (View view){
            //DELETES GROUP
            DatabaseReference dbRefUntilGroup = FirebaseDatabase.getInstance().getReference().child("Leaders")
                    .child(uid).child(nameOfGroupSelected);
            dbRefUntilGroup.removeValue();

            DatabaseReference dbRefUntilInvolvedIn = FirebaseDatabase.getInstance().getReference().child("Leader")
                    .child(uid).child("INVOLVEDIN").child(nameOfGroupSelected);
            dbRefUntilInvolvedIn.removeValue();

            DatabaseReference dbRefUntilOwned = FirebaseDatabase.getInstance().getReference().child("Leaders")
                    .child(uid).child("OWNEDGROUPS");
            dbRefUntilOwned.removeValue();



        }};






}
