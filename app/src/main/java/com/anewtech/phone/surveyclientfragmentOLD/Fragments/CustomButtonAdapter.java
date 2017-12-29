package com.anewtech.phone.surveyclientfragmentOLD.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.anewtech.phone.surveyclientfragmentOLD.R;
import com.anewtech.phone.surveyclientfragmentOLD.models.services.Answernaire;
import java.util.List;

public class CustomButtonAdapter extends BaseAdapter{
    private Context context;
    private List<Answernaire> textToGenerate;
    private int[] icons;
    private int icon;
    LayoutInflater inflater;

    public CustomButtonAdapter(Context c, List<Answernaire> textToGenerate, int icon)
    {
        this.context = c;
        this.textToGenerate = textToGenerate;
        this.icon = icon;
        inflater = (LayoutInflater.from(c));
    }
    @Override
    public int getCount() {
        return textToGenerate.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.fragment_answer_detail, null);
        TextView tv = view.findViewById(R.id.answertext);
        ImageView iv = view.findViewById(R.id.answerimage);
        iv.setImageResource(this.icon);
        tv.setText(this.textToGenerate.get(i).answer);
        return view;
//        View gridview;
//        if(view == null){
//            gridview = new View(context);
//            gridview = inflater.inflate(R.layout.fragment_answer_detail, null);
//            TextView textView = gridview.findViewById(R.id.answertext);
//            textView.setText(feedbackNames[i]);
//        }else {
//            gridview = view;
//        }
//        return gridview;
    }




}
