package common;
import java.rmi.*;

public interface IntCallbackCliente extends Remote{
	
	public void notificame(String mensaje) throws RemoteException;
}
