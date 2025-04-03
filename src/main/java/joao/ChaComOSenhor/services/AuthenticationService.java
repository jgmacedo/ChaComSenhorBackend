package joao.ChaComOSenhor.services;

import joao.ChaComOSenhor.domain.user.RegisterDTO;
import joao.ChaComOSenhor.domain.user.User;
import joao.ChaComOSenhor.domain.user.UserRole;
import joao.ChaComOSenhor.infra.security.TokenService;
import joao.ChaComOSenhor.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class for handling authentication-related operations such as login and registration.
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param login the user's login
     * @param password the user's password
     * @return a JWT token if authentication is successful
     */
    public String login(String login, String password) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(login, password);
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        User user = (User) auth.getPrincipal();
        return tokenService.generateToken(user);
    }

    /**
     * Registers a new user.
     *
     * @param registerDTO the data transfer object containing user registration details
     * @return the newly registered user
     * @throws RuntimeException if the username or email already exists
     */
    public User register(RegisterDTO registerDTO) {
        if(userRepository.findByLogin(registerDTO.login()) != null) {
            throw new RuntimeException("Usuário já existe");
        }
        if(userRepository.findByEmail(registerDTO.email()) != null){
            throw new RuntimeException("Email já cadastrado");
        }

        String encryptedPassword = passwordEncoder.encode(registerDTO.password());
        User newUser = new User();
        newUser.setLogin(registerDTO.login());
        newUser.setPassword(encryptedPassword);
        newUser.setName(registerDTO.name());
        newUser.setEmail(registerDTO.email());
        newUser.setRole(UserRole.valueOf("USER"));

        return userRepository.save(newUser);
    }
}