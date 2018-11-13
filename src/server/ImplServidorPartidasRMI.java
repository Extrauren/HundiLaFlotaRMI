package server;

import java.rmi.RemoteException;

import common.IntServidorPartidasRMI;
import common.Partida;

public class ImplServidorPartidasRMI implements IntServidorPartidasRMI {
		
	private Partida partida;
	
	public ImplServidorPartidasRMI() throws RemoteException{
		super();
	}
	
	@Override
	public void nuevaPartida(int nf, int nc, int nb)  throws RemoteException{
		// TODO Auto-generated method stub
		partida = new Partida(nf, nc, nb);
	}

	@Override
	public int pruebaCasilla(int fila, int col) throws RemoteException{
		// TODO Auto-generated method stub
		return partida.pruebaCasilla(fila, col);
	}

	@Override
	public String getBarco(int id) throws RemoteException{
		// TODO Auto-generated method stub
		return partida.getBarco(id);
	}

	@Override
	public String[] getSolucion() {
		// TODO Auto-generated method stub
		return partida.getSolucion();
	}
	
}
