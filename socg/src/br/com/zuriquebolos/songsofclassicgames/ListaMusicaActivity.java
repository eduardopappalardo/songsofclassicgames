package br.com.zuriquebolos.songsofclassicgames;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.vending.expansion.zipfile.ZipResourceFile;

public class ListaMusicaActivity extends ListActivity {
	
	private final static String DIRETORIO_INICIAL = "musicas";
	private String diretorioAtual;
	private Map<String, String[]> mapaDiretorios = new HashMap<String, String[]>();
	private List<String> listaMusicas = new ArrayList<String>();
	private int posicaoMusicaSelecionada = -1;
	private ImageView imagemCabecalho;
	private TextView textoCabecalho;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_lista_musica);
        super.registerForContextMenu(super.getListView());
        this.gerarCabecalho();
        this.popularInformacoesArquivos();
        this.listarItensDiretorio(DIRETORIO_INICIAL);
    }
    
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	
    	if(this.listaMusicas.contains(this.diretorioAtual + File.separator + this.mapaDiretorios.get(this.diretorioAtual)[((AdapterContextMenuInfo) menuInfo).position - 1])) {
    		menu.add(Menu.NONE, 1, Menu.NONE, super.getString(R.string.exportar_ringtone));
    		menu.add(Menu.NONE, 2, Menu.NONE, super.getString(R.string.exportar_alarm));
    	}
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	final String caminhoMusica = this.diretorioAtual + File.separator + this.mapaDiretorios.get(this.diretorioAtual)[((AdapterContextMenuInfo) item.getMenuInfo()).position - 1];
    	final File diretorioDestino = new File(Environment.getExternalStorageDirectory() + File.separator + "media" + File.separator + (item.getItemId() == 1 ? "ringtones" : "alarms"));
    	final ZipResourceFile arquivoExpansao = ((PrincipalActivity) super.getParent()).getArquivoExpansao();
    	
    	if(!diretorioDestino.exists()) {
    		
    		if(!diretorioDestino.mkdirs()) {
    			Toast.makeText(this, super.getString(R.string.falha_ao_escrever_armazenamento_externo), Toast.LENGTH_LONG).show();
    			return super.onOptionsItemSelected(item);
    		}
    	}
    	((PrincipalActivity) super.getParent()).getPlayerActivity().liberarRecursos();
    	final Activity activity = this;
    	
    	(new AsyncTask<Void, String, String>() {
    		
    		private ProgressDialog progressDialog;
    		
    		protected void onPreExecute() {
    			this.progressDialog = new ProgressDialog(activity);
    			this.progressDialog.setTitle(getString(R.string.processando));
    			this.progressDialog.setMessage(getString(R.string.por_favor_aguarde));
    			this.progressDialog.setCancelable(false);
    			this.progressDialog.setIndeterminate(true);
    			this.progressDialog.show();
    		}
    		
    		protected String doInBackground(Void... arg0) {
    			try {
    				return "File '" + PlayerUtil.obterPlayer(caminhoMusica).exportarMusica(diretorioDestino, caminhoMusica, arquivoExpansao) + "' exported";
    			}
    			catch(Exception excecao) {
    				return "Failed to export file";
    			}
    		}
    		
    		protected void onPostExecute(String result) {
    			this.progressDialog.dismiss();
    			Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
    			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + diretorioDestino.getAbsolutePath())));
    		}
    		
    	}).execute();
    	return super.onOptionsItemSelected(item);
    }
    
    public void onBackPressed() {
    	
    	if(!this.diretorioAtual.equals(DIRETORIO_INICIAL)) {
    		this.listarItensDiretorio(Util.removerUltimoNivel(this.diretorioAtual));
    	}
    	else {
    		((PrincipalActivity) super.getParent()).sair(this);
    	}
    }
    
    public void onListItemClick(ListView l, View v, int position, long id) {
    	this.listarItensDiretorio(this.diretorioAtual + File.separator + this.mapaDiretorios.get(this.diretorioAtual)[position - 1]);
    }
    
    public String obterMusicaAnterior() {
    	
    	if((this.posicaoMusicaSelecionada - 1) < 0) {
    		return this.obterMusicaNaPosicao(this.listaMusicas.size() - 1);
    	}
    	else {
    		return this.obterMusicaNaPosicao(this.posicaoMusicaSelecionada - 1);
    	}
    }
    
    public String obterMusicaProximoDiretorio() {
    	
    	if(this.posicaoMusicaSelecionada != -1) {
    		String diretorioMusicaAtual = Util.removerUltimoNivel(this.listaMusicas.get(this.posicaoMusicaSelecionada));
    		
    		for(int cont = 0, posicaoMusica = (this.posicaoMusicaSelecionada + 1); cont < this.listaMusicas.size(); cont++, posicaoMusica++) {
    			
    			if(posicaoMusica >= this.listaMusicas.size()) {
    				posicaoMusica = 0;
    			}
    			if(!this.listaMusicas.get(posicaoMusica).startsWith(diretorioMusicaAtual + File.separator)) {
    				return this.obterMusicaNaPosicao(posicaoMusica);
    			}
    		}
    		return this.obterProximaMusica();
    	}
    	else {
    		return this.obterProximaMusica();
    	}
    }
    
    public String obterProximaMusica() {
    	
    	if((this.posicaoMusicaSelecionada + 1) >= this.listaMusicas.size()) {
    		return this.obterMusicaNaPosicao(0);
    	}
    	else {
    		return this.obterMusicaNaPosicao(this.posicaoMusicaSelecionada + 1);
    	}
    }
    
    public String obterMusicaDiretorioAnterior() {
    	
    	if(this.posicaoMusicaSelecionada != -1) {
    		String diretorioMusicaAtual = Util.removerUltimoNivel(this.listaMusicas.get(this.posicaoMusicaSelecionada));
    		
    		for(int cont = 0, posicaoMusica = (this.posicaoMusicaSelecionada - 1); cont < this.listaMusicas.size(); cont++, posicaoMusica--) {
    			
    			if(posicaoMusica < 0) {
    				posicaoMusica = (this.listaMusicas.size() - 1);
    			}
    			if(!this.listaMusicas.get(posicaoMusica).startsWith(diretorioMusicaAtual + File.separator)) {
    				return this.obterMusicaNaPosicao(posicaoMusica);
    			}
    		}
    		return this.obterMusicaAnterior();
    	}
    	else {
    		return this.obterMusicaAnterior();
    	}
    }
    
    public String obterMusicaAleatoria() {
    	int posicaoMusica;
    	while((posicaoMusica = new Random().nextInt(this.listaMusicas.size())) == this.posicaoMusicaSelecionada);
    	return this.obterMusicaNaPosicao(posicaoMusica);
    }
    
    public String obterMusicaAtual() {
    	return this.obterMusicaNaPosicao(this.posicaoMusicaSelecionada);
    }

	private String obterMusicaNaPosicao(int posicaoMusica) {
    	this.posicaoMusicaSelecionada = posicaoMusica;
    	return this.listaMusicas.get(posicaoMusica);
    }
	
    private void listarItensDiretorio(String diretorio) {
    	
    	if(!this.listaMusicas.contains(diretorio)) {
    		this.diretorioAtual = diretorio;
    		ZipResourceFile arquivoExpansao = ((PrincipalActivity) super.getParent()).getArquivoExpansao();
    		List<ListaMusicaItem> itens = new ArrayList<ListaMusicaItem>();
    		
    		for(String textoItem : this.mapaDiretorios.get(diretorio)) {
    			
    			if(this.listaMusicas.contains(diretorio + File.separator + textoItem)) {
    				itens.add(new ListaMusicaItem(super.getResources().getDrawable(R.drawable.music), textoItem, true, super.getResources().getDisplayMetrics()));
    			}
    			else {
    				try {
						InputStream imagemItem = arquivoExpansao.getInputStream(diretorio + File.separator + textoItem + File.separator + "imagem_previa");
						itens.add(new ListaMusicaItem(Drawable.createFromStream(imagemItem, null), textoItem, false, null));
					}
					catch(Exception excecao) {
					}
    			}
    		}
    		try {
				this.imagemCabecalho.setImageDrawable(Drawable.createFromStream(arquivoExpansao.getInputStream(diretorio + File.separator + "imagem_previa"), null));
			}
			catch(Exception excecao) {
			}
    		this.textoCabecalho.setText(Util.obterUltimoNivel(diretorio).replaceFirst(("^" + DIRETORIO_INICIAL), ""));
    		super.setListAdapter(new ListaMusicaAdapter(this, itens));
    	}
    	else {
    		this.posicaoMusicaSelecionada = this.listaMusicas.indexOf(diretorio);
    		((PrincipalActivity) super.getParent()).getPlayerActivity().selecionarMusica(diretorio);
    		((PrincipalActivity) super.getParent()).mostrarPlayerActivity();
    	}
    }
    
    private void popularInformacoesArquivos() {
    	ZipResourceFile arquivoExpansao = ((PrincipalActivity) super.getParent()).getArquivoExpansao();
    	String linha = null;
    	String[] vetor = null;
    	try {
    		BufferedReader br = new BufferedReader(new InputStreamReader(arquivoExpansao.getInputStream("musicas.txt")));
    		
			while((linha = br.readLine()) != null) {
				this.listaMusicas.add(linha);
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(arquivoExpansao.getInputStream("diretorios.txt")));
			
			while((linha = br.readLine()) != null) {
				vetor = linha.split("=");
				this.mapaDiretorios.put(vetor[0], vetor[1].split(";"));
			}
			br.close();
		}
		catch(Exception excecao) {
		}
    }
    
    private void gerarCabecalho() {
    	View cabecalho = super.getLayoutInflater().inflate(R.layout.lista_musica_cabecalho, null);
        cabecalho.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBackPressed();
			}
		});
        super.getListView().addHeaderView(cabecalho);
        this.imagemCabecalho = (ImageView) cabecalho.findViewById(R.id.lista_musica_cabecalho_imagem);
        this.textoCabecalho = (TextView) cabecalho.findViewById(R.id.lista_musica_cabecalho_texto);
    }
}