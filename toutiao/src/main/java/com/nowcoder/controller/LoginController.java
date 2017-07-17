package com.nowcoder.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.User;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;
    
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String register(@RequestParam(value = "username") String username,
                          @RequestParam(value = "password") String password,
                          @RequestParam(value = "rember",defaultValue="1") int remember,
                          HttpServletResponse response) {
    	try{
    		Map<String, Object> map =userService.register(username, password);
    		if (map.containsKey("ticket"))
    		{
    			Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
    			cookie.setPath("/");
    			response.addCookie(cookie);
    			
    			
    			
    			return ToutiaoUtil.getJSONString(0, "注册成功,欢迎--"+username+"---来到王者峡谷"); 
    		}
    		return ToutiaoUtil.getJSONString(0, map); 
    		
    	}catch(Exception e)
    	{
    		logger.error("注册异常"+e.getMessage());
    		return ToutiaoUtil.getJSONString(1, "注册异常");
    	}
    	
    }
    
    @RequestMapping(path = {"/login/"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String login(@RequestParam(value = "username") String username,
                          @RequestParam(value = "password") String password,
                          @RequestParam(value = "rember",defaultValue="1") int remember,
                          HttpServletResponse response) {
    	try{
    		Map<String, Object> map =userService.login(username, password);
    		User user = (User)map.get("user");
    		if (map.containsKey("ticket"))                   
    		{
    			Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
    			cookie.setPath("/");
    			response.addCookie(cookie);
    			
    			
    			
    			eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                .setActorId(user.getId()));
    			return ToutiaoUtil.getJSONString(0, "登录成功,欢迎--"+username+"---来到王者荣耀"); 
    		}
    		else {
				
    			return ToutiaoUtil.getJSONString(1, map); 
			}
    		
    	}catch(Exception e)
    	{
    		logger.error("登录异常"+e.getMessage());
    		return ToutiaoUtil.getJSONString(1, "登录异常");
    	}
    	
    }
    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logOut(@CookieValue("ticket") String ticket)
    {
    	userService.logOut(ticket);
    	return "redirect:/";
    }
    
}
