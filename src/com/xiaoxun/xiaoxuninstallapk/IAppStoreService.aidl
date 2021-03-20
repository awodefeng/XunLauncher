// IAppStoreService.aidl
package com.xiaoxun.xiaoxuninstallapk;

// Declare any non-default types here with import statements
import com.xiaoxun.xiaoxuninstallapk.IProgressCallBack;

interface IAppStoreService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void downloadapk();
    
    void pausedownload();
    
    void registercallback(IProgressCallBack cb);

    void unregistercallback(IProgressCallBack cb);
    
    void updatetask();
    
}
