package tqs.backend.dto;


public class RegisterResponse {
        private String message;
        private long userId;
        private String email;
        private String role;

        public RegisterResponse(String message, long userId, String email, String role) {
                this.message = message;
                this.userId = userId;
                this.email = email;
                this.role = role;
        }

        public String getMessage() {
                return message;
        }

        public void setMessage(String message) {
                this.message = message;
        }

        public long getUserId() {
                return userId;
        }

        public void setUserId(long userId) {
                this.userId = userId;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getRole() {
                return role;
        }

        public void setRole(String role) {
                this.role = role;
        }
}