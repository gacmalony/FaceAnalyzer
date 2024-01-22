package com.example.faceanalyzer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btn;
    TextView textView;

    ImageView imageView;

    private static final int REQUEST_IMAGE_CAPTURE = 124;
    InputImage inputImage;
    FaceDetector faceDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        FirebaseApp.initializeApp(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFile();
            }
        });

        Toast.makeText(getApplicationContext(), "App is started", Toast.LENGTH_SHORT).show();
    }

    private void OpenFile() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Toast.makeText(getApplicationContext(), "Youre here", Toast.LENGTH_SHORT).show();
        if (i.resolveActivity(getPackageManager()) != null){
            Toast.makeText(getApplicationContext(),"And not here", Toast.LENGTH_SHORT).show();
            startActivityIfNeeded(i , REQUEST_IMAGE_CAPTURE);
        }else{
            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");
        FaceDetectionProcess(bitmap);
        Toast.makeText(getApplicationContext(), "Succeed!!", Toast.LENGTH_SHORT).show();
    }

    private void FaceDetectionProcess(Bitmap bitmap) {
        textView.setText("Processing image ... ");
        final StringBuilder builder = new StringBuilder();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking().build();

        FaceDetector detector = FaceDetection.getClient(options);
        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        if (faces.size() !=0){
                                            if(faces.size() == 1){
                                                builder.append(faces.size()+ " Face Detected\n\n");
                                            }else if(faces.size() >1){
                                                builder.append(faces.size() + " Faces Detected\n\n");
                                            }
                                        }


                                        for (Face face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                            // nose available):
                                            FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
                                            if (leftEar != null) {
                                                PointF leftEarPos = leftEar.getPosition();
                                            }

                                            // If contour detection was enabled:
                                            List<PointF> leftEyeContour =
                                                    face.getContour(FaceContour.LEFT_EYE).getPoints();
                                            List<PointF> leftCheek =
                                                    face.getContour(FaceContour.LEFT_CHEEK).getPoints();
                                            List<PointF> rightEyeContour =
                                                    face.getContour(FaceContour.RIGHT_EYE).getPoints();
                                            for(PointF p : leftEyeContour){
                                                System.out.println(p);
                                            }
                                            for(PointF p : rightEyeContour ){
                                                System.out.println(p);
                                            }
                                            for(PointF p : leftCheek){
                                                System.out.println(p);
                                            }


                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != null) {
                                                float smileProb = face.getSmilingProbability();
                                                builder.append("4. Smiling Probability ["+ String.format("%.2f", smileProb)+"]\n");


                                            }
                                            if (face.getRightEyeOpenProbability() != null) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                builder.append("5. Right Eye Open Probability ["+ String.format("%.2f", rightEyeOpenProb)+"]\n");

                                            }

                                            if (face.getLeftEyeOpenProbability() != null) {
                                                float leftEyeOpenProb = face.getLeftEyeOpenProbability();
                                                builder.append("5. Left Eye Open Probability ["+ String.format("%.2f", leftEyeOpenProb)+"]\n");
                                            }



                                            // If face tracking was enabled:
                                            if (face.getTrackingId() != null) {
                                                int id = face.getTrackingId();
                                                builder.append("1. Face Tracking ID ["+id+"]\n");
                                                builder.append("2. Head Rotation to Right["+String.format("%.2f",rotY) +"deg. ]\n");
                                                builder.append("2. Head Tilted Sideways["+String.format("%.2f",rotZ) +"deg. ]\n");
                                            }
                                            builder.append("\n");
                                        }
                                        ShowDetection("Face Detection", builder, true);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        StringBuilder builder1 = new StringBuilder();
                                        builder1.append("Sorry there is a Mistake here!!");
                                        ShowDetection("Face Detection", builder, false);

                                    }
                                });






    }

    private void ShowDetection(final String title, final StringBuilder builder, boolean b) {

        if(b == true){
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());

            if(builder.length() != 0){
                textView.append(builder);
                if(title.substring(0, title.indexOf(" ")).equalsIgnoreCase("OCR")){
                    textView.append("\n(Hold the Text and copy it!)");

                }else{
                    textView.append("(Hold the text and copy it!)");
                }

                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(title, builder);
                        clipboardManager.setPrimaryClip(clipData);
                        return true;
                    }

            });
        }else {
                textView.append(title.substring(0, title.indexOf(" "))+"Failed to find anything!!!");
            }


    } else if (b == false) {
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.append(builder);
        }
            
        }
    }
