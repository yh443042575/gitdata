package edu.hit.yh.gitdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        List<Integer> list1 = new ArrayList<Integer>();
        Integer a = 1;
        list1.add(a);
        
        List<Integer> list2 = new ArrayList<Integer>(list1);
        
        
        list1.set(0, 2);
        
        System.out.println(list1.get(0));
        System.out.println(list2.get(0));
    	
    }
}
