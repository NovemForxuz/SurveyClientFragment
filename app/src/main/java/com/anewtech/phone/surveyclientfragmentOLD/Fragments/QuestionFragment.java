package com.anewtech.phone.surveyclientfragmentOLD.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anewtech.phone.surveyclientfragmentOLD.FragmentLayout;
import com.anewtech.phone.surveyclientfragmentOLD.R;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static org.greenrobot.eventbus.ThreadMode.MAIN;

public class QuestionFragment extends Fragment{
    private final static boolean LOG_QUESTION_FRAGMENT = true;

    FragmentLayout fragmentLayout;
    OnQuestionPass questionPasser;

    private TextView quetx;
    private TextView totalquetx;
    private EventBus myevent = EventBus.getDefault();;

    private Handler handler = new Handler();


    public static QuestionFragment newInstance(String title) {
        QuestionFragment questionFragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        questionFragment.setArguments(args);
        return questionFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        myevent.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        myevent.unregister(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.fragmentLayout = (FragmentLayout) context;
        }
        questionPasser = (OnQuestionPass) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View rootvew = inflater.inflate(R.layout.fragment_questionaire, parent, false);
        quetx = rootvew.findViewById(R.id.mainQuestion);
        totalquetx = rootvew.findViewById(R.id.mainQuestionCount);
        return rootvew;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) { }

    @Override
    public void onDetach() {
        super.onDetach();
        this.fragmentLayout = null;
    }

    @Subscribe(threadMode = MAIN)
    public void onEvent(CommonEvent event) {
        this.quetx.setText(event.getMessage().question);
        passData(event.getMessage().question);
        this.totalquetx.setText(Integer.toString(event.getMessage().id + 1) + " of " + event.getTotalQuestions());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public interface OnQuestionPass{
        public void onQuestionPass(String question);
    }

    public void passData(final String data){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                questionPasser.onQuestionPass(data);
            }
        },1000);
        toLog("Passdata: "+data);
    }

    public void toLog(String msg) {
        if(LOG_QUESTION_FRAGMENT){
            Logger.d("QuestionFragment: " + msg + "\n\r");
        }
    }

}
