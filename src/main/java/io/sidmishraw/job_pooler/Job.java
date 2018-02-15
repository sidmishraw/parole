/**
 * Job.java
 * 
 * @author Sidharth Mishra <sidmishraw@gmail.com>
 * @description
 * @created Feb 15, 2018 12:15:06 AM
 * @last-modified Feb 15, 2018 12:15:06 AM
 * @copyright 2018 Sidharth Mishra
 */
package io.sidmishraw.job_pooler;

import java.util.Observable;
import java.util.function.Supplier;

import lombok.Builder;

/**
 * @author sidmishraw
 *
 *         Qualified Name: io.sidmishraw.job_pooler.Job
 *
 */
@Builder
public class Job<R> extends Observable {
    
    /**
     * 
     */
    private Supplier<R> todo;
    
    /**
     * @param jobPool
     */
    public void perform(JobPool jobPool) {
        
        new Thread(() -> {
            
            R result = this.todo.get();
            
            this.setChanged();
            this.notifyObservers(result);
        }).start();
    }
    
}
