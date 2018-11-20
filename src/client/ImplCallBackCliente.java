package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import common.IntCallbackCliente;

public class ImplCallBackCliente extends UnicastRemoteObject implements IntCallbackCliente{

	/**
	 * 
	 */
	private static final long serialVersionUID = 864251279623181587L;

	ImplCallBackCliente() throws RemoteException{
		super();
	}

	@Override
	public void notificame(String mensaje) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println(mensaje + " ha aceptado tu partida");
	}

}
