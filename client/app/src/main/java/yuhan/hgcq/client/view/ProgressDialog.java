package yuhan.hgcq.client.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.PulseRing;

import yuhan.hgcq.client.R;

public class ProgressDialog extends Dialog {
    public ProgressDialog (@NonNull Context context){
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.spin_kit);
        Sprite pulseRing = new PulseRing();
        progressBar.setIndeterminateDrawable(pulseRing);
    }
}
