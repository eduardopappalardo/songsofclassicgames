package br.com.zuriquebolos.songsofclassicgames;

import java.io.File;
import com.android.vending.expansion.zipfile.ZipResourceFile;

public interface Player {
	
	public void carregarMusica(String caminhoMusica, ZipResourceFile arquivoExpansao) throws Exception;
	public void tocar();
	public void pausar();
	public void liberarRecursosJava();
	public boolean estaTocando();
	public void atribuirAcaoAoTerminarMusica(Acao acao);
	public String exportarMusica(File diretorioDestino, String caminhoMusica, ZipResourceFile arquivoExpansao) throws Exception;
	
}