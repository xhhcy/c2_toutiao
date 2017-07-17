package com.nowcoder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nowcoder.model.EntityType;
import com.nowcoder.service.LikeService;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class LikeServiceTests {
	
	@Autowired
	LikeService likeService;
	@Test
	public void testLike()
	{
		likeService.like(123, EntityType.ENTITY_NEWS, 1);
		Assert.assertEquals(1, likeService.getLikeStatus(123, EntityType.ENTITY_NEWS, 1));
	}
	
	@Test
	public void testDisLike()
	{
		likeService.disLike(123, EntityType.ENTITY_NEWS, 1);
		Assert.assertEquals(-1, likeService.getLikeStatus(123, EntityType.ENTITY_NEWS, 1));
	}
	@Before
	public void setUp()
	{
		System.out.println("setUp");
	}
	@After
	public void tearDown()
	{
		System.out.println("tearDown");
	}
	@BeforeClass
	public static void before()
	{
		System.out.println("beforeClass");
	}
	@AfterClass
	public static void after()
	{
		System.out.println("afterClass");
	}
	

}
