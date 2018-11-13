package server;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.*;
import java.io.*;

public class ServidorFlotaRMI {
	
	public static  void main(String[] args) {
		String  URLRegistro;
		int numPuerto;
		try {
			numPuerto = 1099;
			ImplServidorJuegoRMI implServidorJuegoRMI = new ImplServidorJuegoRMI();
			arrancaRegistro(numPuerto);
			URLRegistro = "rmi://localhost:1099/HundirLaFlota";
			Naming.rebind(URLRegistro, implServidorJuegoRMI);
			System.out.println("Servidor registrado, el servidor contiene actialmente: ");
			listaRegistro(URLRegistro);
			System.out.println("Servidor preparado");
		}catch(Exception ex) {
			System.out.println("Exception in ServidorFlotaRMI.main: " + ex);
		}

	}
	private static void listaRegistro(String URLRegistro) throws RemoteException, MalformedURLException {
		System.out.println("Registro " + URLRegistro + "contiene: ");
		String[] nombres = Naming.list(URLRegistro);
		for(int i = 0; i<nombres.length; i++) {
			System.out.println(nombres[i]);
		}

	}
	private static void arrancaRegistro(int numPuerto) throws RemoteException{
		try {
			Registry registro = LocateRegistry.getRegistry(numPuerto);
			registro.list();	
		}catch (RemoteException e) {
			System.out.println("El registro RMI no se puede localizar en el puerto: " + numPuerto);
			Registry registro = LocateRegistry.createRegistry(numPuerto);
			System.out.println("Registro RMI creado en el puerto "+ numPuerto);
		}
	}

}