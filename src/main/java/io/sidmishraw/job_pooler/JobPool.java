/**
 * JobPool.java
 * 
 * @author Sidharth Mishra <sidmishraw@gmail.com>
 * @description
 * @created Feb 15, 2018 12:14:11 AM
 * @last-modified Feb 15, 2018 12:14:11 AM
 * @copyright 2018 Sidharth Mishra
 */
package io.sidmishraw.job_pooler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;

import lombok.Getter;
import lombok.NonNull;

/**
 * @author sidmishraw
 *
 *         Qualified Name: io.sidmishraw.job_pooler.JobPool
 *
 */
public class JobPool {
    
    /**
     * 
     */
    private static transient JobPool self = null;
    
    /**
     * 
     */
    private @Getter LinkedBlockingDeque<Job<?>> jobQ;
    
    /**
     * 
     */
    private @Getter Thread poolWorker;
    
    /**
     * 
     */
    private @Getter Boolean active;
    
    /**
     * 
     */
    private @NonNull @Getter Map<UUID, Promise<?>> promises;
    
    /**
     * @return
     */
    public static synchronized JobPool getInstance() {
        
        if (Objects.isNull(self)) self = new JobPool();
        
        return self;
    }
    
    /**
     * 
     */
    private JobPool() {
        
        this.jobQ = new LinkedBlockingDeque<>();
        this.active = true;
        this.promises = new HashMap<>();
        
        this.poolWorker = new Thread(() -> {
            
            try {
                
                while (this.active) {
                    
                    Job<?> job = this.jobQ.take();
                    job.perform(this);
                }
            } catch (Exception e) {
                
                e.printStackTrace();
            }
        });
        this.poolWorker.start();
    }
    
    /**
     * 
     */
    public void shutdown() {
        
        this.active = false;
    }
    
    /**
     * @param job
     * @return
     */
    public <R> Promise<R> submit(Job<R> job) {
        
        Promise<R> promise = new Promise<>(job);
        
        this.jobQ.add(job);
        this.promises.put(promise.getId(), promise);
        
        return promise;
    }
}
