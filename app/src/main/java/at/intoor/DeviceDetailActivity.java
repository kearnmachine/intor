package at.intoor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import at.intoor.db.DbHelper;
import at.intoor.db.DistanceTranslationHelper;
import at.intoor.db.generated.DaoSession;
import at.intoor.db.generated.DeviceEntity;
import at.intoor.db.generated.DeviceEntityDao;
import at.intoor.db.generated.DistanceEntity;
import at.intoor.db.generated.DistanceEntityDao;
import at.intoor.intoor.R;

public class DeviceDetailActivity extends Activity {

  public final static String TAG = DeviceDetailActivity.class.getSimpleName();
  public final static String EXTRA_ADDRESS = "address";

  private DeviceEntityDao deviceEntityDao = null;
  private DistanceEntityDao distanceEntityDao = null;
  private String address;
  private EditText editTxPower;
  private Button deleteButton;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_device_detail);
    Bundle bundle = getIntent().getExtras();
    address = bundle.getString(EXTRA_ADDRESS);
    EditText editAddress = (EditText) findViewById(R.id.editAddress);
    editTxPower = (EditText) findViewById(R.id.editTxPower);
    Button saveButton = (Button) findViewById(R.id.saveDeviceButton);
    deleteButton = (Button) findViewById(R.id.deleteDeviceButton);

    //address must be defined;
    editAddress.setText(address);

    DaoSession daoSession = DbHelper.getWritable(this);
    deviceEntityDao = daoSession.getDeviceEntityDao();
    distanceEntityDao = daoSession.getDistanceEntityDao();


    DeviceEntity selected = deviceEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Address.eq(address)).unique();
    saveButton.setOnClickListener(saveButtonAction);
    if (selected == null) {
      Toast.makeText(this, "Creating new device", Toast.LENGTH_SHORT).show();
      deleteButton.setEnabled(false);
    } else {
      Toast.makeText(this, "Persisted device found", Toast.LENGTH_SHORT).show();
      editTxPower.setText(selected.getTxPower().toString());
      deleteButton.setEnabled(true);
      deleteButton.setOnClickListener(deleteButtonAction);
    }
  }

  private void calculateTranslationTable() {

  }

  private View.OnClickListener saveButtonAction = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      int txPower;
      try {
        txPower = Integer.parseInt(editTxPower.getText().toString());
      } catch (IllegalArgumentException e) {
        Log.d(TAG, "invalid value");
        return;
      }
      //save;
      DeviceEntity dev = deviceEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Address.eq(address)).unique();
      if (dev == null) {
        Log.d(TAG, "Creating new entry: " + address);
        dev = new DeviceEntity();
        dev.setAddress(address);
        dev.setTxPower(txPower);
        dev.setX(200f);
        dev.setY(200f);
        deviceEntityDao.insert(dev);
        deleteButton.setEnabled(true);
        deleteButton.setOnClickListener(deleteButtonAction);
        Toast.makeText(getApplicationContext(), "Created", Toast.LENGTH_SHORT).show();
      } else {
        Log.d(TAG, "Resetting position for already persisted: " + dev.getAddress());
        dev.setTxPower(txPower);
        deviceEntityDao.update(dev);
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
      }
      List<DistanceEntity> currentTranslationTable = distanceEntityDao.queryBuilder().where(DistanceEntityDao.Properties.TxPower.eq(txPower)).list();
      if (currentTranslationTable.size() < DistanceTranslationHelper.getListLength(txPower)) {
        List<DistanceEntity> newTable = DistanceTranslationHelper.createRSSITable(txPower);
        distanceEntityDao.deleteInTx(currentTranslationTable);
        distanceEntityDao.insertInTx(newTable);
      }
    }
  };
  private View.OnClickListener deleteButtonAction = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      //delete;
      DeviceEntity dev = deviceEntityDao.queryBuilder().where(DeviceEntityDao.Properties.Address.eq(address)).unique();
      deviceEntityDao.delete(dev);
      Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();

      deleteButton.setEnabled(false);
      deleteButton.setOnClickListener(null);
    }
  };


//  @Override
//  public boolean onCreateOptionsMenu(Menu menu) {
//    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.menu_device_detail, menu);
//    return true;
//  }
//
//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    // Handle action bar item clicks here. The action bar will
//    // automatically handle clicks on the Home/Up button, so long
//    // as you specify a parent activity in AndroidManifest.xml.
//    int id = item.getItemId();
//
//    //noinspection SimplifiableIfStatement
//    if (id == R.id.action_settings) {
//      return true;
//    }
//
//    return super.onOptionsItemSelected(item);
//  }
}
