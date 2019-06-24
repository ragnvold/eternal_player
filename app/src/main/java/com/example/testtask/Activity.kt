package com.example.testtask

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.lang.Exception

class Activity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener, View.OnClickListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
    Contract.View {

    private var mPresenter: Presenter = Presenter(this)
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    private lateinit var view: View
    private lateinit var snackBar: Snackbar

    //Activity State

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBar = findViewById(R.id.seekBar)
        pickBtn1 = findViewById(R.id.pickBtn1)
        pickBtn2 = findViewById(R.id.pickBtn2)
        playPauseBtn = findViewById(R.id.playPauseBtn)
        view = findViewById(R.id.main_activity)

        seekBar.setOnSeekBarChangeListener(this)
        pickBtn1.setOnClickListener(this)
        pickBtn2.setOnClickListener(this)
        playPauseBtn.setOnClickListener(this)
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setOnPreparedListener(this)
        mediaPlayer.setOnSeekCompleteListener(this)

        mPresenter.prepareForWork()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMP()
    }

    //SnackBar

    override fun showSnackBar(message: String, duration: Int) {
        snackBar = Snackbar.make(
            view,
            message,
            duration
        )
        snackBar.show()
    }

    //SeekBar

    override fun setSeekMax(value: Int) {
        seekBar.max = value
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        mPresenter.onCrossFadeChange(p1)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(p0: SeekBar?) {
        mPresenter.onStopSeekBarTracking()
    }

    //Buttons

    override fun onClick(p0: View?) {
        if (p0 != null) {
            when (p0.id) {
                R.id.pickBtn1 -> {
                    mPresenter.onPickBtn1WasClicked()
                }
                R.id.pickBtn2 -> {
                    mPresenter.onPickBtn2WasClicked()
                }
                R.id.playPauseBtn -> {
                    mPresenter.onPlayPauseBtnWasClicked(mediaPlayer.isPlaying)
                }
            }
        }
    }

    override fun showPlay() {
        playPauseBtn.setImageResource(R.mipmap.ic_action_pause)
    }

    override fun showPause() {
        playPauseBtn.setImageResource(R.mipmap.ic_action_play)
    }

    override fun clickableBtn(id: Int, boolean: Boolean) {
        when (id) {
            R.id.pickBtn1 -> {
                pickBtn1.isEnabled = boolean
            }
            R.id.pickBtn2 -> {
                pickBtn2.isEnabled = boolean
            }
            R.id.playPauseBtn -> {
                playPauseBtn.isClickable = boolean
            }
        }
    }

    override fun setText(id: Int, textID: Int) {
        when(id) {
            R.id.pickBtn1 -> { pickBtn1.text = getString(textID) }
            R.id.pickBtn2 -> { pickBtn2.text = getString(textID) }
        }
    }

    //MediaPlayer

    override fun onCompletion(p0: MediaPlayer?) {
        if (p0 == mediaPlayer) {
            mPresenter.completeTrack()
        }
    }

    override fun onPrepared(p0: MediaPlayer?) {
        if (p0 == mediaPlayer) {
            mPresenter.prepareTrack()
        }
    }

    override fun onSeekComplete(p0: MediaPlayer?) {
        p0?.start()
    }

    private fun releaseMP() {
        try {
            mediaPlayer.release()
        } catch (e: Exception) {
            Log.i("EXCEPT", e.message!!)
        }
    }

    override fun getCurTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun seekToCurTime(curTime: Int) {
        mediaPlayer.seekTo(curTime)
    }

    override fun pauseMusic() {
        mediaPlayer.pause()
    }

    override fun startMusic() {
        mediaPlayer.start()
    }

    override fun prepareToMusic() {
        mediaPlayer.prepareAsync()
    }

    override fun resetMP() {
        mediaPlayer.reset()
    }

    override fun getMediaPlayerState(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun setNextTrack(uri: Uri) {
        mediaPlayer.setDataSource(this, uri)
    }

    //Pick Files

    override fun selectAudio(pick_num: Int) {
        val audioIntent = Intent(Intent.ACTION_GET_CONTENT)
        audioIntent.type = "audio/*"
        startActivityForResult(Intent.createChooser(audioIntent, "Select audio"), pick_num)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            val uri = data.data
            if (uri != Uri.EMPTY
                && uri != null
            ) {
                mPresenter.addTrackToMusicList(requestCode, uri)
                mPresenter.onGetUri(requestCode)
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var seekBar: SeekBar
        @SuppressLint("StaticFieldLeak")
        private lateinit var pickBtn1: Button
        @SuppressLint("StaticFieldLeak")
        private lateinit var pickBtn2: Button
        @SuppressLint("StaticFieldLeak")
        private lateinit var playPauseBtn: ImageButton
    }
}
