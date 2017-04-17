package br.com.zuriquebolos.songsofclassicgames;

import java.util.List;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListaMusicaAdapter extends ArrayAdapter<ListaMusicaItem> {
	
	public ListaMusicaAdapter(Context context, List<ListaMusicaItem> objects) {
		super(context, R.layout.lista_musica_item, objects);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ListaMusicaItem listaMusicaItem = super.getItem(position);
		ViewHolder viewHolder;
		
		if(convertView == null) {
			convertView = LayoutInflater.from(super.getContext()).inflate(R.layout.lista_musica_item, null);
			viewHolder = new ViewHolder();
			viewHolder.imagemItem = (ImageView) convertView.findViewById(R.id.lista_musica_item_imagem);
			viewHolder.textoItem = (TextView) convertView.findViewById(R.id.lista_musica_item_texto);
			
			if(listaMusicaItem.getItemMusica()) {
				viewHolder.imagemItem.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, listaMusicaItem.getDisplayMetrics());
				viewHolder.imagemItem.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, listaMusicaItem.getDisplayMetrics());
			}
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.imagemItem.setImageDrawable(listaMusicaItem.getImagemItem());
		viewHolder.textoItem.setText(listaMusicaItem.getTextoItem());
		return convertView;
	}
	
	private static class ViewHolder {
		ImageView imagemItem;
		TextView textoItem;
	}
}