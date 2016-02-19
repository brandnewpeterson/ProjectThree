package barqsoft.footballscores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{

    public static String getLeague(Context context, String league_num)
    {
        String[] league_nums = context.getResources().getStringArray(R.array.leagues_codes);
        String[] league_names = context.getResources().getStringArray(R.array.leagues_names);
        String result = null;
        for (int i = 0; i < league_nums.length; i++){
            if (league_num.contains(league_nums[i])){
                result = league_names[i];
            }
        }

        if (result==null){
            result = R.string.no_league_name_error + league_num + ".";
        }

        return result;

    }
    public static String getMatchDay(Context context, int match_day,int league_num)
    {
        String[] league_nums = context.getResources().getStringArray(R.array.leagues_codes);

        if(league_num == Integer.valueOf(league_nums[2]))
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }
    //It's lame to provide only a few crests. Used API to pre-load 250+ of them.
    public static Bitmap getTeamCrestByTeamId (Context context, String id)
    {

        InputStream ims = null;
        try {
            ims = context.getAssets().open(id + ".png");
        } catch (IOException e) {
            e.printStackTrace();
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.no_icon);
        }
        Bitmap b = BitmapFactory.decodeStream(ims);
        return b;
        /*
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
        */
    }
}
