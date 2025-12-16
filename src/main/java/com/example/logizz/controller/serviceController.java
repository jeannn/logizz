package com.example.logizz.controller;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.example.logizz.model.Question;
import com.example.logizz.service.QuestionService;
import com.example.logizz.service.UserService;

@RestController
@RequestMapping("/api")
public class serviceController {

    private final QuestionService questionService;
    private final UserService userService;

    public serviceController(QuestionService questionService, UserService userService) {
        this.questionService = questionService;
        this.userService = userService;
    }

    // ---------- DTOs ----------
    public static class RegisterRequest {
        public String username;
        public String email;
        public String password;
        public String role;
    }

    public static class QuestionRequest {
        public int id;
        public String questionText;
        public ArrayList<String> options;
        public String answer;
    }

    public static class SubmitAnswersRequest {
        public Map<Integer, String> answers;
    }

    // ---------- Helpers ----------
    private static final Pattern EMAIL_RX =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static String clean(String s) {
        return (s == null) ? null : s.trim();
    }

    // ---------- REGISTER (API ONLY) ----------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (req == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Request body required"));
        }

        String username = clean(req.username);
        String email = clean(req.email);
        String password = req.password;

        if (username == null || username.length() < 3) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid username"));
        }
        if (email == null || !EMAIL_RX.matcher(email).matches()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid email"));
        }
        if (password == null || password.length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid password"));
        }

        try {
            // Force USER from public API
            userService.registerUser(username, email, password, "USER");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "User registered"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ---------- QUESTIONS ----------
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions(Authentication auth) {

        List<Question> questions = questionService.loadQuestion();

        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(questions);
        }

        // hide answers for normal users
        List<Question> safe = questions.stream()
                .map(q -> new Question(
                        q.getId(),
                        q.getQuestionText(),
                        q.getOptions(),
                        null))
                .collect(Collectors.toList());

        return ResponseEntity.ok(safe);
    }

    // ---------- ADMIN CRUD ----------
    @PostMapping("/questions")
    public ResponseEntity<?> addQuestion(@RequestBody QuestionRequest req) {

        if (req == null || req.id <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid request"));
        }

        Question q = new Question(
                req.id,
                clean(req.questionText),
                req.options,
                clean(req.answer));

        questionService.addQuestion(q);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Question added"));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> editQuestion(@PathVariable int id,
                                          @RequestBody QuestionRequest req) {

        if (req == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid request"));
        }

        Question q = new Question(
                id,
                clean(req.questionText),
                req.options,
                clean(req.answer));

        questionService.editQuestion(q);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable int id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- SUBMIT (API version, optional) ----------
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestBody SubmitAnswersRequest req) {

        List<Question> questions = questionService.loadQuestion();
        int score = 0;

        if (req != null && req.answers != null) {
            for (Question q : questions) {
                String selected = req.answers.get(q.getId());
                if (selected != null && selected.equals(q.getAnswer())) {
                    score++;
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "score", score,
                "total", questions.size()
        ));
    }
}
