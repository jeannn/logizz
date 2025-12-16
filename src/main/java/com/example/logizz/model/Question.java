package com.example.logizz.model;

import java.util.ArrayList;

//model class for Quiz
public class Question {
    private int id;
    private String questionText;
    private ArrayList<String> options;
    private String answer;

    public Question(int id, String questionText, ArrayList<String> options, String answer){
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.answer = answer;
    }
    public int getId(){
        return id;
    }
    public String getQuestionText(){
        return questionText;
    }
    public ArrayList<String> getOptions(){
        return options;
    }
    public String getAnswer(){
        return answer;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setQuestionText(String questionText){
        this.questionText = questionText;
    }
    public void setOptions(ArrayList<String> options){
        this.options = options;
    }
    public void setAnswer(String answer){
        this.answer = answer;
    }
    public String toString(){
        return "Quiz{ " +
                "id=" + id +
                ", questionText='" + questionText + '\'' +
                ", options=" + options +
                ", answer='" + answer + '\'' +
                '}';
    }
}
