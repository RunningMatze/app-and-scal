package de.androloc.schoolplaner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class FaecherSearchAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private int idxFach, idxKurzcode;

	public FaecherSearchAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxFach = c.getColumnIndex(context.getString(R.string.field_fach));
		idxKurzcode = c.getColumnIndex(context.getString(R.string.field_kurzcode));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView faecher_Fach = (TextView)view.findViewById(R.id.tv_faecher_search_fach);
		TextView faecher_Kurzcode = (TextView)view.findViewById(R.id.tv_faecher_search_kurzcode);
		String fach = cursor.getString(idxFach);
		String kurzcode = cursor.getString(idxKurzcode);
		faecher_Fach.setText(fach);
		faecher_Kurzcode.setText(kurzcode);
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.faecher_search_item, null);
	}
}
