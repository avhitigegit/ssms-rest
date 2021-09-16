package com.dev.ssms.component.constant;

public class AppConstant {

    //=============================
    // API URL
    //=============================
    public static final String API_URL = "http://localhost:8080/api";

    //=============================
    // EMAIL VERIFICATION
    //=============================
    public static final String EMAIL_VERIFICATION_SUBJECT = "Email Verification";
    public static final String EMAIL_VERIFICATION_BODY = "To confirm your account " + "\n" + "Please click here : http://localhost:8080/verification?token=";

    //=============================
    // PASSWORD RESET
    //=============================
    public static final String PASSWORD_RESET_VERIFICATION_SUBJECT = "Password Reset";
    public static final String PASSWORD_RESET_VERIFICATION_BODY = "To reset Your password " + "\n" + "Please click here : http://localhost:8080/pwd-verification?token=";

    //=============================
    // JSON WEB TOKEN
    //=============================
    public static final String JSON_SECRET = "seSBhIHRlc3QgZW1haWwuIFNvcnJ5IGZvciB0aGUgaW5jb252ZW5pZW5jZS4gRG9uJ3QgbWl";
    public static final String JSON_VALIDITY_PERIOD = "21600";  // 6 hours (60*60*6)
    public static final String JSON_HEADER = "Authorization";

    //=============================
    // SESSION
    //=============================
    public static final String TOKEN = "TOKEN";
}