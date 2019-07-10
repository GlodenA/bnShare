package com.ipu.server.util;
import java.util.concurrent.atomic.AtomicInteger;

public class SequenceUtils
{

//    public static volatile int seqNumber = 100000;
    public static AtomicInteger seqNumber = new AtomicInteger(0);
    public static AtomicInteger seqNumber10000 = new AtomicInteger(0);
    public static AtomicInteger seqNumber1000000 = new AtomicInteger(0);
    public static AtomicInteger seqNumber2 = new AtomicInteger(0);

    public static synchronized int getSeqNumber()
    {
        /*if (seqNumber == 999999)
        {
            seqNumber = 100000;
        }
        return seqNumber;*/
        return seqNumber.getAndIncrement();
    }
    public static synchronized int getSeqNumber10000()
    {
        /*if (seqNumber == 999999)
        {
            seqNumber = 100000;
        }
        return seqNumber;*/
        int  tmp = seqNumber10000.getAndIncrement();
        if (tmp == 10000)
        {
            seqNumber10000.set(0);
        }
        return seqNumber10000.getAndIncrement();
    }
    public static synchronized int getSeqNumber1000000()
    {
        int  tmp = seqNumber1000000.getAndIncrement();
        if (tmp == 1000000)
        {
            seqNumber1000000.set(0);
            return seqNumber1000000.getAndIncrement();
        }
        return tmp;
    }
    public static synchronized int getSeqNumber2()
    {
        int  tmp = seqNumber2.getAndIncrement();
        if (tmp == 1000000)
        {
            seqNumber2.set(0);
            return seqNumber2.getAndIncrement();
        }
        return tmp;
    }


    /**
     * 返回长度为【strLength】的随机数，在前面补0
     */
    public static String getFixLengthString(int strLength)
    {
//    // 返回固定的长度的随机数
        double d = Double.parseDouble(String.valueOf(Math.pow(10, strLength)));
        long r = (long) Math.floor(Math.random() * d);
        String random = String.valueOf(r);
        for (int i = 0; i < strLength - String.valueOf(r).length(); i++)
        {
            random = "0" + random;
        }
        return random.equals("0") ? "" : random;
    }

    /**
     * 随机生成6位随机验证码
     * author by dingxy3
     * @return
     */
    public static String createRandomVcode(int VerifyCode){
        //验证码
        String vcode = "";
        for (int i = 0; i < VerifyCode; i++) {
            vcode = vcode + (int)(Math.random() * 9);
        }
        return vcode;
    }
}
