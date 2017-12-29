package com.anewtech.phone.surveyclientfragmentOLD.services;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import com.anewtech.phone.surveyclientfragmentOLD.models.data.*;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SurveyDataService {
    private static final String SURVEY_DATA_SERVICE_ID = "SurveyDataService";
    private static final boolean LOG_SURVEY_DATA_SERVICE = true;


    private String jsonData;
    private Gson g;
    private SurveyDataModel sm;

    private List<String> SecondComing;

    public String getId() {
        return SURVEY_DATA_SERVICE_ID;
    }

    public SurveyDataService() {
        g = new Gson();
        this.toLog(this.getClass().toString());
    }

    public String getJson() {
        return this.jsonData;
    }

    public void setJson(String requiredjson){
        sm = g.fromJson(requiredjson,SurveyDataModel.class);
    }

    public HashMap<String,String> getToken() {
        HashMap<String,String> local = new HashMap<>();
        local.put("version",sm.version);
        local.put("title", sm.title);
        local.put("datetime", sm.datetime);
        local.put("uploadedby",sm.uploadedby);
        local.put("access",sm.access);
        return local;
    }

    public Template getTemplate(){
        return sm.template;
    }

    public ArrayList<Questionaire> getFirstLayerQuestionaire(){ //First Layered Questions
        ArrayList<Questionaire> local = new ArrayList<>();      //local variable to store questionaire
        this.SecondComing = new ArrayList<>();                 //clear global storage of second questionaire id
        for (Question que: sm.layersquestions) {
            Questionaire localnaire = new Questionaire();       //new questionaire for every questions.
            if(!que.answerid.isEmpty()){ continue;}             //check answerid to make sure that question's from first layer
            this.toLog(que.message);
            localnaire.questionid = que.id;
            localnaire.id = que.seq;
            localnaire.question = que.message;                  //Actual Question
            List<Answernaire> localans = new ArrayList<>();     //multiple answers for single question
            for (Answer ans: sm.layersanswers) {                //Loop answers
                if(ans.questionid.equals(que.id)) {                 //check questionid is equal in answer
                    localans.add( new Answernaire(Integer.parseInt(ans.seq), ans.message, ans.id, ans.sub)); // store seq and answer
                    if(ans.sub == true){
                        this.SecondComing.add(ans.id);              // reference id to second questionaire
                    }
                    this.toLog(ans.message);
                }
            }
            Collections.sort(localans);
            localnaire.answers = localans;
            local.add(localnaire);
            this.toLog("------------------------------");
        }
        Collections.sort(local);
        return local;
    }

    public ArrayList<Questionaire> getSubQuestionaire(String ansid) {
        ArrayList<Questionaire> local = new ArrayList<>();
        for (Question que: sm.layersquestions) {
            Questionaire localnaire = new Questionaire();       //new questionaire for every questions.
            if(que.answerid.isEmpty()){ continue;}             //check answerid to make sure that question's from second layer
           // for (String answerid : this.SecondComing ) {
              //  if( que.answerid.equals(answerid)) {
                    if(que.answerid.equals(ansid)) {
                        this.toLog(que.message);
                        localnaire.questionid = que.id;
                        localnaire.id = que.seq;
                        localnaire.question = que.message;         //Actual Question
                        localnaire.answerid = que.answerid;
                        List<Answernaire> localans = new ArrayList<>();     //multiple answers for single question
                        for (Answer ans : sm.layersanswers) {                //Loop answers
                            if (ans.questionid.equals(que.id)) {                 //check questionid is equal in answer
                                localans.add(new Answernaire(Integer.parseInt(ans.seq), ans.message, ans.id, ans.sub)); // store seq and answer
                                this.toLog(ans.message);
                            }
                        }
                        Collections.sort(localans);
                        localnaire.answers = localans;
                        local.add(localnaire);
                        this.toLog("------------------------------");
                    }
             //   }
           // }
        }
        Collections.sort(local);
        return local;
    }

    public ArrayList<Finaire> getThankYou() {
        ArrayList<Finaire> local = new ArrayList<>();
        for(FinalMessage fm : sm.layerslastmessages){
            if(fm.oneforall && fm.answerid.isEmpty()){
                local.add(new Finaire(Integer.parseInt(fm.seq), fm.message, fm.answerid));
            }
        }
        Collections.sort(local);
        return local;
    }

    private void toLog(String msg){
        if(LOG_SURVEY_DATA_SERVICE){
            Logger.d("Constructor " + SURVEY_DATA_SERVICE_ID + " : " + msg);
        }
    }
}

