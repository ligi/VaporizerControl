package org.ligi.vaporizercontrol;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class ChangeDialogs {

    public static void showLEDPercentageDialog(Context ctx,final VaporizerCommunicator comm) {
        final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(ctx);
        discreteSeekBar.setMax(100);
        discreteSeekBar.setIndicatorFormatter("%d%%");

        if (comm.getData().ledPercentage != null) {
            discreteSeekBar.setProgress(comm.getData().ledPercentage);
        }

        discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(final DiscreteSeekBar discreteSeekBar, final int i, final boolean b) {
                comm.setLEDBrightness(i);
            }
        });

        new AlertDialog.Builder(ctx).setMessage("Set LED brightness").setView(discreteSeekBar).setPositiveButton("OK", null).show();
    }

    public static void showTemperatureDialog(final Context ctx, final VaporizerCommunicator comm) {
        final DiscreteSeekBar discreteSeekBar = new DiscreteSeekBar(ctx);
        discreteSeekBar.setMin(40);
        discreteSeekBar.setMax(210);
        discreteSeekBar.setIndicatorFormatter("%d°");

        if (comm.getData().setTemperature!=null) {
            discreteSeekBar.setProgress(comm.getData().setTemperature / 10);
        }

        new AlertDialog.Builder(ctx).setView(discreteSeekBar)
                                     .setItems(new CharSequence[]{"foo", "bar"}, null)
                                     .setMessage("Set Temperature")
                                     .setPositiveButton("OK", null)
                                     .show();
    }
}
