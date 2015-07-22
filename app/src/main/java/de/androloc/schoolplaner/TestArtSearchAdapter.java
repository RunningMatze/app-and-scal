package de.androloc.schoolplaner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.content.Context;
import android.database.Cursor;

public class TestArtSearchAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	private int idxTestArt;

	public TestArtSearchAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxTestArt = c.getColumnIndex(context.getString(R.string.field_testart));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tv_testart = (TextView)view.findViewById(R.id.tv_testart_search_art);
		tv_testart.setText(cursor.getString(idxTestArt));
	}
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.testart_search_item, null);
	}

}
