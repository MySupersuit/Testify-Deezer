package com.tom.deezergame.utils

import android.media.MediaPlayer

class CustomMediaPlayer : MediaPlayer() {
    var datasource: String? = null
    override fun setDataSource(path: String?) {
        super.setDataSource(path)
        datasource = path
    }
}