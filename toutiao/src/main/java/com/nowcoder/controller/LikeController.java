package com.nowcoder.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.ToutiaoUtil;

@Controller
public class LikeController {
	@Autowired
	LikeService likeService;
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	NewsService newsService;
	
	@Autowired
	EventProducer eventProducer;
	
	@Autowired
	JedisAdapter jedisAdapter;
	
	
	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	
	@RequestMapping(path={"/like"},method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String like(@RequestParam("newsId") int newsId)
	{
		try {
			int userId = hostHolder.getUser().getId();
			
			long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);
			
			
			newsService.updateLikeCount((int)likeCount, newsId);
			
			
			EventModel eventModel = new EventModel();
			eventModel.setActorId(hostHolder.getUser().getId())
					  .setEntityType(EntityType.ENTITY_NEWS)
					  .setEntityId(newsId)
					  .setType(EventType.LIKE)
					  .setEntityOwnerId(newsService.getById(newsId).getUserId());
			eventProducer.fireEvent(eventModel);
			return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
			
		} catch (Exception e) {
			logger.error("异常"+e.getMessage());
			
			return ToutiaoUtil.getJSONString(1, "出现异常");
		}
		
	}
	@RequestMapping(path={"/dislike"},method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String dislike(@RequestParam("newsId") int newsId)
	{
		try {
			int userId = hostHolder.getUser().getId();
			
			long likeCount = likeService.disLike(userId, EntityType.ENTITY_NEWS, newsId);
			
			
			newsService.updateLikeCount((int)likeCount, newsId);
			return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
			
		} catch (Exception e) {
			logger.error("异常"+e.getMessage());
			
			return ToutiaoUtil.getJSONString(1, "出现异常");
		}
		
	}
}
