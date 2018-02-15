/**
 * StandardPooler.java
 * 
 * @author Sidharth Mishra <sidmishraw@gmail.com>
 * @description
 * @created Feb 15, 2018 11:58:24 AM
 * @last-modified Feb 15, 2018 11:58:24 AM
 * @copyright 2018 Sidharth Mishra
 */
package io.sidmishraw.job_pooler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sidmishraw
 *
 *         Qualified Name: io.sidmishraw.job_pooler.StandardPooler
 *
 */
public class StandardPooler {
    
    /**
     * 
     */
    static final JobPool pool = JobPool.getInstance();
    
    static transient List<Integer> ints = new ArrayList<>();
    
    static synchronized void add(int i) {
        ints.add(i);
    }
    
    static synchronized void remove(int i) {
        ints.remove(i);
    }
    
    public static void main(String[] args) throws Exception {
        
        Job<List<Integer>> adder55 = Job.<List<Integer>> builder().todo(() -> {
            
            add(55);
            
            return ints;
        }).build();
        
        Job<List<Integer>> adder95 = Job.<List<Integer>> builder().todo(() -> {
            
            add(95);
            
            return ints;
        }).build();
        
        Job<List<Integer>> remover = Job.<List<Integer>> builder().todo(() -> {
            
            remove(0);
            
            return ints;
        }).build();
        
        System.out.println("ints = " + ints);
        
        pool.submit(adder95).then((t) -> {
            
            System.out.println("adder95 thened = " + t);
            return null;
        }).error((e) -> System.out.println("Error = " + e.getMessage()));
        
        pool.submit(remover).then((t) -> {
            
            System.out.println("remover thened = " + t);
            return null;
        }).error((e) -> System.out.println("Error = " + e.getMessage()));
        
        pool.submit(adder55).then((t) -> {
            
            System.out.println("Thened = " + t);
            return null;
        }).error((e) -> System.out.println("Error = " + e.getMessage()));
    }
}
