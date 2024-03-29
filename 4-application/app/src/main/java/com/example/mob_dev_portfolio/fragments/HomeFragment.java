package com.example.mob_dev_portfolio.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CallLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mob_dev_portfolio.classes.ContactData;
import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.adapters.ContactDataAdapter;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ArrayList<ContactData> contactDataList = new ArrayList<ContactData>();
    RecyclerView contactDataView;
    private ContactDataAdapter contactDataAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        contactDataView = view.findViewById(R.id.contact_list_view);
        checkPermission();
        return view;
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CALL_LOG}, 100);
        } else {
            getPhoneContacts();
        }

    }


    /**Please test this on a phone that has contact data
     *If the phone it's being test on have no data in the call log,
     * please call the emulator using the extend control.
     *Otherwise, please check out the 6-video included Gitlab for proof of the UI of this
     (I have been granted permission to include an extra file into my repository for
      proof of the call log.*/

    private void getPhoneContacts() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri uri = CallLog.Calls.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, CallLog.Calls.DATE + " DESC");
        Log.i("CONTACT_PROVIDER", "TOTAL # OF CONTACTS ::: " + Integer.toString(cursor.getCount()));
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                ContactData contactData = new ContactData();
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                contactData.setContactName(contactName);

                String contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                contactData.setPhoneNumber(contactNumber);

                String callDate = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                Date dateFormat= new Date(Long.valueOf(callDate));
                String callDateFormatted = String.valueOf(dateFormat);
                contactData.setCallDate(callDateFormatted);
                contactDataList.add(contactData);

                String direction = null;
                switch (Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE)))) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        direction = "OUTGOING";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        direction = "INCOMING";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        direction = "MISSED";
                        break;
                    default:
                        break;
                }
                contactData.setCallType(direction);
                //Log.i("CONTACT_PROVIDER", contactData.toString() + "call type: " + direction);
            }
        }
        contactDataAdapter = new ContactDataAdapter(contactDataList);
        layoutManager = new LinearLayoutManager(getContext());
        contactDataView.setLayoutManager(layoutManager);
        contactDataView.setAdapter(contactDataAdapter);
    }
}
