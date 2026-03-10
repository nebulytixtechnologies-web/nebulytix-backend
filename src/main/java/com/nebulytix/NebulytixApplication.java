package com.nebulytix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NebulytixApplication {

	public static void main(String[] args) {
		SpringApplication.run(NebulytixApplication.class, args);
	}

}

// package com.nebulytix;
//
// import com.nebulytix.entity.User;
// import com.nebulytix.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.security.crypto.password.PasswordEncoder;
//
// @SpringBootApplication
// public class NebulytixApplication implements CommandLineRunner {
//    
//     @Autowired
//     private UserRepository userRepository;
//    
//     @Autowired
//     private PasswordEncoder passwordEncoder;
//    
//     public static void main(String[] args) {
//         SpringApplication.run(NebulytixApplication.class, args);
//     }
//    
//     @Override
//     public void run(String... args) throws Exception {
//         // Create admin user if not exists
//         if (!userRepository.findByEmail("admin@nebulytix.com").isPresent()) {
//             User admin = new User();
//             admin.setEmail("admin@nebulytix.com");
//             admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
//             admin.setFullName("System Admin");
//             admin.setRole(User.Role.ADMIN);
//             admin.setActive(true);
//             userRepository.save(admin);
//             System.out.println("Admin user created successfully!");
//         }
//     }
// }
