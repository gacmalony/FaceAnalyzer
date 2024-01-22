package com.example.faceanalyzer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultDialog extends DialogFragment {

    Button btn;
    TextView txt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmentresult_dialog, container, false);
        String text = "";

        btn = view.findViewById(R.id.button2);
        txt = view.findViewById(R.id.textView2);

        // Getting Bundle
        Bundle bundle = getArguments();
        text = bundle.getString("RESULT_TEXT");
        txt.setText(text);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;


    }
}
