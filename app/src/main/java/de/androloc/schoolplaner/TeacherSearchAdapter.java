package de.androloc.schoolplaner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TeacherSearchAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private int idxAnrede, idxVorname, idxName;
	private String anzeigeName;

	public TeacherSearchAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxAnrede = c.getColumnIndex(context.getString(R.string.field_anrede));
		idxVorname = c.getColumnIndex(context.getString(R.string.field_vorname));
		idxName = c.getColumnIndex(context.getString(R.string.field_nachname));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView teacher_Name = (TextView)view.findViewById(R.id.tv_teacher_search_name);
		ImageView teacher_Icon = (ImageView)view.findViewById(R.id.icon_teacher_search_mw);
		anzeigeName = "";
		String anrede = cursor.getString(idxAnrede);
		String vorname = cursor.getString(idxVorname);
		String nachname = cursor.getString(idxName);
		anzeigeName = nachname;
		if (vorname.length() > 0) {
			if (anzeigeName.length() > 0) {
				anzeigeName += ", ";
			}
			anzeigeName += vorname;
		}
		teacher_Name.setText(anzeigeName);
		if (anrede.equals("Frau")) {
			teacher_Icon.setImageResource(R.drawable.teacher_woman);
		}
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.teacher_search_item, null);
	}
}
