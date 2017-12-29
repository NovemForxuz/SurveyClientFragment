package com.anewtech.phone.surveyclientfragmentOLD.services;

import com.anewtech.phone.surveyclientfragmentOLD.Fragments.CommonEvent;
import com.anewtech.phone.surveyclientfragmentOLD.models.reports.SurveyAnswerReport;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.Answernaire;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.Finaire;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.Questionaire;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phone on 16/11/2017.
 */

public class SurveyBervice {


    private SurveyDataService sds;
    private SurveyReportService srs;
    private ArrayList<Questionaire> thisquestionaire = new ArrayList<>();
    private ArrayList<Questionaire> thissubquestionaire = new ArrayList<>();
    private ArrayList<Finaire> thisthankyou = new ArrayList<>();
    private List<Answernaire> an = new ArrayList<>();
    private EventBus myevent = EventBus.getDefault();;

    private int currentIndex = -1;
    private boolean LOG_ON_BERVICE = true;
    private Questionaire myque = new Questionaire();

    public SurveyBervice(){
        this.sds = new SurveyDataService();
        this.srs = new SurveyReportService();
    }

    public void loadJsonData(String myjson){
        this.sds.setJson(myjson);
    }

    public int totalCount() {
        if ( this.thisquestionaire.size() <= 0 ) {
            return 0;
        }
        return thisquestionaire.size();
    }

    public void doFirst(){
        if( currentIndex < 0 ) {
            this.thisquestionaire = this.sds.getFirstLayerQuestionaire();
            this.currentIndex = 0;
            this.myque = this.thisquestionaire.get(currentIndex);
            //myevent.post(new CommonEvent(myque, totalCount()));
            this.currentIndex++;
        }
    }

    public void nextQuestion(){
        if( this.currentIndex >= thisquestionaire.size() ){
            this.currentIndex = 0;
        }
        myque = thisquestionaire.get(currentIndex);
        this.currentIndex++;
        //myevent.post(new CommonEvent(myque,totalCount()));
    }

    public String returnQuestion() {
        return myque.question;
    }

    public String returnQustionId() {
        return myque.questionid;
    }

    public List<Answernaire> returnAnswer() {
        return myque.answers;
    }

    public void currentQuestion(){
        myevent.post(new CommonEvent(myque,totalCount()));
    }

    public void restartQuestion() {
        this.currentIndex = -1;
        doFirst();
        currentQuestion();
    }

    public boolean isFirstQuestion() {
        if(this.currentIndex == 0){
            return true;
        }
        return false;
    }

//    public List<Answernaire> test(){
//        return thisquestionaire.get(currentIndex).answers;
//    }

    public ArrayList<Questionaire> doSub(String ansid) {
        thissubquestionaire = this.sds.getSubQuestionaire(ansid);
//        for(Questionaire q : thissubquestionaire){
//            if(!q.answerid.contains(id)) {
//                thissubquestionaire.remove(q);
//            }
//        }
        return thissubquestionaire;
    }

    public ArrayList<Finaire> doThankYou(){
        thisthankyou = this.sds.getThankYou();
        return thisthankyou;
    }

    public boolean isLastQuestion() {
        if(this.currentIndex == thisquestionaire.size()){
            return true;
        }
        return false;
    }

    public void doFeedback (SurveyAnswerReport sar){
        this.srs.reportToFirebase(sar);
    }

    private void toLog(String msg){
        if(LOG_ON_BERVICE){
            Logger.e("Survey(sBervice) :" + msg);
        }
    }

}
