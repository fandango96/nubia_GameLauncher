package cn.nubia.game.networkacceleration.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface INetworkAccelerationService extends IInterface {

    public static abstract class Stub extends Binder implements INetworkAccelerationService {
        private static final String DESCRIPTOR = "cn.nubia.game.networkacceleration.service.INetworkAccelerationService";
        static final int TRANSACTION_doCloseVPN = 5;
        static final int TRANSACTION_getAccelerationState = 4;
        static final int TRANSACTION_isAccountLogined = 1;
        static final int TRANSACTION_loginNubiaAccount = 2;
        static final int TRANSACTION_queryXunyouUserState = 3;
        static final int TRANSACTION_registerCallback = 6;
        static final int TRANSACTION_unregisterCallback = 7;

        private static class Proxy implements INetworkAccelerationService {
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

            public boolean isAccountLogined() throws RemoteException {
                boolean _result = true;
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() == 0) {
                        _result = false;
                    }
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void loginNubiaAccount() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void queryXunyouUserState(String token, String code) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(token);
                    _data.writeString(code);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getAccelerationState() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    return _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void doCloseVPN() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerCallback(INetworkAccelerationCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterCallback(INetworkAccelerationCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb != null ? cb.asBinder() : null);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static INetworkAccelerationService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof INetworkAccelerationService)) {
                return new Proxy(obj);
            }
            return (INetworkAccelerationService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    boolean _result = isAccountLogined();
                    reply.writeNoException();
                    reply.writeInt(_result ? 1 : 0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    loginNubiaAccount();
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    queryXunyouUserState(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface(DESCRIPTOR);
                    int _result2 = getAccelerationState();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 5:
                    data.enforceInterface(DESCRIPTOR);
                    doCloseVPN();
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface(DESCRIPTOR);
                    registerCallback(cn.nubia.game.networkacceleration.service.INetworkAccelerationCallback.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface(DESCRIPTOR);
                    unregisterCallback(cn.nubia.game.networkacceleration.service.INetworkAccelerationCallback.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void doCloseVPN() throws RemoteException;

    int getAccelerationState() throws RemoteException;

    boolean isAccountLogined() throws RemoteException;

    void loginNubiaAccount() throws RemoteException;

    void queryXunyouUserState(String str, String str2) throws RemoteException;

    void registerCallback(INetworkAccelerationCallback iNetworkAccelerationCallback) throws RemoteException;

    void unregisterCallback(INetworkAccelerationCallback iNetworkAccelerationCallback) throws RemoteException;
}
