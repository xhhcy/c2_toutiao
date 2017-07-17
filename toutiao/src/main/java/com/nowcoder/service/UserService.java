package com.nowcoder.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;

@Service
public class UserService {
	@Autowired
	UserDAO userDao;
	
	@Autowired
	LoginTicketDAO loginTicketDAO;
	public Map<String,Object> register(String username,String password)
	{
		Map<String,Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(username))
		{
			map.put("msgusername", "用户名不能为空");
			
		}else if(StringUtils.isBlank(password))
		{
			map.put("msgpassword", "密码不能为空");
			
		}
		else if(userDao.selectByName(username)!=null)
		{
			map.put("msgname", "用户名已经被注册");
		}
		else{
		//密码强度
		
		User user=  new User();
		user.setName(username);
		user.setSalt(UUID.randomUUID().toString().substring(0,5));
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
		user.setPassword(password);
		user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
		
		userDao.addUser(user);
		String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
		}
		
		return map;
		
	}
	public Map<String,Object> login(String username,String password)
	{
		Map<String,Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(username))
		{
			map.put("msgusername", "用户名不能为空");
			return map;
			
		}
		
		if(StringUtils.isBlank(password))
			
		{
			map.put("msgpassword", "密码不能为空");
			return map;
		}
		User user  = userDao.selectByName(username);
		//System.out.println(user.getName()+"  "+user.getPassword());
		if(user == null )
		{
			
			map.put("登录失败", "用户名不存在");
			return map;
		}
		if (!user.getPassword().equals(ToutiaoUtil.MD5(password+user.getSalt()))) {
			
			map.put("登录失败", "密码错误");
			return map;
		}
		map.put("user", user);
		String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
		return map;
		
		
	}
	
	private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }
	
	public User getUserById(int id)
	{
		return userDao.selectById(id);
	}
	public void logOut(String ticket)
	{
		loginTicketDAO.updateStatus(ticket, 1);
	}
}
