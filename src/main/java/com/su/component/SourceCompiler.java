package com.su.component;


import lombok.Getter;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceCompiler {

    private static Pattern CLASS_PATTERN=Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
    private static Map<String,JavaFileObject>fileObejctMap=new ConcurrentHashMap<>();

    public static byte[]compile(String source,DiagnosticCollector<JavaFileObject> compileCollector)
    {
//        获取动态编译器
        JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();

        JavaFileManager javaFileManager=
                new TmpJavaFileManager(compiler.getStandardFileManager(compileCollector,null,null));
//        获取类名
        Matcher matcher=CLASS_PATTERN.matcher(source);
        String className;
        if(matcher.find())
        {
//            整个字符串为class run，所以不能全部取，而是取出run
//            这边取第一列，第0列是个class
            className=matcher.group(1);
        }
        else
        {
            throw new IllegalArgumentException("No valid class");
        }
        JavaFileObject sourceJavaFileObeject=new TmpJavaFileObject(className,source);
        Boolean result=compiler.getTask(
                null,javaFileManager,compileCollector,null,null, Arrays.asList(sourceJavaFileObeject)).call();

        JavaFileObject resJavaFileObeject=fileObejctMap.get(className);
        if(result&&resJavaFileObeject!=null)
        {
            return ((TmpJavaFileObject)resJavaFileObeject).getOutputStream().toByteArray();
        }
        return null;
    }

    public  static class TmpJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        public TmpJavaFileManager(JavaFileManager javaFileManager)
        {
            super(javaFileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput
                (JavaFileManager.Location location,String className,JavaFileObject.Kind kind,FileObject sibling)
                throws IOException
        {
            JavaFileObject javaFileObject=new TmpJavaFileObject(className,kind);
            fileObejctMap.put(className,javaFileObject);
            return javaFileObject;
        }
    }

    @Getter
    public static class TmpJavaFileObject extends SimpleJavaFileObject {

//        用于存放已经编译好的类
//      源码
        private  String source;
//      源码编译后的字节码
        private  ByteArrayOutputStream outputStream;

        public TmpJavaFileObject(String name, String source) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        public TmpJavaFileObject(String name, Kind kind) {
            super(URI.create("String:///" + name + Kind.SOURCE.extension), kind);
            this.source = null;
        }

        @Override
//        重写父类方法，获取编译文件的字符
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
        {
            if(source==null)
            {
                throw new IllegalArgumentException("souce is null");
            }
            return source;
        }

        @Override
        public OutputStream openOutputStream() throws  IOException
        {
            outputStream=new ByteArrayOutputStream();
            return outputStream;
        }


    }
}
