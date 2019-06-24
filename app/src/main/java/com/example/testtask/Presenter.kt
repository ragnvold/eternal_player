package com.example.testtask

import android.net.Uri
import java.util.*
import kotlin.concurrent.timerTask

class Presenter(var mView: Activity) : Contract.Presenter {

    private val min: Int = 2
    private val max: Int = 10
    private var fade: Int = min
    private var mCompleted: Int = 0
    private var curMusicTime: Int = 0

    private var musicList: Array<Uri> = Array(2) { Uri.EMPTY }

    override fun prepareForWork() {
        mView.setSeekMax(max - min)
    }

    //Buttons

    override fun onPickBtn1WasClicked() {
        mView.selectAudio(0)
    }

    override fun onPickBtn2WasClicked() {
        mView.selectAudio(1)
    }

    override fun onPlayPauseBtnWasClicked(boolean: Boolean) {
        if (musicList[0] != Uri.EMPTY
            && musicList[1] != Uri.EMPTY
        ) {
            when (boolean) {
                false -> {
                    if (curMusicTime > 0) {
                        mView.seekToCurTime(curMusicTime)
                    } else {
                        mView.prepareToMusic()
                    }
                    mView.showPlay()
                }
                true -> {
                    mView.pauseMusic()
                    if (!mView.getMediaPlayerState()) {
                        curMusicTime = mView.getCurTime()
                    }
                    mView.showPause()
                }
            }
        } else {
            mView.showSnackBar("Выберите недостающие файлы", 2000)
        }
    }

    //Download Files

    override fun onGetUri(index: Int) {
        when (index) {
            0 -> {
                mView.setNextTrack(musicList[mCompleted++])
                mView.setText(R.id.pickBtn1, R.string.file_pick)
                mView.clickableBtn(R.id.pickBtn1, false)
            }
            1 -> {
                mView.setText(R.id.pickBtn2, R.string.file_pick)
                mView.clickableBtn(R.id.pickBtn2, false)
            }
        }
    }

    override fun addTrackToMusicList(index: Int, uri: Uri) {
        musicList[index] = uri
    }

    //SeekBar

    override fun onStopSeekBarTracking() {
        mView.showSnackBar(
            "CrossFade = $fade seconds",
            1000
        )
    }

    //CrossFade

    override fun onCrossFadeChange(value: Int) {
        fade = min + value
    }

    private fun crossFade(fade: Int) {
        Timer().schedule(timerTask { mView.prepareToMusic() }, fade.toLong() * 1000)
    }

    //MediaPlayer

    override fun completeTrack() {
        mView.resetMP()
        when (mCompleted) {
            0 -> {
                mView.setNextTrack(musicList[mCompleted++])
            }
            1 -> {
                mView.setNextTrack(musicList[mCompleted--])
            }
        }
        mView.showPause()
        mView.clickableBtn(R.id.playPauseBtn, false)
        crossFade(fade)
    }

    override fun prepareTrack() {
        mView.clickableBtn(R.id.playPauseBtn, true)
        mView.showPlay()
        mView.startMusic()
    }
}