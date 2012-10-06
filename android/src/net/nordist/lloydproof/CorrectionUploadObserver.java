// Copyright © 2012 by Brent J. Nordquist. Some Rights Reserved.
// This work is licensed under http://creativecommons.org/licenses/by-sa/3.0/

package net.nordist.lloydproof;

public interface CorrectionUploadObserver {
    void uploadStart();
    void uploadSuccess(int count);
    void uploadFailure(String message);
    void uploadStop();
}
