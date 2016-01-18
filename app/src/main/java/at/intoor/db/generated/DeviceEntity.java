package at.intoor.db.generated;

import java.util.List;
import at.intoor.db.generated.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table DEVICE_ENTITY.
 */
public class DeviceEntity {

    private Long id;
    private String address;
    private String name;
    private Integer txPower;
    private Float x;
    private Float y;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient DeviceEntityDao myDao;

    private List<MeasurementEntity> measurements;

    public DeviceEntity() {
    }

    public DeviceEntity(Long id) {
        this.id = id;
    }

    public DeviceEntity(Long id, String address, String name, Integer txPower, Float x, Float y) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.txPower = txPower;
        this.x = x;
        this.y = y;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDeviceEntityDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTxPower() {
        return txPower;
    }

    public void setTxPower(Integer txPower) {
        this.txPower = txPower;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MeasurementEntity> getMeasurements() {
        if (measurements == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MeasurementEntityDao targetDao = daoSession.getMeasurementEntityDao();
            List<MeasurementEntity> measurementsNew = targetDao._queryDeviceEntity_Measurements(id);
            synchronized (this) {
                if(measurements == null) {
                    measurements = measurementsNew;
                }
            }
        }
        return measurements;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMeasurements() {
        measurements = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}