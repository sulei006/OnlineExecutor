package com.su;


import com.su.service.ExecuteService;

public class test {
    public static void main(String[] argvs)
    {
        String code=new String("public class Run {\n" +
                "    public static void main(String[] args) {\n" +
                "       System.out.println(\"hello world\"); \n" +
                "    }\n" +
                "}");
        String input=new String("");
        ExecuteService e=new ExecuteService();
        System.out.println(e.execute(code,input));
    }
}
