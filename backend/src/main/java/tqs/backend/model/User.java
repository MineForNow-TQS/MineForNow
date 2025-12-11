package tqs.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
// lombok removed: generating constructors, getters and setters manually

/**
 * Entidade User - representa utilizadores do sistema (Renters e Owners).
 */
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @JsonIgnore // NÃO serializar password no JSON (segurança)
    @Column(nullable = false)
    private String password; // Em produção, usar BCrypt

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // ADMIN, OWNER, RENTER

    private String phone;
    private String address;

    // Constructors
    public User() {
    }

    public User(Long id, String email, String name, String password, UserRole role, String phone, String address) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    // Getters and setters
    public Long getId() { return id;
    }

    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name;}

    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address;
    }

    // Builder (manual replacement for Lombok @Builder)
    public static UserBuilder builder() { return new UserBuilder(); }

    public static class UserBuilder {
        private Long id;
        private String email;
        private String name;
        private String password;
        private UserRole role;
        private String phone;
        private String address;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder role(UserRole role) { this.role = role; return this; }
        public UserBuilder phone(String phone) { this.phone = phone; return this; }
        public UserBuilder address(String address) { this.address = address; return this; }

        public User build() {
            return new User(id, email, name, password, role, phone, address);
        }
    }

    public enum UserRole {
        ADMIN,   // Administrador do sistema
        OWNER,   // Proprietário de veículos (pode listar carros)
        RENTER   // Utilizador que apenas aluga carros
    }
}
