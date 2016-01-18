package at.intoor.connectivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.intoor.db.DbHelper;
import at.intoor.db.Device;
import at.intoor.db.generated.DaoSession;
import at.intoor.db.generated.DeviceEntity;
import at.intoor.db.generated.DeviceEntityDao;
import at.intoor.db.generated.MeasurementEntity;
import at.intoor.db.generated.MeasurementEntityDao;

/**
 * Scans and persists asynchronously ble devices
 */
public class ScanService {
  private final static String TAG = ScanService.class.getSimpleName();
  private DeviceEntityDao deviceEntityDao;
  private MeasurementEntityDao measurementEntityDao;
  private Handler handler = new Handler();
  private BluetoothLeScanner bluetoothLeScanner;

  private ScanSettings settings;
  private List<ScanFilter> filters = new ArrayList<>();


  private final static Integer SCAN_ON_TIME = 550;
  private final static Integer SCAN_OFF_TIME = 0;
  private boolean toggle = false;

  public ScanService(Context context, BluetoothManager manager, List<Device> deviceList) {
    DaoSession session = DbHelper.getWritable(context);
    deviceEntityDao = session.getDeviceEntityDao();
    measurementEntityDao = session.getMeasurementEntityDao();
    ScanSettings.Builder builder = new ScanSettings.Builder();
    builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
    builder.setReportDelay(0);
    settings = builder.build();
    for (Device d : deviceList) {
      filters.add(new ScanFilter.Builder().setDeviceAddress(d.getAddress()).build());
    }
    BluetoothAdapter bluetoothAdapter = manager.getAdapter();
    bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
  }

  public void startScanning() {
    measurementEntityDao.deleteAll();
    scan(true);
  }

  public void stopScanning() {
    scan(false);
  }

  private ScanCallback scanCallback = new ScanCallback() {
    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Currently could only be
     *            {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_ALL_MATCHES}.
     * @param result A Bluetooth LE scan result.
     */
    public void onScanResult(final int callbackType, ScanResult result) {
      Log.d(TAG, "callbacktype: " + callbackType);
      BluetoothDevice device = result.getDevice();
      final String address = device.getAddress();
      final Integer rssi = result.getRssi();
//      result.getScanRecord().getTxPowerLevel();
      final long timestamp = Calendar.getInstance().getTimeInMillis();
      DeviceEntity dev = deviceEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Address.eq(address)).unique();
      Log.d(TAG, dev == null ? "not found in list" : "found in list " + address);
      if (dev == null) return;
      MeasurementEntity measurement = new MeasurementEntity(null, timestamp, dev.getId(), rssi);
      measurementEntityDao.insert(measurement);
    }

    public void onBatchScanResults(List<ScanResult> results) {
      Log.d(TAG, "onBatchScanResults called");
      for (ScanResult r : results) onScanResult(1, r);
    }
  };

  private Runnable continuousScanTimer = new Runnable() {
    @Override
    public void run() {
      // Stops scanning after a pre-defined scan period.
      if (toggle) {
        bluetoothLeScanner.startScan(filters, settings, scanCallback);
        handler.postDelayed(continuousScanTimer, SCAN_ON_TIME);
      } else {
        bluetoothLeScanner.stopScan(scanCallback);
        handler.postDelayed(continuousScanTimer, SCAN_OFF_TIME);
      }
      toggle = !toggle;
    }
  };

  private void scan(boolean enable) {
    Log.d(TAG, "scanning starting: " + enable);
    handler.removeCallbacksAndMessages(null);
    if (enable) {
      bluetoothLeScanner.startScan(filters, settings, scanCallback);
      handler.postDelayed(continuousScanTimer, SCAN_ON_TIME);
    } else {
      handler.removeCallbacks(continuousScanTimer);
      bluetoothLeScanner.stopScan(scanCallback);
      toggle = true;
    }
  }
}
