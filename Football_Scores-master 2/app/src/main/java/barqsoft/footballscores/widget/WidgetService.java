package barqsoft.footballscores.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by brandnewpeterson on 2/17/16.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        WidgetDataProvider dataProvider = new WidgetDataProvider(
                getApplicationContext(), intent);
        return dataProvider;



    }
}
