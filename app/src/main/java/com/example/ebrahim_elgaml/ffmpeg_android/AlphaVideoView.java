package com.example.ebrahim_elgaml.ffmpeg_android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class AlphaVideoView extends VideoView
{

    private IVideoViewActionListener mVideoViewListener;
    private boolean mIsOnPauseMode = false;

    public interface IVideoViewActionListener
    {
        void onPause();
        void onResume();
        void onTimeBarSeekChanged(int currentTime);
    }

    public void setVideoViewListener(IVideoViewActionListener listener)
    {
        mVideoViewListener = listener;
    }

    @Override
    public void pause()
    {
        super.pause();

        if (mVideoViewListener != null)
        {
            mVideoViewListener.onPause();
        }

        mIsOnPauseMode = true;
    }


    @Override
    public void start()
    {
        super.start();

        if (mIsOnPauseMode)
        {
            if (mVideoViewListener != null)
            {
                mVideoViewListener.onResume();
            }

            mIsOnPauseMode = false;
        }
    }

    @Override
    public void seekTo(int msec)
    {
        super.seekTo(msec);
//        super.start();

        if (mVideoViewListener != null)
        {
            mVideoViewListener.onTimeBarSeekChanged(msec);
        }
    }

    public AlphaVideoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AlphaVideoView(Context context)
    {
        super(context);
    }

    public AlphaVideoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
}
