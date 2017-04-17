package br.com.zuriquebolos.songsofclassicgames;

import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class ListaMusicaItem {
	
	private Drawable imagemItem;
	private String textoItem;
	private boolean itemMusica;
	private DisplayMetrics displayMetrics;

	public ListaMusicaItem(Drawable imagemItem, String textoItem, boolean itemMusica, DisplayMetrics displayMetrics) {
		this.imagemItem = imagemItem;
		this.textoItem = textoItem;
		this.itemMusica = itemMusica;
		this.displayMetrics = displayMetrics;
	}

	public Drawable getImagemItem() {
		return this.imagemItem;
	}

	public String getTextoItem() {
		return this.textoItem;
	}
	
	public boolean getItemMusica() {
		return this.itemMusica;
	}
	
	public DisplayMetrics getDisplayMetrics() {
		return this.displayMetrics;
	}
}