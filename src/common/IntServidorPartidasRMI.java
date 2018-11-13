package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IntServidorPartidasRMI extends Remote{

	public void nuevaPartida(int nf, int nc, int nb) throws RemoteException;

	public int pruebaCasilla(int fila, int col) throws RemoteException;

	public String getBarco(int id) throws RemoteException;

	public String[] getSolucion() throws RemoteException;

}
