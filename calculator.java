/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */


package com.mycompany.calculator;

/**
 *
 * @author mahdi
 */

import java.util.Scanner;

public class Calculator {
    static void operators(){
        System.out.println("+, -, *, /, %");
    }

    public static void main(String[] args) {
        Scanner obj = new Scanner(System.in);
        
        System.out.println("Enter first number: ");
        int fnum = obj.nextInt();
        //System.out.println(fnum);
        
        System.out.println("Enter second number: ");
        int snum = obj.nextInt();
        //System.out.println(snum);
        obj.next();
        
        System.out.println("Choose an operator to use");
        operators();
        String op = obj.nextLine();
        //System.out.println(op);
        
        switch(op){
            case "+":
                System.out.println(fnum + snum);
                break;
            case "-":
                System.out.println(fnum - snum);
                break;
            case "*":
                System.out.println(fnum * snum);
                break;
            case "/":
                if(snum != 0){
                    System.out.println(fnum / snum);
                } else {
                    System.out.println("Error cannot dive by zero");
                }
                break;
            case "%":
                System.out.println(fnum % snum);
                break;
            default:
                System.out.println("Enter a valid opartor");
        }
        /*System.out.println("Enter Username: ");
        
        String userName = obj.nextLine();
        System.out.println(userName);*/
    }
}
