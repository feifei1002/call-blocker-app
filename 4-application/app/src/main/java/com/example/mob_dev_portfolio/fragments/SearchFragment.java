package com.example.mob_dev_portfolio.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mob_dev_portfolio.classes.PhoneNoAPI;
import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.adapters.PhoneNoAPIAdapter;
import com.example.mob_dev_portfolio.databases.BlockListDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String phoneNo;
    private String phoneNoKnown;
    private Button reportBtn;
    private Button blockBtn;
    private SearchView searchView;
    private TextView phoneTextView;
    private TextView phoneTypeTextView;
    private TextView phoneRegionTextView;
    private TextView phoneCountryTextView;
    private TextView carrierTextView;
    private PhoneNoAPIAdapter phoneNoAPIAdapter;
    private ArrayList<PhoneNoAPI> phoneNoAPIS = new ArrayList<PhoneNoAPI>();
    private RecyclerView.LayoutManager layoutManager;
    private ExecutorService executorService;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
            phoneNoKnown = getArguments().getString("phoneNo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint(getString(R.string.search_for_a_phone_number_here));
        searchView.onActionViewExpanded();
        reportBtn = view.findViewById(R.id.report_button);
        blockBtn = view.findViewById(R.id.block_button);
        phoneTextView = view.findViewById(R.id.phone_textView);
        phoneTypeTextView = view.findViewById(R.id.phoneType_textView);
        phoneRegionTextView = view.findViewById(R.id.phoneRegion_textView);
        phoneCountryTextView = view.findViewById(R.id.country_textView);
        carrierTextView = view.findViewById(R.id.carrier_textView);
        searchView.setQuery(phoneNoKnown, false);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ReportFragment reportFragment = new ReportFragment();
                Bundle bundle = new Bundle();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Log.i("SearchView", String.valueOf(searchView.getQuery()));
                bundle.putString("phoneNoKnown", String.valueOf(searchView.getQuery()));
                reportFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame_layout, reportFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                phoneNo = query;
                for(int i = 0; i < phoneNoAPIS.size(); i++){
                    phoneNoAPIS.get(i).getPhoneNo();
                }
                onRequestPhoneNos();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        this.executorService = Executors.newFixedThreadPool(4);
        BlockListDatabase listDatabase = Room.databaseBuilder(getContext(), BlockListDatabase.class, "Block List Database").build();
        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockDialog blockDialog = new BlockDialog();
                blockDialog.show(getFragmentManager(), "BlockDialog");
                String phoneNumber = String.valueOf(searchView.getQuery());
                Bundle bundle = new Bundle();
                bundle.putString("phoneNoBlock", phoneNumber);
                blockDialog.setArguments(bundle);
            }
        });
        return view;
    }

    public void onRequestPhoneNos () {
        //searchPhoneNumber();
        String url1 = "https://api.veriphone.io/v2/verify?key=B4F636D300CC4387BA0A02284991B5CB";
        String url2 = "https://api.veriphone.io/v2/verify?phone=%2B49-15123577723&key=B4F636D300CC4387BA0A02284991B5CB";
        String url3 = "https://api.veriphone.io/v2/verify?phone="+phoneNo+"&key=B4F636D300CC4387BA0A02284991B5CB";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url3, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String phoneNumber;
                        String phoneType;
                        String phoneRegion;
                        String country;
                        String carrier;
                        try {
                            phoneNumber = response.getString("phone");
                            phoneTextView.setText(String.format("Phone: %s", phoneNumber));
                            phoneType = response.getString("phone_type");
                            phoneTypeTextView.setText(String.format("Phone Type: %s", phoneType));
                            phoneRegion = response.getString("phone_region");
                            phoneRegionTextView.setText(String.format("Phone Region: %s", phoneRegion));
                            country = response.getString("country");
                            phoneCountryTextView.setText(String.format("Country: %s", country));
                            carrier = response.getString("carrier");
                            carrierTextView.setText(String.format("Carrier: %s", carrier));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(),Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}
