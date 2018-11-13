package client;

import java.io.*;
import java.rmi.*;

import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

public class ClienteFlotaRMI {
	public static void main(String[] args) {
		try {
			int numPuerto;
			String nombreNodo;
			InputStreamReader ent = new InputStreamReader(System.in);
			BufferedReader buf = new BufferedReader(ent);
			System.out.println("Introduce el nombre del nodo del registro RMI: ");
			nombreNodo = buf.readLine();
			System.out.println("Introduce el numero de puerto del registro RMI: ");
			numPuerto = Integer.parseInt(buf.readLine());
			String URLRegistro = "rmi://localhost: "+numPuerto+ "/HundirLaFlota";
			IntServidorJuegoRMI intServidorJuegoRMI = (IntServidorJuegoRMI) Naming.lookup(URLRegistro);
			System.out.println("Busqueda completa");
			IntServidorPartidasRMI nuevaPartida = intServidorJuegoRMI.nuevoServidorPartidas();	
		}catch (Exception e) {
			System.out.println("Excepcion en ClienteFlotaRMI");
		}
	}
}
