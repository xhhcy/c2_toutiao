package com.nowcoder.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;

@Controller
public class MessageController {
	
	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	@Autowired
	MessageService messageService;
	@Autowired
	UserService userService;
	
	@Autowired
	HostHolder hostHolder;
	
	@RequestMapping(path={"/msg/list"},method={RequestMethod.GET})
	public String conversionList(Model model)
	{
		try {
			int localUserId = hostHolder.getUser().getId();
			List<Message> messages = messageService.getConversationList(localUserId);
			List<ViewObject> messageVos = new ArrayList<ViewObject>();
			
			for(Message message:messages)
			{
				ViewObject vo = new ViewObject();
				vo.set("conversation", message);
				int targetId = message.getFromId() == localUserId?message.getToId():message.getFromId();
				User user = userService.getUserById(targetId);
				if(user != null)
					vo.set("target", user);
				messageVos.add(vo);
				vo.set("unread", messageService.getUnread(localUserId, message.getConversationId()));
			}
			model.addAttribute("conversations", messageVos);
			return "letter";
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("获取站内信列表失败" + e.getMessage());
		}
		return "letter";
	}
	
	@RequestMapping(path={"/msg/detail"},method={RequestMethod.GET})
	public String conversionDetail(Model model,@Param("conversionId") String conversionId)
	{
		try {
			List<Message> messages = messageService.getConversationDetail(conversionId);
			List<ViewObject> messageVos = new ArrayList<ViewObject>();
			for(Message message:messages)
			{
				ViewObject vo = new ViewObject();
				vo.set("message", message);
				User user = userService.getUserById(message.getFromId());
				if(user != null)
					vo.set("userName", user.getName());
					vo.set("headUrl", user.getHeadUrl());
					vo.set("userId", user.getId());
				messageService.readMessage(hostHolder.getUser().getId(), message.getConversationId());
				messageVos.add(vo);
			}
			model.addAttribute("messages", messageVos);
			return "letterDetail";
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("获取站内信列表失败" + e.getMessage());
		}
		return "letterDetail";
	}
	@RequestMapping(path={"/msg/addMessage"},method={RequestMethod.POST})
	@ResponseBody
	public String addMessage(@RequestParam("fromId") int fromId,@RequestParam("toId") int toId,
			@RequestParam("content") String content)
			{
				try {
					Message message = new Message();
					message.setContent(content);
					message.setFromId(fromId);
					message.setToId(toId);
					message.setCreatedDate(new Date());
					message.setConversationId(fromId<toId?String.format("%d_%d", fromId,toId):String.format("%d_%d",toId,fromId));
					messageService.addMessage(message);
					return ToutiaoUtil.getJSONString(0, String.valueOf(message.getId()));
				} catch (Exception e) {
					// TODO: handle exception
					logger.error("添加消息失败"+e.getMessage());
					return ToutiaoUtil.getJSONString(1,"添加消息失败");
					
				}
			}
			
	
}
