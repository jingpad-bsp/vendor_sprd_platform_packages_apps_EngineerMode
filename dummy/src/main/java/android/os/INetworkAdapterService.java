package android.os;

public interface INetworkAdapterService extends android.os.IInterface {

    public static abstract class Stub extends android.os.Binder implements android.os.INetworkAdapterService {
        public static android.os.INetworkAdapterService asInterface(android.os.IBinder obj) {
            throw new RuntimeException("You can't be here");
        }
    }

    public void setDnsFilterEnable(int enable) throws android.os.RemoteException;
}
