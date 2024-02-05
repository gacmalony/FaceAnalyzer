package com.example.faceanalyzer;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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








    private static final int REQUEST_IMAGE_CAPTURE = 1;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        FirebaseApp.initializeApp(this);




        askPermission();
        btn.setOnClickListener(v -> openFile());

        Toast.makeText(getApplicationContext(), "App is started", Toast.LENGTH_SHORT).show();
    }

    private void openFile() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Toast.makeText(getApplicationContext(), "You're here", Toast.LENGTH_SHORT).show();
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        }
        


        private void askPermission(){
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            }
        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FaceDetectionProcess(bitmap);
        }catch (NullPointerException e){
            e.printStackTrace();
        }finally {
            Toast.makeText(this, "MainActivityFirstSavedPoint", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getApplicationContext(), "Succeed!!", Toast.LENGTH_SHORT).show();

    }

    private void FaceDetectionProcess(Bitmap bitmap) {
        textView.setText(R.string.process);
        final StringBuilder builder = new StringBuilder();
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking().build();
        Toast.makeText(this, "MainActivitySavedPoint Second", Toast.LENGTH_SHORT).show();
        btn.setVisibility(View.GONE);


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
            Toast.makeText(this, "MainActivitySavedPoint Third", Toast.LENGTH_SHORT).show();


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
        Toast.makeText(this, "MainActivitySavedPoint Fourth", Toast.LENGTH_SHORT).show();

    }
    }
