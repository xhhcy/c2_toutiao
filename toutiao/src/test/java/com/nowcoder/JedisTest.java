package com.nowcoder;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nowcoder.model.User;
import com.nowcoder.util.JedisAdapter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class JedisTest {
	@Autowired
	JedisAdapter jedisAdapter;

	@Test
	public void jedisTest() {
		User user = new User();
		user.setName("xhh");
		user.setPassword("123");
		jedisAdapter.setObject("user1", user);
		
		User user1 = jedisAdapter.getObjet("user1", User.class);
		System.out.print(ToStringBuilder.reflectionToString(user1));
	}
}
