package com.su.component;

import com.su.util.HackInputStream;
import com.su.util.HackSystem;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JavaClassExecutor {

    public String execute(byte[]classBytes,String systemIn)
    {

        //先将字节数组中的System和Scanner进行替换
        ClassModifier classModifiler=new ClassModifier(classBytes);
        byte[] modifyBytes=classModifiler.modifyUTF8("java/lang/System","com/su/util/HackSystem");
        modifyBytes=classModifiler.modifyUTF8("java/util/Scanner","com/su/util/HackScanner");

        //设置用户的输入HackScanner
        ((HackInputStream)HackSystem.in).set(systemIn);

        //new一个加载器来进行加载类
        HotSwapClassLoader hotSwapClassLoader=new HotSwapClassLoader();
        Class clazz=hotSwapClassLoader.loadByte(modifyBytes);
        //反射调用main方法
        try{
//            获取方法，第二个参数是表示形参的class类型
            Method method=clazz.getMethod("main",new Class[]{String[].class});
//            第一个参数是对应的对象，第二个参数是传入形参，main方法的形参为String[]
            method.invoke(null,new String[]{null});
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
//        这部分异常是客户端方法进行反射调用时出现的异常，
//        因此要将抛出的异常封装进入HackSystem err，
//        返回给客户端。
        catch (InvocationTargetException e)
        {
            e.getCause().printStackTrace(HackSystem.err);
        }

        String res=HackSystem.getBufferString();
        //移除threadlocal
        HackSystem.closeBuffer();
        return res;
    }
}
