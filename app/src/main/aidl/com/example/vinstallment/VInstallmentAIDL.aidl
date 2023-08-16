// VInstallmentAIDL.aidl
package com.example.vinstallment;

// Declare any non-default types here with import statements

interface VInstallmentAIDL {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void showNotif(String title, String subtitle, int id);
    void stopPlaying();
    void startPlaying();
    void removeNotif();
    void enableCamera();
    void disableCamera();
    void suspendApps();
    void unsuspendApps();
    void installmentComplete();
}