//package com.example.unicode.configuration;
//
//import com.example.unicode.entity.Privilege;
//import com.example.unicode.entity.Role;
//import com.example.unicode.entity.Users;
//import com.example.unicode.repository.PrivilegeRepository;
//import com.example.unicode.repository.RoleRepository;
//import com.example.unicode.repository.UsersRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Set;
//
//@Component
//@RequiredArgsConstructor
//public class DataSender implements CommandLineRunner {
//    private final RoleRepository roleRepository;
//    private final PrivilegeRepository privilegeRepository;
//    private final UsersRepository usersRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        if (roleRepository.count() > 0) return; // tránh insert lại
//
//        // ===== Create Privileges =====
//        Privilege p1 = new Privilege("USER_VIEW", "View user", "View users");
//        Privilege p2 = new Privilege("USER_CREATE", "Create user", "Create users");
//        Privilege p3 = new Privilege("USER_UPDATE", "Update user", "Update users");
//        Privilege p4 = new Privilege("USER_DELETE", "Delete user", "Delete users");
//
//        Privilege p5 = new Privilege("COURSE_CREATE", "Create course", "Create course");
//        Privilege p6 = new Privilege("COURSE_UPDATE", "Update course", "Update course");
//        Privilege p7 = new Privilege("COURSE_DELETE", "Delete course", "Delete course");
//
//        Privilege p8 = new Privilege("LEARN", "Learn course", "Join course");
//        Privilege p9 = new Privilege("TAKE_EXAM", "Take exam", "Do exam");
//
//        privilegeRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9));
//
//        // ===== Create Roles =====
//        Role admin = new Role();
//        admin.setRoleCode("ADMIN");
//        admin.setRoleName("Administrator");
//        admin.setDescription("System admin");
//        admin.getPrivileges().addAll(List.of(p1, p2, p3, p4, p5, p6, p7));
//
//        Role instructor = new Role();
//        instructor.setRoleCode("INSTRUCTOR");
//        instructor.setRoleName("Instructor");
//        instructor.setDescription("Teacher");
//        instructor.getPrivileges().addAll(List.of(p5, p6, p8));
//
//        Role learner = new Role();
//        learner.setRoleCode("LEARNER");
//        learner.setRoleName("Learner");
//        learner.setDescription("Student");
//        learner.getPrivileges().addAll(List.of(p8, p9));
//
//        roleRepository.saveAll(List.of(admin, instructor, learner));
//
//        // ===== Create Users =====
//        Users u1 = new Users();
//        u1.setEmail("admin@gmail.com");
//        u1.setPassword(passwordEncoder.encode("123456"));
//        u1.setName("Admin");
//        u1.setTokenVersion(0);
//
//        Users u2 = new Users();
//        u2.setEmail("teacher@gmail.com");
//        u2.setPassword(passwordEncoder.encode("123456"));
//        u2.setName("Instructor");
//        u2.setTokenVersion(0);
//
//        Users u3 = new Users();
//        u3.setEmail("student@gmail.com");
//        u3.setPassword(passwordEncoder.encode("123456"));
//        u3.setName("Learner");
//        u3.setTokenVersion(0);
//
//        usersRepository.saveAll(List.of(u1, u2, u3));
//
//        // ===== Map role to user =====
//        admin.setUserslist(Set.of(u1));
//        instructor.setUserslist(Set.of(u2));
//        learner.setUserslist(Set.of(u3));
//
//        roleRepository.saveAll(List.of(admin, instructor, learner));
//
//        System.out.println("✅ Seed data created successfully!");
//    }
//
//
//}
