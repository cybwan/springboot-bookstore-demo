package io.flomesh.demo.simple.dubbo;

import lombok.extern.slf4j.Slf4j;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import io.flomesh.demo.api.HttpbinService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Date;
import java.text.SimpleDateFormat;

@Controller
@Slf4j
@Profile("dubbo")
public class CurlController {

	@GetMapping("/qps")
	public @ResponseBody String qps(@RequestParam(name = "q") int targetQps,
			@RequestParam(name = "c") int concurrencyLevel) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		AtomicLong escapeTime = new AtomicLong(0);
		AtomicLong totalRequests = new AtomicLong(0);
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(concurrencyLevel);
		CountDownLatch latch = new CountDownLatch(targetQps);

		int durationSeconds = 1;

		// 计算每次请求间隔（纳秒）
		long intervalNanos = TimeUnit.SECONDS.toNanos(1) / targetQps;

		Runnable requestTask = () -> {
			Date requestDate = new Date();
			String requestTime = sdf.format(requestDate);
			System.out.println("请求时间：" + requestTime);
			try {
				// 记录开始时间
				long startTime = System.nanoTime();
				httpbinService.hostname();
				// 记录结束时间
				long endTime = System.nanoTime();
				escapeTime.addAndGet(endTime - startTime);
				totalRequests.incrementAndGet();
			} catch (Exception ignored) {
			} finally {
				latch.countDown();
			}
		};

		Date beginDate = new Date();
		String beginTime = sdf.format(beginDate);
		System.out.println("任务开始时间：" + beginTime);

		// 启动请求发射器
		scheduler.scheduleAtFixedRate(requestTask, 0, intervalNanos, TimeUnit.NANOSECONDS);

		// 自动停止
		scheduler.schedule(() -> scheduler.shutdownNow(), durationSeconds, TimeUnit.SECONDS);

		// 等待所有请求完成
		try {
			latch.await();
		} catch (InterruptedException e) {
//			log.error("Thread interrupted", e);
		}

		scheduler.shutdownNow();

		Date endDate = new Date();
		String endTime = sdf.format(endDate);
		System.out.println("任务结束时间：" + endTime);

		return "totalRequests: " + totalRequests.get() + ", concurrencyLevel: " + concurrencyLevel + ", totalTime: "
				+ escapeTime.get() + "ns, avg: " + (escapeTime.get() / totalRequests.get()) + "ns";
	}

	@GetMapping("/meter")
	public @ResponseBody String meter(@RequestParam(name = "n") int totalRequests,
			@RequestParam(name = "c") int concurrencyLevel) {
		if (totalRequests < 1) {
			totalRequests = 1;
		}
		if (concurrencyLevel < 1) {
			concurrencyLevel = 1;
		}

		AtomicLong escapeTime = new AtomicLong(0);
		ExecutorService threadPool = Executors.newFixedThreadPool(concurrencyLevel);
		CountDownLatch latch = new CountDownLatch(concurrencyLevel);

		// 每个线程执行 (totalRequests / concurrencyLevel) 次请求
		int requestPerThread = totalRequests / concurrencyLevel;
		totalRequests = requestPerThread * concurrencyLevel;

		for (int i = 0; i < concurrencyLevel; i++) {
			threadPool.execute(() -> {
				try {
					// 记录开始时间
					long startTime = System.nanoTime();
					for (int j = 0; j < requestPerThread; j++) {
						httpbinService.hostname();
					}
					// 记录结束时间
					long endTime = System.nanoTime();
					escapeTime.addAndGet(endTime - startTime);
				} finally {
					latch.countDown();
				}
			});
		}

		// 等待所有线程完成
		try {
			latch.await();
		} catch (InterruptedException e) {
//			log.error("Thread interrupted", e);
		}

		threadPool.shutdown();
		return "totalRequests: " + totalRequests + ", concurrencyLevel: " + concurrencyLevel + ", totalTime: "
				+ escapeTime.get() + "ns, avg: " + (escapeTime.get() / totalRequests) + "ns";
	}

	@GetMapping("/")
	public @ResponseBody String index(@RequestParam int count) {
		if (count < 1) {
			count = 1;
		}
		// 记录开始时间
		long startTime = System.nanoTime();
		for (int i = 0; i < count; i++) {
			httpbinService.hostname();
		}
		// 记录结束时间
		long endTime = System.nanoTime();
		return "endTime:" + endTime + " - startTime:" + startTime + "=" + (endTime - startTime) + " count:" + count
				+ " avg:" + ((endTime - startTime) / count) + "ns";
	}

	@GetMapping("/hostname")
	public @ResponseBody String hostname() {
		return httpbinService.hostname();
	}

	@DubboReference(version = "v1")
	private HttpbinService httpbinService;
}
