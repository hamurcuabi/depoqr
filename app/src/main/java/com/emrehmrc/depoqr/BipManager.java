package com.emrehmrc.depoqr;

import android.media.AudioManager;
import android.media.ToneGenerator;

/**
 * Created by Emre Hmrc on 5.03.2018.
 */

public class BipManager {

  private ToneGenerator toneG;

    public BipManager(ToneGenerator toneG) {
        toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        this.toneG = toneG;
    }
}
