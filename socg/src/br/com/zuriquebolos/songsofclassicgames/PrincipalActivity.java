package br.com.zuriquebolos.songsofclassicgames;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;
import com.google.android.vending.expansion.downloader.Helpers;

@SuppressWarnings("deprecation")
public class PrincipalActivity extends TabActivity {
	
	private ZipResourceFile arquivoExpansao;
	private ListaMusicaActivity listaMusicaActivity;
	private PlayerActivity playerActivity;
	
	private final static String nomeListaMusicaActivity = "Songs";
	private final static String nomePlayerActivity = "Player";
	
	public static final ArquivoExpansao[] arquivosExpansao = {new ArquivoExpansao(true, 5, 222339476L)};
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        	
        	if(!this.arquivosExpansaoExistem()) {
				super.startActivity(new Intent(this, MyDownloaderActivity.class));
        		super.finish();
        		return;
        	}
        	else {
        		try {
        			//int versionCode = super.getPackageManager().getPackageInfo(super.getPackageName(), 0).versionCode;
        			this.arquivoExpansao = APKExpansionSupport.getAPKExpansionZipFile(this, arquivosExpansao[0].mFileVersion, 0);
        		}
        		catch(Exception excecao) {
        			new AlertDialog.Builder(this)
	        			.setMessage(super.getString(R.string.falha_ao_carregar_arquivos))
	        			.setPositiveButton(super.getString(R.string.ok),
        					new Dialog.OnClickListener() {
		        				public void onClick(DialogInterface dialog, int which) {
		        					finish();
		        				}
		        			}
	        			)
	        			.show();
        			return;
        		}
        	}
        }
        else {
        	new AlertDialog.Builder(this)
				.setMessage(super.getString(R.string.monte_cartao_memoria))
				.setPositiveButton(super.getString(R.string.ok),
					new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}
				)
				.show();
			return;
        }
        super.setContentView(R.layout.activity_principal);
        TabHost tabHost = super.getTabHost();
        
        TabSpec abaMusicas = tabHost.newTabSpec(nomeListaMusicaActivity);
        abaMusicas.setIndicator(this.criarAba(nomeListaMusicaActivity));
        abaMusicas.setContent(new Intent(this, ListaMusicaActivity.class));
        
        TabSpec abaPlayer = tabHost.newTabSpec(nomePlayerActivity);
        abaPlayer.setIndicator(this.criarAba(nomePlayerActivity));
        abaPlayer.setContent(new Intent(this, PlayerActivity.class));
        
        tabHost.addTab(abaMusicas);
        tabHost.addTab(abaPlayer);
        
        tabHost.setCurrentTabByTag(nomeListaMusicaActivity);
        this.listaMusicaActivity = (ListaMusicaActivity) super.getCurrentActivity();
        
        tabHost.setCurrentTabByTag(nomePlayerActivity);
        this.playerActivity = (PlayerActivity) super.getCurrentActivity();
    }
    
    public ZipResourceFile getArquivoExpansao() {
		return this.arquivoExpansao;
	}

    public ListaMusicaActivity getListaMusicaActivity() {
		return this.listaMusicaActivity;
	}

    public PlayerActivity getPlayerActivity() {
		return this.playerActivity;
	}

    public void mostrarPlayerActivity() {
    	super.getTabHost().setCurrentTabByTag(nomePlayerActivity);
    }
	
    public void sair(Activity activity) {
    	new AlertDialog.Builder(activity)
			.setMessage(super.getString(R.string.deseja_fechar_aplicacao))
			.setPositiveButton(super.getString(R.string.sim),
				new Dialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
				    	finish();
					}
				}
			)
			.setNegativeButton(super.getString(R.string.nao), null)
			.show();
	}

	private View criarAba(String texto) {
    	View view = super.getLayoutInflater().inflate(R.layout.aba, null);
    	((TextView) view.findViewById(R.id.aba_texto)).setText(texto);
    	return view;
    }
	
	private boolean arquivosExpansaoExistem() {
		
	    for(ArquivoExpansao arquivoExpansao : arquivosExpansao) {
	        String fileName = Helpers.getExpansionAPKFileName(this, arquivoExpansao.mIsMain, arquivoExpansao.mFileVersion);
	        
	        if(!Helpers.doesFileExist(this, fileName, arquivoExpansao.mFileSize, false)) {
	        	return false;
	        }
	    }
	    return true;
	}
}