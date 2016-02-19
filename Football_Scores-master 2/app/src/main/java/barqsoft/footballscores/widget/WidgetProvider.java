package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by brandnewpeterson on 2/17/16.
 */
public class WidgetProvider extends AppWidgetProvider {
    public static final String ACTION_TOAST = "barqsoft.footballscores.widget.ACTION_TOAST";
    public static final String EXTRA_STRING = "barqsoft.footballscores.widget.EXTRA_STRING";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        //System.out.println("Intent received: " + intent.getAction());

        if (intent != null) {
            if (intent.hasExtra(EXTRA_STRING)){
                String item = intent.getExtras().getString(EXTRA_STRING);
                //Toast.makeText(context, item, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int widgetId : appWidgetIds) {
            RemoteViews mView = initViews(context, appWidgetManager, widgetId);

            Intent startActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mView.setPendingIntentTemplate(R.id.widgetCollectionList, startActivityPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, mView);
        }
    }

    private RemoteViews initViews(Context context,
                                  AppWidgetManager widgetManager, int widgetId) {

        RemoteViews mView = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        mView.setRemoteAdapter(widgetId, R.id.widgetCollectionList, intent);

        return mView;
    }
}
