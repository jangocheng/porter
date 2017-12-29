/**
 * All rights Reserved, Designed By Suixingpay.
 *
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月24日 11:32
 * @Copyright ©2017 Suixingpay. All rights reserved.
 * 注意：本内容仅限于随行付支付有限公司内部传阅，禁止外泄以及用于其他的商业用途。
 */
package com.suixingpay.datas.node.task.transform;

import com.suixingpay.datas.common.util.ApplicationContextUtils;
import com.suixingpay.datas.node.core.event.ETLBucket;
import com.suixingpay.datas.node.core.task.AbstractStageJob;
import com.suixingpay.datas.node.core.task.StageType;
import com.suixingpay.datas.node.datacarrier.DataCarrier;
import com.suixingpay.datas.node.datacarrier.DataCarrierFactory;
import com.suixingpay.datas.node.task.transform.transformer.TransformFactory;
import com.suixingpay.datas.node.task.worker.TaskWork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiFunction;

/**
 * 多线程执行,完成字段、表的映射转化。
 * @author: zhangkewei[zhang_kw@suixingpay.com]
 * @date: 2017年12月24日 11:32
 * @version: V1.0
 * @review: zhangkewei[zhang_kw@suixingpay.com]/2017年12月24日 11:32
 */
public class TransformJob extends AbstractStageJob {
    private final TransformFactory transformFactory;
    private final ExecutorService executorService;
    private final Map<String, Future<ETLBucket>> carrier = new ConcurrentHashMap<>();
    private final TaskWork work;
    public TransformJob(TaskWork work) {
        super(work.getBasicThreadName());
        this.work = work;
        transformFactory = ApplicationContextUtils.INSTANCE.getBean(TransformFactory.class);
        //线程阻塞时，在调用者线程中执行
        executorService = new ThreadPoolExecutor(LOGIC_THREAD_SIZE, LOGIC_THREAD_SIZE,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                getThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    protected void doStop() {
        executorService.shutdown();
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void loopLogic() {
        //只要队列有消息，持续读取
        ETLBucket bucket = null;
        do {
            try {
                bucket = work.waitEvent(StageType.EXTRACT);
                if (null != bucket) {
                    final ETLBucket inThreadBucket = bucket;
                    Future<ETLBucket> result = executorService.submit(new Callable<ETLBucket>() {
                        @Override
                        public ETLBucket call() throws Exception {
                            try {
                                transformFactory.transform(inThreadBucket, work.getTableMapper());
                            } catch (Exception e) {
                                LOGGER.error("批次[{}]执行TransformJob失败!", inThreadBucket.getSequence(), e);
                            }
                            return inThreadBucket;
                        }
                    });
                    carrier.put(inThreadBucket.getSequence() + "", result);
                }
            } catch (Exception e) {
                LOGGER.error("transform ETLBucket error!", e);
            }
        } while (null != bucket);
    }

    @Override
    public ETLBucket output() throws ExecutionException, InterruptedException {
        Long sequence = work.waitSequence();
        Future<ETLBucket> result = null != sequence ? carrier.computeIfPresent(sequence + "", new BiFunction<String, Future<ETLBucket>, Future<ETLBucket>>() {
            @Override
            public Future<ETLBucket> apply(String key, Future<ETLBucket> etlBucketFuture) {
                carrier.remove(key);
                return etlBucketFuture;
            }
        }) : null;
        return null != result ? result.get() : null;
    }
}
