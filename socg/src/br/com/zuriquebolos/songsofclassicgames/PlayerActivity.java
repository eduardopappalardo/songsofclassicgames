package br.com.zuriquebolos.songsofclassicgames;

import java.io.File;
import java.io.InputStream;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.vending.expansion.zipfile.ZipResourceFile;

public class PlayerActivity extends Activity {
	
	private Player player;
	
	private ImageView imagemJogo;
	private TextView nomeMusica;
	
	private ImageButton botaoMusicaAnterior;
	private ImageView textoBotaoMusicaAnterior;
	
	private ImageButton botaoProximoDiretorio;
	private ImageView textoBotaoProximoDiretorio;
	
	private ImageButton botaoProximaMusica;
	private ImageView textoBotaoProximaMusica;
	
	private ImageButton botaoDiretorioAnterior;
	private ImageView textoBotaoDiretorioAnterior;
	
	private ImageButton botaoMusicaAleatoria;
	private TextView textoBotaoMusicaAleatoria;
	private boolean musicaAleatoria = false;
	
	private ImageButton botaoRepeticaoMusica;
	private TextView textoBotaoRepeticaoMusica;
	private boolean repeticaoMusica = false;
	
	private ImageButton botaoAjuda;
	private TextView textoBotaoAjuda;
	private boolean controleBotaoAjuda = false;
	
