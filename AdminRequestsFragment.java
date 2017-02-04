package aakarsh.familyshop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;



public class AdminRequestsFragment extends Fragment {

    public FirebaseAuth.AuthStateListener AuthListener;
    DatabaseReference leadRef = FirebaseDatabase.getInstance().getReference().child("Leaders");
    DatabaseReference groupRef;
    private static  String nameOfGroupSelected;
  HashMap<String, String> userMap = new HashMap<>();
    private static final String ARG_PARAM1 = "param1";
    TextView noReqField;
    private static final String ARG_PARAM2 = "param2";
    ArrayAdapter adapter;
    public FirebaseAuth Auth;
    ArrayList<String> namesofRequests = new ArrayList<>();


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ListView lview;
    public AdminRequestsFragment() {
        // Required empty public constructor
    }


    public static AdminRequestsFragment newInstance(String param1, String param2) {
        AdminRequestsFragment fragment = new AdminRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nameOfGroupSelected = (String) bundle.getSerializable("DBREFGROUP");

        }

        AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid().toString();
                    groupRef = leadRef.child(uid).child(nameOfGroupSelected).child("REQUESTS");

                    groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot d: dataSnapshot.getChildren()) {
                                if (d.getKey().equals("FakeReq")) {
                                    //wont add to list

                                } else {
                                    namesofRequests.add(d.getValue().toString()); //Adds disp names not UIDS
                                    userMap.put(d.getValue().toString(), d.getKey().toString());
                                }
                            }
                            onPost();
                        }
                        public void onPost(){
                            if(namesofRequests.size() == 0){
                                noReqField.setVisibility(View.VISIBLE);

                            } else {
                                if (getActivity() != null) {
                                    adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, namesofRequests);
                                    lview.setAdapter(adapter);

                                    lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            // Toast.makeText(getApplicationContext(), "# " + arrayList.get(position), Toast.LENGTH_SHORT).show();
                                            String key = namesofRequests.get(position); //name of the person who was selected.
                                            //Log.i("", itemClicked);
                                            String uidOfPicked = userMap.get(key);
                                            System.out.println("Request selected was " + uidOfPicked + "  " + key);
                                            Fragment goToAdminPanel = new AdminDecideRequestOutcomeFragment();
                                            Bundle bundle = new Bundle(); //Create a bundle.
                                            bundle.putSerializable("DBREFGROUP", nameOfGroupSelected); //send DB Ref String to Fragment
                                            bundle.putSerializable("DBUIDOFREQUESTER", uidOfPicked);
                                            bundle.putSerializable("DBNAMEOFREQUESTER", key);
                                            goToAdminPanel.setArguments(bundle);
                                            FragmentManager fragmentManager = getFragmentManager();
                                            fragmentManager.beginTransaction().replace(R.id.flContent, goToAdminPanel).addToBackStack(null)//flcontent is the FrameLayout in
                                                    // Main Activity class (the main holder of the frags)
                                                    .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]


                                        }
                                    });

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
        View v = inflater.inflate(R.layout.fragment_admin_requests, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Admin Requests");
        lview = (ListView) v.findViewById(R.id.lview);
        noReqField = (TextView) v.findViewById(R.id.EmailField);
        noReqField.setVisibility(View.INVISIBLE);
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
        //Auth.removeAuthStateListener(AuthListener);
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
