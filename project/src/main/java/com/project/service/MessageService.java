package com.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.project.model.Message;
import com.project.model.Reply;
import com.project.repository.MessageRepository;
import com.project.repository.ReplyRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReplyRepository replyRepository;

    // Save a new message
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    // Save a reply to an existing message
    public Reply saveReply(Long messageId, Reply reply) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message with ID " + messageId + " not found"));

        reply.setMessage(message);  // Associate the reply with the message
        return replyRepository.save(reply);  // Save the reply
    }
}