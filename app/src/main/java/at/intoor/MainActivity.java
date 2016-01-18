package at.intoor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import at.intoor.connectivity.ScanService;
import at.intoor.db.DbHelper;
import at.intoor.db.Device;
import at.intoor.db.DistanceTranslationHelper;
import at.intoor.db.MapImageHelper;
import at.intoor.db.generated.DaoSession;
import at.intoor.db.generated.DeviceEntity;
import at.intoor.db.generated.DeviceEntityDao;
import at.intoor.db.generated.DistanceEntity;
import at.intoor.db.generated.DistanceEntityDao;
import at.intoor.db.generated.MapImageEntity;
import at.intoor.db.generated.MapImageEntityDao;
import at.intoor.db.generated.MeasurementEntity;
import at.intoor.db.generated.MeasurementEntityDao;
import at.intoor.draw.DevicePositioner;
import at.intoor.draw.MapView;
import at.intoor.draw.PicturePositioner;
import at.intoor.draw.ScaleLine;
import at.intoor.intoor.R;


public class MainActivity extends Activity {
  private final static String TAG = "MainActivity";

  private final static int REQUEST_PICTURE = 1;
  private final static int REQUEST_ENABLE_BT = 2;
  private final static PicturePositioner picturePositioner = new PicturePositioner();
  private final static DevicePositioner beaconPositioner = new DevicePositioner();
  private final static ScaleLine scaleLine = new ScaleLine(250f, 250f, 350f, 350f);
  private final static String FILENAME_MAP = "map";
  private final static int BUFSIZE = 1024 * 16;

  private MapView mapView;
  private boolean resizeMode = false;
  private boolean editMode = false;
  private boolean scaleMode = false;

  private boolean updateValues = true;

  private List<Device> deviceList = new ArrayList<>();

  private MapImageEntityDao mapImageEntityDao;
  private DeviceEntityDao deviceEntityDao;
  private MeasurementEntityDao measurementEntityDao;
  private DistanceEntityDao distanceEntityDao;

  private Thread helper = null;
  private ScanService scanService;

//  private BluetoothLeScanner leScanner;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    DaoSession db = DbHelper.getWritable(this);
    mapImageEntityDao = db.getMapImageEntityDao();
    deviceEntityDao = db.getDeviceEntityDao();
    measurementEntityDao = db.getMeasurementEntityDao();
    distanceEntityDao = db.getDistanceEntityDao();

