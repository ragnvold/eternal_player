package com.example.testtask

import android.net.Uri
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class Presenter(var mView: Activity) : Contract.Presenter {

    private val min = 2
    private val max = 10

    private val maxVolume = 1F
    private val minVolume = 0F
    private var curVolume = 0F

    private var fade = min

    private var curMediaPlayer = 0

    private val longDuration = 5000
    private val mediumDuration = 3000
    private val shortDuration = 1000

    private var musicList: Array<Uri> = Array(2) { Uri.EMPTY }
    private lateinit var timer: ScheduledExecutorService

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

    override fun onPlayPauseBtnWasClicked() {
        if (musicList[0] != Uri.EMPTY && musicList[1] != Uri.EMPTY
        ) {
            when (mView.getMediaPlayerState(0) || mView.getMediaPlayerState(1)) {
                false -> {
                    startMusic()
                }
                true -> {
                    completeMusic()
                }
            }
        } else {
            mView.showSnackBar("Выберите недостающие файлы", mediumDuration)
        }
    }

    private fun startMusic() {
        mView.prepareToMusic(curMediaPlayer)
        mView.enableSeekBar(false)
        mView.showSnackBar("Во время воспроизведения параметры нельзя изменить", longDuration)
        mView.showPause()
    }

    private fun completeMusic() {
        mView.stopMusic()
        mView.showSnackBar("Вы можете выбрать параметры заново", mediumDuration)
        mView.resetMP(0)
        mView.resetMP(1)
        mView.clickableBtn(R.id.pickBtn1, true)
        mView.clickableBtn(R.id.pickBtn2, true)
        mView.enableSeekBar(true)
        musicList[0] = Uri.EMPTY
        musicList[1] = Uri.EMPTY
        mView.showPlay()
    }

    //Download Files

    override fun onGetUri(index: Int) {
        when (index) {
            0 -> {
                mView.setTrack(musicList[index], index)
                mView.setText(R.id.pickBtn1, R.string.file_pick)
                mView.clickableBtn(R.id.pickBtn1, false)
            }
            1 -> {
                mView.setTrack(musicList[index], index)
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
            shortDuration
        )
    }

    //CrossFade

    override fun onCrossFadeChange(value: Int) {
        if (!mView.getMediaPlayerState(curMediaPlayer)) {
            fade = min + value
        }
    }

    private fun crossFade(complete: Int) {
        timer = Executors.newScheduledThreadPool(1)
        when (complete) {
            0 -> {
                timer.scheduleWithFixedDelay({ volumeDown() }, 0, 100, TimeUnit.MILLISECONDS)
            }
            1 -> {
                timer.scheduleWithFixedDelay({ volumeUp() }, 0, 100, TimeUnit.MILLISECONDS)
            }
        }
    }

    private fun volumeDown() {
        curVolume -= 100 / (fade * 1000).toFloat()
        mView.changeMediaPlayerVolume(maxVolume - curVolume, 0)
        mView.changeMediaPlayerVolume(curVolume, 1)
        if (curVolume <= minVolume) {
            timer.shutdown()
            curVolume = minVolume
        }
    }

    private fun volumeUp() {
        curVolume += 100 / (fade * 1000).toFloat()
        mView.changeMediaPlayerVolume(maxVolume - curVolume, 0)
        mView.changeMediaPlayerVolume(curVolume, 1)
        if (curVolume >= maxVolume) {
            timer.shutdown()
            curVolume = maxVolume
        }
    }

    //MediaPlayer

    override fun prepareTrack() {
        mView.showPause()
        mView.startMusic(curMediaPlayer)
        waitToStartNextTrack(
            mView.getTrackDuration(curMediaPlayer) - mView.getCurTime(curMediaPlayer) - fade * 1000
        )
    }

    //Timer to start next track

    private fun waitToStartNextTrack(time: Int) {
        Executors.newSingleThreadScheduledExecutor()
            .schedule({
                when (curMediaPlayer) {
                    0 -> {
                        curMediaPlayer++
                    }
                    1 -> {
                        curMediaPlayer--
                    }
                }
                mView.resetMP(curMediaPlayer)
                mView.setTrack(musicList[curMediaPlayer], curMediaPlayer)
                mView.prepareToMusic(curMediaPlayer)
                crossFade(curMediaPlayer)
            }, time.toLong(), TimeUnit.MILLISECONDS)
    }
}