package com.anewtech.phone.surveyclientfragmentOLD.models.services;
import android.support.annotation.NonNull;
public class Finaire implements Comparable<Finaire> {
    public int id;
    public String message;
    public String answerid;

    public Finaire(int _id, String _message) { this.id = _id; this.message = _message;}
    public Finaire(int _id, String _message, String _answerid) { this.id = _id; this.message = _message;this.answerid = _answerid;}

    @Override
    public int compareTo(@NonNull Finaire f) {
        if( this.id > f.id){
            return 1;
        }else if (this.id < f.id){
            return -1;
        }else{
            return 0;
        }
    }
}
