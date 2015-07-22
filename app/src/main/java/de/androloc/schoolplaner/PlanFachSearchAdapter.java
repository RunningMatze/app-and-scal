package de.androloc.schoolplaner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.content.Context;
import android.database.Cursor;

public class PlanFachSearchAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private int idxFach, idxKurzCode;

	public PlanFachSearchAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxFach = c.getColumnIndex(context.getString(R.string.field_fach));
		idxKurzCode = c.getColumnIndex(context.getString(R.string.field_kurzcode));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView faecher_Fach = (TextView)view.findViewById(R.id.tv_faecher_search_fach);
		TextView faecher_Kurzcode = (TextView)view.findViewById(R.id.tv_faecher_search_kurzcode);
		String fach = cursor.getString(idxFach);
		faecher_Fach.setText(fach);
		String kurzcode = cursor.getString(idxKurzCode);
		faecher_Kurzcode.setText(kurzcode);
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.faecher_search_item, null);
	}

}
