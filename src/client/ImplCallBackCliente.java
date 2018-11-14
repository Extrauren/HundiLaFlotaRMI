package client;

import java.rmi.RemoteException;

import common.IntCallbackCliente;

public class ImplCallBackCliente implements IntCallbackCliente{

	ImplCallBackCliente(){
		super();
	}

	@Override
	public void notificame(String mensaje) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println(mensaje + "ha aceptado tu partida");
	}

}
