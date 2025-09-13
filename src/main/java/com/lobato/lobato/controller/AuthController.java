package com.lobato.lobato.controller;

import com.lobato.lobato.model.User;
import com.lobato.lobato.service.CustomUserDetailsService;
import com.lobato.lobato.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.security.Principal;
import java.io.IOException;

@Controller
public class AuthController {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("error",
                    "Nome de usuário ou senha inválidos! Use seu nome de usuário (não email) para fazer login.");
        }
        if (logout != null) {
            model.addAttribute("message", "Você saiu da sua conta!");
        }
        return "login";
    }

    @GetMapping("/create-simple-user")
    @ResponseBody
    public String createSimpleUser() {
        try {
            
            User user = new User();
            user.setUsername("simpleuser");
            user.setEmail("simple@example.com");
            user.setPassword("password123");

           
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            User savedUser = userDetailsService.saveUser(user);
            return "Usuário criado com sucesso! ID: " + savedUser.getId();
        } catch (Exception e) {
            return "Erro: " + e.getMessage() + " - " + e.getClass().getSimpleName();
        }
    }

    @GetMapping("/list-all-users")
    @ResponseBody
    public String listAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            StringBuilder response = new StringBuilder();
            response.append("Total de usuários encontrado no MongoDB: ").append(users.size()).append("<br><br>");

            for (User user : users) {
                response.append("Username: ").append(user.getUsername()).append("<br>");
                response.append("Email: ").append(user.getEmail()).append("<br>");
                response.append("Status: ").append(user.isEnabled()).append("<br>");
                response.append("Roles: ").append(user.getRoles()).append("<br>");
                response.append("Senhas (primeiros 20 chars): ")
                        .append(user.getPassword().substring(0, Math.min(20, user.getPassword().length())))
                        .append("...<br>");
                response.append("---<br><br>");
            }

            return response.toString();
        } catch (Exception e) {
            return "MongoDB error: " + e.getMessage();
        }
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "profile";
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "settings";
    }

    @GetMapping("/users")
    public String users(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        List<User> allUsers = userRepository.findAll();
        model.addAttribute("users", allUsers);
        return "users";
    }

    @GetMapping("/loans")
    public String loans(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "redirect:/users"; 
    }

    @GetMapping("/returns")
    public String returns(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "redirect:/users"; 
    }

    @PostMapping("/register-user-complete")
    public String registerUserComplete(User user, 
                                     @RequestParam(value = "comprovanteResidenciaFile", required = false) MultipartFile comprovanteResidenciaFile,
                                     @RequestParam(value = "comprovanteDocumentoFile", required = false) MultipartFile comprovanteDocumentoFile,
                                     RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== DEBUG REGISTER USER COMPLETE ===");
            System.out.println("Nome: " + user.getNome());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Username: " + user.getUsername());
            
            // Debug dos arquivos
            if (comprovanteResidenciaFile != null && !comprovanteResidenciaFile.isEmpty()) {
                System.out.println("Arquivo comprovante residência: " + comprovanteResidenciaFile.getOriginalFilename());
                System.out.println("Tamanho: " + comprovanteResidenciaFile.getSize() + " bytes");
            }
            if (comprovanteDocumentoFile != null && !comprovanteDocumentoFile.isEmpty()) {
                System.out.println("Arquivo comprovante documento: " + comprovanteDocumentoFile.getOriginalFilename());
                System.out.println("Tamanho: " + comprovanteDocumentoFile.getSize() + " bytes");
            }
            System.out.println("=====================================");

            
            if (userDetailsService.existsByUsername(user.getUsername())) {
                redirectAttributes.addFlashAttribute("error",
                        "Nome de usuário já existe. Escolha outro nome de usuário.");
                return "redirect:/users";
            }

            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "E-mail já cadastrado. Use outro e-mail.");
                return "redirect:/users";
            }


            if (comprovanteResidenciaFile == null || comprovanteResidenciaFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Comprovante de residência é obrigatório.");
                return "redirect:/users";
            }

            if (comprovanteDocumentoFile == null || comprovanteDocumentoFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Comprovante de documento pessoal é obrigatório.");
                return "redirect:/users";
            }

        
            try {
                user.setComprovanteResidencia(comprovanteResidenciaFile.getBytes());
                user.setComprovanteResidenciaNome(comprovanteResidenciaFile.getOriginalFilename());
                user.setComprovanteResidenciaContentType(comprovanteResidenciaFile.getContentType());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Erro ao processar comprovante de residência: " + e.getMessage());
                return "redirect:/users";
            }

            try {
                user.setComprovanteDocumento(comprovanteDocumentoFile.getBytes());
                user.setComprovanteDocumentoNome(comprovanteDocumentoFile.getOriginalFilename());
                user.setComprovanteDocumentoContentType(comprovanteDocumentoFile.getContentType());
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Erro ao processar comprovante de documento: " + e.getMessage());
                return "redirect:/users";
            }

          
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);

            
            userDetailsService.saveUser(user);

            redirectAttributes.addFlashAttribute("success", "Usuário '" + user.getNome() + "' cadastrado com sucesso!");
            return "redirect:/users";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar usuário: " + e.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("/delete-user")
    public String deleteUser(@RequestParam String username, RedirectAttributes redirectAttributes) {
        try {
          
            List<User> usersWithSameUsername = userRepository.findAllByUsername(username);
            
            if (usersWithSameUsername.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuário não encontrado.");
            } else if (usersWithSameUsername.size() == 1) {
              
                userRepository.delete(usersWithSameUsername.get(0));
                redirectAttributes.addFlashAttribute("success", "Usuário '" + username + "' excluído com sucesso!");
            } else {
               
                userRepository.deleteAll(usersWithSameUsername);
                redirectAttributes.addFlashAttribute("success", 
                    "Encontrados " + usersWithSameUsername.size() + " usuários duplicados com username '" + username + "'. Todos foram excluídos.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao excluir usuário: " + e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/clean-duplicates")
    @ResponseBody
    public String cleanDuplicateUsers() {
        try {
            List<User> allUsers = userRepository.findAll();
            java.util.Map<String, List<User>> usersByUsername = allUsers.stream()
                .collect(java.util.stream.Collectors.groupingBy(User::getUsername));
            
            int totalDuplicates = 0;
            StringBuilder report = new StringBuilder();
            report.append("Relatório de limpeza de usuários duplicados:<br><br>");
            
            for (java.util.Map.Entry<String, List<User>> entry : usersByUsername.entrySet()) {
                String username = entry.getKey();
                List<User> users = entry.getValue();
                
                if (users.size() > 1) {
                    report.append("Username '").append(username).append("': ")
                          .append(users.size()).append(" duplicatas encontradas<br>");
                 
                    List<User> duplicatesToDelete = users.subList(1, users.size());
                    userRepository.deleteAll(duplicatesToDelete);
                    totalDuplicates += duplicatesToDelete.size();
                    
                    report.append("- Mantido: ").append(users.get(0).getId()).append("<br>");
                    report.append("- Excluídos: ");
                    for (User duplicate : duplicatesToDelete) {
                        report.append(duplicate.getId()).append(" ");
                    }
                    report.append("<br><br>");
                }
            }
            
            if (totalDuplicates == 0) {
                report.append("Nenhum usuário duplicado encontrado!");
            } else {
                report.append("Total de duplicatas removidas: ").append(totalDuplicates);
            }
            
            return report.toString();
        } catch (Exception e) {
            return "Erro ao limpar duplicatas: " + e.getMessage();
        }
    }

   
    @GetMapping("/user/{userId}/comprovante-residencia")
    public ResponseEntity<byte[]> getComprovanteResidencia(@PathVariable String userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getComprovanteResidencia() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(user.getComprovanteResidenciaContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "inline; filename=\"" + user.getComprovanteResidenciaNome() + "\"")
                    .body(user.getComprovanteResidencia());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/comprovante-documento")
    public ResponseEntity<byte[]> getComprovanteDocumento(@PathVariable String userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getComprovanteDocumento() == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(user.getComprovanteDocumentoContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "inline; filename=\"" + user.getComprovanteDocumentoNome() + "\"")
                    .body(user.getComprovanteDocumento());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
