package aakarsh.familyshop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;



public class DetailOfGroupClickedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
  private static  String uid, displayname, groupname; //target info
   private static String uidOfUser, DisplayOfUser;
    Button button;
    TextView num, nameG, nameL;
    FirebaseAuth.AuthStateListener AuthListener;
    ArrayList<String> detail = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public DetailOfGroupClickedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailOfGroupClickedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailOfGroupClickedFragment newInstance(String param1, String param2) {
        DetailOfGroupClickedFragment fragment = new DetailOfGroupClickedFragment();
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

                    uidOfUser = user.getUid();
                    DisplayOfUser = user.getDisplayName();

                } else {
                    Log.i("Problem:", "couldn't login.");
                }
            }
        };
        FirebaseAuth.getInstance().addAuthStateListener(AuthListener);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            detail = (ArrayList<String>) bundle.getSerializable("List_Key"); //(uid, displayname, groupname)
            uid = detail.get(0);
            displayname = detail.get(1);
            groupname = detail.get(2);
        }

        System.out.println(displayname + " " + groupname + " " + uid);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_of_group_clicked, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Group Details");
        num = (TextView) v.findViewById(R.id.numOfMem);
        nameG = (TextView) v.findViewById(R.id.nameOfGroup);
        nameL = (TextView) v.findViewById(R.id.nameOfLeader);
        nameG.setText(groupname);
        nameL.setText(displayname);


        button = (Button) v.findViewById(R.id.memberButton);
        button.setOnClickListener(askButtonListener);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public View.OnClickListener askButtonListener = new View.OnClickListener() {
        public void onClick (View view){

           SendRequest(uid, groupname);

        }};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
            //Removed garbage.


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    public void SendRequest(String uid, String groupname){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference leadersRoot = root.child("Leaders"); //Get leaders root.
        DatabaseReference specificLeaderRoot = leadersRoot.child(uid); //specific leader root
        DatabaseReference targetGroupRoot = specificLeaderRoot.child(groupname);
        //Now write a request.
        targetGroupRoot.child("REQUESTS").child(uidOfUser).setValue(DisplayOfUser);

        //Toast.makeText(getActivity(), "Request Sent", Toast.LENGTH_SHORT).show();
        Snackbar.make(getView(), "Request Sent", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


    }

}
