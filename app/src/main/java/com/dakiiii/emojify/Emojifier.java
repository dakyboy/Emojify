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
    private static final double SMILING_PROB_THRESHOLD = .2;
    private static final double EYE_OPEN_THRESHOLD = .6;

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
                            Toast.makeText(context, firebaseVisionFaces.size() + " faces found", Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < firebaseVisionFaces.size(); i++) {
                                FirebaseVisionFace face = firebaseVisionFaces.get(i);
                                whichEmoji(face);
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

    private static void whichEmoji(FirebaseVisionFace face) {
        Log.d(LOG_TAG, "getClassifications: smiling Prob = " + face.getSmilingProbability());
        Log.d(LOG_TAG, "getClassifications: Left Eye Open Prob = " + face.getLeftEyeOpenProbability());
        Log.d(LOG_TAG, "getClassifications: Right Eye Open Prob = " + face.getRightEyeOpenProbability());

        boolean smiling = face.getSmilingProbability() > SMILING_PROB_THRESHOLD;
        boolean leftEyeClosed = face.getLeftEyeOpenProbability() < EYE_OPEN_THRESHOLD;
        boolean rightEyeClosed = face.getRightEyeOpenProbability() < EYE_OPEN_THRESHOLD;

        Emoji emoji;
        if (smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = Emoji.LEFT_WINK_FROWN;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }
        Log.d(LOG_TAG, "dat emoji: " + emoji.name());
    }

    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }
}
