package net.nordist.lloydproof;

public interface CorrectionUploadObserver {
    void uploadStart();
    void uploadSuccess(int count);
    void uploadFailure(String message);
    void uploadStop();
}
