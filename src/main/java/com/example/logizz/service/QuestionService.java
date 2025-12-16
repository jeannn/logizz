package com.example.logizz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.logizz.model.Question;

@Service
public class QuestionService {
    
    //in memory storage for quizzes using hashmap
    HashMap<Integer,Question> quizList = new HashMap<>();

    //method to add quiz to the quiz list
    public void addQuestion(Question question){
        quizList.put(question.getId(),question);
    }

    //method to edit quiz in the quiz list
    public void editQuestion(Question question){
        quizList.put(question.getId(), question);
    } 

    //method to get load quizzes 
    public List<Question> loadQuestion(){
        return new ArrayList<>(quizList.values());
    }

    public void deleteQuestion(int id){
        quizList.remove(id);
    }

}
