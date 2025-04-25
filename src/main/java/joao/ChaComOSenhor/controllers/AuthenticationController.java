package joao.ChaComOSenhor.controllers;

import joao.ChaComOSenhor.domain.user.*;
import joao.ChaComOSenhor.infra.security.TokenService;
import joao.ChaComOSenhor.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
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
                    dto.email() == null) {
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

            // Set role to USER
            UserRole role = UserRole.USER;

            String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());
            User newUser = new User(dto.name(), dto.login(), dto.email(), encryptedPassword, role);

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
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO dto) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            var user = (User) auth.getPrincipal(); // Get the authenticated user
            var token = tokenService.generateToken(user);

            // Create the response with token and user details
            var response = new LoginResponseDTO(
                    token,
                    user.getUsername(), // Username
                    user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList() // Roles
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> registerAdmin(@RequestBody RegisterAdminDTO dto) {
        try {
            // Validate null fields
            if (dto.login() == null || dto.password() == null || dto.name() == null ||
                    dto.email() == null) {
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

            // Set role to ADMIN
            UserRole role = UserRole.ADMIN;

            String encryptedPassword = new BCryptPasswordEncoder().encode(dto.password());
            User newUser = new User(dto.name(), dto.login(), dto.email(), encryptedPassword, role);

            try {
                this.userRepository.save(newUser);
            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.badRequest().body("Database error: " + e.getMessage());
            }

            return ResponseEntity.ok().body("Admin registered successfully");
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

}