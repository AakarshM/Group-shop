package aakarsh.familyshop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentHostCallback;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindPeopleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindPeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindPeopleFragment extends  Fragment{


    public FirebaseAuth.AuthStateListener AuthListener;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference leaderListNamesTopLevel = db.child("Names");
    ArrayList<String> namesOfLeaders = new ArrayList<>();
    HashMap<String, String> userMap = new HashMap<>();
    ArrayAdapter adapter;
    ListView lview;
   private static String uid; //uid of the user logged in.

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static  String userID;
    private OnFragmentInteractionListener mListener;
    ArrayAdapter listAdapter;

    public FindPeopleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindPeopleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindPeopleFragment newInstance(String param1, String param2) {
        FindPeopleFragment fragment = new FindPeopleFragment();
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
                    userID = user.getUid().toString();

                } else {
                    Log.i("Problem:", "couldn't login.");
                }
            }
        };FirebaseAuth.getInstance().addAuthStateListener(AuthListener);
        leaderListNamesTopLevel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){

                    if(d.getKey().equals(userID)){
                        //skip

                    } else {

                        //System.out.println(d.getKey().toString()); //prints the names(keys)/dispnames
                        namesOfLeaders.add(d.getValue().toString());
                        //The above line adds to arraylist of users


                        //This adds to a hashmap correlating user/DispName to uid
                        userMap.put(d.getValue().toString(), d.getKey().toString());

                    }

                    //Print the items (values corresponding to keys) from the ArrayList
                    // Then the araylistSize
                }
                System.out.print(namesOfLeaders.toString() + " , ");
                System.out.println(+ namesOfLeaders.size());
                onPost();


            }

            public void onPost(){
                onPostExecute();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i("Error, " , databaseError.getDetails().toString());

            }


        });


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_find_people, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Find People");
       lview = (ListView) view.findViewById(R.id.lview);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);



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


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void onPostExecute(){


        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, namesOfLeaders);
        lview.setAdapter(adapter);
        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Gives the varios elements from the arraylist related to position.
                System.out.println("The user has clicked on   " + position); //gives the click pos.
                String userDispClickedOn = namesOfLeaders.get(position);
                String userClickedUID = userMap.get(userDispClickedOn); //Get the UID of user clicked.
                System.out.println(userClickedUID);
                ArrayList<String> detailOfUserClicked = new ArrayList<String>(); //(UID, Display Name)
                detailOfUserClicked.add(userClickedUID); //Add UID
                detailOfUserClicked.add(userDispClickedOn); //Add Disp. Name
                Fragment ClickedOnSomeOne = new FindPeopleClickedOnFragment();
                Bundle bundle = new Bundle(); //Create a bundle.
                bundle.putSerializable("List_Key", detailOfUserClicked); //send arraylists to Fragment
                ClickedOnSomeOne.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent,ClickedOnSomeOne) //flcontent is the FrameLayout in
                        // Main Activity class (the main holder of the frags)
.commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]



            }
        });
    }


    //The code below works to link Frag-->Frag

    /*

     NextFragment nextFrag= new NextFragment();
     this.getFragmentManager().beginTransaction()
     .replace(R.id.Layout_container, nextFrag,TAG_FRAGMENT)
     .addToBackStack(null)
     .commit();
     */

}
