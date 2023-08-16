package com.example.vinstallment_server.activity;

import android.app.NotificationChannel;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vinstallment_server.R;
import com.example.vinstallment.VInstallmentAIDL;

import android.app.admin.DevicePolicyManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String CAMERA_PACKAGE_NAME = "com.android.camera";
    private MediaPlayer mediaPlayer;

    private Switch switchH1;
    private Switch switchH0;
    private Switch switchHplus1;
    private Switch switchHplus2;
    private Switch switchHplus3;
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID_H1 = 1;
    private final int NOTIFICATION_ID_H0 = 2;
    private boolean isCameraEnabled = true;
    private Button bayarBtn;
    private Button lunasBtn;

    private VInstallmentAIDL punishmentService;
    private boolean isServiceBound = false;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SwitchStates";
    private static final String KEY_SWITCH_H1 = "switch_h1";
    private static final String KEY_SWITCH_H0 = "switch_h0";
    private static final String KEY_SWITCH_HPLUS1 = "switch_hplus1";
    private static final String KEY_SWITCH_HPLUS2 = "switch_hplus2";
    private static final String KEY_SWITCH_HPLUS3 = "switch_hplus3";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindToPunishmentService();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        switchH1 = findViewById(R.id.switchH_1);
        switchH0 = findViewById(R.id.switchH_0);
        switchHplus1 = findViewById(R.id.switchH_plus_1);
        switchHplus2 = findViewById(R.id.switchH_plus_2);
        switchHplus3 = findViewById(R.id.switchH_plus_3);
        bayarBtn = findViewById(R.id.buttonBayar);
        lunasBtn = findViewById(R.id.buttonLunas);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        switchH1.setChecked(sharedPreferences.getBoolean(KEY_SWITCH_H1, false));
        switchH0.setChecked(sharedPreferences.getBoolean(KEY_SWITCH_H0, false));
        switchHplus1.setChecked(sharedPreferences.getBoolean(KEY_SWITCH_HPLUS1, false));
        switchHplus2.setChecked(sharedPreferences.getBoolean(KEY_SWITCH_HPLUS2, false));
        switchHplus3.setChecked(sharedPreferences.getBoolean(KEY_SWITCH_HPLUS3, false));

        switchH1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (isChecked) {
                        switchH1.setChecked(true);
                        switchH0.setChecked(false);
                        switchHplus1.setChecked(false);
                        switchHplus2.setChecked(false);
                        switchHplus3.setChecked(false);
                        editor.putBoolean(KEY_SWITCH_H1, true);
                        editor.putBoolean(KEY_SWITCH_H0, false);
                        editor.putBoolean(KEY_SWITCH_HPLUS1, false);
                        editor.putBoolean(KEY_SWITCH_HPLUS2, false);
                        editor.putBoolean(KEY_SWITCH_HPLUS3, false);
                        editor.apply();
                    }

                    if (punishmentService == null){
                        Toast.makeText(MainActivity.this, "Service null", Toast.LENGTH_SHORT).show();
                    }

                    // Manage notification based on switch state
                    if (isChecked) {
                        // Display notification
                        try {
                            punishmentService.showNotif("Besok adalah tenggat pembayaran!",
                                    "Segera lakukan pembayaran sebelum tenggat pembayaran agar hpmu tetap berfungsi dengan baik",
                                    NOTIFICATION_ID_H1);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // Remove notification
                        editor.putBoolean(KEY_SWITCH_H1, false);
                        editor.apply();
                        try {
                            punishmentService.removeNotif();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        });

        switchH0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (isChecked) {
                        switchH0.setChecked(true);
                        switchH1.setChecked(false);
                        switchHplus1.setChecked(false);
                        switchHplus2.setChecked(false);
                        switchHplus3.setChecked(false);
                        editor.putBoolean(KEY_SWITCH_H0, true);
                        editor.putBoolean(KEY_SWITCH_H1, false);
                        editor.putBoolean(KEY_SWITCH_HPLUS1, false);
                        editor.putBoolean(KEY_SWITCH_HPLUS2, false);
                        editor.putBoolean(KEY_SWITCH_HPLUS3, false);
                        editor.apply();
                    }

                    // Manage notification based on switch state
                    if (isChecked) {
                        // Display notification
                        try {
                            punishmentService.showNotif("Hari ini adalah tenggat pembayaran!", "Segera lakukan pembayaran agar hpmu tetap berfungsi dengan baik", NOTIFICATION_ID_H0);
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // Remove notification
                        editor.putBoolean(KEY_SWITCH_H0, false);
                        editor.apply();
                        try {
                            punishmentService.removeNotif();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

        });

        switchHplus1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    switchHplus1.setChecked(true);
                    switchH0.setChecked(false);
                    switchH1.setChecked(false);
                    switchHplus2.setChecked(false);
                    switchHplus3.setChecked(false);
                    editor.putBoolean(KEY_SWITCH_HPLUS1, true);
                    editor.apply();
                }

                // Manage notification based on switch state
                if (isChecked) {
                    // Display notification
                    try {
                        punishmentService.showNotif("Pembayaranmu terlambat satu hari!", "Saat ini fitur kamera akan dinonaktifkan sampai pembayaran dilakukan", 3);
                        punishmentService.disableCamera();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(MainActivity.this, "Camera disabled", Toast.LENGTH_SHORT).show();

                } else {
                    // Remove notification
                    editor.putBoolean(KEY_SWITCH_HPLUS1, false);
                    editor.apply();
                    try {
                        punishmentService.removeNotif();
                        punishmentService.enableCamera();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(MainActivity.this, "Camera enabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchHplus2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    switchHplus2.setChecked(true);
                    switchH0.setChecked(false);
                    switchHplus1.setChecked(false);
                    switchH1.setChecked(false);
                    editor.putBoolean(KEY_SWITCH_HPLUS2, true);
                    editor.apply();
                }

                if (isChecked) {
                    try {
                        punishmentService.showNotif("Pembayaranmu terlambat dua hari!", "Saat ini beberapa aplikasi akan terkunci sampai kamu melakukan pembayaran", 3);
                        punishmentService.suspendApps();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(MainActivity.this, "Apps suspended", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean(KEY_SWITCH_HPLUS2, false);
                    editor.apply();
                    try {
                        punishmentService.removeNotif();
                        punishmentService.unsuspendApps();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(MainActivity.this, "Apps unsuspended", Toast.LENGTH_SHORT).show();
                }
            }
        });

        switchHplus3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Turn off other switches if this switch is turned on
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    switchHplus3.setChecked(true);
                    switchH0.setChecked(false);
                    switchHplus1.setChecked(false);
                    switchHplus2.setChecked(true);
                    switchH1.setChecked(false);
                    editor.putBoolean(KEY_SWITCH_HPLUS3, true);
                    editor.apply();
                }

                if (isChecked) {
                    // Start sound
                    try {
                        punishmentService.showNotif("Pembayaranmu terlambat lebih dua hari!", "Beberapa aplikasi akan terkunci dan kami akan selalu mengingatkanmu untuk melakukan pembayaran", 3);
                        punishmentService.startPlaying();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Stop the sound when the switch is turned off
                    editor.putBoolean(KEY_SWITCH_HPLUS3, false);
                    editor.apply();
                    try {
                        punishmentService.removeNotif();
                        punishmentService.stopPlaying();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        bayarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchH1.setChecked(false);
                switchH0.setChecked(false);
                switchHplus1.setChecked(false);
                switchHplus2.setChecked(false);
                switchHplus3.setChecked(false);
                try {
                    punishmentService.showNotif("Pembayaran berhasil terverifikasi!", "Terima kasih telah melakukan pembayaran, kamu bisa mengabaikan pemberitahuan ini", 3);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            punishmentService.removeNotif();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 10000);
            }
        });

        lunasBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    punishmentService.showNotif("Kamu telah melakukan pelunasan!", "Sekarang kamu sudah bisa uninstall aplikasi ini", 3);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                try {
                    punishmentService.installmentComplete();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void bindToPunishmentService() {
        Intent intent = new Intent();
        intent.setAction("com.example.vinstallment.VInstallmentAIDL");
        intent.setPackage("com.example.vinstallment");
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            punishmentService = VInstallmentAIDL.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            punishmentService = null;
        }
    };


}


