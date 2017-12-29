package com.anewtech.phone.surveyclientfragmentOLD.services;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qihancloud.opensdk.base.BindBaseService;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SpeechManager;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zeusys on 5/12/2017.
 */

public class SpeechService extends BindBaseService implements Runnable{

    private boolean LOG_ON_SPEECH_SERVICE   = true  ;
    private SpeechManager   sm                      ;
    private boolean         isInterrupted   = false ;
    private boolean         nextQuestion    = false ;
    private boolean         isReadingAns    = false ;
    private boolean         isReadingQue    = false ;
    private boolean         isSleeping      = false ;
    private double          questionDelay   = 0     ;
    private double          answerDelay     = 0     ;
    private String          currentQue              ;
    private ArrayList<String> listOfAnswers         ;
    private Thread          thread                  ;

    @Override
    public void onCreate() {
        super.onCreate();
        register(SpeechService.class);
        sm = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toLog("onStartCommand()");

        // called every time a client starts the service
        return super.onStartCommand(intent, flags, startId);
    }

    public class MyBinder extends Binder {

        public SpeechService getService(){
            return SpeechService.this;
        }
    }

    private MyBinder myBinder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        toLog("onBind()");
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        toLog("onUnbind()");
        return true;
    }

    @Override
    public void onDestroy() {
        toLog("onDestroy()");
        if(thread.isAlive())
        {
            isInterrupted = true;
        }
        super.onDestroy();
    }

    @Override
    protected void onMainServiceConnected() {

    }


    @Override
    public void run() {
        toLog("run() started");
        currentQnA:
        while(!isInterrupted) // 1 loop is equivalent to questions and answers read.
        {                     // 20 secs delay before reset
            if(currentQue!=null ){
//                sm.doSleep();
                try {
                    Thread.sleep(1000); // This delay is to let boolean 'isReadingAns' pass through first before qn being read
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sm.startSpeak(currentQue);
                isReadingQue = true;
                isReadingAns = false;
                toLog("Question: "+currentQue);
                toLog("Delay: "+questionDelay+" sec");
                try {
                    Thread.sleep((long)questionDelay * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                isReadingQue = false;
                if(nextQuestion){
                    nextQuestion = false;
                    continue;
                }
                if(listOfAnswers!=null){
                    for(String ans : listOfAnswers){
                        isReadingAns =  true;
                        varDelayAns(ans.length());
                        sm.startSpeak(ans);
                        toLog("Answer: "+ans);
                        try {
                            Thread.sleep((long)answerDelay * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        Log.e("xxx", "[SpeechService] nextQuestion: "+nextQuestion);
                        if(nextQuestion){
                            toLog("Answer loop break");
                            nextQuestion = false;
//                            break;
                            continue currentQnA;
                        }
                    }
                    isReadingAns = false;
                }
                for(int i=0;i<20;i++){
//                    toLog("nextQn2: "+nextQuestion);
                    isSleeping = true;
                    if(!nextQuestion){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        nextQuestion = false;
                        break;
                    }
                }
                isSleeping = false;
            }
        }

        toLog("run() exiting");
    }

    public void readQuestion(String question) {

        if(currentQue != question && (isReadingQue || isReadingAns || isSleeping)){
            nextQuestion = true;
//            toLog("nextQn1: "+nextQuestion);
        }

        currentQue = question;
        wordCount(question);
        sm.stopSpeak();
    }


    public void setNextQuestion(boolean isIt){
        nextQuestion = isIt;
    }

    public void setAnsList(ArrayList<String> answers) {
        listOfAnswers = new ArrayList<>();
        listOfAnswers = answers;
    }

    public void wordCount(String sentence) {
        ArrayList<String> words = new ArrayList<>(Arrays.asList(sentence.split(" ")));
        int count = words.size();
        varDelayQue(count);

    }

    public void varDelayQue(int countW) {
//        toLog("word count: "+countW);
        questionDelay = (countW * 0.2938) + 2;
    }

    public void varDelayAns(int countC) {
//        toLog("word count: "+countC);
        answerDelay = (countC * 0.0626) + 1;
    }

    public void toLog(String msg) {
//        com.orhanobut.logger.Logger.e("SpeechService: " + msg + "\n\r");
        if(LOG_ON_SPEECH_SERVICE){
            Log.e("xxx", "SpeechService: "+msg+"\n\r");
        }
    }

}
