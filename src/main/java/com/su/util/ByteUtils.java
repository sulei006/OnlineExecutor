package com.su.util;

import java.util.Arrays;

//用于修改byte数组，和获取byte数组内容
public class ByteUtils {
    public static int byte2Int(byte []a,int start,int len)
    {
        int res=0;
        int end=start+len;
        for(int i=start;i<end;i++)
        {
//            一个byte8位，转为int之后只取低八位
            int cur=((int)a[i]&0xff);
//            一个byte占8位，将数字移到其对应位置上
            cur<<=(--len) * 8;
            res+=cur;
        }
        return res;
    }

    public  static  byte[] int2Byte(int num,int len)
    {
//        len表示要转变为字节数组的长度
        byte[] res=new byte[len];
        for(int i=0;i<len;i++)
        {
            res[len-1-i]=(byte)((num>>(8*i))&0xff);
        }
        return res;
    }

    public static String byte2String(byte[] a,int off,int len)
    {
        return new String(a,off,len);
    }
    public static byte[] string2Byte(String str)
    {
        return str.getBytes();
    }

    public static byte[] byteReplace(byte[] old,int off,int len,byte[] rep)
    {
        byte[] res=new byte[old.length-len+rep.length];
        System.arraycopy(old,0,res,0,off);
        System.arraycopy(rep,0,res,off,rep.length);
        System.arraycopy(old,off+len,res,off+rep.length,old.length-off-len);

        return res;
    }
}
