package common;
import java.rmi.*;

public interface IntCallbackCliente extends java.rmi.Remote{
	
	public void notificame(String mensaje) throws java.rmi.RemoteException;
}
