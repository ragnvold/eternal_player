package com.example.testtask

import android.net.Uri

interface Contract {

    interface View {
        fun showPlay()
        fun showPause()
        fun seekToCurTime(curTime: Int)
        fun selectAudio(pick_num: Int)
        fun setSeekMax(value: Int)
        fun showSnackBar(message: String, duration: Int)
        fun getCurTime(): Int
        fun prepareToMusic()
        fun pauseMusic()
        fun startMusic()
        fun clickableBtn(id: Int, boolean: Boolean)
        fun resetMP()
        fun getMediaPlayerState(): Boolean
        fun setNextTrack(uri: Uri)
        fun setText(id: Int, textID: Int)
    }

    interface Presenter {
        fun prepareForWork()
        fun onPickBtn1WasClicked()
        fun onPickBtn2WasClicked()
        fun onPlayPauseBtnWasClicked(boolean: Boolean)
        fun onStopSeekBarTracking()
        fun onGetUri(index: Int)
        fun onCrossFadeChange(value: Int)
        fun addTrackToMusicList(index: Int, uri: Uri)
        fun completeTrack()
        fun prepareTrack()
    }
}