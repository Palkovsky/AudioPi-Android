package com.example.andrzej.audiocontroller.utils;

public class Communicator {

    //Messages
    public static final int LOCAL_PLAYLIST_POSITION_CHANGED = 0x00;
    public static final int LOCAL_PLAYLIST_ITEM_REMOVED = 0x01;
    public static final int LOCAL_PLAYLIST_REMOVED = 0x02;

    public interface OnCustomStateListener {
        void onMessage();
        void onMessage(Object data);
    }

    private static Communicator mInstance;
    private OnCustomStateListener mListener;
    private int message;

    private Communicator() {}

    public static Communicator getInstance() {
        if(mInstance == null) {
            mInstance = new Communicator();
        }
        return mInstance;
    }

    public void setListener(OnCustomStateListener listener) {
        mListener = listener;
    }

    public void sendMessage(int code) {
        if(mListener != null) {
            message = code;
            mListener.onMessage();
        }
    }

    public void sendMessage(int code, Object data) {
        if(mListener != null) {
            message = code;
            mListener.onMessage(data);
        }
    }

    public int getMessage() {
        return message;
    }

}