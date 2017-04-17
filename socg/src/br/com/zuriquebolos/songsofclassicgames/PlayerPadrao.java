package br.com.zuriquebolos.songsofclassicgames;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import com.android.vending.expansion.zipfile.ZipResourceFile;

public class PlayerPadrao implements Player {
	
	private MediaPlayer mediaPlayer = new MediaPlayer();
	
	public PlayerPadrao() {
	}
	
	public void carregarMusica(String caminhoMusica, ZipResourceFile arquivoExpansao) throws Exception {
		this.mediaPlayer.reset();
		AssetFileDescriptor assetFileDescriptor = arquivoExpansao.getAssetFileDescriptor(caminhoMusica);
		this.mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
		this.mediaPlayer.prepare();
	}
	
	public void tocar() {
		this.mediaPlayer.start();
	}
	
	public void pausar() {
		this.mediaPlayer.pause();
	}
	
	public void liberarRecursosJava() {
		this.mediaPlayer.stop();
		this.mediaPlayer.release();
	}
	
	public boolean estaTocando() {
		return this.mediaPlayer.isPlaying();
	}
	
	public void atribuirAcaoAoTerminarMusica(final Acao acao) {
		this.mediaPlayer.setOnCompletionListener(
			new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					acao.executar();
				}
			}
		);
	}
	
	public String exportarMusica(File diretorioDestino, String caminhoMusica, ZipResourceFile arquivoExpansao) throws Exception {
		String nomeArquivoMusica = Util.obterPenultimoNivel(caminhoMusica) + " - " + Util.obterUltimoNivel(caminhoMusica) + ".mp3";
		FileOutputStream arquivoMusica = new FileOutputStream(new File(diretorioDestino.getAbsolutePath() + File.separator + nomeArquivoMusica));
		InputStream dadosMusica = arquivoExpansao.getInputStream(caminhoMusica);
		byte[] buffer = new byte[1024];
		int bytesLidos;
		
        while((bytesLidos = dadosMusica.read(buffer)) > 0) {
        	arquivoMusica.write(buffer, 0, bytesLidos);
        }
        dadosMusica.close();
        arquivoMusica.close();
		return nomeArquivoMusica;
	}
}