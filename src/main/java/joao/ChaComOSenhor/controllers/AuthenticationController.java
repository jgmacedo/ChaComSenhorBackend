package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.user.UserRole;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import joao.ChaComOSenhor.domain.user.LoginResponseDTO;
import joao.ChaComOSenhor.domain.user.RegisterDTO;
import joao.ChaComOSenhor.domain.user.User;
import joao.ChaComOSenhor.infra.security.TokenService;
import joao.ChaComOSenhor.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterDTO dto) {
        try {
            // Validate null fields
            if (dto.login() == null || dto.password() == null || dto.name() == null ||
                    dto.email() == null || dto.role() == null) {
                return ResponseEntity.badRequest().body("All fields are required");
            }

            // Validate empty fields
            if (dto.login().isBlank() || dto.password().isBlank() ||
                    dto.name().isBlank() || dto.email().isBlank()) {
                return ResponseEntity.badRequest().body("Fields cannot be empty");
            }

            // Validate email format
            if (!dto.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest().body("Invalid email format");
            }

            // Validate password strength (at least 8 characters, 1 number, 1 letter)
            if (!dto.password().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
                return ResponseEntity.badRequest().body("Password must be at least 8 characters long and contain at least one letter and one number");
            }

            // Check if user exists
            if (this.userRepository.findByLogin(dto.login()) != null) {
                return ResponseEntity.badRequest().body("User already exists");
            }

            // Check if email is already registered
            if (this.userRepository.findByEmail(dto.email()) != null) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            // Validate role
            try {
                UserRole.valueOf(String.valueOf(dto.role()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid role. Must be either ADMIN or USER");
            }

            String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());
            User newUser = new User(dto.name(), dto.login(), dto.email(), encryptedPassword, dto.role());

            try {
                this.userRepository.save(newUser);
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.badRequest().body("Database error: " + e.getMessage());
            }

            return ResponseEntity.ok().body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Database access error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody RegisterDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}