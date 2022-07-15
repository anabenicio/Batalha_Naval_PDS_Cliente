package br.ufc.cliente;

public class VerificadorCoordenada{
	
	public VerificadorCoordenada() {}
		
	public boolean tamanhoValido(String coordenada){		
		return coordenada.length() == 2;		
	}
			
	public boolean coordenadaExiste(String coordenada){
		char digito1 = coordenada.charAt(0);
		char digito2 = coordenada.charAt(1);
		
		return (digito1 >= 'A' && digito1 <= 'J') && (digito2 >= '0' && digito2 <= '9');
	}
}
