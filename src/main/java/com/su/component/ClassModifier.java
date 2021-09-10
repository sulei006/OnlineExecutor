package com.su.component;

import com.su.util.ByteUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClassModifier {

    //class文件前八个字节为魔数和版本号
    private static final int CONSTANT_POOP_COUNT_INDEX=8;

//    utf8的tag为1
    private static  final int CONSTANT_UTF8_INFO=1;

//    11种常量的长度，通过CONSTANT_ITEM_LENGTH[TAG]来取得
    private static final int[] CONSTANT_ITEM_LENGTH={-1,-1,-1,5,5,9,9,3,3,5,5,5,5,};

//    tag占用一个字节，len占用两个字节
    private static final int u1=1;
    private static final int u2=2;

    private byte[] classByte;

//    获取class文件中常量个数
    public int getConstantPoopCount(){
        return ByteUtils.byte2Int(classByte,CONSTANT_POOP_COUNT_INDEX,u2);
    }

//    将字节数组中，包含oldStr字符串的内容修改为newStr
    public byte[] modifyUTF8(String oldStr,String newStr)
    {
        int count=getConstantPoopCount();
//        Offset=8+2表示真正存放常量的起始坐标
        int offset=CONSTANT_POOP_COUNT_INDEX+u2;

        //常量池常量的计数从1开始
        for(int i=1;i<count;i++)
        {
            int tag=ByteUtils.byte2Int(classByte,offset,u1);
            if(tag== CONSTANT_UTF8_INFO)
            {
                int len=ByteUtils.byte2Int(classByte,offset+u1,u2);
                offset+=u1+u2;
                String str=ByteUtils.byte2String(classByte,offset,len);
                if(str.equals(oldStr))
                {
                    byte[] rep=ByteUtils.string2Byte(newStr);
                    byte[] repLen=ByteUtils.int2Byte(rep.length,u2);
//                    先替换u2长度的字符串长度
                    classByte=ByteUtils.byteReplace(classByte,offset-u2,u2,repLen);
//                    在替换字符串内容
                    classByte=ByteUtils.byteReplace(classByte,offset,len,rep);
                    return classByte;
                }
                else
                {
                    offset += len;
                }
            }
            else {
                //  其他常量池内容长度固定，直接加即可
                offset+=CONSTANT_ITEM_LENGTH[tag];
            }
        }
        return  classByte;
    }
}
