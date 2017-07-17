package com.nowcoder.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.QiniuService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;

@Controller
public class NewsController {
	private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
	
	@Autowired
	NewsService newsService;
	@Autowired
	QiniuService qiniuService;
	
	
	@Autowired
	HostHolder hostHolder;
	@Autowired
	UserService userService;
	
	@Autowired
	CommentService commentService;
	
	
	 @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
	 @ResponseBody
	 public void getImage(@RequestParam("name") String imageName,
	                         HttpServletResponse response) {
	        try {
	            response.setContentType("image/jpeg");// 这里不太懂
	            StreamUtils.copy(new FileInputStream(new
	                    File(ToutiaoUtil.LOCAL_IMAGE_DIR+ imageName)), response.getOutputStream());
	        } catch (Exception e) {
	            logger.error("读取图片错误" + imageName + e.getMessage());
	        }
	    }
	 @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
	 public String addComment(@RequestParam("newsId") int newsId,
			 @RequestParam("content") String content) {
	        try {
	            Comment comment = new Comment();
	            comment.setContent(content);
	            comment.setCreatedDate(new Date());
	            comment.setEntityId(newsId);
	            comment.setEntityType(EntityType.ENTITY_NEWS);
	            comment.setStatus(0);
	            comment.setUserId(hostHolder.getUser().getId());
	            commentService.addComment(comment);
	            
	            //更新评论数量
	            int count = commentService.getCommentCount(newsId, EntityType.ENTITY_NEWS);
	            newsService.updateCommentCount(count, newsId);
	            
	        } catch (Exception e) {
	            logger.error("添加评论失败" +e.getMessage());
	        }
	        return "redirect:/news/"+String.valueOf(newsId);
	    }
	 
	@RequestMapping(path={"/news/{newsId}"},method = {RequestMethod.GET})
	public String newsDetail(@PathVariable("newsId") int newsId,Model model){
		News news = newsService.getById(newsId);
		
		if(news!= null)
		{
			List<ViewObject> commentVos = new ArrayList<ViewObject>();
			List<Comment> comments = commentService.getCommentByEntity(news.getId(), EntityType.ENTITY_NEWS);
			for(Comment comment:comments)
			{
				ViewObject commentVo = new ViewObject();
				commentVo.set("comment", comment);
				commentVo.set("user",userService.getUserById(comment.getUserId()));
				commentVos.add(commentVo);
				model.addAttribute("comments",commentVos);
			}
		}
		model.addAttribute("news", news);
		model.addAttribute("owner", userService.getUserById(news.getUserId()));
		
		return "detail";
		
	}
	@RequestMapping(path = {"/uploadImage/"},method={RequestMethod.POST})
	@ResponseBody
	public String upLoadImage(@RequestParam("file") MultipartFile file)
	{
		try {
			//String fileUrl = newsService.saveImage(file);
			String fileUrl =qiniuService.saveImage(file);
			
			if(fileUrl == null)
				return ToutiaoUtil.getJSONString(1,"上传图片失败");
			else 
				return ToutiaoUtil.getJSONString(0,fileUrl);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("上传图片失败"+e.getMessage());
			return ToutiaoUtil.getJSONString(1,"上传图片失败");
		}
		
	}
	@RequestMapping(path={"/user/addNews"},method={RequestMethod.POST})
	@ResponseBody
	public String addNews(@RequestParam("image") String image,
						  @RequestParam("title") String title,
						  @RequestParam("link") String link)
						  {
		
		try {
			News news = new News();
			news.setCreatedDate(new Date());
			news.setImage(image);
			news.setTitle(title);
			news.setLink(link);
			if(hostHolder.getUser()!=null)
				news.setUserId(hostHolder.getUser().getId());
			else {
				//设置匿名用户
				news.setUserId(1);
			}
			newsService.addNews(news);
			return ToutiaoUtil.getJSONString(0);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("添加咨詢失敗"+e.getMessage());
			return ToutiaoUtil.getJSONString(1,"發佈失敗");
		}
						  }
	
}
