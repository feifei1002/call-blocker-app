package com.example.mob_dev_portfolio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.example.mob_dev_portfolio.R;
import com.example.mob_dev_portfolio.databases.BlockList;
import com.example.mob_dev_portfolio.databases.BlockListDatabase;

public class BlockDialog extends DialogFragment {

    private TextView yesBtn;
    private TextView cancelBtn;
    private TextView askBlockPhoneNo;
    private String phoneNoBlock;

    public static BlockDialog newInstance() {
        Bundle args = new Bundle();
        BlockDialog fragment = new BlockDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            phoneNoBlock = getArguments().getString("phoneNoBlock");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.block_dialog, container, false);
        askBlockPhoneNo = view.findViewById(R.id.askBlockPhoneNo_textView);
        askBlockPhoneNo.setText(phoneNoBlock);
        yesBtn = view.findViewById(R.id.action_yes);
        cancelBtn = view.findViewById(R.id.action_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        BlockListDatabase listDatabase = Room.databaseBuilder(getContext(), BlockListDatabase.class, "Block List Database").allowMainThreadQueries().build();
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlockList blockListSubmit = new BlockList(phoneNoBlock);
                System.out.println(blockListSubmit.getPhoneNo());
                if (listDatabase.blockListDAO().getBlockListByPhoneNo(blockListSubmit.getPhoneNo()) == false) {
                    listDatabase.blockListDAO().insertAll(blockListSubmit);
                } else {
                    Toast.makeText(getContext(), R.string.toast_blocked_message, Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
                BlockFragment blockFragment = new BlockFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, blockFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}
