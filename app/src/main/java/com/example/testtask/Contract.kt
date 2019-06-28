package com.example.testtask

import android.net.Uri

interface Contract {

    interface View {
        fun showPlay()
        fun showPause()
        fun selectAudio(pick_num: Int)
        fun setSeekMax(value: Int)
        fun showSnackBar(message: String, duration: Int)
        fun getCurTime(index: Int): Int
        fun prepareToMusic(index: Int)
        fun stopMusic()
        fun startMusic(index: Int)
        fun clickableBtn(id: Int, boolean: Boolean)
        fun resetMP(index: Int)
        fun getMediaPlayerState(index: Int): Boolean
        fun setTrack(uri: Uri, index: Int)
        fun setText(id: Int, textID: Int)
        fun changeMediaPlayerVolume(volume: Float, index: Int)
        fun getTrackDuration(index: Int): Int
        fun enableSeekBar(boolean: Boolean)
    }

    interface Presenter {
        fun prepareForWork()
        fun onPickBtn1WasClicked()
        fun onPickBtn2WasClicked()
        fun onPlayPauseBtnWasClicked()
        fun onStopSeekBarTracking()
        fun onGetUri(index: Int)
        fun onCrossFadeChange(value: Int)
        fun addTrackToMusicList(index: Int, uri: Uri)
        fun prepareTrack()
    }
}