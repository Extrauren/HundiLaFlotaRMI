package client;

import java.io.*;
import java.rmi.*;

import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

public class ClienteFlotaRMI {

	private IntServidorJuegoRMI intServidorJuegoRMI;

	public static void main(String[] args) throws IOException, NotBoundException {
		ClienteFlotaRMI cliente = new ClienteFlotaRMI();
		cliente.run();
	}

	public void run() {

		try {

			int numPuerto;
			String nombreNodo;
			InputStreamReader ent = new InputStreamReader(System.in);
			BufferedReader buf = new BufferedReader(ent);
			System.out.println("Introduce el nombre del nodo del registro RMI: ");
			nombreNodo = buf.readLine();
			System.out.println("Introduce el numero de puerto del registro RMI: ");
			//numPuerto = Integer.parseInt(buf.readLine());
			String URLRegistro = "rmi://localhost:1099/HundirLaFlota";
			intServidorJuegoRMI = (IntServidorJuegoRMI) Naming.lookup(URLRegistro);
			System.out.println("Busqueda completa");
			IntServidorPartidasRMI nuevaPartida = intServidorJuegoRMI.nuevoServidorPartidas();

		}catch (Exception e) {
			System.out.println("Excepcion en ClienteFlotaRMI");
		}
	}
}
