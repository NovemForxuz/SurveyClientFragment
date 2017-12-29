package com.anewtech.phone.surveyclientfragmentOLD;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.anewtech.phone.surveyclientfragmentOLD.Fragments.CustomActivityMessageEvent;
import com.anewtech.phone.surveyclientfragmentOLD.Fragments.CustomButtonAdapter;
import com.anewtech.phone.surveyclientfragmentOLD.Fragments.QuestionFragment;
import com.anewtech.phone.surveyclientfragmentOLD.models.reports.SurveyAnswerReport;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.Answernaire;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.Questionaire;
import com.anewtech.phone.surveyclientfragmentOLD.services.SpeechService;
import com.anewtech.phone.surveyclientfragmentOLD.services.SurveyBervice;
import com.orhanobut.logger.Logger;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.speech.Grammar;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.interfaces.speech.RecognizeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubFragmentLayout
        extends TopBaseActivity
        implements QuestionFragment.OnQuestionPass {

    private final static boolean LOG_ON_ACTIVITY_LIFECYCLE = false;

    private EventBus myevent = EventBus.getDefault();

    private SurveyBervice brain;
    private SpeechManager sm;
    private FragmentLayout frgLayout = new FragmentLayout();
    private Intent serviceIntent;
    public SpeechService speechService;

    private Handler handler = new Handler();

    private GridView gv;
    private CustomButtonAdapter cba;
    private TextView tvSubQue;

    private ArrayList<Questionaire> q = new ArrayList<>();
    private List<Answernaire> answers = new ArrayList<>();
    private Questionaire currentque = new Questionaire();
    private Set<String> setAns;
    private ArrayList<String> answ = new ArrayList<>();
    private int indexofque = -1;
    private String answerpos = "";
    private int countdown = 0;
    private boolean thisLayer;

    private int icon = R.drawable.ic_sentiment_satisfied_black_48dp;

    @Override
    protected void onStart() {
        super.onStart();
        thisLayer = true;
        startService(serviceIntent);
        myevent.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        thisLayer = false;
        frgLayout.passBoolean(thisLayer);
        if (speechService != null) {
            unbindService(connection);
        }
        stopService(serviceIntent);
        myevent.unregister(this);
    }

    @Override
    protected void onMainServiceConnected() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CustomActivityMessageEvent event) {
        final CustomActivityMessageEvent mevent = event;
        if (event.getDoOrDoNot() == 1) {
            finishHim();
        }
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SpeechService.MyBinder binder = (SpeechService.MyBinder) service;
            speechService = binder.getService();
            Log.e("xxx", "[SubFragmentLayout]: onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            speechService    = null;
            Log.e("xxx", "[SubFragmentLayout]: onServiceDisconnected()");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subquestionaire_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("choice") != null) {
            answerpos = bundle.getString("choice");
        } else {
            frgLayout.passBoolean(false);
            finish();
        }

        sm = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        serviceIntent = new Intent(this, SpeechService.class);
        serviceIntent.setPackage(getPackageName());
        // To call onServiceConnected() if the service already started
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        this.brain = new SurveyBervice();
        this.brain.loadJsonData(loadJsonFromAsset());

        tvSubQue = findViewById(R.id.subQuestion);
        gv = findViewById(R.id.subAnswersGrid);
        tvSubQue = findViewById(R.id.subQuestion);
        gv.setNumColumns(3);

        //this.brain.doFirst();
        doSub();
        currentSubQuestion();
        populate();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void finishHim() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", "finish");
        setResult(2, resultIntent);
        //SubFragmentLayout.this.finish();

        this.finish();
    }

    private void normalFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("result", "normal");
        setResult(2, resultIntent);
        //SubFragmentLayout.this.finish();
        frgLayout.passBoolean(thisLayer);
        this.finish();
    }

    private void populate() {
        tvSubQue.setText(currentque.question);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {onQuestionPass(currentque.question);
            } // pass to speechmanager
        }, 1000); //
        cba = new CustomButtonAdapter(this, currentque.answers, icon);
        answers = currentque.answers;
        Thread readAns = new Thread(getAns);
        readAns.start();
        gv.setAdapter(cba);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SurveyAnswerReport local = new SurveyAnswerReport(currentque.questionid,currentque.question,currentque.answers.get(i).answerid,currentque.answers.get(i).answer);
                brain.doFeedback(local);

                if (indexofque >= q.size()) {
                    normalFinish();
                } else {
                    nextSubQuestion();
                    tvSubQue.setText(currentque.question);
                    onQuestionPass(currentque.question);
                    ((CustomButtonAdapter) gv.getAdapter()).notifyDataSetChanged();
                    cba = new CustomButtonAdapter(SubFragmentLayout.this, currentque.answers, icon);
                    answ = new ArrayList<>();
                    Thread readAns = new Thread(getAns);
                    readAns.start();
                    gv.setAdapter(cba);
                    //Toast.makeText(SubFragmentLayout.this, Integer.toString(indexofque), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Runnable getAns = new Runnable() {
        @Override
        public void run() {
            answers = currentque.answers;
            setAns = new HashSet<>();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Answernaire a : answers) {
                final String answer = a.answer;
                try {
                    Thread.sleep(1250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setAns.add(answer);
                answ = new ArrayList<>(setAns);
                if(thisLayer){
                    speechService.setAnsList(answ);
                }
                answerListener(answ);
                toLog(String.valueOf(answ.size()));
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //populate();
    }

    private String loadJsonFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("survey.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            this.toLog(ex.toString());
            return null;
        }
        return json;
    }

    private void doSub() {
        q = this.brain.doSub(answerpos);
    }

    private void currentSubQuestion() {
        if (indexofque < 0) {
            indexofque = 0;
        }
        currentque = q.get(indexofque);
        indexofque++;
    }

    private void nextSubQuestion() {
        currentque = q.get(indexofque);
        if (indexofque <= q.size()) indexofque++;
    }

    private void toLog(String msg) {
        if (LOG_ON_ACTIVITY_LIFECYCLE) {
            Logger.e("Survey(sfl) :" + msg);
        }
    }

    @Override   /* Voice over for 2nd layer questions */
    public void onQuestionPass(String question) {
        sm.stopSpeak();
        if(thisLayer){
            speechService.readQuestion(question);
        }
    }

    /* Voice over for 2nd layer answers and listener to perform click programmatically */
    public void answerListener(ArrayList<String> arrayList) {
        final ArrayList<String> answer = arrayList;
        sm.setOnSpeechListener(new RecognizeListener() {
            @Override
            public boolean onRecognizeResult(Grammar grammar) {
                String inputSpeech = wordFirstCap(grammar.getText());
                toLog("speech: " + inputSpeech);
                toLog("answ size: " + answer.size());
                for (int i = 0; i < answer.size(); i++) {
                    toLog(answer.get(i));
                    if (inputSpeech.contains(answer.get(i))) {
                        gv.performItemClick(gv.getChildAt(i), i, gv.getItemIdAtPosition(i));
                        break;
                    }
                }
                return true;
            }

            @Override
            public void onRecognizeVolume(int i) {

            }
        });
    }

    public String wordFirstCap(String str) {
        String[] words = str.trim().split(" ");
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].trim().length() > 0) {
//                Log.e("words[i].trim",""+words[i].trim().charAt(0));
                ret.append(Character.toUpperCase(words[i].trim().charAt(0)));
                ret.append(words[i].trim().substring(1));
                if (i < words.length - 1) {
                    ret.append(' ');
                }
            }
        }

        return ret.toString();
    }

}
