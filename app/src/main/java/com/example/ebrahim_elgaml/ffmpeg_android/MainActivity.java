package com.example.ebrahim_elgaml.ffmpeg_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;


public class MainActivity extends AppCompatActivity {
    private TextView myTextView;
    private static final String VIDEO_PATH = Environment.getExternalStorageDirectory() + "/zoobetest/videos/";
    private static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/zoobetest/images/";
    private static final String[] engineVideoParams = new String[]{"-c:v",  "libx264", "-profile:v", "baseline", "-level", "3.0", "-b:v", "800k"
    , "-g", "10", "-qmin", "10", "-qmax", "51", "-i_qfactor", "0.71", "-qcomp", "0.6", "-me_method", "hex"
    , "-subq", "5", "-pix_fmt", "yuv420p"};
    private AlphaVideoView myVideoView;
    private ProgressDialog progressDialog;
    private MediaController mediaControls;
    private  long startTime = System.currentTimeMillis();
    private int myCounter = 0;
    private  Uri videoURI;
    private  boolean isFinshied = false;
    private int position = 0;
    private boolean seek = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView)(findViewById(R.id.mTextView));
        myVideoView = (AlphaVideoView)(findViewById(R.id.videoView));
        myVideoView.setVideoViewListener(mVideoViewListener);
        myVideoView.setOnCompletionListener(mVideoViewCompleteListener);
        Drawable d = Drawable.createFromPath(IMAGE_PATH + "bg.jpg");
        mediaControls = new MediaController(this);
