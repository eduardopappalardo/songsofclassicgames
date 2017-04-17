package br.com.zuriquebolos.songsofclassicgames;

public class PlayerUtil {
	
	public static Player obterPlayer(String caminhoMusica) {
		caminhoMusica = caminhoMusica.toUpperCase();
		
		if(caminhoMusica.contains("NINTENDO 64")) {
			return new PlayerPadrao();
		}
		else if(caminhoMusica.contains("MASTER SYSTEM")
		|| caminhoMusica.contains("MEGA DRIVE")
		|| caminhoMusica.contains("SUPER NINTENDO")
		|| caminhoMusica.contains("NINTENDO")) {
			return new GMEPlayer();
		}
		else {
			return new PlayerPadrao();
		}
	}
}