package at.intoor;


import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import at.intoor.db.DbHelper;
import at.intoor.db.Device;
import at.intoor.db.generated.DeviceEntity;
import at.intoor.db.generated.DeviceEntityDao;
import at.intoor.intoor.R;

public class FindDeviceActivity extends Activity {

  private final static String className = FindDeviceActivity.class.getName();

  private static final int SCAN_ON_TIME = 2000;
  private static final int SCAN_OFF_TIME = 100;
  private static final int REQUEST_ENABLE_BT = 1;
  private BluetoothAdapter bluetoothAdapter = null;
  private BluetoothLeScanner bluetoothScanner = null;
  private ArrayAdapter<Device> adapter;
  private List<Device> list;
  private Handler handler;

  private boolean isScanning = false;
  private boolean toggle = true;


  //////////////////////////////////////////////////////////////////////////////////
  // Callbacks and Handlers
  //////////////////////////////////////////////////////////////////////////////////

  private AdapterView.OnItemClickListener selectListItemHandler = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      // Do something when a list item is clicked
      Device selected = adapter.getItem(position);
      //todo new Activity to persist
      Intent saveDetails = new Intent(getApplicationContext(), DeviceDetailActivity.class);
      saveDetails.putExtra(DeviceDetailActivity.EXTRA_ADDRESS, selected.getAddress());
      startActivity(saveDetails);
    }
  };

  private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int newState) {
      if (newState == BluetoothProfile.STATE_CONNECTED)
        gatt.discoverServices();
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      if(status != BluetoothGatt.GATT_SUCCESS) return;
      List<BluetoothGattService> services = gatt.getServices();
      for (BluetoothGattService s : services) {
        List<BluetoothGattCharacteristic> chars = s.getCharacteristics();
//        List<BluetoothGattService> inclServices = s.getIncludedServices();
        Log.d(className,"asdf");
        for(BluetoothGattCharacteristic c : chars){
          byte[] val =c.getValue();
          Log.i(className, c.getUuid() + " " + (val!=null ? new String(val):null));
        }
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
      if(status != BluetoothGatt.GATT_SUCCESS) return;
      List<BluetoothGattDescriptor> descs = characteristic.getDescriptors();
      for (BluetoothGattDescriptor d : descs) {
        BluetoothGattCharacteristic c = d.getCharacteristic();
        Log.d(className,"asdf");
      }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
      String a = new String(descriptor.getValue());
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
    }
  };

  private ScanCallback scanCallback = new ScanCallback() {
    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Currently could only be
     *            {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_ALL_MATCHES}.
     * @param result A Bluetooth LE scan result.
     */
    public void onScanResult(int callbackType, ScanResult result) {
      if (!isScanning) return;
      Log.d(className, "callbacktype: " + callbackType);
      ScanRecord record = result.getScanRecord();
      BluetoothDevice device = result.getDevice();
      int rssi = result.getRssi();
      String address = device.getAddress();
      // todo setter
      Device dev = Device.getFromList(address, list);
      Log.d(className, dev == null ? "not found in list" : "found in list " + address);
      if (dev == null) {
        DeviceEntity deviceEntity = new DeviceEntity(null, address, null, null, null, null);
        dev = new Device(deviceEntity);
        list.add(dev);
      }
      if (record != null) {
        Integer txPower = record.getTxPowerLevel();
        //manual read
        if (txPower == Integer.MIN_VALUE) {
          // make android implementation transparent
          txPower = null;
        }
        //use name for rssi presentation
        dev.setName(record.getDeviceName() + " " + rssi + "/" + txPower);
      }

//      device.connectGatt(getApplicationContext(), false, gattCallback);
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          adapter.notifyDataSetChanged();
        }
      });
    }

    /**
     * Callback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    public void onBatchScanResults(List<ScanResult> results) {
      Log.d(className, "batchScanResult: " + results.size());
    }

    /**
     * Callback when scan could not be started.
     *
     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
     */
    public void onScanFailed(int errorCode) {
      Log.e(className, "scan failed with " + errorCode);
    }
  };

  private Runnable continuousScanTimer = new Runnable() {
    @Override
    public void run() {
      if (isScanning) {
        // Stops scanning after a pre-defined scan period.
        if (toggle) {
          bluetoothScanner.startScan(scanCallback);
          handler.postDelayed(continuousScanTimer, SCAN_ON_TIME);
        } else {
          bluetoothScanner.stopScan(scanCallback);
          handler.postDelayed(continuousScanTimer, SCAN_OFF_TIME);
        }
        toggle = !toggle;
      } else {
        bluetoothScanner.stopScan(scanCallback);
        toggle = true;
      }
    }
  };

  @Override
  protected void onDestroy() {
    isScanning = false;
    handler.removeCallbacksAndMessages(null);
    bluetoothScanner.stopScan(scanCallback);
    super.onDestroy();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handler = new Handler();
    setContentView(R.layout.activity_new_device);

    DeviceEntityDao deviceEntityDao = DbHelper.getWritable(this).getDeviceEntityDao();
    List<DeviceEntity> rawList = deviceEntityDao.loadAll();
    list = Device.wrap(rawList);
    for (Device d : list) {
      d.setName("Currently in use");
    }
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
    ListView listView = (ListView) findViewById(R.id.device_list);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(selectListItemHandler);
    checkBT();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode == RESULT_OK) {
        checkBT();
      } else {
        Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_LONG).show();
        this.finish();
      }
    }
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
//    int id = item.getItemId();
//    if (id == R.id.show_all) {
//      scan(true);
//      clearView();
//      isListCleaned = false;
//      return true;
//    } else if (id == R.id.keep_selected) {
//      clearView();
//      isListCleaned = true;
//      scan(true);
//    }
    return super.onOptionsItemSelected(item);
  }


  //////////////////////////////////////////////////////////////////////////////////
  // Activity methods
  //////////////////////////////////////////////////////////////////////////////////

  ///// Options
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.menu_new_device, menu);
    return false;
  }

  //////////////////////////////////////////////////////////////////////////////////
  // Business Logic
  //////////////////////////////////////////////////////////////////////////////////
  private void checkBT() {
    // is possible?
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
      Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
      finish();
    }
    // is enabled?
    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(android.content.Context.BLUETOOTH_SERVICE);
    if (bluetoothAdapter == null) bluetoothAdapter = bluetoothManager.getAdapter();
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
      scan(true);
    }
  }

  private void scan(boolean enable) {
    if (bluetoothScanner == null) bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
    ActionBar actionBar = getActionBar();
    handler.removeCallbacksAndMessages(null);
    isScanning = enable;
    if (enable) {
      // Stops scanning after a pre-defined scan period.
      if (actionBar != null) actionBar.setSubtitle("Scanning ...");
      bluetoothScanner.startScan(scanCallback);
      handler.postDelayed(continuousScanTimer, SCAN_ON_TIME);
    }
  }

//  public void clearView() {
//    scan(false);
//    //clean view
//    ListView listView = (ListView) findViewById(R.id.device_list);
//    for (Integer i = 0; i < listView.getChildCount(); i++) {
//      View itemView = listView.getChildAt(i);
//      if (itemView != null) itemView.setBackgroundColor(Color.TRANSPARENT);
//    }
//    //clean devs for next scan
//    Iterator<DeviceEntity> i = list.iterator();
//    while (i.hasNext()) {
//      DeviceEntity b = i.next();
//      if (!b.isSelected()) {
//        //cleanup
//        i.remove();
//      }
//    }
//    for (DeviceEntity b : list) {
//      b.setSelected(false); // not selected next time scan starts
//    }
//  }


}
