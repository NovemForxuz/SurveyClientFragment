package com.anewtech.phone.surveyclientfragmentOLD.models.data;
import java.util.List;

public class SurveyDataModel {
    public String version;
    public String title;
    public String datetime;
    public String uploadedby;
    public String access;
    public Template template;
    public List<Question> layersquestions;
    public List<Answer> layersanswers;
    public List<FinalMessage> layerslastmessages;
}
