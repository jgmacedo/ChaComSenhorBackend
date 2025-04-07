package joao.ChaComOSenhor.repositories;

    import joao.ChaComOSenhor.domain.user.User;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.security.core.userdetails.UserDetails;

    /**
     * Repository interface for User entities.
     * Extends JpaRepository to provide CRUD operations.
     */
    public interface UserRepository extends JpaRepository<User, Long> {
        // Change return type from UserDetails to User
        User findByLogin(String login);
        User findByEmail(String email);
    }