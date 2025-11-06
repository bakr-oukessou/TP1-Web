package com.isima.tp.Repositories;

import com.isima.tp.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByDestinataireId(Long destinataireId);

    List<Message> findByExpediteurId(Long expediteurId);

    @Query("SELECT m FROM Message m WHERE (m.expediteur.id = ?1 AND m.destinataire.id = ?2) OR (m.expediteur.id = ?2 AND m.destinataire.id = ?1) ORDER BY m.dateEnvoi ASC")
    List<Message> findConversation(Long utilisateurId1, Long utilisateurId2);
}