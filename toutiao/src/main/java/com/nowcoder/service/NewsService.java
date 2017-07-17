package com.nowcoder.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;

@Service
public class NewsService {
	@Autowired
	private NewsDAO newsDao;
	
	
	public List<News> getLatedNews(int userId,int offset,int limit)
	{
		return newsDao.selectByUserIdAndOffset(userId, offset, limit);
	}
	public News getById(int newsId)
	{
		return newsDao.getNewsById(newsId);
	}
	public void addNews(News news)
	{
		newsDao.addNews(news);
		
	}
	public void updateCommentCount(int count,int newsId)
	{
		newsDao.updateCommentCount(count, newsId);
	}
	
	public void updateLikeCount(int count,int newsId)
	{
		newsDao.updateLikeCount(count, newsId);
	}
	public String saveImage(MultipartFile file) throws IOException
	{
		
		int dotPos = file.getOriginalFilename().lastIndexOf(".");
		if(dotPos<0)
			return null;
		String fileExt = file.getOriginalFilename().substring(dotPos+1).toLowerCase();
		if(!ToutiaoUtil.isFileAllowed(fileExt))
			return null;
		
		//由于图片数据库中可能有图片名重复的情况，故需要用UUID 生成一个唯一的图片名
		String fileName  = UUID.randomUUID().toString().replace("-", "")+"."+fileExt;
		//将上传上来的图片通过复制二进制流的方式复制到指定目录
		Files.copy(file.getInputStream(), new File(ToutiaoUtil.LOCAL_IMAGE_DIR+fileName).toPath(),
				StandardCopyOption.REPLACE_EXISTING);
		return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
		
	}
}
