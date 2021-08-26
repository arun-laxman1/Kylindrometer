package com.example.kylindrometer;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class k_v1_graph extends AppCompatActivity {

    TextView display_msg;
    TextView display_prediction;
    Interpreter tflite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DatabaseReference reff_three = FirebaseDatabase.getInstance().getReference().child("Entries");
        reff_three.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
                String currentDate = sdf.format(new Date());
                String display_wt;
                if (dataSnapshot.hasChild(currentDate)) {
                    display_wt = dataSnapshot.child(currentDate).child("cur_weight").getValue().toString();
                    display_msg = (TextView)findViewById(R.id.weight_from_manual);
                    display_prediction = (TextView)findViewById(R.id.days_prediction);
                    display_msg.setText("Date : "+ currentDate +" Entry : "+ display_wt);
                    int predict = (int) Math.floor(doInference(display_wt));
                    display_prediction.setText("Number of days left : "+ predict);
                }
                else {
                    display_msg = (TextView)findViewById(R.id.weight_from_manual);
                    display_msg.setText("You haven't made any entry for today!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_v1_graph);

//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
//        String currentDate = sdf.format(new Date());



//        Intent intent = getIntent();
//        String str = intent.getStringExtra("message");
//        receiver_msg.setText("           Date : "+ currentDate +"\n"+ str);

        try {
            tflite = new Interpreter(loadModelFile(),null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor=this.getAssets().openFd("weights_data.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=fileDescriptor.getStartOffset();
        long declareLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLength);
    }

    private float doInference(String inputString) {
        float[] inputVal=new float[1];
        inputVal[0]=Float.parseFloat(inputString);
        float[][] output=new float[1][1];
        tflite.run(inputVal,output);
        return output[0][0];
    }
}