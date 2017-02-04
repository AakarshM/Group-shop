package aakarsh.familyshop;

import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class AdminSideFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static  String uid;
    ArrayList<String> listOfOwnedGroups = new ArrayList<>();
    DatabaseReference leadRef = FirebaseDatabase.getInstance().getReference().child("Leaders");
    DatabaseReference userRef;
    DatabaseReference ownedRef;
    TextView ownedField;
    ArrayAdapter listAdapter;
    public FirebaseAuth.AuthStateListener AuthListener;
    public FirebaseAuth Auth;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView lview;

    private OnFragmentInteractionListener mListener;

    public AdminSideFragment() {
        // Required empty public constructor
    }


    public static AdminSideFragment newInstance(String param1, String param2) {
        AdminSideFragment fragment = new AdminSideFragment();
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
                   uid = user.getUid().toString();
                    userRef = leadRef.child(uid);
                    ownedRef = userRef.child("OWNEDGROUPS"); //Fetch dat data.
                    ownedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot d: dataSnapshot.getChildren()){

                                if(d.getKey().equals("Sample Group")){
                                        //Skip

                                } else {
                                    listOfOwnedGroups.add(d.getKey().toString());
                                }
                            }

                            onPost();
                        }
                        public void onPost(){
                            if(listOfOwnedGroups.size() == 0){
                                System.out.println("No items in the ArrayList of user's groups.");
                                ownedField.setVisibility(View.VISIBLE);

                            } else {
                                if(getActivity() != null) {

                                    listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listOfOwnedGroups);
                                    lview.setAdapter(listAdapter);
                                    controlActions();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                } else {
                    Log.i("Problem:", "User wasn't logged in.");
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(AuthListener);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_side, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Admin Manager (Main)");
        lview = (ListView) view.findViewById(R.id.lview);
        ownedField = (TextView) view.findViewById(R.id.ownedField);
        ownedField.setVisibility(View.INVISIBLE);
        return view;
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
       // Auth.removeAuthStateListener(AuthListener);
    }

    public void controlActions(){
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              System.out.println("Admin has clicked on " + position); //corr. list is LIST.
                String nameOfGroup = listOfOwnedGroups.get(position); //Grabs the name of corr. lst.
                DatabaseReference groupPickedRef = leadRef.child(uid).child(nameOfGroup);

                Fragment goToAdminPanel = new AdminPanelFragment();
                Bundle bundle = new Bundle(); //Create a bundle.
                bundle.putSerializable("DBREFGROUP", nameOfGroup); //send DB Ref String to Fragment
                goToAdminPanel.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent,goToAdminPanel) //flcontent is the FrameLayout in
                        // Main Activity class (the main holder of the frags)
                        .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]



            }
        });

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
