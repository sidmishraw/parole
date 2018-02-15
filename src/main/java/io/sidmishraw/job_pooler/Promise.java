/**
 * Promise.java
 * 
 * @author Sidharth Mishra <sidmishraw@gmail.com>
 * @description
 * @created Feb 15, 2018 12:28:48 AM
 * @last-modified Feb 15, 2018 12:28:48 AM
 * @copyright 2018 Sidharth Mishra
 */
package io.sidmishraw.job_pooler;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.Getter;

/**
 * @author sidmishraw
 *
 *         Qualified Name: io.sidmishraw.job_pooler.Promise
 *
 */
public class Promise<R> implements Observer {
    
    public static enum PromiseState {
        PENDING, FULFILLED, REJECTED;
    }
    
    private @Getter PromiseState state;
    
    private Function<R, ?>      thenable;
    private Consumer<Exception> failable;
    
    /**
     * 
     */
    private @Getter UUID id;
    
    private @Getter R result;
    
    /**
     * 
     */
    private Job<R> theJob;
    
    /**
     * @param theJob
     */
    public Promise(Job<R> theJob) {
        
        this.state = PromiseState.PENDING;
        this.id = UUID.randomUUID();
        this.theJob = theJob;
        
        this.theJob.addObserver(this);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable job, Object arg) {
        
        if (Objects.isNull(arg))
            rejected(new Exception("The Job has has failed"));
        else
            fulfilled((R) arg);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    private void fulfilled(R result) {
        
        if (this.state != PromiseState.PENDING) return;
        
        this.state = PromiseState.FULFILLED;
        this.result = result;
        
        if (Objects.isNull(this.result)) return;
        
        this.result = (R) this.thenable.apply(this.result);
    }
    
    /**
     * 
     */
    private void rejected(Exception e) {
        
        if (this.state != PromiseState.PENDING) return;
        
        this.state = PromiseState.REJECTED;
        this.result = null;
        
        this.failable.accept(e);
    }
    
    /**
     * @param transformer
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Promise<R> then(Function transformer) {
        
        if (Objects.isNull(this.thenable))
            this.thenable = transformer;
        else
            this.thenable.andThen(transformer);
        
        return this;
    }
    
    /**
     * @param failable
     * @return
     */
    public Promise<R> error(Consumer<Exception> failable) {
        
        this.failable = failable;
        
        return this;
    }
}
