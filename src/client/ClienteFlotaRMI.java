package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.rmi.*;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import common.IntServidorJuegoRMI;
import common.IntServidorPartidasRMI;
import server.Partida;

public class ClienteFlotaRMI {

	private IntServidorJuegoRMI intServidorJuegoRMI;
	public static final int AGUA = -1, TOCADO = -2, HUNDIDO = -3;
	public static final int NUMFILAS=8, NUMCOLUMNAS=8, NUMBARCOS=6;
	public static final String PROPON="Proponer partida", BORRA="Borrar partida", LISTA="Listar partidas", ACEPTA="Aceptar partida";


	private GuiTablero guiTablero=null;			// El juego se encarga de crear y modificar la interfaz gráfica
	private Partida partida = null;                 
	private static IntServidorPartidasRMI partida1 = null; 
	private static IntServidorJuegoRMI serverPartidaEnJuego;			// Objeto con los datos de la partida en juego
	private ImplCallBackCliente callB;
	private String nombre;

	private int quedan = NUMBARCOS, disparos = 0;

	public static void main(String[] args) throws IOException, NotBoundException {
		ClienteFlotaRMI cliente = new ClienteFlotaRMI();
		cliente.dale();
	}

	public void dale() {

		try {			
			
			Scanner sc = new Scanner(System.in);	
			System.out.println("Introduce el nombre: ");
			nombre = sc.nextLine();			
			
			String URLRegistro = "rmi://localhost:1099/HundirLaFlota";
			intServidorJuegoRMI = (IntServidorJuegoRMI) Naming.lookup(URLRegistro);
			System.out.println("Busqueda completa");
			partida1 = intServidorJuegoRMI.nuevoServidorPartidas();
			System.out.println("Busqueda completa patata");
			partida1.nuevaPartida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
			callB = new ImplCallBackCliente ();

			partida = new Partida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					guiTablero = new GuiTablero(NUMFILAS, NUMCOLUMNAS);
					guiTablero.dibujaTablero();
				}
			});

		}catch (Exception e) {
			System.out.println("Excepcion en ClienteFlotaRMI");
			e.printStackTrace();
		}
	}
	class GuiTablero {

		private int numFilas, numColumnas;

		private JFrame frame = null;        // Tablero de juego
		private JLabel estado = null;       // Texto en el panel de estado
		private JButton buttons[][] = null; // Botones asociados a las casillas de la partida

		/**
		 * Constructor de una tablero dadas sus dimensiones
		 */
		GuiTablero(int numFilas, int numColumnas) {
			this.numFilas = numFilas;
			this.numColumnas = numColumnas;
			frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.addWindowListener(new EscuchadorVentana());
		}

		/**
		 * Dibuja el tablero de juego y crea la partida inicial
		 */
		public void dibujaTablero() {
			anyadeMenu();
			anyadeGrid(numFilas, numColumnas);		
			anyadePanelEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);		
			frame.setSize(300, 300);
			frame.setVisible(true);	
		} // end dibujaTablero

		/**
		 * Anyade el menu de opciones del juego y le asocia un escuchador
		 */
		private void anyadeMenu() {
			// POR IMPLEMENTAR
			JMenu menu;
			JMenuBar barra;
			JMenuItem salir, nuevaPartida, solucion;
			barra=new JMenuBar();							//crea la barra superior donde se colocara el desplegable
			menu = new JMenu("Opciones");					//crea el menu desplegable
			salir=new JMenuItem("Salir");					
			nuevaPartida= new JMenuItem("Nueva Partida");	//crea los botones de dentro del desplegale
			solucion= new JMenuItem("Solucion");			
			menu.add(nuevaPartida);
			menu.add(solucion);								//a�ade los botones al desplegable
			menu.add(salir);  
			barra.add(menu);								//a�ade el desplegable a la barra superior
			frame.setJMenuBar(barra);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			solucion.addActionListener(new MenuListener());
			nuevaPartida.addActionListener(new MenuListener());			//escuchadores de los botones
			salir.addActionListener(new MenuListener());

			menu = new JMenu("Multijugador");
			barra.add(menu);

			MultijugadorListener listenerMulti = new MultijugadorListener();

			JMenuItem propon = new JMenuItem(PROPON); //Se crea el boton Proponer partida
			propon.setActionCommand(PROPON);
			propon.addActionListener(listenerMulti);

			JMenuItem borra = new JMenuItem(BORRA);	//Se crea el boton borrar partida
			borra.setActionCommand(BORRA);
			borra.addActionListener(listenerMulti);

			JMenuItem lista = new JMenuItem(LISTA); //Se crea el boton Listar partidas
			lista.setActionCommand(LISTA);
			lista.addActionListener(listenerMulti);

			JMenuItem acepta = new JMenuItem(ACEPTA);	//Se crea el boton Aceptar partida
			acepta.setActionCommand(ACEPTA);
			acepta.addActionListener(listenerMulti);

			menu.add(propon);
			menu.add(borra);
			menu.add(lista);
			menu.add(acepta);

			frame.add(barra,BorderLayout.NORTH);

		} // end anyadeMenu

		/**
		 * Anyade el panel con las casillas del mar y sus etiquetas.
		 * Cada casilla sera un boton con su correspondiente escuchador
		 * @param nf	numero de filas
		 * @param nc	numero de columnas
		 */
		private void anyadeGrid(int nf, int nc) {
			// POR IMPLEMENTAR
			ButtonListener listener = new ButtonListener();
			buttons=new JButton[nf][nc];
			JPanel panel = new JPanel();
			nf++;
			nc+=2;
			panel.setLayout(new GridLayout(nf, nc));
			char c = 'A';
			//dibuja la primera fila de la matriz * 1 2 3 4 5 6 7 8 *  
			for (int a = 0; a < nc ; a++) { 
				Integer ent = a;
				if(a == 0|| a== nc-1) panel.add(new JLabel("", JLabel.CENTER));
				else panel.add(new JLabel(ent.toString(), JLabel.CENTER));

			}
			//crea el resto de la matriz, botones y las letras de las coordenadas
			for(int i = 1; i<nf; i++) {	
				for (int j =0; j<nc ; j++) {
					if(j == 0|| j == nc-1) panel.add(new JLabel(Character.toString(c), JLabel.CENTER));
					else { 
						JButton boton = new JButton();
						boton.putClientProperty("fila", i-1);		//guardamos los datos de cada boton para usarlos en el escuchador
						boton.putClientProperty("col", j-1);
						boton.addActionListener(listener);
						buttons[i-1][j-1] = boton;				//a�adimos los botones a la matriz de botones 
						panel.add(boton);
					}
				}
				c++;
			}

			frame.add(panel);

		} // end anyadeGrid

		/**
		 * Anyade el panel de estado al tablero
		 * @param cadena	cadena inicial del panel de estado
		 */
		private void anyadePanelEstado(String cadena) {	
			JPanel panelEstado = new JPanel();
			estado = new JLabel(cadena);
			panelEstado.add(estado);
			frame.getContentPane().add(panelEstado, BorderLayout.SOUTH);
		} // end anyadePanel Estado

		/**
		 * Cambia la cadena mostrada en el panel de estado
		 * @param cadenaEstado	nuevo estado
		 */
		public void cambiaEstado(String cadenaEstado) {
			estado.setText(cadenaEstado);
		} // end cambiaEstado

		/**
		 * Muestra la solucion de la partida y marca la partida como finalizada
		 */
		public void muestraSolucion() {
			// POR IMPLEMENTAR

			for(int i=0;i<numFilas;i++){
				for (int j = 0; j < numColumnas; j++) {
					//veremos que nos devuelve prueba casilla 
					pintaBoton(buttons[i][j], Color.CYAN);
				}
			}
			String solucion[]= partida.getSolucion();
			for(String idBarco : solucion) {
				pintaBarcoHundido(idBarco);
			}
		} // end muestraSolucion

		/**
		 * Pinta un barco como hundido en el tablero
		 * @param cadenaBarco	cadena con los datos del barco codifificados como
		 *                      "filaInicial#columnaInicial#orientacion#tamanyo"
		 */
		public void pintaBarcoHundido(String cadenaBarco) {
			// POR IMPLEMENTAR
			String[] vectorBarco = cadenaBarco.split("#");
			String filaIni = vectorBarco[0];
			String colIni= vectorBarco[1];
			String orientacion= vectorBarco[2];
			String tamanyo= vectorBarco[3];
			int fila=Integer.parseInt(filaIni);
			int col = Integer.parseInt(colIni);
			if(Integer.parseInt(tamanyo)==1) {
				JButton boton = buttons[fila][col];
				pintaBoton(boton, Color.RED);
			}else {
				for(int i = 0; i< Integer.parseInt(tamanyo) ; i++) {
					if(orientacion.equals("H")) {
						JButton boton = buttons[fila][col];
						pintaBoton(boton, Color.RED);
						col++;
					}else if(orientacion.equals("V")){
						JButton boton = buttons[fila][col];
						pintaBoton(boton, Color.RED);
						fila++;
					}
				}
			}


		} // end pintaBarcoHundido

		/**
		 * Pinta un botón de un color dado
		 * @param b			boton a pintar
		 * @param color		color a usar
		 */
		public void pintaBoton(JButton b, Color color) {
			b.setBackground(color);
			// El siguiente código solo es necesario en Mac OS X
			b.setOpaque(true);
			b.setBorderPainted(false);
		} // end pintaBoton

		/**
		 * Limpia las casillas del tablero pintándolas del gris por defecto
		 */
		public void limpiaTablero() {
			for (int i = 0; i < numFilas; i++) {
				for (int j = 0; j < numColumnas; j++) {
					buttons[i][j].setBackground(null);
					buttons[i][j].setOpaque(true);
					buttons[i][j].setBorderPainted(true);
				}
			}
		} // end limpiaTablero

		/**
		 * 	Destruye y libera la memoria de todos los componentes del frame
		 */
		public void liberaRecursos() {
			frame.dispose();
		} // end liberaRecursos


	} // end class GuiTablero

	/******************************************************************************************/
	/*********************  CLASE INTERNA MenuListener ****************************************/
	/******************************************************************************************/

	/**
	 * Clase interna que escucha el menu de Opciones del tablero
	 * 
	 */
	class MenuListener implements ActionListener {

		public MenuListener() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// POR IMPLEMENTAR
			String comando = e.getActionCommand();
			if (comando.equals("Nueva Partida")) {				//Los comandos a realizar al pulsar cada boton de las opciones
				quedan = NUMBARCOS;
				disparos = 0;	        	   
				partida = new Partida(NUMFILAS, NUMCOLUMNAS, NUMBARCOS);
				guiTablero.limpiaTablero();
				guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);
			}
			else if (comando.equals("Solucion")) {
				guiTablero.muestraSolucion();
			}
			else if (comando.equals("Salir")) {
				System.exit(0);		   
				guiTablero.liberaRecursos();
			}
		} // end actionPerformed

	} // end class MenuListener



	/******************************************************************************************/
	/*********************  CLASE INTERNA ButtonListener **************************************/
	/******************************************************************************************/
	/**
	 * Clase interna que escucha cada uno de los botones del tablero
	 * Para poder identificar el boton que ha generado el evento se pueden usar las propiedades
	 * de los componentes, apoyandose en los metodos putClientProperty y getClientProperty
	 */
	class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// POR IMPLEMENTA
			JButton boton; 
			boton = (JButton) e.getSource();
			int fila = (Integer) boton.getClientProperty("fila");
			int col = (Integer) boton.getClientProperty("col");
			int estado = partida.pruebaCasilla(fila, col);
			if(!boton.getBackground().equals(Color.CYAN) && !boton.getBackground().equals( Color.ORANGE) &&  !boton.getBackground().equals(Color.RED) && quedan>0) {
				if(estado == -1) {									//si es mar
					guiTablero.pintaBoton(boton, Color.CYAN);
				}else if( estado == -2) {							//si es tocado
					guiTablero.pintaBoton(boton, Color.ORANGE);	
				}else{												//si es un hundido
					guiTablero.pintaBarcoHundido(partida.getBarco(estado));
					quedan--;
				}
			}
			disparos++;
			if(quedan == 0) guiTablero.cambiaEstado("Game Over"); 

			else guiTablero.cambiaEstado("Intentos: " + disparos + "    Barcos restantes: " + quedan);

		} // end actionPerformed

	} // end class ButtonListener
	//Clase interna para escuchar el menu Multijugador

	private class MultijugadorListener implements ActionListener{

		public void actionPerformed(ActionEvent e){
			try{
				switch(e.getActionCommand()){
				case PROPON:
					if (serverPartidaEnJuego.proponPartida(nombre, callB))
						System.out.println("Has propuesto una partida.");
					else
						System.out.println("Ya tienes propuesta una partida.");
					break;
				case BORRA:
					if (serverPartidaEnJuego.borraPartida(nombre))
						System.out.println("Se ha borrado tu partida propuesta.");
					else
						System.out.println("No tenias ninguna partida propuesta.");					
					break;
				case LISTA:
					String[] lista = serverPartidaEnJuego.listaPartidas();
					if (lista.length == 0) {
						System.out.println("No hay partidas propuestas.");
					}else {
						System.out.print("Hay las siguientes partidas propuestas: ");
						for (String nombre : lista){
							System.out.print(nombre+" ");
						}
						System.out.println("");
					}
					break;
				case ACEPTA:
					System.out.print("Introduce el nombre de partida que quieres aceptar: ");
					Scanner sc = new Scanner(System.in);	
					System.out.println("Introduce el otro nombre: ");
					String otro = sc.nextLine();
					if (otro.equals(nombre)) { //Se hace aqui la comprobacion para poder mostrar distintos mensajes.
						System.out.println("No puedes aceptar tu propia partida.");
					}else if (serverPartidaEnJuego.aceptaPartida(nombre, otro)) {
						System.out.println("Has aceptado la partida de "+otro+".");
					}else {
						System.out.println("No se puede aceptar la partida de "+otro+". No hay partida o no esta en linea.");
					}
					break;
				}
			}catch(Exception ex){
				System.out.println("Exception MultijugadorListener.");
			}
		}
	}
	private class EscuchadorVentana extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			try {
				serverPartidaEnJuego.borraPartida(nombre);
			}catch(RemoteException ex) {
				ex.printStackTrace();
			}
			System.exit(0);
		}

	} 

}
