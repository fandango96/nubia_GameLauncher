package cn.nubia.gamelauncherx.processmanager;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface IProcessManagerService extends IInterface {

    public static abstract class Stub extends Binder implements IProcessManagerService {
        private static final String DESCRIPTOR = "NBProcessManagerService";
        static final int TRANSACTION_addWhiteList = 7;
        static final int TRANSACTION_addWhiteListWithId = 11;
        static final int TRANSACTION_deleteWhiteList = 8;
        static final int TRANSACTION_deleteWhiteListWithId = 12;
        static final int TRANSACTION_getCanBeKilledRunningApps = 2;
        static final int TRANSACTION_killRunningApps = 3;
        static final int TRANSACTION_nbForceStopPackage = 9;
        static final int TRANSACTION_nbForceStopPackageWithId = 13;
        static final int TRANSACTION_oneKeyCleanExcludeCurrentApp = 4;
        static final int TRANSACTION_oneKeyCleanExcludeCurrentAppWithId = 10;
        static final int TRANSACTION_oneKeyCleanIncludeCurrentApp = 5;
        static final int TRANSACTION_queryWhiteList = 6;

        private static class Proxy implements IProcessManagerService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public List<Bundle> getCanBeKilledRunningApps() throws RemoteException {
                Parcel data = Parcel.obtain();
                Parcel reply = Parcel.obtain();
                data.writeInterfaceToken(Stub.DESCRIPTOR);
                this.mRemote.transact(2, data, reply, 0);
                reply.readException();
                ArrayList<Bundle> list = reply.createTypedArrayList(Bundle.CREATOR);
                data.recycle();
                reply.recycle();
                return list;
            }

            public int killRunningApps(List<Bundle> list) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedList(list);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int oneKeyCleanExcludeCurrentApp(String currentPkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(currentPkgName);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int oneKeyCleanExcludeCurrentAppWithId(String currentPkgName, int instanceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(currentPkgName);
                    _data.writeInt(instanceId);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int oneKeyCleanIncludeCurrentApp() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public List<String> queryWhiteList() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    return _reply.createStringArrayList();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long addWhiteList(String pkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public long addWhiteListWithId(String pkgName, int instanceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeInt(instanceId);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readLong();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int deleteWhiteList(String pkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int deleteWhiteListWithId(String pkgName, int instanceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeInt(instanceId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int nbForceStopPackageWithId(String pkgName, int instanceId) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    _data.writeInt(instanceId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int nbForceStopPackage(String pkgName) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(pkgName);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IProcessManagerService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IProcessManagerService)) {
                return new Proxy(obj);
            }
            return (IProcessManagerService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    List<Bundle> list = getCanBeKilledRunningApps();
                    reply.writeNoException();
                    reply.writeTypedList(list);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _result = killRunningApps(data.createTypedArrayList(Bundle.CREATOR));
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _result2 = oneKeyCleanExcludeCurrentApp(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    int _result3 = oneKeyCleanIncludeCurrentApp();
                    reply.writeNoException();
                    reply.writeInt(_result3);
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    List<String> _result4 = queryWhiteList();
                    reply.writeNoException();
                    reply.writeStringList(_result4);
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    long _result5 = addWhiteList(data.readString());
                    reply.writeNoException();
                    reply.writeLong(_result5);
                    return true;
                case 8:
                    data.enforceInterface(DESCRIPTOR);
                    int _result6 = deleteWhiteList(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result6);
                    return true;
                case 9:
                    data.enforceInterface(DESCRIPTOR);
                    int _result7 = nbForceStopPackage(data.readString());
                    reply.writeNoException();
                    reply.writeInt(_result7);
                    return true;
                case 10:
                    data.enforceInterface(DESCRIPTOR);
                    int _result8 = oneKeyCleanExcludeCurrentAppWithId(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result8);
                    return true;
                case 11:
                    data.enforceInterface(DESCRIPTOR);
                    long _result9 = addWhiteListWithId(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeLong(_result9);
                    return true;
                case 12:
                    data.enforceInterface(DESCRIPTOR);
                    int _result10 = deleteWhiteListWithId(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result10);
                    return true;
                case 13:
                    data.enforceInterface(DESCRIPTOR);
                    int _result11 = nbForceStopPackageWithId(data.readString(), data.readInt());
                    reply.writeNoException();
                    reply.writeInt(_result11);
                    return true;
                case 1598968902:
                    try {
                        reply.writeString(DESCRIPTOR);
                        return true;
                    } catch (Exception e) {
                        if (e instanceof SecurityException) {
                            reply.writeException(e);
                        } else if (e instanceof IllegalArgumentException) {
                            reply.writeException(e);
                        } else if (e instanceof NullPointerException) {
                            reply.writeException(e);
                        } else if (e instanceof IllegalStateException) {
                            reply.writeException(e);
                        } else if (e instanceof UnsupportedOperationException) {
                            reply.writeException(e);
                        } else {
                            reply.writeException(new IllegalStateException(e));
                        }
                        return true;
                    }
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    long addWhiteList(String str) throws RemoteException;

    long addWhiteListWithId(String str, int i) throws RemoteException;

    int deleteWhiteList(String str) throws RemoteException;

    int deleteWhiteListWithId(String str, int i) throws RemoteException;

    List<Bundle> getCanBeKilledRunningApps() throws RemoteException;

    int killRunningApps(List<Bundle> list) throws RemoteException;

    int nbForceStopPackage(String str) throws RemoteException;

    int nbForceStopPackageWithId(String str, int i) throws RemoteException;

    int oneKeyCleanExcludeCurrentApp(String str) throws RemoteException;

    int oneKeyCleanExcludeCurrentAppWithId(String str, int i) throws RemoteException;

    int oneKeyCleanIncludeCurrentApp() throws RemoteException;

    List<String> queryWhiteList() throws RemoteException;
}
