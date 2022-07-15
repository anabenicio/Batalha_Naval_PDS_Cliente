package br.ufc.cliente;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

import br.ufc.interpretador.InterfaceInterpretadorComandos;
import br.ufc.interpretador.InterpretadorComandos;

public class Cliente extends JFrame implements InterfaceCliente{	
	private static final long serialVersionUID = 1L;
	
	private JTextField JTAentrada;		
	
	private JTextArea JTTabuleiroDefesa;
	private JTextArea JTTabuleiroAtaque;
	private JTextArea JTInstrucoes;
	
	private JButton JBEnviar;
	
	private PrintStream saida;
	
	private InterfaceInterpretadorComandos interpretador;
	
	private Socket socket;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente frame = new Cliente();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private String formatar(String tabuleiro){
		return tabuleiro.replace("/p", "\n"); 
	}
		
	private void enviarMensagem() throws IOException{
		String mensagem = JTAentrada.getText();
		
		JTAentrada.setText("");
		
		VerificadorCoordenada verificador = new VerificadorCoordenada();		
		
		if (mensagem != null){
			mensagem = mensagem.toUpperCase();
			
			if (!verificador.tamanhoValido(mensagem)){
				escreverMensagem("Erro! Coordenadas devem possuir apenas a letra e o numero que definem a linha e a coluna."
						+ "\nDigite a coordenada novamente!");
			}
			else if (!verificador.coordenadaExiste(mensagem)){
				escreverMensagem("Erro! A coordenada não corresponde a nenhuma das coordenadas do tabuleiro!"
						+ "\nDigite a coordenada novamente!");
			}
			else
				saida.println(mensagem);
		}
	}
	
	private void mostrarMensagem(){		
		Thread mostrarMensagem = new Thread() {			
			public void run() {
				try {
					@SuppressWarnings("resource")
					Scanner entrada = new Scanner(socket.getInputStream());

					while (true) {
						String msg = entrada.nextLine();
						interpretador.interpretarComando(msg);									
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch(NoSuchElementException e){}	
			}
		};
		
		mostrarMensagem.start();
	}

	public Cliente() {
		String IP = JOptionPane.showInputDialog("Informe o IP do servidor");
		int porta = Integer.parseInt(JOptionPane.showInputDialog("Informe a porta do servidor"));
		
		try {
			socket = new Socket(IP, porta);
			saida = new PrintStream(socket.getOutputStream(), true);
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(this, "O servidor não foi encontrado!");
			System.exit(0);
		}
		
		interpretador = new InterpretadorComandos(this);				

		this.setSize(780, 421);
		this.setTitle("Batalha Naval");
		this.setLocationRelativeTo(null); 
		this.setDefaultCloseOperation(EXIT_ON_CLOSE); 
		this.setResizable(false);
		this.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 11, 774, 381);		
		panel.setLayout(null);
		getContentPane().add(panel);			
		
		JTTabuleiroDefesa = new JTextArea();
		JTTabuleiroDefesa.setFont(new Font("Monospaced", Font.BOLD, 16));
		JTTabuleiroDefesa.setBorder(new MatteBorder(2, 2, 2, 2, (Color) new Color(105, 105, 105)));
		JTTabuleiroDefesa.setVerifyInputWhenFocusTarget(false);
		JTTabuleiroDefesa.setRequestFocusEnabled(false);
		JTTabuleiroDefesa.setFocusable(false);
		JTTabuleiroDefesa.setEditable(false);	
		JTTabuleiroDefesa.setBounds(20, 40, 356, 247);
		JTTabuleiroDefesa.setLineWrap(true);
		JTTabuleiroDefesa.setBackground(new Color(135, 206, 250));
		panel.add(JTTabuleiroDefesa);			
		
		JTTabuleiroAtaque = new JTextArea();
		JTTabuleiroAtaque.setFocusable(false);
		JTTabuleiroAtaque.setFont(new Font("Monospaced", Font.BOLD, 16));
		JTTabuleiroAtaque.setBorder(new MatteBorder(2, 2, 2, 2, (Color) new Color(105, 105, 105)));
		JTTabuleiroAtaque.setEditable(false);
		JTTabuleiroAtaque.setLineWrap(true);
		JTTabuleiroAtaque.setBackground(new Color(135, 206, 235));
		JTTabuleiroAtaque.setBounds(398, 40, 356, 247);
		panel.add(JTTabuleiroAtaque);
		
		JTextField jTFTabDefesa = new JTextField();
		jTFTabDefesa.setFont(new Font("Tahoma", Font.BOLD, 11));
		jTFTabDefesa.setDisabledTextColor(new Color(0, 0, 0));
		jTFTabDefesa.setText("TABULEIRO DE DEFESA");
		jTFTabDefesa.setHorizontalAlignment(SwingConstants.CENTER);
		jTFTabDefesa.setEnabled(false);
		jTFTabDefesa.setEditable(false);
		jTFTabDefesa.setBounds(20, 11, 356, 20);
		jTFTabDefesa.setColumns(10);
		panel.add(jTFTabDefesa);		
		
		JTextField jTFTabAtaque = new JTextField();
		jTFTabAtaque.setFont(new Font("Tahoma", Font.BOLD, 11));
		jTFTabAtaque.setDisabledTextColor(new Color(0, 0, 0));
		jTFTabAtaque.setText("TABULEIRO DE ATAQUE");
		jTFTabAtaque.setHorizontalAlignment(SwingConstants.CENTER);
		jTFTabAtaque.setEnabled(false);
		jTFTabAtaque.setEditable(false);
		jTFTabAtaque.setColumns(10);
		jTFTabAtaque.setBounds(398, 11, 356, 20);
		panel.add(jTFTabAtaque);
		
		JTInstrucoes = new JTextArea();
		JTInstrucoes.setText("Aguarde a conex\u00E3o do outro jogador!");
		JTInstrucoes.setWrapStyleWord(true);
		JTInstrucoes.setLineWrap(true);
		JTInstrucoes.setRows(3);
		JTInstrucoes.setBounds(20, 332, 356, 46);		
		JTInstrucoes.setRequestFocusEnabled(false);
		JTInstrucoes.setFont(new Font("Arial", Font.BOLD, 13));
		JTInstrucoes.setBackground(UIManager.getColor("Button.background"));
		JTInstrucoes.setFocusable(false);
		JTInstrucoes.setFocusTraversalKeysEnabled(false);
		JTInstrucoes.setEditable(false);		
		panel.add(JTInstrucoes);
		
		JTAentrada = new JTextField();		
		JTAentrada.setDisabledTextColor(Color.LIGHT_GRAY);						
		JTAentrada.setBounds(398, 345, 248, 21);		
		JTAentrada.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		JTAentrada.setPreferredSize(new Dimension(250, 50));
		JTAentrada.setBackground(Color.WHITE);
		JTAentrada.setEnabled(false);
		JTAentrada.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {			
				try {
					enviarMensagem();
				} catch (IOException e1) {					
					e1.printStackTrace();
				}								
			}
		});
		