    mapView = (MapView) (findViewById(R.id.mapView));
    mapView.setDeviceList(deviceList);
    beaconPositioner.setDeviceList(deviceList);
    restorePersistedMap();
  }

  @Override
  protected void onResume() {
    super.onResume();
    refreshDeviceList();
    BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
//    leScanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
    scanService = new ScanService(this, bluetoothManager, deviceList);
    scanService.startScanning();
    enableContinuousRefreshing(true);
  }

  @Override
  protected void onPause() {
    enableContinuousRefreshing(false);
    scanService.stopScanning();
    super.onPause();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.register_device) {
      Intent intent = new Intent(MainActivity.this, FindDeviceActivity.class);
      startActivity(intent);
    } else if (id == R.id.import_floor_map) {
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
      intent.setAction(Intent.ACTION_GET_CONTENT);
      startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICTURE);
    } else if (id == R.id.toggle_resize_map) {
      setResizeMode(!resizeMode);
      item.setTitle(resizeMode ? R.string.exit_resize_mode : R.string.resize_map);
    } else if (id == R.id.toggle_edit_devices) {
      setEditMode(!editMode);
      item.setTitle(editMode ? R.string.exit_edit_mode : R.string.edit_devices);
    } else if (id == R.id.toggle_scale_mode) {
      setScaleMode(!scaleMode);
      item.setTitle(scaleMode ? R.string.exit_scale_mode : R.string.scale_mode);
    }

    return super.onOptionsItemSelected(item);
  }

  private void checkBT() {
    // is possible?
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
      Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
      finish();
    }
    // is enabled?
    BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(android.content.Context.BLUETOOTH_SERVICE);
    BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
  }

  private void enableContinuousRefreshing(boolean status) {
    if (status) {
      updateValues = true;
      if (helper == null) {
        helper = new Thread(new Runnable() {
          @Override
          public void run() {
            while (updateValues) {
              long timeout = Calendar.getInstance().getTimeInMillis() + Device.MAX_TIMEOUT * 1000;
              List<MeasurementEntity> toDelete = new ArrayList<>();
              for (Device d : deviceList) {
                //get newest results
                d.getDeviceEntity().resetMeasurements();
                List<MeasurementEntity> measurements = d.getMeasurements();
                //prepare cleanup
                for (MeasurementEntity m : measurements) {
                  if (m.getTimestamp() < timeout)
                    toDelete.add(m);
                }
                //refresh
                Integer newRssi = d.getLastMeasurement().getRssi();
                double distance;
                if (newRssi >= DistanceTranslationHelper.LIMIT) {
                  DistanceEntity translator = distanceEntityDao.queryBuilder()
                      .where(
                          DistanceEntityDao.Properties.TxPower.eq(d.getTxPower()),
                          DistanceEntityDao.Properties.Rssi.eq(newRssi))
                      .unique();
                  if (translator == null) {
                    Log.e(TAG, "translator entry not found: " + newRssi + "/" + d.getTxPower());
                    continue;
                  }
                  distance = translator.getDistance() == null ? 0 : translator.getDistance();
                } else {
                  distance = 0f;
                }
                d.setCalculatedRadius((float) (distance * mapView.getScale()));
              }
              //render
              mapView.postInvalidate();
              if (toDelete.size() > 0)
                measurementEntityDao.deleteInTx(toDelete);
              // interrupt hook
              if (!updateValues) return;
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
                Log.i(TAG, "interrupted while not active", e);
              }
            }
          }
        });
      }
      helper.start();
    } else {
      updateValues = false;
      helper.interrupt(); //should throw on NULL!
      helper = null;
    }
  }

  private void setResizeMode(boolean status) {
    resizeMode = status;
    if (resizeMode) {
//      picturePositioner.setImageMatrix(mapView.getImageMatrix());
      mapView.setOnTouchListener(picturePositioner);
    } else {
      mapView.setOnTouchListener(null);
      saveMatrix();
    }
  }

  private void setEditMode(boolean status) {
    editMode = status;
    if (editMode) {
      enableContinuousRefreshing(false);
      mapView.setOnTouchListener(beaconPositioner);
    } else {
      mapView.setOnTouchListener(null);
      saveBeaconPositions();
      enableContinuousRefreshing(true);
    }
  }

  private void setScaleMode(boolean status) {
    scaleMode = status;
    if (scaleMode) {
      mapView.setOnTouchListener(scaleLine);
      mapView.setScaleLine(scaleLine);
    } else {
      mapView.setOnTouchListener(null);
      mapView.setScaleLine(null);
      mapView.postInvalidate();
      saveScale();
    }
  }

  private void saveMatrix() {
    float[] values = new float[9];
    mapView.getImageMatrix().getValues(values);
    MapImageEntity map = mapImageEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Name.eq(FILENAME_MAP)).unique();
    MapImageHelper.setValues(map, values);
    mapImageEntityDao.update(map);
  }

  private void saveBeaconPositions() {
    deviceEntityDao.updateInTx(Device.unwrap(deviceList));
  }

  private void saveScale() {
    Double scale = scaleLine.getScale();
    MapImageEntity map = mapImageEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Name.eq(FILENAME_MAP)).unique();
    map.setScale(scale);
    mapImageEntityDao.update(map);
    mapView.setScale(scale);
  }


  private void refreshDeviceList() {
    List<DeviceEntity> deviceEntities = deviceEntityDao.queryBuilder().list();
    deviceList.clear();
    for (DeviceEntity d : deviceEntities) {
      deviceList.add(new Device(d));
    }
    mapView.postInvalidate();
  }

  private void restoreMatrix() {
    MapImageEntity map = mapImageEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Name.eq(FILENAME_MAP)).unique();
    Matrix matrix = new Matrix();
    float[] values = MapImageHelper.getValues(map);
    matrix.setValues(values);
    mapView.setImageMatrix(matrix);
    mapView.setScale(map.getScale());
  }

  private void restorePersistedMap() {
    try {
      final InputStream in = openFileInput(FILENAME_MAP);
      final Bitmap image = BitmapFactory.decodeStream(in);
      mapView.setImageBitmap(image);
      in.close();
      restoreMatrix();
    } catch (IOException e) {
      Log.i(TAG, "No image set or error on reading", e);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
      case REQUEST_PICTURE:
        if (resultCode == RESULT_OK && null != data) {
          try {
            final Uri imagePath = data.getData();
            InputStream in = getContentResolver().openInputStream(imagePath);
            final Bitmap selectedImage = BitmapFactory.decodeStream(in);

            //set
            ImageView imageView = (ImageView) findViewById(R.id.mapView);
            imageView.setImageBitmap(selectedImage);
            // save in local storage
            FileOutputStream fos = openFileOutput(FILENAME_MAP, Context.MODE_PRIVATE);
            in.close();
            in = getContentResolver().openInputStream(imagePath);
            byte[] buf = new byte[BUFSIZE];
            int length;
            while ((length = in.read(buf)) != -1) {
              fos.write(buf, 0, length);
            }
            in.close();
            fos.close();
            float[] values = new float[9];
            imageView.getImageMatrix().getValues(values);

            //todo remove
            MapImageEntity current = mapImageEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Name.eq(FILENAME_MAP)).unique();
            mapImageEntityDao.deleteAll();
            mapImageEntityDao.insert(new MapImageEntity(
                null,
                FILENAME_MAP,
                null,
                values[0],
                values[1],
                values[2],
                values[3],
                values[4],
                values[5],
                values[6],
                values[7],
                values[8],
                0d
            ));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        break;
      case REQUEST_ENABLE_BT:
        if (resultCode == RESULT_OK) {
          checkBT();
        } else {
          Toast.makeText(this, R.string.no_bluetooth, Toast.LENGTH_LONG).show();
          this.finish();
        }
        break;
      default:
        Log.e(TAG, "unknown result: " + requestCode);

    }
  }


}
