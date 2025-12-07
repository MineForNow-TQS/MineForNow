package tqs.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade User - representa utilizadores do sistema (Renters e Owners).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public enum UserRole {
        ADMIN,   // Administrador do sistema
        OWNER,   // Proprietário de veículos (pode listar carros)
        RENTER   // Utilizador que apenas aluga carros
    }
}
