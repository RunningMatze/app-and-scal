package de.androloc.schoolplaner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class DayNotesListAdapter extends CursorAdapter {

	private Context listContext;
	private Cursor listCursor;
	private LayoutInflater inflater;
	private ClassCalendar cal;
	private int idxID, idxDatum, idxNote;
	private DB_DatabaseHelper db;
	
	/*Flag für die Anzeige des Tages bei Wochenansicht */
	private boolean showDay;
	public boolean isShowDay() {
		return showDay;
	}
	public void setShowDay(boolean showDay) {
		this.showDay = showDay;
	}

	public DayNotesListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxID = c.getColumnIndex(context.getString(R.string.field_id));
		idxDatum = c.getColumnIndex(context.getString(R.string.field_kalendertag));
		idxNote = c.getColumnIndex(context.getString(R.string.field_notiz));
		cal = new ClassCalendar(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		listContext = context;
		listCursor = cursor;
		final long listID = cursor.getLong(idxID);
		
		/* Datum Notiz */
		TextView tvDatum = (TextView)view.findViewById(R.id.tv_datum);
		ClassCalendarDay day = cal.GetDayByID(cursor.getLong(idxDatum));
		tvDatum.setText(day.getDatum_show());

		/* Notiz */
		TextView tvNote = (TextView)view.findViewById(R.id.tv_notiz);
		tvNote.setText(cursor.getString(idxNote));

		/* In der Wochenansicht den Tag anzeigen */
		TextView tvDay = (TextView)view.findViewById(R.id.tv_note_day);
		if (showDay) {
			tvDay.setText(day.getWochentag());
			tvDay.setVisibility(View.VISIBLE);
		} else {
			tvDay.setVisibility(View.GONE);
		}

		/* Bearbeiten Button zum Ändern der Notiz 
		 * -------------------------------------- */
		ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.button_edit_note);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(listContext, NotesEditActivity.class);
		 		i.putExtra("index", listID);
		 		listContext.startActivity(i);
			}
		});


		/* Löschen Button zum Entfernen der Notiz
		 * -------------------------------------- */
		ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.button_delete_note);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//AlertDialog-Builder referenzieren
				AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
				//AlertDialog-Builder konfigurieren
				builder.setTitle(listContext.getString(R.string.msg_title_note_delete));         										
				builder.setCancelable(true);											
				//OK/Ja Button
				builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Notiz löschen
						try {
							db = new DB_DatabaseHelper();
							db.Delete(listContext.getString(R.string.table_notizen), listID);
							db.close();
							listCursor.requery();
							DayNotesListAdapter.this.notifyDataSetChanged();
						} catch (Exception e) {
						}
					}
				});
				//Abbrechen/nein Button
				builder.setNegativeButton(R.string.msg_no, new DialogInterface.OnClickListener() {		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				builder.show();
			}
		});
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.day_note_list_item, null);
	}

}