//        myVideoView.setBackground(d);
        videoURI = Uri.parse(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts");
        startTime = System.currentTimeMillis();
//        new MyThread().start();
        final String[] command = formFFMPEGCommand(15, 29.27, "bg.jpg", "ojob4_Full1_pre_full.mp4", "ojob4_Full1_pre_full.mp4_alpha.mp4", "0:1");
        final Context myContext = this;
        final FFmpeg ffmpeg = FFmpeg.getInstance(myContext);

        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {
                }

                @Override
                public void onFailure() {
                }

                @Override
                public void onSuccess() {
                    try {
                        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                            @Override
                            public void onStart() {
                                progressDialog = ProgressDialog.show(myContext, "",
                                        "Buffering video...", true);
                                progressDialog.setCancelable(true);
                                startTime = System.currentTimeMillis();
                                Log.i("FFMPEG_TRAC", "STARETED");
//
                            }
                            @Override
                            public void onProgress(String message) {
                                if(myVideoView.getCurrentPosition() <= 0){
                                    if(System.currentTimeMillis() - startTime > 3000 ) {
                                        playVideoOriginal();
                                        seek = true;
                                        startTime = System.currentTimeMillis();

                                    }
                                }else{
                                    if( myVideoView.getDuration() - myVideoView.getCurrentPosition() <= 1000){
//                                        myVideoView.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if(myVideoView.canSeekForward()) {
//                                                    if (myVideoView.getCurrentPosition() > myVideoView.getDuration()) {
//                                                        position = myVideoView.getDuration();
//                                                    } else {
//                                                        position = myVideoView.getCurrentPosition();
//                                                    }
//                                                    if(position > 0) {
//                                                        playVideoOriginal();
//                                                    }
//                                                }
//                                            }
//                                        });
                                        startTime = System.currentTimeMillis();
                                        myCounter++;
                                    }
                                }
                                Log.i("FFMPEG_TRAC", message);
                            }
                            @Override
                            public void onFailure(String message) {
//                                Toast.makeText(myContext, message, Toast.LENGTH_LONG);
                                Log.i("FFMPEG_TRAC", message);
                            }

                            @Override
                            public void onSuccess(String message) {
//                                Toast.makeText(myContext, message, Toast.LENGTH_LONG);
                                isFinshied = true;
                                myTextView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        myTextView.setText("Time elapsed in milliseconds : " + (System.currentTimeMillis() - startTime) + ", Counter : " + myCounter);
                                    }
                                });
                                playVideoOriginal();
                                Log.i("FFMPEG_TRAC", message);
                            }

                            @Override
                            public void onFinish() {
//                                Toast.makeText(myContext, "Fininsh", Toast.LENGTH_LONG);
                                Log.i("FFMPEG_TRAC", "FINISH");
                                isFinshied = true;
                            }
                        });
                    } catch (FFmpegCommandAlreadyRunningException e) {
                        // Handle if FFmpeg is already running
                    }
                }

                @Override
                public void onFinish() {
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device
        }


    }

    // duration in seconds example : 22.1
    // SAR is string as "0:1"
    public static String[] formFFMPEGCommand(double frameRate, double duration, String bgPath, String videoColoredPath, String videoAlphaPath, String SAR){
        String orgVideo = videoColoredPath;
        videoColoredPath = VIDEO_PATH + videoColoredPath;
        videoAlphaPath = VIDEO_PATH + videoAlphaPath;
        bgPath = IMAGE_PATH + bgPath;
        String[] filters = new String[]{"-filter_complex", "[0:v]setsar=sar=" + SAR + ", format=pix_fmts=yuv420p[cimg];[1:v]scale=320x180[corg];[2:v]scale=320x180[cmask];[cimg][corg][cmask]maskedmerge[out]", "-map", "[out]", "-map", "1:a", "-t", ""+duration};
        String[] inputs = new String[]{"-r", "" + frameRate, "-loop", "1", "-i", bgPath, "-i", videoColoredPath, "-i", videoAlphaPath, "-y", "-t", "1"};
        String[] output = new String[]{VIDEO_PATH + orgVideo + "_transparent.ts"};
        String[] command = new String[inputs.length + engineVideoParams.length + filters.length + output.length];
        int start = 0;
        for(String s : inputs){
            command[start] = s ;
            start++;
        }
        for(String s : engineVideoParams){
            command[start] = s ;
            start++;
        }
        for(String s : filters){
            command[start] = s ;
            start++;
        }
        for(String s : output){
            command[start] = s ;
            start++;
        }
        return command;
    }
    private AlphaVideoView.IVideoViewActionListener mVideoViewListener = new AlphaVideoView.IVideoViewActionListener()
    {
        @Override
        public void onTimeBarSeekChanged(int currentTime)
        {
            //TODO what you want
            myVideoView.start();
        }

        @Override
        public void onResume()
        {
            //TODO what you want
        }

        @Override
        public void onPause()
        {
            //TODO what you want
        }
    };

    private MediaPlayer.OnCompletionListener mVideoViewCompleteListener = new MediaPlayer.OnCompletionListener()
    {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if(!isFinshied) {
                position = myVideoView.getDuration() - 100;
                playVideoOriginal();
            }
        }

    };
    public  void playVideoOriginal() {
        try {
            Log.i("THREAD_TRAC", "SEEK : " + seek + " And postion is : " +  position);
            if(seek && position <= 0) {
                position = myVideoView.getDuration() - 100;
            }
            myVideoView.setVideoURI(videoURI);
            progressDialog.dismiss();
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    myVideoView.seekTo(position);
//                    myVideoView.seekTo(position);
//                    if(seek) {
//                        myVideoView.seekTo(position);
//                    }else{
//                        myVideoView.seekTo(0);
//                        progressDialog.dismiss();
//                        seek = true;
//                    }

                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }

    public class MyThread extends Thread {
        public MyThread(){
            super("My Thread");
        }
        public void run() {
            while (!isFinshied) {
                        if (myVideoView.getCurrentPosition() <= 0) {
                            if (System.currentTimeMillis() - startTime > 6000) {
                                playVideoOriginal();
                                seek = true;
                                startTime = System.currentTimeMillis();

                            }
                        } else {
                            if (myVideoView.getDuration() - myVideoView.getCurrentPosition() <= 500) {
                                if (myVideoView.canSeekForward()) {
                                    if (myVideoView.getCurrentPosition() > myVideoView.getDuration()) {
                                        position = myVideoView.getDuration() - 100;
                                    } else {
                                        position = myVideoView.getCurrentPosition();
                                    }
                                    playVideoOriginal();
                                }
                                startTime = System.currentTimeMillis();
                                myCounter++;
                            }
                        }
            }
        }
    }

}
