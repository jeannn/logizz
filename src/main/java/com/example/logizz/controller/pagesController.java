package com.example.logizz.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logizz.model.Question;
import com.example.logizz.model.User;
import com.example.logizz.service.QuestionService;
import com.example.logizz.service.UserService;

@Controller
@RequestMapping("/")
public class pagesController {

    private final QuestionService questionService;
    private final UserService userService;

    public pagesController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    // ---------- PAGES (GET) ----------

    @GetMapping("error")
    public String errorPage() {
        return "error";
    }

    @GetMapping({"/", "index"})
    public String login() {
        return "index";
    }

    @GetMapping("register")
    public String registerPage(Model model) {
        // (optional) used if you want th:object in register.html
        model.addAttribute("user", new User());
        return "register";
    }

    @GetMapping("profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("quiz")
    public String quiz(Model model) {
        model.addAttribute("questions", questionService.loadQuestion());
        return "quiz";
    }

    @GetMapping("result")
    public String result() {
        return "result";
    }

    // Admin pages
    @GetMapping("quizList")
    public String quizList(Model model) {
        model.addAttribute("questions", questionService.loadQuestion());
        return "quizList";
    }

    @GetMapping("addQuiz")
    public String addQuizPage() {
        return "addQuiz";
    }

    @GetMapping("editQuiz/{id}")
    public String editQuizPage(@PathVariable int id, Model model) {
        // Pre-fill the edit page with the question data
        List<Question> questions = questionService.loadQuestion();
        Question found = questions.stream().filter(q -> q.getId() == id).findFirst().orElse(null);

        model.addAttribute("question", found);
        return "editQuiz";
    }

    // ---------- ACTIONS (POST) ----------

    // Register from HTML form: POST /register
    @PostMapping("register")
    public String registerPost(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false, defaultValue = "USER") String role,
                               RedirectAttributes ra) {
        try {
            userService.registerUser(username, email, password, role);
            ra.addFlashAttribute("msg", "Registered successfully. Please login.");
            return "redirect:/index";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/register?error=true";
        }
    }

    // Add quiz from HTML form: POST /addQuiz
    @PostMapping("addQuiz")
    public String addQuizPost(@RequestParam int id,
                              @RequestParam String questionText,
                              @RequestParam String opt0,
                              @RequestParam String opt1,
                              @RequestParam String opt2,
                              @RequestParam String opt3,
                              @RequestParam String answer,
                              RedirectAttributes ra) {

        ArrayList<String> options = new ArrayList<>();
        options.add(opt0);
        options.add(opt1);
        options.add(opt2);
        options.add(opt3);

        // simple safety: ensure answer matches one option
        if (!options.contains(answer)) {
            ra.addFlashAttribute("error", "Answer must match one of the options exactly.");
            return "redirect:/addQuiz";
        }

        questionService.addQuestion(new Question(id, questionText, options, answer));
        ra.addFlashAttribute("msg", "Question added.");
        return "redirect:/quizList";
    }

    // Edit quiz from HTML form: POST /editQuiz/{id}
    @PostMapping("editQuiz/{id}")
    public String editQuizPost(@PathVariable int id,
                               @RequestParam String questionText,
                               @RequestParam String opt0,
                               @RequestParam String opt1,
                               @RequestParam String opt2,
                               @RequestParam String opt3,
                               @RequestParam String answer,
                               RedirectAttributes ra) {

        ArrayList<String> options = new ArrayList<>();
        options.add(opt0);
        options.add(opt1);
        options.add(opt2);
        options.add(opt3);

        if (!options.contains(answer)) {
            ra.addFlashAttribute("error", "Answer must match one of the options exactly.");
            return "redirect:/editQuiz/" + id;
        }

        questionService.editQuestion(new Question(id, questionText, options, answer));
        ra.addFlashAttribute("msg", "Question updated.");
        return "redirect:/quizList";
    }

    // Delete quiz: POST /quizList/delete/{id}
    @PostMapping("quizList/delete/{id}")
    public String deleteQuizPost(@PathVariable int id, RedirectAttributes ra) {
        questionService.deleteQuestion(id);
        ra.addFlashAttribute("msg", "Question deleted.");
        return "redirect:/quizList";
    }

    // Submit quiz answers: POST /quiz/submit
    @PostMapping("quiz/submit")
    public String submitQuiz(@RequestParam Map<String, String> params,
                             RedirectAttributes ra) {

        List<Question> questions = questionService.loadQuestion();
        int score = 0;

        for (Question q : questions) {
            String selected = params.get("q_" + q.getId());
            if (selected != null && selected.equals(q.getAnswer())) score++;
        }

        ra.addFlashAttribute("score", score);
        ra.addFlashAttribute("total", questions.size());
        return "redirect:/result";
    }

    
}