		panel.add(JTAentrada);
		
		JBEnviar = new JButton("Enviar");
		JBEnviar.setBounds(656, 344, 89, 23);
		JBEnviar.setEnabled(false);
		JBEnviar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					enviarMensagem();
				} catch (IOException e1) {					
					e1.printStackTrace();
				}				
			}
		});
		panel.add(JBEnviar);
		
		JTextArea txtrLegenda = new JTextArea();
		txtrLegenda.setFocusable(false);
		txtrLegenda.setEditable(false);
		txtrLegenda.setFont(new Font("Arial", Font.BOLD, 13));
		txtrLegenda.setBackground(UIManager.getColor("Button.background"));
		txtrLegenda.setText("Legenda:\t# - Barco\tX - Barco Atingido\t* - Tiro na \u00E1gua\t~ - Posi\u00E7\u00E3o vazia\r\n");
		txtrLegenda.setBounds(40, 298, 694, 23);
		panel.add(txtrLegenda);
		setVisible(true);
		
		mostrarMensagem();		
	}

	@Override
	public void escreverMensagem(String mensagem) {				
		JTInstrucoes.setText(mensagem);	
	}

	@Override
	public void escreverTabuleiroAtaque(String tabuleiro) {
		String tab = formatar(tabuleiro);
		JTTabuleiroAtaque.setText(tab);
	}

	@Override
	public void escreverTabuleiroDefesa(String tabuleiro) {
		String tab = formatar(tabuleiro);
		JTTabuleiroDefesa.setText(tab);	
	}

	@Override
	public void suaVez() {
		JTAentrada.setEnabled(true);	
		JBEnviar.setEnabled(true);
	}

	@Override
	public void espere() {		
		JTAentrada.setEnabled(false);
		JBEnviar.setEnabled(false);
	}	
}