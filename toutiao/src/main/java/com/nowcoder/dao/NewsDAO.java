package com.nowcoder.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.nowcoder.model.News;

@Mapper
public interface NewsDAO {
	String TABLE_NAME = "news";
	String INSERT_FIELD = " title,link,image,like_count,comment_count,"
			+ "created_date,user_id ";
	String SELECT_FIELD = " id,title,link,image,like_count,comment_count, "
			+ "created_date,user_id ";
	
	@Insert({"insert into "+TABLE_NAME+"("+INSERT_FIELD+") values (#{title},#{link},#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})"})
	int addNews(News news);
	@Update({"update",TABLE_NAME,"set comment_count=#{count} where id=#{newsId}"})
	int updateCommentCount(@Param("count") int count,@Param("newsId") int newsId);
	
	
	@Update({"update",TABLE_NAME,"set like_count=#{count} where id=#{newsId}"})
	int updateLikeCount(@Param("count") int count,@Param("newsId") int newsId);
	
	List<News> selectByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset,
            @Param("limit") int limit);
	@Select({"select ",SELECT_FIELD," from ",TABLE_NAME ,"where id=#{newsId}"})
	News getNewsById(int newsId);
	
}
