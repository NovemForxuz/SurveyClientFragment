package com.anewtech.phone.surveyclientfragmentOLD.models.services;
import android.support.annotation.NonNull;
import java.util.List;
public class Questionaire implements Comparable<Questionaire>{
    public String questionid; //index
    public int id; //postion
    public String question;
    public String answerid;
    public List<Answernaire> answers;

    @Override
    public int compareTo(@NonNull Questionaire f) {
        if( this.id > f.id ) {
            return 1;
        }else if ( this.id < f.id ) {
            return -1;
        }else {
            return 0;
        }
    }
}
