package com.anewtech.phone.surveyclientfragmentOLD.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.anewtech.phone.surveyclientfragmentOLD.FragmentLayout;
import com.anewtech.phone.surveyclientfragmentOLD.R;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.Answernaire;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class AnswersFragment extends Fragment implements FragmentLayout.SendDataToFragment{
    private final static boolean LOG_ANSWERS_FRAGMENT = false;

    private FragmentLayout fragmentLayout;
    private GridView gv;
    private EventBus myevent = EventBus.getDefault();
    private List<Answernaire> localAnswers = new ArrayList<>();

    private String[] feedbackNames = {
            "Excellent",
            "Very Good",
            "Good",
            "Bad",
            "Poor"

    };

    private int icon = R.drawable.ic_sentiment_satisfied_black_48dp;

    private int[] icons = {
            R.drawable.ic_sentiment_very_satisfied_black_48dp,
            R.drawable.ic_sentiment_satisfied_black_48dp,
            R.drawable.ic_sentiment_neutral_black_48dp,
            R.drawable.ic_sentiment_dissatisfied_black_48dp,
            R.drawable.ic_sentiment_very_dissatisfied_black_48dp,

    };


    public static AnswersFragment newInstance(String title) {
        AnswersFragment answerFragment = new AnswersFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        answerFragment.setArguments(args);
        return answerFragment;
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_answernaire, parent,false);
        gv = rootview.findViewById(R.id.fragment_answernaire);

//        gv.setNumColumns(5);
//        gv.setMinimumHeight(100);
//        CustomButtonAdapter cba = new CustomButtonAdapter(this.getContext(),feedbackNames,icons);
//        gv.setAdapter(cba);
//        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //Toast.makeText(fragmentLayout, Integer.toString(i), Toast.LENGTH_SHORT).show();
//                try{
//                    ((FragmentToActivityInterface) fragmentLayout).OnItemPickedInFragment(i);
//                }catch (ClassCastException cce){
//
//                }
//            }
//        });
        return rootview;
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CommonEvent event) {
        //Toast.makeText(fragmentLayout, event.getMessage().question, Toast.LENGTH_SHORT).show();
        //toLog(event.getMessage().question);
        final CommonEvent mevent = event;

        localAnswers = mevent.getMessage().answers;

        gv.setNumColumns(event.getMessage().answers.size());
        CustomButtonAdapter cba = new CustomButtonAdapter(this.getContext(),mevent.getMessage().answers,icon);
        gv.setAdapter(cba);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(fragmentLayout, Integer.toString(i), Toast.LENGTH_SHORT).show();
                try{
                    ((FragmentToActivityInterface) fragmentLayout).OnItemPickedInFragment(i, mevent.getMessage().answers.get(i));

                }catch (ClassCastException cce){

                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.fragmentLayout = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void toLog(String msg) {
        if(LOG_ANSWERS_FRAGMENT){
            Logger.e("AnswerFragment: " + msg + "\n\r");
        }
    }

    @Override   /* Data from FragmentLayout is passed here to perform click action programmatically */
    public void passDataArrayList(ArrayList<String> list, String inputSpeech) {
        Toast.makeText(getContext(),""+inputSpeech,Toast.LENGTH_SHORT).show();
        answerLoop:
        for (int i = 0; i < list.size(); i++) {
            toLog("list.get(i): "+list.get(i));
            if (list.get(i).contains(inputSpeech)) {
                gv.performItemClick(gv.getChildAt(i), i, gv.getItemIdAtPosition(i));
                break answerLoop;
            }
        }
        toLog("inputSpeech: "+inputSpeech);
    }

    public List<Answernaire> getanswersFromFragment(){
        return localAnswers;
    }
}
