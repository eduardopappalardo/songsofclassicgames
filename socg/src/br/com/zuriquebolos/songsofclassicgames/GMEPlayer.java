package br.com.zuriquebolos.songsofclassicgames;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import com.android.vending.expansion.zipfile.ZipResourceFile;

public class GMEPlayer implements Player, Runnable {
	
	private int taxaAmostra = 44100;
	private int tamanhoBuffer = 65536;
	private int tamanhoAmostra = (this.tamanhoBuffer / 6);
	private long milisegundosDuracaoMaximaMusica = 4 * 60 * 1000;
	private long milisegundosDuracaoDiminuicaoVolume = 10 * 1000;
	private boolean estaTocando = false;
	private Thread threadPlayer;
	private Acao acaoAoTerminarMusica;
	
	private AudioTrack audioTrack = new AudioTrack(
			AudioManager.STREAM_MUSIC,
			this.taxaAmostra,
			AudioFormat.CHANNEL_CONFIGURATION_STEREO,
			AudioFormat.ENCODING_PCM_16BIT,
			this.tamanhoBuffer,
			AudioTrack.MODE_STREAM);
	
	static {
		System.loadLibrary("gmeplayer");
	}
	
	public void carregarMusica(String caminhoMusica, ZipResourceFile arquivoExpansao) throws Exception {
		
		if(!this.carregarDados(this.obterDadosMusica(caminhoMusica, arquivoExpansao), this.taxaAmostra)) {
			throw new Exception(this.obterUltimoErro());
		}
		this.configurarDiminuicaoVolume(this.obterDuracaoMusica(), this.milisegundosDuracaoDiminuicaoVolume);
	}
	
	public void tocar() {
		this.threadPlayer = new Thread(this);
		this.threadPlayer.setPriority(Thread.MAX_PRIORITY);
		this.threadPlayer.start();
	}
	
	public void pausar() {
		this.estaTocando = false;
		
		if(this.threadPlayer != null) {
			try {
				this.threadPlayer.join();
			}
			catch(Exception excecao) {
			}
		}
	}
	
	public void liberarRecursosJava() {
		this.pausar();
		this.liberarRecursos();
		this.audioTrack.release();
	}
	
	public boolean estaTocando() {
		return this.estaTocando;
	}
	
	public void atribuirAcaoAoTerminarMusica(Acao acao) {
		this.acaoAoTerminarMusica = acao;
	}
	
	public void run() {
		this.estaTocando = true;
		this.audioTrack.play();
		
		while(this.estaTocando && !this.terminouMusica()) {
			this.audioTrack.write(this.obterAmostra(this.tamanhoAmostra), 0, this.tamanhoAmostra);
		}
		this.audioTrack.pause();
		this.estaTocando = false;
		
		if(this.terminouMusica() && this.acaoAoTerminarMusica != null) {
			this.acaoAoTerminarMusica.executar();
		}
	}
	
	public String exportarMusica(File diretorioDestino, String caminhoMusica, ZipResourceFile arquivoExpansao) throws Exception {
		String nomeArquivoMusica = Util.obterPenultimoNivel(caminhoMusica) + " - " + Util.obterUltimoNivel(caminhoMusica) + ".wav";
		RandomAccessFile arquivoMusica = new RandomAccessFile(new File(diretorioDestino.getAbsolutePath() + File.separator + nomeArquivoMusica), "rw");
		
		arquivoMusica.setLength(0); // Set file length to 0, to prevent unexpected behavior in case the file already existed
		arquivoMusica.writeBytes("RIFF");
		arquivoMusica.writeInt(0); // Final file size not known yet, write 0
		arquivoMusica.writeBytes("WAVE");
		arquivoMusica.writeBytes("fmt ");
		arquivoMusica.writeInt(Integer.reverseBytes(16)); // Sub-chunk size, 16 for PCM
		arquivoMusica.writeShort(Short.reverseBytes((short) 1)); // AudioFormat, 1 for PCM
		arquivoMusica.writeShort(Short.reverseBytes((short) 2));// Number of channels, 1 for mono, 2 for stereo
		arquivoMusica.writeInt(Integer.reverseBytes(this.taxaAmostra)); // Sample rate
		arquivoMusica.writeInt(Integer.reverseBytes(this.taxaAmostra * 2 * 16 / 8)); // Byte rate, SampleRate*NumberOfChannels*BitsPerSample/8
		arquivoMusica.writeShort(Short.reverseBytes((short) (2 * 16 / 8))); // Block align, NumberOfChannels*BitsPerSample/8
		arquivoMusica.writeShort(Short.reverseBytes((short) 16)); // Bits per sample
		arquivoMusica.writeBytes("data");
		arquivoMusica.writeInt(0); // Data chunk size not known yet, write 0
		
		this.milisegundosDuracaoDiminuicaoVolume = 100;
		this.carregarMusica(caminhoMusica, arquivoExpansao);
		int bytesArquivoMusica = 0;
		
		while(!this.terminouMusica()) {
			bytesArquivoMusica += this.tamanhoAmostra * 2;
			ByteBuffer byteBuffer = ByteBuffer.allocate(this.tamanhoAmostra * 2);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			byteBuffer.asShortBuffer().put(this.obterAmostra(this.tamanhoAmostra));
			arquivoMusica.write(byteBuffer.array());
		}
		this.liberarRecursos();
		
		arquivoMusica.seek(4); // Write size to RIFF header
		arquivoMusica.writeInt(Integer.reverseBytes(36 + bytesArquivoMusica));
		arquivoMusica.seek(40); // Write size to Subchunk2Size field
		arquivoMusica.writeInt(Integer.reverseBytes(bytesArquivoMusica));
		arquivoMusica.close();
		
		return nomeArquivoMusica;
	}
	
