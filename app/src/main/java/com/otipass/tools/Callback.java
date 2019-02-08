/**
================================================================================

    OTIPASS
    Pass Museum Application.

    package com.otipass.tools

    @copyright Copyright (c) Otipass 2011. All rights reserved.
    @author ED ($Author: ede $)

    @version $Rev: 2320 $
    $Id: Callback.java 2320 2013-07-12 12:20:28Z ede $

================================================================================
*/
package com.otipass.tools;

public class Callback {
    // callback interface for CANCEL button
    public interface OnCancelButtonClickedListener {
        public void onCancelButtonClicked(); 
    }    
    // callback interface for CONTINUE button
    public interface OnContinueButtonClickedListener {
        public void onContinueButtonClicked(); 
    }    
    // callback interface for RETURN button
    public interface OnReturnButtonClickedListener {
        public void onReturnButtonClicked(); 
    }    
    // callback interface for OTHER FUNCTION button
    public interface OnOtherButtonClickedListener {
        public void onOtherButtonClicked(int function); 
    }
    // callback interface for Qrcode
    public interface OnQrcodeListener {
        public void onQrcodeScanned(String content);
    }

}
