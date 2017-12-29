package com.anewtech.phone.surveyclientfragmentOLD.Fragments;

import com.anewtech.phone.surveyclientfragmentOLD.models.services.Questionaire;

/**
 * Created by phone on 15/11/2017.
 */

public class CommonEvent {
    private final Questionaire message;
    private int totalQuestions;

    public CommonEvent(Questionaire message,Integer totalQuestions){
        this.message = message; this.totalQuestions = totalQuestions;
    }
    public Questionaire getMessage() {
        return message;
    }
    public int getTotalQuestions(){
        return totalQuestions;
    }
}
