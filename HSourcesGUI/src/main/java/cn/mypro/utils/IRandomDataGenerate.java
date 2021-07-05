package cn.mypro.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface IRandomDataGenerate extends Library {

    //IRandomDataGenerate INSTANCE = Native.load("randDataGenerate", IRandomDataGenerate.class);
    IRandomDataGenerate INSTANCE = null;

    public void initRandData(int seed);

    public int getRandData();
}
