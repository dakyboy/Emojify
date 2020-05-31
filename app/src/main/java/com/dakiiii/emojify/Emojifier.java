package com.dakiiii.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class Emojifier {
    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    static void detectFaces(final Context context, Bitmap bitmap) throws IOException {
        FirebaseVisionFaceDetectorOptions faceDetectorOptions;
        faceDetectorOptions = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();

        FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(faceDetectorOptions);

        FirebaseVisionImage frame = FirebaseVisionImage.fromBitmap(bitmap);
        Task<List<FirebaseVisionFace>> result = faceDetector.detectInImage(frame)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        if (firebaseVisionFaces.size() == 0) {
                            Toast.makeText(context, "no faces found", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i <firebaseVisionFaces.size(); i++) {
                                FirebaseVisionFace face = firebaseVisionFaces.get(i);
                                getClassifications(face);
                            }
                        }
                        Log.d(LOG_TAG, Integer.toString(firebaseVisionFaces.size()));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
        faceDetector.close();
    }

    private static void getClassifications(FirebaseVisionFace face) {
        Log.d(LOG_TAG, "getClassifications: smilingProb = " + face.getSmilingProbability());
    }
}
