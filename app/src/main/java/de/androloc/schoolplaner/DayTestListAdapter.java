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

public class DayTestListAdapter extends CursorAdapter {

	private Context listContext;
	private Cursor listCursor;
	private LayoutInflater inflater;
	private ClassCalendar cal;
	private int idxID, idxArt, idxTermin, idxFach, idxBeschreibung, idxNote, idxDurchschnitt;
	private DB_DatabaseHelper db;
	
	/*Flag für die Anzeige des Tages bei Wochenansicht */
	private boolean showDay;
	public boolean isShowDay() {
		return showDay;
	}
	public void setShowDay(boolean showDay) {
		this.showDay = showDay;
	}

	public DayTestListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxID = c.getColumnIndex(context.getString(R.string.field_id));
		idxFach = c.getColumnIndex(context.getString(R.string.field_fach));
		idxArt = c.getColumnIndex(context.getString(R.string.field_art));
		idxBeschreibung = c.getColumnIndex(context.getString(R.string.field_aufgaben));
		idxTermin = c.getColumnIndex(context.getString(R.string.field_kalendertag));
		idxNote = c.getColumnIndex(context.getString(R.string.field_note));
		idxDurchschnitt = c.getColumnIndex(context.getString(R.string.field_durchschnitt));
		cal = new ClassCalendar(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		listContext = context;
		listCursor = cursor;
		final long listID = cursor.getLong(idxID);
		
		/* Fach-Kurzcode */
		long fachID = cursor.getLong(idxFach);
		TextView tvFach = (TextView)view.findViewById(R.id.tv_hw_fach);
		ClassFaecher fach = new ClassFaecher(context);
		if (fach.findFachByID(fachID)) {
			tvFach.setText(fach.getKurzcode());
		} else {
			tvFach.setText(context.getString(R.string.tv_undefined));
		}
		
		/* Test-Art */
		TextView tvArt = (TextView)view.findViewById(R.id.tv_test_art);
		final String testArt = cursor.getString(idxArt);
		tvArt.setText(testArt);

		/* Datum Termin */
		TextView tvTermin = (TextView)view.findViewById(R.id.tv_date_termin);
		ClassCalendarDay day = cal.GetDayByID(cursor.getLong(idxTermin));
		tvTermin.setText(day.getDatum_show());

		/* Note */
		TextView tvNote = (TextView)view.findViewById(R.id.tv_test_note);
		tvNote.setText(cursor.getString(idxNote));

		/* Durchschnitt */
		TextView tvDurchschnitt = (TextView)view.findViewById(R.id.tv_test_durchschnitt);
		tvDurchschnitt.setText(cursor.getString(idxDurchschnitt));

		/*Aufgaben-Beschreibung */
		TextView tvBeschreibung = (TextView)view.findViewById(R.id.tv_test_beschreibung);
		tvBeschreibung.setText(cursor.getString(idxBeschreibung));

		/* In der Wochenansicht den Tag anzeigen */
		TextView tvDay = (TextView)view.findViewById(R.id.tv_test_day);
		if (showDay) {
			tvDay.setText(day.getWochentag());
			tvDay.setVisibility(View.VISIBLE);
		} else {
			tvDay.setVisibility(View.GONE);
		}

		/* Bearbeiten Button zum Ändern der Hausaufgabe 
		 * -------------------------------------------- */
		ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.button_edit_test);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(listContext, TestsEditActivity.class);
		 		i.putExtra("index", listID);
		 		listContext.startActivity(i);
			}
		});


		/* Löschen Button zum Entfernen einer Hausaufgabe
		 * ---------------------------------------------- */
		ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.button_delete_test);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//AlertDialog-Builder referenzieren
				AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
				//AlertDialog-Builder konfigurieren
				builder.setTitle(testArt + " " + listContext.getString(R.string.msg_title_delete));         										
				builder.setCancelable(true);											
				//OK/Ja Button
				builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Aufgabe löschen
						try {
							db = new DB_DatabaseHelper();
							db.Delete(listContext.getString(R.string.table_tests), listID);
							db.close();
							listCursor.requery();
							DayTestListAdapter.this.notifyDataSetChanged();
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
		return inflater.inflate(R.layout.day_test_list_item, null);
	}

}
