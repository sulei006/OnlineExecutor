package com.su.service;

import com.su.component.JavaClassExecutor;
import com.su.component.SourceCompiler;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

@Service
public class ExecuteService {
//    限制客户端程序的运行时间，防止恶意代码进行服务器的攻击。
    private static final int RUN_TIME_LIMITED=30;
//      核心线程数的数目
    private static final int N_THREAD=3;

    private static final String WAIT_MSG="服务器正忙，请稍后提交.";
//    没有输出
    private static final String NO_OUTPUT="Nothing.";

    private static final ExecutorService pool=new ThreadPoolExecutor
            (N_THREAD,N_THREAD,0L, TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(5));


    public String execute(String source,String systemIn)
    {

        DiagnosticCollector<JavaFileObject> diagnosticCollector=new DiagnosticCollector<>();
        byte[] classBytes= SourceCompiler.compile(source,diagnosticCollector);

//       编译失败的话，返回错误信息
        if(classBytes==null)
        {
            List<Diagnostic<? extends JavaFileObject>> compilerErrors=diagnosticCollector.getDiagnostics();
//            这里可以用StringBuilder而不是StringBuffer
//              StringBuilder非线程安全，但是效率更高
            StringBuilder compileErrorRes=new StringBuilder();
            for(Diagnostic diagnostic:compilerErrors)
            {
                compileErrorRes.append
                        ("Compilation error at Row: "+diagnostic.getLineNumber()+" Col: "+diagnostic.getColumnNumber()+" .");
                compileErrorRes.append(System.lineSeparator());

                compileErrorRes.append(diagnostic.getMessage(null)+".");
                compileErrorRes.append(System.lineSeparator());
            }
            return compileErrorRes.toString();
        }
        Callable<String> runTask=new Callable<String>() {
            @Override
            public String call() throws Exception {
                return new JavaClassExecutor().execute(classBytes,systemIn);
            }
        };
        Future<String> res=null;
        try {
//            提交线程池处理
            res=pool.submit(runTask);
        }
//        默认拒绝策略，丢弃任务，抛出异常
        catch (RejectedExecutionException e)
        {
            return WAIT_MSG;
        }

//        获取运行结果，Future.get可以设置在规定时间内获取
        String resStr;
        try{
            resStr=res.get(RUN_TIME_LIMITED,TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
//            线程被中断了
            resStr="Program interrupted.";
        }
        catch(ExecutionException e)
        {
            resStr=e.getCause().getMessage();
        }
        catch (TimeoutException e)
        {
            resStr="Time Limit Exceeded.";
        }
        finally {
            //在超时时间内未执行完，则取消当前的任务
            res.cancel(true);
        }
        return resStr==null?NO_OUTPUT:resStr;
    }

}
