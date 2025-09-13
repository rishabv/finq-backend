package com.finq.dtos.requests;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotBlank(message = "PAN number is required")
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN number format")
    private String panNumber;

    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar number must be 12 digits")
    private String aadhaarNumber;

    @NotNull(message = "Personal questions are required")
    @Size(min = 5, max = 5, message = "Exactly 5 personal questions are required")
    private List<PersonalQuestionDto> personalQuestions;

    // Constructors
    public CreateUserRequest() {}

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public List<PersonalQuestionDto> getPersonalQuestions() {
        return personalQuestions;
    }

    public void setPersonalQuestions(List<PersonalQuestionDto> personalQuestions) {
        this.personalQuestions = personalQuestions;
    }

    // Inner class for personal questions
    public static class PersonalQuestionDto {
        @NotBlank(message = "Question text is required")
        @Size(min = 10, max = 500, message = "Question must be between 10 and 500 characters")
        private String questionText;

        @NotBlank(message = "Answer is required")
        @Size(min = 2, max = 100, message = "Answer must be between 2 and 100 characters")
        private String answer;

        @NotNull(message = "Question order is required")
        @Min(value = 1, message = "Question order must be between 1 and 5")
        @Max(value = 5, message = "Question order must be between 1 and 5")
        private Integer questionOrder;

        // Constructors
        public PersonalQuestionDto() {}

        public PersonalQuestionDto(String questionText, String answer, Integer questionOrder) {
            this.questionText = questionText;
            this.answer = answer;
            this.questionOrder = questionOrder;
        }

        // Getters and Setters
        public String getQuestionText() {
            return questionText;
        }

        public void setQuestionText(String questionText) {
            this.questionText = questionText;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public Integer getQuestionOrder() {
            return questionOrder;
        }

        public void setQuestionOrder(Integer questionOrder) {
            this.questionOrder = questionOrder;
        }
    }
}
