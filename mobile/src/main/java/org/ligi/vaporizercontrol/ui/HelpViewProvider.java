package org.ligi.vaporizercontrol.ui;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.ligi.vaporizercontrol.R;
import org.ligi.vaporizercontrol.model.VaporizerData;
import org.ligi.vaporizercontrol.wiring.App;

public class HelpViewProvider {

    @Bind(R.id.serial)
    TextView serial;

    @Bind(R.id.model)
    TextView model;

    @Bind(R.id.version)
    TextView version;

    @Bind(R.id.hours)
    TextView hours;

    @Bind(R.id.help)
    TextView help;

    public View getView(Context ctx) {

        final VaporizerData data = ((App) ctx.getApplicationContext()).getVaporizerCommunicator().getData();

        final View view = LayoutInflater.from(ctx).inflate(R.layout.help, null);

        ButterKnife.bind(this, view);

        serial.setText(data.serial);
        model.setText(data.model);
        hours.setText(String.valueOf(data.hoursOfOperation));
        version.setText(data.version);

        help.setText(Html.fromHtml(
                "Find out more about this app and the source-code <a href='http://github.com/ligi/VaporizerControl'>on GitHub</a><br/><br/> Happy vaping!-) "));
        help.setMovementMethod(new LinkMovementMethod());

        return view;

    }
}
