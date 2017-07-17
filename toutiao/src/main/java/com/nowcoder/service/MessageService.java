package com.nowcoder.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;

@Service
public class MessageService {
	@Autowired
	MessageDAO messageDAO;
	public void addMessage(Message message)
	{
		if(message.getConversationId()==null)
		{
			int fromId = message.getFromId();
			int toId = message.getToId();
			message.setConversationId(fromId<toId?String.format("%d_%d", fromId,toId):String.format("%d_%d",toId,fromId));
		}
		messageDAO.addMessage(message);
	}
	public List<Message> getConversationDetail(String conversationId)
	{
		return messageDAO.getConversationDetail(conversationId, 0, 10);
	}
	public List<Message> getConversationList(int userId)
	{
		
		return messageDAO.getConversationList(userId, 0, 10);
	}
	public int getUnread(int userId,String conversationId)
	{
		return messageDAO.getConversationUnReadCount(userId, conversationId);
	}
	public void readMessage(int userId,String conversationId)
	{
		messageDAO.updateHasRead(userId, conversationId);
	}
	
}
