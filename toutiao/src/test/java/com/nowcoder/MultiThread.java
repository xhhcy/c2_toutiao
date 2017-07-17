package com.nowcoder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.bag.SynchronizedBag;

import com.nowcoder.service.NewsService;


class MyThread extends Thread{
	private int id=0;
	private static Object object = new Object();
	public MyThread(int id)
	{
		this.id = id;
	}
	@Override
	public void run() {
		synchronized (object) {
			try {
				for (int i = 0; i < 10;i++) {
					System.out.println("T"+id+":"+i);
					sleep(1000);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}
	
}
class Producer implements Runnable{
	private BlockingQueue<String> bq;
	public Producer(BlockingQueue<String> bq) {
		this.bq = bq;
	}
	@Override
	public void run() {

			try {
				for (int i = 0; i < 100; i++) {
					System.out.println(Thread.currentThread().getName()+":"+i);
					bq.put(String.valueOf(i));
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		
	}
	
}

class Consumer implements Runnable{
	private BlockingQueue<String> bq;
	public Consumer(BlockingQueue<String> bq) {
		this.bq = bq;
	}
	@Override
	public void run() {

			try {
				while(true)
				{
					System.out.println(Thread.currentThread().getName()+":"+bq.take());
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		
	}
	
}

public class MultiThread {

	
	public static int count =0;
	private static final Object object = new Object();
	public static  void testWithMultiThread()
	{
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
						for (int j = 0; j < 10;j++) {
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							synchronized (object) {
								count++;
							}
							
							System.out.println(count);
						}
				}
			}).start();
		}
	}
	public static void testVolatile()
	{
		testWithMultiThread();
	}
	
	public static void testExecutor() 
	{
		ExecutorService service =  Executors.newFixedThreadPool(2);
		service.submit(new Runnable() {
			
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						TimeUnit.SECONDS.sleep(1);
						System.out.println("executor1:"+i);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
		});
		service.shutdownNow();
service.submit(new Runnable() {
			
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					try {
						TimeUnit.SECONDS.sleep(1);
						System.out.println("executor2:"+i);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					
				}
				
			}
		});
		
		while(!service.isTerminated())
		{
			try {
				TimeUnit.SECONDS.sleep(1);
				System.out.println("wait for terminated");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
	}
	public static void testBlockingQueue()
	{
		BlockingQueue<String> bq = new ArrayBlockingQueue<String>(10);
		Producer producer = new Producer(bq);
		Consumer consumer = new Consumer(bq);
		Consumer consumer1 = new Consumer(bq);
		new Thread(producer,"producer").start();
		new Thread(consumer,"consumer").start();
		new Thread(consumer,"consumer1").start();
	}
	public static void sleep(long i)
	{
		try {
			TimeUnit.SECONDS.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testFuture()
	{
		ExecutorService service = Executors.newSingleThreadExecutor();
		Future<Integer> future = service.submit(new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				// TODO Auto-generated method stub
				sleep(1);
				return 1;
			}
		});
		try {
			System.out.println(future.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				sleep(2);
				service.shutdown();
				
			}
		}).start();
		while(true)
		{
			if(!service.isTerminated())
			{
				sleep(1);
				
				System.out.println("wait for executor");
			}else {
				System.out.println("shutdown");
				break;
			}
			
		}
		
		
	}
	public static void main(String[] args) {
		
		//testVolatile();
		//testExecutor();
		testFuture();
	}
	
}
