package com.isima.tp.business;


import com.isima.tp.Repositories.MessageRepository;
import com.isima.tp.Repositories.UserRepository;
import com.isima.tp.models.Message;
import com.isima.tp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository utilisateurRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository utilisateurRepository) {
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Envoie un nouveau message entre deux utilisateurs.
     */
    @Transactional
    public Message sendMessage(Long expediteurId, Long destinataireId, String contenu) {
        Optional<User> expediteurOpt = utilisateurRepository.findById(expediteurId);
        Optional<User> destinataireOpt = utilisateurRepository.findById(destinataireId);

        if (expediteurOpt.isPresent() && destinataireOpt.isPresent()) {
            Message message = new Message();
            message.setContenu(contenu);
            message.setExpediteur(expediteurOpt.get());
            message.setDestinataire(destinataireOpt.get());

            return messageRepository.save(message);
        }
        throw new IllegalArgumentException("Expéditeur ou destinataire non trouvé.");
    }

    /**
     * Récupère la conversation complète (messages envoyés et reçus) entre deux utilisateurs.
     */
    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }
    
    /**
     * Récupère tous les messages reçus par un utilisateur
     */
    public List<Message> getMessagesRecus(Long destinataireId) {
        return messageRepository.findByDestinataireId(destinataireId);
    }
    
    /**
     * Récupère tous les messages envoyés par un utilisateur
     */
    public List<Message> getMessagesEnvoyes(Long expediteurId) {
        return messageRepository.findByExpediteurId(expediteurId);
    }
    
    /**
     * Marque un message comme lu
     */
    @Transactional
    public void markAsRead(Long messageId) {
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            Message message = messageOpt.get();
            message.setLu(true);
            messageRepository.save(message);
        } else {
            throw new IllegalArgumentException("Message non trouvé.");
        }
    }
}