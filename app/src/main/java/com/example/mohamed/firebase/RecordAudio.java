package com.example.mohamed.firebase;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class RecordAudio extends AppCompatActivity {
    private Button Record;
    private TextView hold_and_wait;
private MediaRecorder mRecorder;
    private  String downloadedAudio;
    private static final String LOG_TAG = "MESSAGE : ";

    private StorageReference mStorgeRef;

    private  String mFileName=null;

    private ProgressDialog mProgressDialog;
    private  Uri mDownloadedAudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);

        Record=(Button)findViewById(R.id.RecordAudio);
        hold_and_wait=(TextView)findViewById(R.id.HoldRecord);



mFileName= Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName+="/recorded_audio.3gp";

mStorgeRef= FirebaseStorage.getInstance().getReference();
mProgressDialog=new ProgressDialog(this);

        Record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()== MotionEvent.ACTION_DOWN){
//                    startRecording();
                    startRecording();
                    hold_and_wait.setText("Recording..");

                }else if(event.getAction()== MotionEvent.ACTION_UP){
                             stopRecording();
                    hold_and_wait.setText("Stop Recording...");


                }

                return false;
            }
        });

        Button play=(Button)findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                palyAudio(mDownloadedAudio);
            }
        });

    }

    private void palyAudio(Uri uri) {

        try {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(uri.toString());
            player.prepare();
            player.start();
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        uploadingAudio();
    }

    private void uploadingAudio() {
        mProgressDialog.setMessage("uploading");
        mProgressDialog.show();
        StorageReference mSR=mStorgeRef.child("myaudio").child("recorded_audio.3gp");
        Uri uri=Uri.fromFile(new File(mFileName));

        mSR.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadeduri=taskSnapshot.getDownloadUrl();

               mDownloadedAudio=downloadeduri;

              mProgressDialog.dismiss();
                Toast.makeText(getBaseContext(),"Done",Toast.LENGTH_LONG).show();
            }
        });
    }


}
