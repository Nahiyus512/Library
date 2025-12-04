package com.wkc.library;


import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoniao
 * @date 2023/2/24 20:05
 */
public class testOther {


    @Test
    public void test1(){
        BigInteger aa;
        BigInteger bb;
        BigInteger cc;

        String a = "123";
        String b = "234";
        aa = new BigInteger(a);
        bb = new BigInteger(b);
        cc = aa.add(bb);
//        BigInteger bb = new BigInteger(b);

        //long cc = aa + bb;
        //String dd = String.valueOf(cc);

        String s = cc.toString();
        System.out.println(s);

    }


    @Test
    public void test2(){
        int []arr = {1,0,3,4};

        Arrays.sort(arr);

        System.out.println(arr[0]);
        System.out.println(arr[arr.length-1]);
    }



    @Test
    public void test3(){
        String a = "ABC123def";
        StringBuilder sb = new StringBuilder(a);
        sb.reverse();
        System.out.println(sb);
    }


    @Test
    public void test4(){
        int []num1 = new int[]{1,3};
        int []num2 = new int[]{2,4};

        System.out.println(findMedianSortedArrays(num1,num2));
    }


    public static double findMedianSortedArrays(int[] nums1, int[] nums2) {



        int len = nums1.length + nums2.length;

        int [] arr = new int[len];

        for(int index = 0,i = 0,j = 0;index < arr.length;index++){
            if(i >= nums1.length){
                arr[index] = nums2[j++];
            }else if(j >= nums2.length){
                arr[index] = nums1[i++];
            }else if(nums1[i] <= nums2[j]){
                arr[index] = nums1[i++];
            }else {
                arr[index] = nums2[j++];
            }
        }


        if(len % 2 == 0){
            int d = len / 2;
            int e = len / 2 - 1;
            return (double)(arr[d] + arr[e]) / 2;
        }else{
            int c = len / 2;
            return (double) arr[c];
        }

    }

}
