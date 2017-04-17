package br.com.zuriquebolos.songsofclassicgames;

import java.io.File;

public class Util {
	
	private Util() {
	}
	
	public static String obterUltimoNivel(String diretorio) {
		int ultimaPosicaoSeparador = diretorio.lastIndexOf(File.separator);
		
		if(ultimaPosicaoSeparador > -1) {
			return diretorio.substring((ultimaPosicaoSeparador + 1));
		}
		else {
			return diretorio;
		}
	}
	
	public static String obterPenultimoNivel(String diretorio) {
		String[] niveis = diretorio.split(File.separator);
		return niveis[niveis.length - 2];
	}
	
	public static String removerUltimoNivel(String diretorio) {
		int ultimaPosicaoSeparador = diretorio.lastIndexOf(File.separator);
		return diretorio.substring(0, ultimaPosicaoSeparador);
	}
	
	public static String multiplicarCaractere(String caractere, int quantidade) {
		StringBuilder string = new StringBuilder();
		
		for(int cont = 0; cont < quantidade; cont++) {
			string.append(caractere);
		}
		return string.toString();
	}
}