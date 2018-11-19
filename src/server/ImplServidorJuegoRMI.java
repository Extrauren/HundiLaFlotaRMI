package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import common.IntCallbackCliente;
import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;

public class ImplServidorJuegoRMI extends UnicastRemoteObject implements IntServidorJuegoRMI {


	private static final long serialVersionUID = 7984651231546889L;
	private HashMap<String,IntCallbackCliente> mapaCallbacks;

	public ImplServidorJuegoRMI() throws RemoteException{
		super();
		mapaCallbacks = new HashMap<>();
	}
	@Override
	public IntServidorPartidasRMI nuevoServidorPartidas() throws RemoteException {

		return new ImplServidorPartidasRMI();
	}

	@Override
	public synchronized boolean proponPartida(String nombreJugador, IntCallbackCliente callbackClientObject)
			throws RemoteException {
		// TODO Auto-generated method stub
		if (mapaCallbacks.containsKey(nombreJugador)) { //El juegador intenta hacer una petición de juego pero si ya está en el map no la pone.
			System.out.println("El jugador "+ nombreJugador + "ha intentado proponer una partida pero ya tenia una propuesta");
			return false;
		}else {
			mapaCallbacks.put(nombreJugador, callbackClientObject);
			System.out.println("El jugador "+ nombreJugador + "ha propuesto una partida");
			return true;
		}

	}

	@Override
	public synchronized boolean borraPartida(String nombreJugador) throws RemoteException {
		// TODO Auto-generated method stub
		if(mapaCallbacks.containsKey(nombreJugador)) {						//Intenta borrar una peticion si la ha hecho
			mapaCallbacks.remove(nombreJugador);
			return true;
		}else {
			System.out.println("Has intentado borrar una peticion que no has hecho");
			return false;
		}
	}

	@Override
	public synchronized String[] listaPartidas() throws RemoteException {
		// TODO Auto-generated method stub
		String[] listado = new String[mapaCallbacks.size()];
		int indice=0;
		for( String propuesta : mapaCallbacks.keySet()) {
			listado[indice]=propuesta;
			indice++;
		}
		return listado;
	}

	@Override
	public synchronized boolean aceptaPartida(	String nombreJugador, String nombreRival) throws RemoteException {
		// TODO Auto-generated method stub
		if(!mapaCallbacks.containsKey(nombreRival)) {
			System.out.println("El rival " + nombreRival + "no ha propuesto partida.");
			return false;
		}else {
			try {
				mapaCallbacks.get(nombreRival).notificame(nombreJugador);

			}catch(Exception e){
				System.out.println("El rival" +nombreRival + "se ha desconectado o su partida ha sido borrada");
				return false;
			}finally {
				mapaCallbacks.remove(nombreRival);
			}
			System.out.println("Se ha aceptado la notificación y se ha borrado");
			return true;
		}
	}
}
