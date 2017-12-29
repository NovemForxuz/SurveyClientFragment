package com.anewtech.phone.surveyclientfragmentOLD.services;

import com.anewtech.phone.surveyclientfragmentOLD.models.reports.SurveyAnswerReport;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zeusys on 1/12/2017.
 */

public class SurveyReportService {
    private FirebaseFirestore db;
    private String COLLECTION_NAME = "surveyfeedbacks";

    public SurveyReportService(){
        db = FirebaseFirestore.getInstance();
    }

    public void reportToFirebase(SurveyAnswerReport msar){
        if ( msar != null){
            timeStamp(msar);
            db.collection(COLLECTION_NAME).add(msar);
        }
    }

    private void timeStamp(SurveyAnswerReport msar){
        if ( msar != null ) {
            Date currentTime = Calendar.getInstance().getTime();
            msar.createdat =  currentTime;
            msar.updatedat = currentTime;
        }

    }
}
