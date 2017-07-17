package com.nowcoder.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;

@Service
public class CommentService {
	@Autowired
	private CommentDAO commentDAO;
	public List<Comment> getCommentByEntity(int entityId,EntityType entityType)
	{
		return commentDAO.selectByEntity(entityId, entityType);
		
	}
	public int getCommentCount(int entityId,EntityType entityType)
	{
		return commentDAO.getCommentCount(entityId, entityType);
	}
	public void addComment(Comment comment)
	{
		commentDAO.addComment(comment);
	}
	public void deleteComment(int entityId,EntityType entityType)
	{
		commentDAO.updateStatus(entityId, entityType, 1);
	}

}
