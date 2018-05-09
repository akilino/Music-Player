package com.example.akilino.player;

/**
 * Created by akili on 15/05/2016.
 */
public class Utilities {

    //Converts milliseconds into a more readable way
    public String millisecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString;

        int hours = (int) (milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        if(hours > 0){
            finalTimerString = hours + ":";
        }

        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    //converts the progress percentage, through currentDuration and totalDuration values
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int)(totalDuration / 1000);

        percentage = (((double)currentSeconds)/totalSeconds) * 100;

        return percentage.intValue();
    }

    //Calculates the progress to reach total duration
    public int progressToTimer(int progress, int totalDuration){
        int currentDuration;
        totalDuration = totalDuration / 1000;
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);

        return currentDuration * 1000;
    }
}
