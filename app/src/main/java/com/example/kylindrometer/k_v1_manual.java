package com.example.kylindrometer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class k_v1_manual extends AppCompatActivity {

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_v1_manual);

        submitButton = findViewById(R.id.manual_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.weight);
                if(TextUtils.isEmpty(editText.getText().toString()) || editText.getText().toString().equals(".") == true ) {
                    Toast.makeText(k_v1_manual.this, "Enter valid input!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(Double.parseDouble(editText.getText().toString()) < 15.0 || Double.parseDouble(editText.getText().toString()) > 30.0) {
                        Toast.makeText(k_v1_manual.this, "Enter within valid range! \n          (15kgs - 30kgs)", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        new AlertDialog.Builder(k_v1_manual.this).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Making Entry").setMessage("Are you sure you want to make this entry?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        process(editText);
                                    }
                                }).setNegativeButton("No", null).show();
                    }
                }
            }
        });
    }
    public void process(EditText et) {

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("Entries");
        double value = Double.parseDouble(et.getText().toString());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
        String currentDate = sdf.format(new Date());

        Entries entry = new Entries();
        entry.cur_date = currentDate;
        entry.cur_weight = String.valueOf(value);

        reff.child(currentDate).setValue(entry);
        Toast.makeText(k_v1_manual.this, "Data Entered Successfully!", Toast.LENGTH_SHORT).show();

        Intent switchActivityIntent = new Intent(this, MainActivity.class);
//        String str = "Weight you have entered is : " + String.valueOf(value);
//        switchActivityIntent.putExtra("message", str);
        startActivity(switchActivityIntent);
        finish();
    }
}