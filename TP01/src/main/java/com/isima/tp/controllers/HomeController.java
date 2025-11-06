package com.isima.tp.Controllers;

import com.isima.tp.business.AnnonceService;
import com.isima.tp.business.MessageService;
import com.isima.tp.business.UserService;
import com.isima.tp.Repositories.AnnonceRepository;
import com.isima.tp.Repositories.MessageRepository;
import com.isima.tp.Repositories.UserRepository;
import com.isima.tp.models.Annonce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private AnnonceRepository annonceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Get statistics
        long totalAnnonces = annonceRepository.count();
        long totalUsers = userRepository.count();
        long totalMessages = messageRepository.count();
        
        // Get recent announcements (last 6)
        List<Annonce> recentAnnonces = annonceRepository.findAll().stream()
                .sorted((a1, a2) -> a2.getDatePublication().compareTo(a1.getDatePublication()))
                .limit(6)
                .collect(Collectors.toList());
        
        model.addAttribute("totalAnnonces", totalAnnonces);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalMessages", totalMessages);
        model.addAttribute("recentAnnonces", recentAnnonces);
        
        return "index";
    }
    
    @GetMapping("/health")
    public String health() {
        return "redirect:/";
    }
}
