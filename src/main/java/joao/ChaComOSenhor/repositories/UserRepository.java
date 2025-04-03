package joao.ChaComOSenhor.repositories;

    import joao.ChaComOSenhor.domain.user.User;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.security.core.userdetails.UserDetails;

    /**
     * Repository interface for User entities.
     * Extends JpaRepository to provide CRUD operations.
     */
    public interface UserRepository extends JpaRepository<User, Long> {

        /**
         * Finds a User by their login.
         *
         * @param login the login of the user
         * @return the UserDetails of the user with the given login
         */
        UserDetails findByLogin(String login);

        /**
         * Finds a User by their email.
         *
         * @param email the email of the user
         * @return the User with the given email
         */
        User findByEmail(String email);
    }