	private long obterDuracaoMusica() {
		GMEInformacoesMusica gmeInformacoesMusica = this.obterGMEInformacoesMusica();
		long milisegundosTamanhoMusica = this.milisegundosDuracaoMaximaMusica;
		
		if(gmeInformacoesMusica.length > 0) {
			milisegundosTamanhoMusica = gmeInformacoesMusica.length;
		}
		else if(gmeInformacoesMusica.loopLength > 0) {
			milisegundosTamanhoMusica = (gmeInformacoesMusica.introLength + (gmeInformacoesMusica.loopLength * 2));
		}
		if(milisegundosTamanhoMusica > this.milisegundosDuracaoMaximaMusica) {
			return this.milisegundosDuracaoMaximaMusica;
		}
		else {
			return milisegundosTamanhoMusica;
		}
	}
	
	private GMEInformacoesMusica obterGMEInformacoesMusica() {
		GMEInformacoesMusica gmeInformacoesMusica = new GMEInformacoesMusica();
		String[] informacoesMusica = this.obterInformacoesMusica();
		
		gmeInformacoesMusica.length = Long.parseLong(informacoesMusica[0]);
		gmeInformacoesMusica.introLength = Long.parseLong(informacoesMusica[1]);
		gmeInformacoesMusica.loopLength = Long.parseLong(informacoesMusica[2]);
		gmeInformacoesMusica.song = informacoesMusica[3];
		gmeInformacoesMusica.game = informacoesMusica[4];
		gmeInformacoesMusica.system = informacoesMusica[5];
		gmeInformacoesMusica.author = informacoesMusica[6];
		gmeInformacoesMusica.copyright = informacoesMusica[7];
		gmeInformacoesMusica.comment = informacoesMusica[8];
		gmeInformacoesMusica.dumper = informacoesMusica[9];
		
		return gmeInformacoesMusica;
	}
	
	private byte[] obterDadosMusica(String caminhoMusica, ZipResourceFile arquivoExpansao) throws Exception {
		ZipInputStream zipInputStream = new ZipInputStream(arquivoExpansao.getInputStream(caminhoMusica));
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		byte[] dadosMusica = new byte[(int) zipEntry.getSize()];
		
		for(int posicao = 0, bytesLidos = zipInputStream.read(dadosMusica, posicao, dadosMusica.length); bytesLidos > 0;) {
			posicao += bytesLidos;
			bytesLidos = zipInputStream.read(dadosMusica, posicao, (dadosMusica.length - posicao));
		}
		zipInputStream.closeEntry();
		zipInputStream.close();
		return dadosMusica;
	}
	
	private native boolean carregarArquivo(String nomeArquivo, int taxaAmostra);
	
	private native boolean carregarDados(byte[] dados, int taxaAmostra);
	
	private native short[] obterAmostra(long tamanhoAmostra);
	
	private native boolean terminouMusica();
	
	private native void configurarDiminuicaoVolume(long milisegundosInicio, long milisegundosDuracao);
	
	private native String[] obterInformacoesMusica();
	
	private native String obterUltimoErro();
	
	private native void liberarRecursos();
	
	private static class GMEInformacoesMusica {
		
		public long length;
		public long introLength;
		public long loopLength;
		public String song;
		public String game;
		public String system;
		public String author;
		public String copyright;
		public String comment;
		public String dumper;
		
	}
}