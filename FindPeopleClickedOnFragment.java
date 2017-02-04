package aakarsh.familyshop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindPeopleClickedOnFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindPeopleClickedOnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindPeopleClickedOnFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference leaderDataRef = rootRef.child("Leaders");
    DatabaseReference specificChildRef;
    DatabaseReference groupsNameRef;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<String> userDetail;
    String uidClicked;
    ArrayList<String> groupList;
    String displayNameClicked;
    ListView lview;
    ArrayList<String> groupNameList = new ArrayList<>();
    ArrayAdapter adapter;

    public FindPeopleClickedOnFragment() {
        // Required empty public constructor
    }

    public static FindPeopleClickedOnFragment newInstance(String param1, String param2) {
        FindPeopleClickedOnFragment fragment = new FindPeopleClickedOnFragment();
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
        //Get bundle from Old Frag.
        Bundle bundle = this.getArguments();
        if (bundle != null) {
           userDetail = (ArrayList<String>) bundle.getSerializable("List_Key"); //(uid, displayname)
           uidClicked = userDetail.get(0);
           displayNameClicked = userDetail.get(1);
        }

        System.out.println(uidClicked.toString() + "         " + displayNameClicked.toString());
        specificChildRef = leaderDataRef.child(uidClicked);
        DatabaseReference OwnedGroupRef = specificChildRef.child("OWNEDGROUPS");


        OwnedGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()) { //every child in specificChildRef

                    if(d.getKey().equals("Sample Group")){
                        //Skip it.

                    } else {

                        System.out.print(d.getKey().toString() + "   ");

                        groupNameList.add(d.getKey().toString());
                    }

                }

              // DatabaseReference groupRefd = specificChildRef.child(d);


               onPost(uidClicked, displayNameClicked);


            }



            public void onPost(final String uid, final String disp){


            if(getActivity() != null) {
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, groupNameList);
                lview.setAdapter(adapter);
                lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Gives the varios elements from the arraylist related to position.
                        System.out.println("The user has clicked on   " + position); //gives the click pos.

                        ArrayList<String> detailsofUIDDispGroupName = new ArrayList<String>();
                        detailsofUIDDispGroupName.add(uid);
                        detailsofUIDDispGroupName.add(disp);
                        detailsofUIDDispGroupName.add(groupNameList.get(position).toString());
                        //UID, DISP, GroupName..


                        Fragment DetailOfGroupClicked = new DetailOfGroupClickedFragment();
                        Bundle bundle = new Bundle(); //Create a bundle.
                        bundle.putSerializable("List_Key", detailsofUIDDispGroupName); //send arraylists to Fragment
                        DetailOfGroupClicked.setArguments(bundle);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, DetailOfGroupClicked) //flcontent is the FrameLayout in
                                // Main Activity class (the main holder of the frags)
                                .commit();  //Replace old frag (findpplfrag) with the (someonegotclickedonfrag) (frag->frag)]


                    }
                });

            }

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
        View v = inflater.inflate(R.layout.fragment_find_people_clicked_on, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Find People");
        lview = (ListView) v.findViewById(R.id.lview);
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
}
