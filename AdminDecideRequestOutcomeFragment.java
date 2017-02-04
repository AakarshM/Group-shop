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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdminDecideRequestOutcomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdminDecideRequestOutcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminDecideRequestOutcomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    public FirebaseAuth.AuthStateListener AuthListener;
    private static final String ARG_PARAM2 = "param2";
   private static String nameOfGroupSelected, uidOfUser, uidOfRequester, nameOfUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button button; //accept button
    Button blacklist, memberButton; //backlist, deny
    TextView nameOfRequesterView;
    DatabaseReference dbRefRoot = FirebaseDatabase.getInstance().getReference().child("Leaders");
    DatabaseReference dbEndRef; //ref towards requests

    private OnFragmentInteractionListener mListener;

    public AdminDecideRequestOutcomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminDecideRequestOutcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminDecideRequestOutcomeFragment newInstance(String param1, String param2) {
        AdminDecideRequestOutcomeFragment fragment = new AdminDecideRequestOutcomeFragment();
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
            nameOfGroupSelected = (String) bundle.getSerializable("DBREFGROUP");
            uidOfRequester = (String) bundle.getSerializable("DBUIDOFREQUESTER");
            nameOfUser = (String) bundle.getSerializable("DBNAMEOFREQUESTER");


        }

        AuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uidOfUser = user.getUid().toString();


                } else {
                    Log.i("Problem:", "couldn't login.");
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(AuthListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_admin_decide_request_outcome, container, false);
        nameOfRequesterView = (TextView) v.findViewById(R.id.groupName);
        button = (Button) v.findViewById(R.id.button2);
        memberButton = (Button) v.findViewById(R.id.memberButton);
        blacklist = (Button) v.findViewById(R.id.blacklistButton);
        button.setOnClickListener(reqListener);
        memberButton.setOnClickListener(memListener);
        blacklist.setOnClickListener(blackListener);
        nameOfRequesterView.setText(nameOfUser);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Choose Outcome");


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
        AuthListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public View.OnClickListener reqListener = new View.OnClickListener() {
        public void onClick (View view){
            //accept
            dbEndRef = dbRefRoot.child(uidOfUser).child(nameOfGroupSelected).child("REQUESTS"); //Request Ref of user
            DatabaseReference dbAcceptRef = dbRefRoot.child(uidOfUser).child(nameOfGroupSelected).child("ALLOWED"); //allowed ref of user
            DatabaseReference dbRequesterINVOLVEDREF = dbRefRoot.child(uidOfRequester).child("INVOLVEDIN"); //Create ref in REQUESTER to add involved group.
            dbRequesterINVOLVEDREF.child(nameOfGroupSelected).setValue(uidOfUser);  //Add the child-key UID of owner, value group name.
            dbAcceptRef.child(uidOfRequester).setValue(nameOfUser);
            dbEndRef.child(uidOfRequester).removeValue();



            Fragment goToAdminRequests = new AdminRequestsFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent,goToAdminRequests) //flcontent is the FrameLayout in
                    // Main Activity class (the main holder of the frags)
                    .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]



        }};


    public View.OnClickListener memListener = new View.OnClickListener() {
        public void onClick (View view){
            //deny
            dbEndRef = dbRefRoot.child(uidOfUser).child(nameOfGroupSelected).child("REQUESTS");
            dbEndRef.child(uidOfRequester).removeValue();




        }};

    public View.OnClickListener blackListener = new View.OnClickListener() {
        public void onClick (View view){
            //blacklist
            DatabaseReference dbEndRef2 = dbRefRoot.child(uidOfUser).child(nameOfGroupSelected).child("BLACKLIST");
            dbEndRef2.child(uidOfRequester).setValue(nameOfUser);



        }};

}
