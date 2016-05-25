package com.example.ebrahim_elgaml.ffmpeg_android;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


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
    private ScrollView myScroll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView)(findViewById(R.id.mTextView));
        myVideoView = (AlphaVideoView)(findViewById(R.id.videoView));

//        myScroll = (ScrollView)(findViewById(R.id.scrollView));
//        myScroll.setBackgroundColor(Color.RED);
        myVideoView.setVideoViewListener(mVideoViewListener);
        myVideoView.setOnCompletionListener(mVideoViewCompleteListener);
        Drawable d = Drawable.createFromPath(IMAGE_PATH + "bg.jpg");
        myVideoView.getRootView().setBackground(d);
//        myScroll.setBackground(d);
        myVideoView.setBackgroundColor(Color.BLUE);
        mediaControls = new MediaController(this);
        videoURI = Uri.parse(VIDEO_PATH + "ojob4_Full1_pre_full.mp4" + "_transparent.ts");
        startTime = System.currentTimeMillis();
        RelativeLayout yourRelLay = (RelativeLayout) myVideoView.getParent();
        myVideoView.getRootView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                }
                saveBitmap(loadBitmapFromView(myVideoView.getRootView(), myVideoView.getRootView().getWidth(), myVideoView.getRootView().getHeight()));
            }
        });
//        Log.i("FILE_TRAC", "LAYOUT WIDTH : " + yourRelLay.getWidth() + " H : " + yourRelLay.getHeight());

//        final String[] command = formFFMPEGCommand(15, 29.27, "bg.jpg", "ojob4_Full1_pre_full.mp4", "ojob4_Full1_pre_full.mp4_alpha.mp4", "0:1");
//        final Context myContext = this;
//        final FFmpeg ffmpeg = FFmpeg.getInstance(myContext);
//        try {
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                @Override
//                public void onStart() {
//                }
//
//                @Override
//                public void onFailure() {
//                }
//
//                @Override
//                public void onSuccess() {
//                    try {
//                        ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
//                            @Override
//                            public void onStart() {
//                                progressDialog = ProgressDialog.show(myContext, "",
//                                        "Buffering video...", true);
//                                progressDialog.setCancelable(true);
//                                startTime = System.currentTimeMillis();
//                                Log.i("FFMPEG_TRAC", "STARETED");
//                            }
//                            @Override
//                            public void onProgress(String message) {
//                                if(myVideoView.getCurrentPosition() <= 0){
//                                    if(System.currentTimeMillis() - startTime > 3000 ) {
//                                        playVideoOriginal();
//                                        seek = true;
//                                        startTime = System.currentTimeMillis();
//
//                                    }
//                                }
//                                Log.i("FFMPEG_TRAC", message);
//                            }
//                            @Override
//                            public void onFailure(String message) {
//                                Log.i("FFMPEG_TRAC", message);
//                            }
//
//                            @Override
//                            public void onSuccess(String message) {
//                                isFinshied = true;
//                                myTextView.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        myTextView.setText("Time elapsed in milliseconds : " + (System.currentTimeMillis() - startTime) + ", Counter : " + myCounter);
//                                    }
//                                });
//                                playVideoOriginal();
//                                Log.i("FFMPEG_TRAC", message);
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                isFinshied = true;
//                                Log.i("FFMPEG_TRAC", "FINISH");
//                                isFinshied = true;
//
//
//                            }
//                        });
//                    } catch (FFmpegCommandAlreadyRunningException e) {
//                        // Handle if FFmpeg is already running
//                    }
//                }
//
//                @Override
//                public void onFinish() {
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            // Handle if FFmpeg is not supported by device
//        }


    }
    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
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
            }else{

//                myVideoView.setAlpha(1.0f);
            }
        }

    };
    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(v.getWidth() , v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Log.i("FILE_TRAC", "WIDTH :  " + width + ", HEIGHT : " + height);

        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);
        return b;
    }
    public static void  saveBitmap(Bitmap b){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File f = new File(IMAGE_PATH, "screenshot.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            Log.i("FILE_TRAC", "SAVING FILE");
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e) {
            Log.i("FILE_TRAC", e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("FILE_TRAC", e.getMessage());
            e.printStackTrace();
        }
    }
    public  void playVideoOriginal() {
        try {
            Log.i("THREAD_TRAC", "SEEK : " + seek + " And postion is : " + position);
            if(!seek) {
                progressDialog.dismiss();
            }
            myVideoView.setVideoURI(videoURI);
            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    myVideoView.seekTo(position);
                }
            });
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
}
