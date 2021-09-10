package com.su.component;

public class HotSwapClassLoader extends ClassLoader{
    public HotSwapClassLoader(){
        super(HotSwapClassLoader.class.getClassLoader());
    }

//    这里没定义为静态方法，这是因为每一来一个类创建一个加载器来进行加载，保证用户的反复提交能够被反复加载
//    另外的原因就是，当类加载器失效，不存在类实例，不存在类被反射调用，这个类信息才能在方法区中被卸载
    public Class loadByte(byte[] classBytes)
    {
        return defineClass(null,classBytes,0,classBytes.length);
    }
}
