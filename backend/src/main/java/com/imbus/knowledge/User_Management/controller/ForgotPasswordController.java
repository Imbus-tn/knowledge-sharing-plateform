package com.imbus.knowledge.User_Management.controller;

import com.imbus.knowledge.User_Management.dto.MailBody;
import com.imbus.knowledge.User_Management.entities.ForgotPassword;
import com.imbus.knowledge.User_Management.entities.User;
import com.imbus.knowledge.User_Management.exception.InvalidOtpException;
import com.imbus.knowledge.User_Management.exception.OtpExpiredException;
import com.imbus.knowledge.User_Management.exception.ValidationException;
import com.imbus.knowledge.User_Management.repositories.ForgotPasswordRepository;
import com.imbus.knowledge.User_Management.repositories.UserRepository;
import com.imbus.knowledge.User_Management.services.EmailService;
import com.imbus.knowledge.User_Management.dto.ChangePassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("api/forgotPassword")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private  final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //send mail for email verification
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        Map <String, List<String>> errors = new HashMap<>();

        // Email validation
        List<String> emailErrors = new ArrayList<>();
        if (email == null || email.trim().isEmpty()){
            emailErrors.add("Email is required");
        } else {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")){
                emailErrors.add("Invalid email format");
            }
            if (!userRepository.existsByEmail(email)){
                emailErrors.add("Email does not exist");
            }
        }

        // Throw validation exception if there are email errors
        if (!emailErrors.isEmpty()) {
            Map<String, List<String>> errorResponse = new HashMap<>();
            errorResponse.put("email", emailErrors);
            throw new ValidationException(errorResponse);
        }

        // Proceed with OTP generation and sending
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

        // Delete existing OTP records for this user before saving a new one
        forgotPasswordRepository.deleteByUser(user);

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(user.getEmail())
                .text("This is the OTP for your Forgot Password request : "+ otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis()+ 70 * 1000)) // OTP expires in 70 seconds
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification!");
    }

    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Please provide an valid email!"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user).orElseThrow(() -> new InvalidOtpException("Invalid OTP for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))){
            logger.warn("OTP expired for email: {}. Deleting from database...", email);
            try {
                forgotPasswordRepository.delete(fp);
                logger.info("Expired OTP deleted successfully for email: {}", email);
            } catch (Exception e) {
                logger.error("Error deleting expired OTP for email: {}", email, e);
            }
            throw new OtpExpiredException("OTP has expired. Please resend the code.");
        }

        logger.info("OTP verified successfully for email: {}", email);
        return ResponseEntity.ok("OTP verified!");
    }

    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword, @PathVariable String email){

        Map<String, List<String>> errors = new HashMap<>();

        // Password validation
        List<String> passwordErrors = new ArrayList<>();
        String password = changePassword.password();
        String repeatPassword = changePassword.repeatPassword();

        if (password == null || password.trim().isEmpty()){
            passwordErrors.add("Password is required");
        } else {
            if (password.length() < 8) {
                passwordErrors.add("Minimum 8 characters");
            }
            if (password.length() > 64) {
                passwordErrors.add("Maximum 64 characters");
            }
            if (!password.matches("^(?=.*[A-Z]).+$")){
                passwordErrors.add("Uppercase letter required");
            }
            if (!password.matches("^(?=.*[a-z]).+$")){
                passwordErrors.add("Lowercase letter required");
            }
            if (!password.matches("^(?=.*\\d).+$")) {
                passwordErrors.add("Number required");
            }
            if (!password.matches("^(?=.*[^A-Za-z0-9]).+$")) {
                passwordErrors.add("Special character required (!@#$%^&*...)");
            }
            if (!password.equals(repeatPassword)) {
                passwordErrors.add("Passwords do not match");
            }
        }
        if (!passwordErrors.isEmpty()) {
            errors.put("password", passwordErrors);
        }

        // Throw validation exception if there are errors
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // If no errors, proceed with password update
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password has been changed!");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000,999_999);
    }
}
