package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.format.Time;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import android.appwidget.AppWidgetProvider;


import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by brandnewpeterson on 2/17/16.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_DATE = 1;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_HOME_ID = 10;
    public static final int COL_AWAY_ID = 11;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;

    private Cursor[] mCursors = new Cursor[7];

    List mCollections = new ArrayList();

    Context mContext = null;

    public WidgetDataProvider(Context context, Intent intent) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        //initData();


    }

    @Override
    public void onDataSetChanged() {
        initData();


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews mView = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);

        WidgetRow row = (WidgetRow) mCollections.get(position);

        mView.setTextViewText(R.id.widget_home_name, row.homeTeamName);
        mView.setBitmap(R.id.widget_home_crest, "setImageBitmap", Utilies.getTeamCrestByTeamId(mContext, row.homeTeamId));
        mView.setTextViewText(R.id.widget_away_name, row.awayTeamName);
        mView.setBitmap(R.id.widget_away_crest, "setImageBitmap", Utilies.getTeamCrestByTeamId(mContext, row.awayTeamId));
        mView.setTextViewText(R.id.widget_time_and_date, row.time + "/" + row.date);
        if (!row.homeTeamScore.contains("-") && !row.awayTeamScore.contains("-")){
            mView.setTextViewText(R.id.widget_score, row.homeTeamScore + "-" + row.awayTeamScore);
        }else{
            mView.setTextViewText(R.id.widget_score, "TBA");
        }

        //Handle Clicks -- For now just start app, as there are no detail views for particular matches.
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(WidgetProvider.EXTRA_STRING, position);
        mView.setOnClickFillInIntent(R.id.widget_list_item_root, fillInIntent);

        //mView.setTextColor(android.R.id.text1, Color.BLACK);
        return mView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void initData() {
        queryDB();
        //System.out.println("Cursor len: " + mCursor.getCount());


        //Try again for up to 7 days out if the DB return nothing for a given day, so widget has some data to show
        /*
        int offset = 1;
        if (mCursor != null){
            while (mCursor.getCount()==0 && offset < 7){
                if (mCursor != null) {
                    mCursor.close();
                    queryDB(offset);
                    offset += 1;
                    //System.out.println("Cursor len: " + mCursor.getCount());
                }
            }
        }*/

        mCollections.clear();
        for (Cursor mCursor : mCursors) {
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    //System.out.println("Widget Home Team: " + mCursor.getString(COL_HOME));
                    mCollections.add(new WidgetRow(
                                    mCursor.getString(COL_HOME),
                                    mCursor.getString(COL_HOME_ID),
                                    mCursor.getString(COL_HOME_GOALS),
                                    mCursor.getString(COL_AWAY),
                                    mCursor.getString(COL_AWAY_ID),
                                    mCursor.getString(COL_AWAY_GOALS),
                                    mCursor.getString(COL_MATCHDAY),
                                    mCursor.getString(COL_MATCHTIME)
                            )
                    );
                }
            }

            if (mCursor != null){
                mCursor.close();
            }
        }


    }

    //Get the 3 days prior today, today, and the 3 days after today from the API.
    private void queryDB(){

        int day = 1000 * 60 * 60 * 24;

        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

        int c = 0;
        for (int i=-3; i<4; i++){
            Date date = new Date(System.currentTimeMillis() + i*day);
            String dayString = mformat.format(date);

            final long token = Binder.clearCallingIdentity();
            try {
                mCursors[c] = mContext.getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), null, null, new String[]{dayString}, null);
            } finally {
                Binder.restoreCallingIdentity(token);
            }

            c += 1;

        }



    }

    private class WidgetRow {
        public String homeTeamName = null;
        public String homeTeamId = null;
        public String homeTeamScore = null;
        public String awayTeamName = null;
        public String awayTeamId = null;
        public String awayTeamScore = null;
        public String date = null;
        public String time = null;

        public WidgetRow(String homeTeamName, String homeTeamId, String homeTeamScore, String awayTeamName, String awayTeamId, String awayTeamScore, String date, String time) {
            this.homeTeamName = homeTeamName;
            this.homeTeamId = homeTeamId;
            this.homeTeamScore = homeTeamScore;
            this.awayTeamName = awayTeamName;
            this.awayTeamId = awayTeamId;
            this.awayTeamScore = awayTeamScore;
            this.date = date;
            this.time = time;
        }
    }
}
