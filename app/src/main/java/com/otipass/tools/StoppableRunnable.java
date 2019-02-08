/**
================================================================================

    OTIPASS
    Pass Museum Application.

    package com.otipass.tools

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2320 $
    $Id: StoppableRunnable.java 2320 2013-07-12 12:20:28Z ede $

================================================================================
*/
package com.otipass.tools;

public abstract class StoppableRunnable implements Runnable {

    private volatile boolean mIsStopped = false;

    public abstract void stoppableRun();

    public void run() {
        setStopped(false);
        while(!mIsStopped) {
            stoppableRun();
            stop();
        }
    }

    public boolean isStopped() {
        return mIsStopped;
    }

    private void setStopped(boolean isStop) {    
        if (mIsStopped != isStop)
            mIsStopped = isStop;
    }

    public void stop() {
        setStopped(true);
    }
}