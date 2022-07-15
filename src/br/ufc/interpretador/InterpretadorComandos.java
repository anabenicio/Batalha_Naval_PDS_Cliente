package br.ufc.interpretador;

import br.ufc.cliente.InterfaceCliente;

public class InterpretadorComandos implements InterfaceInterpretadorComandos{
	public InterfaceCliente janelaCliente;
	
	public InterpretadorComandos(InterfaceCliente janela) {
		janelaCliente = janela;
	}

	@Override
	public void interpretarComando(String comando) {
		String cmd = comando.substring(0, 4);			
		String corpo = comando.substring(4);
		
		if (cmd.equals("/msg"))
			janelaCliente.escreverMensagem(corpo);
		
		else if (cmd.equals("/atk"))
			janelaCliente.escreverTabuleiroAtaque(corpo);		
		else if (cmd.equals("/def"))
			janelaCliente.escreverTabuleiroDefesa(corpo);	
		else if (cmd.equals("/svz"))
			janelaCliente.suaVez();
		else if (cmd.equals("/esp"))
			janelaCliente.espere();
		
	}
}
