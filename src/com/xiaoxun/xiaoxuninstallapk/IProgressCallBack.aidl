// IProgressCallBack.aidl
package com.xiaoxun.xiaoxuninstallapk;

// Declare any non-default types here with import statements

interface IProgressCallBack {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void  getProgress(int progress,String name,int state);
}