	private ImageButton botaoTocarPausar;
	private TextView textoBotaoTocarPausar;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_player);
        
        this.imagemJogo = (ImageView) super.findViewById(R.id.player_activity_imagem_jogo);
        this.nomeMusica = (TextView) super.findViewById(R.id.player_activity_nome_musica);
        
        this.botaoMusicaAnterior = (ImageButton) super.findViewById(R.id.player_activity_botao_musica_anterior);
        this.textoBotaoMusicaAnterior = (ImageView) super.findViewById(R.id.player_activity_texto_botao_musica_anterior);
        
        this.botaoProximoDiretorio = (ImageButton) super.findViewById(R.id.player_activity_botao_proximo_diretorio);
        this.textoBotaoProximoDiretorio = (ImageView) super.findViewById(R.id.player_activity_texto_botao_proximo_diretorio);
        
        this.botaoProximaMusica = (ImageButton) super.findViewById(R.id.player_activity_botao_proxima_musica);
        this.textoBotaoProximaMusica = (ImageView) super.findViewById(R.id.player_activity_texto_botao_proxima_musica);
        
        this.botaoDiretorioAnterior = (ImageButton) super.findViewById(R.id.player_activity_botao_diretorio_anterior);
        this.textoBotaoDiretorioAnterior = (ImageView) super.findViewById(R.id.player_activity_texto_botao_diretorio_anterior);
        
        this.botaoMusicaAleatoria = (ImageButton) super.findViewById(R.id.player_activity_botao_musica_aleatoria);
        this.botaoMusicaAleatoria.setAdjustViewBounds(true);
        this.textoBotaoMusicaAleatoria = (TextView) super.findViewById(R.id.player_activity_texto_botao_musica_aleatoria);
        
        this.botaoRepeticaoMusica = (ImageButton) super.findViewById(R.id.player_activity_botao_repeticao_musica);
        this.botaoRepeticaoMusica.setAdjustViewBounds(true);
        this.textoBotaoRepeticaoMusica = (TextView) super.findViewById(R.id.player_activity_texto_botao_repeticao_musica);
        
        this.botaoAjuda = (ImageButton) super.findViewById(R.id.player_activity_botao_ajuda);
        this.textoBotaoAjuda = (TextView) super.findViewById(R.id.player_activity_texto_botao_ajuda);
        
        this.botaoTocarPausar = (ImageButton) super.findViewById(R.id.player_activity_botao_tocar_pausar);
        this.textoBotaoTocarPausar = (TextView) super.findViewById(R.id.player_activity_texto_botao_tocar_pausar);
        
        this.botaoMusicaAnterior.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					retrocederMusica();
				}
		});
        this.botaoProximoDiretorio.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					avancarDiretorio();
				}
		});
        this.botaoProximaMusica.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					avancarMusica();
				}
		});
        this.botaoDiretorioAnterior.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					retrocederDiretorio();
				}
		});
        this.botaoMusicaAleatoria.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					ativarDesativarMusicaAleatoria();
				}
		});
        this.botaoRepeticaoMusica.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					ativarDesativarRepeticaoMusica();
				}
		});
        this.botaoAjuda.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					ativarDesativarAjuda();
				}
		});
        this.botaoTocarPausar.setOnClickListener(
        	new OnClickListener() {
				public void onClick(View v) {
					iniciarPausarMusica();
				}
		});
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = super.getMenuInflater();
        inflater.inflate(R.menu.activity_player, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	new AlertDialog.Builder(this)
			.setMessage(super.getString(R.string.sobre_texto))
			.setPositiveButton(super.getString(R.string.ok), null)
			.show();
    	return super.onOptionsItemSelected(item);
    }
    
    protected void onDestroy() {
    	this.liberarRecursos();
    	super.onDestroy();
    }
    
    public void onBackPressed() {
    	((PrincipalActivity) super.getParent()).sair(this);
    }
    
    public void selecionarMusica(String caminhoMusica) {
		try {
			this.liberarRecursos();
			ZipResourceFile arquivoExpansao = ((PrincipalActivity) super.getParent()).getArquivoExpansao();
			InputStream imagemJogo = arquivoExpansao.getInputStream(Util.removerUltimoNivel(caminhoMusica) + File.separator + "imagem_previa");
			this.imagemJogo.setImageDrawable(Drawable.createFromStream(imagemJogo, null));
			this.nomeMusica.setText(Util.multiplicarCaractere(" ", 30) + Util.obterUltimoNivel(caminhoMusica));
			this.nomeMusica.setSelected(true);
			this.player = PlayerUtil.obterPlayer(caminhoMusica);
			this.player.carregarMusica(caminhoMusica, arquivoExpansao);
			this.player.atribuirAcaoAoTerminarMusica(
				new Acao() {
					public void executar() {
						acaoAoTerminarMusica();
					}
				}
			);
			this.iniciarMusica();
		}
		catch(Exception excecao) {
			Toast.makeText(this, super.getString(R.string.falha_ao_carregar_musica), Toast.LENGTH_LONG).show();
		}
	}
    
    public void liberarRecursos() {
    	
    	if(this.player != null) {
    		
    		if(this.player.estaTocando()) {
    			this.pausarMusica();
    		}
    		this.player.liberarRecursosJava();
    		this.player = null;
    	}
    }
    
    private void acaoAoTerminarMusica() {
    	super.runOnUiThread(
    		new Runnable() {
				public void run() {
					ListaMusicaActivity listaMusicaActivity = ((PrincipalActivity) getParent()).getListaMusicaActivity();
			    	
			    	if(repeticaoMusica) {
			    		selecionarMusica(listaMusicaActivity.obterMusicaAtual());
			    	}
			    	else {
			    		if(musicaAleatoria) {
			        		selecionarMusica(listaMusicaActivity.obterMusicaAleatoria());
			        	}
			        	else {
			        		selecionarMusica(listaMusicaActivity.obterProximaMusica());
			        	}
			    	}
				}
    		}
    	);
    }
    
    private void retrocederMusica() {
    	
    	if(this.musicaAleatoria) {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterMusicaAleatoria());
    	}
    	else {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterMusicaAnterior());
    	}
    }
    
    private void avancarDiretorio() {
    	
    	if(this.musicaAleatoria) {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterMusicaAleatoria());
    	}
    	else {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterMusicaProximoDiretorio());
    	}
    }
    
	private void avancarMusica() {
		
		if(this.musicaAleatoria) {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterMusicaAleatoria());
    	}
    	else {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterProximaMusica());
    	}
    }
	
	private void retrocederDiretorio() {
		
		if(this.musicaAleatoria) {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterMusicaAleatoria());
    	}
    	else {
    		this.selecionarMusica(((PrincipalActivity) super.getParent()).getListaMusicaActivity().obterMusicaDiretorioAnterior());
    	}
	}
	
	private void ativarDesativarMusicaAleatoria() {
		
		if(this.musicaAleatoria) {
			this.musicaAleatoria = false;
			this.botaoMusicaAleatoria.setImageResource(R.drawable.botao_rosa);
			this.textoBotaoMusicaAleatoria.setText(super.getString(R.string.aleatorio_desligado));
			this.textoBotaoMusicaAleatoria.setTextColor(super.getResources().getColor(R.color.rosa));
		}
		else {
			this.musicaAleatoria = true;
			this.botaoMusicaAleatoria.setImageResource(R.drawable.botao_roxo);
			this.textoBotaoMusicaAleatoria.setText(super.getString(R.string.aleatorio_ligado));
			this.textoBotaoMusicaAleatoria.setTextColor(super.getResources().getColor(R.color.roxa));
		}
	}
	
	private void ativarDesativarRepeticaoMusica() {
		
		if(this.repeticaoMusica) {
			this.repeticaoMusica = false;
			this.botaoRepeticaoMusica.setImageResource(R.drawable.botao_rosa);
			this.textoBotaoRepeticaoMusica.setText(super.getString(R.string.repeticao_desligada));
			this.textoBotaoRepeticaoMusica.setTextColor(super.getResources().getColor(R.color.rosa));
		}
		else {
			this.repeticaoMusica = true;
			this.botaoRepeticaoMusica.setImageResource(R.drawable.botao_roxo);
			this.textoBotaoRepeticaoMusica.setText(super.getString(R.string.repeticao_ligada));
			this.textoBotaoRepeticaoMusica.setTextColor(super.getResources().getColor(R.color.roxa));
		}
	}
	
	private void ativarDesativarAjuda() {
		
		if(this.controleBotaoAjuda) {
			this.controleBotaoAjuda = false;
			this.textoBotaoMusicaAnterior.setVisibility(View.INVISIBLE);
			this.textoBotaoProximoDiretorio.setVisibility(View.INVISIBLE);
			this.textoBotaoProximaMusica.setVisibility(View.INVISIBLE);
			this.textoBotaoDiretorioAnterior.setVisibility(View.INVISIBLE);
			this.textoBotaoMusicaAleatoria.setVisibility(View.INVISIBLE);
			this.textoBotaoRepeticaoMusica.setVisibility(View.INVISIBLE);
			this.textoBotaoAjuda.setText(super.getString(R.string.ajuda));
		}
		else {
			this.controleBotaoAjuda = true;
			this.textoBotaoMusicaAnterior.setVisibility(View.VISIBLE);
			this.textoBotaoProximoDiretorio.setVisibility(View.VISIBLE);
			this.textoBotaoProximaMusica.setVisibility(View.VISIBLE);
			this.textoBotaoDiretorioAnterior.setVisibility(View.VISIBLE);
			this.textoBotaoMusicaAleatoria.setVisibility(View.VISIBLE);
			this.textoBotaoRepeticaoMusica.setVisibility(View.VISIBLE);
			this.textoBotaoAjuda.setText(super.getString(R.string.ocultar));
		}
	}
	
	private void iniciarPausarMusica() {
		
		if(this.player != null && this.player.estaTocando()) {
    		this.pausarMusica();
    	}
    	else {
    		this.iniciarMusica();
    	}
    }
	
    private void iniciarMusica() {
    	ListaMusicaActivity listaMusicaActivity = ((PrincipalActivity) super.getParent()).getListaMusicaActivity();
    	
		if(this.player == null) {
			this.selecionarMusica(listaMusicaActivity.obterProximaMusica());
		}
		else {
			this.player.tocar();
		}
		this.nomeMusica.setVisibility(View.VISIBLE);
		this.nomeMusica.setSelected(true);
		this.textoBotaoTocarPausar.setText(super.getString(R.string.pausar));
		this.criarNotificacao(Util.obterUltimoNivel(listaMusicaActivity.obterMusicaAtual()));
    }
    
    private void pausarMusica() {
    	this.player.pausar();
    	this.nomeMusica.setVisibility(View.INVISIBLE);
    	this.nomeMusica.setSelected(false);
    	this.textoBotaoTocarPausar.setText(super.getString(R.string.iniciar));
    	this.removerNotificacao();
    }
	
	private void criarNotificacao(String nomeMusica) {
		NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this)
											        .setSmallIcon(R.drawable.icone)
											        .setContentTitle(super.getString(R.string.app_name))
											        .setContentText(nomeMusica)
											        .setOngoing(true);
		
		notificacao.setContentIntent(PendingIntent.getActivity(this, 0, super.getParent().getIntent(), PendingIntent.FLAG_UPDATE_CURRENT));
		NotificationManager notificationManager = (NotificationManager) super.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notificacao.build());
	}
	
	private void removerNotificacao() {
		NotificationManager notificationManager = (NotificationManager) super.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(1);
	}
}