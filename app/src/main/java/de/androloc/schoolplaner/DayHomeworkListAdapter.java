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
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class DayHomeworkListAdapter extends CursorAdapter {

	private Context listContext;
	private Cursor listCursor;
	private LayoutInflater inflater;
	private ClassCalendar cal;
	private int idxID, idxErl, idxAbgabe, idxFach, idxAufgabe, idxErledigt;
	private DB_DatabaseHelper db;
	
	/*Flag für die Anzeige des Tages bei Wochenansicht */
	private boolean showDay;
	public boolean isShowDay() {
		return showDay;
	}
	public void setShowDay(boolean showDay) {
		this.showDay = showDay;
	}

	public DayHomeworkListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxID = c.getColumnIndex(context.getString(R.string.field_id));
		idxErl = c.getColumnIndex(context.getString(R.string.field_erledigen_am));
		idxFach = c.getColumnIndex(context.getString(R.string.field_fach));
		idxAbgabe = c.getColumnIndex(context.getString(R.string.field_abgabe_am));
		idxAufgabe = c.getColumnIndex(context.getString(R.string.field_aufgaben));
		idxErledigt = c.getColumnIndex(context.getString(R.string.field_erledigt));
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
		
		/* Datum Erledigen und Abgeben */
		ClassCalendarDay dayDay = null;
		TextView tvErledigen = (TextView)view.findViewById(R.id.tv_date_erl);
		TextView tvAbgeben = (TextView)view.findViewById(R.id.tv_date_ab);
		ClassCalendarDay day = cal.GetDayByID(cursor.getLong(idxErl));
		if (ClassCalendar.getActiveHomeworkView() == 0) dayDay = day;
		tvErledigen.setText(day.getDatum_show());
		day = cal.GetDayByID(cursor.getLong(idxAbgabe));
		if (ClassCalendar.getActiveHomeworkView() == 1) dayDay = day;
		tvAbgeben.setText(day.getDatum_show());

		/* In der Wochenansicht den Tag anzeigen */
		TextView tvDay = (TextView)view.findViewById(R.id.tv_hw_day);
		if (showDay) {
			tvDay.setText(dayDay.getWochentag());
			tvDay.setVisibility(View.VISIBLE);
		} else {
			tvDay.setVisibility(View.GONE);
		}
		
		/*Aufgaben-Beschreibung */
		TextView tvAufgabe = (TextView)view.findViewById(R.id.tv_aufgabe);
		tvAufgabe.setText(cursor.getString(idxAufgabe));

		/* CheckBox erledigt ja/nein 
		 * -------------------------*/
		final CheckBox chkErledigt = (CheckBox)view.findViewById(R.id.check_erledigt);
		if (cursor.getInt(idxErledigt) == 1) {
			chkErledigt.setChecked(true);
			chkErledigt.setText(listContext.getString(R.string.tv_erledigt));
			chkErledigt.setTextColor(listContext.getResources().getColor(R.color.col_erledigt));
		} else {
			chkErledigt.setChecked(false);
			chkErledigt.setText(listContext.getString(R.string.tv_nicht_erledigt));
			chkErledigt.setTextColor(listContext.getResources().getColor(R.color.col_nicht_erledigt));
		}
		chkErledigt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				ClassHomework homework = new ClassHomework(listContext);
				if (homework.findHomeworkByID(listID)) {
					if (isChecked) {
						homework.setErledigt(1);
						chkErledigt.setText(listContext.getString(R.string.tv_erledigt));
						chkErledigt.setTextColor(listContext.getResources().getColor(R.color.col_erledigt));
					} else {
						homework.setErledigt(0);
						chkErledigt.setText(listContext.getString(R.string.tv_nicht_erledigt));
						chkErledigt.setTextColor(listContext.getResources().getColor(R.color.col_nicht_erledigt));
					}
					homework.updateHomework(listID);
				}
			}
		});

		/* Bearbeiten Button zum Ändern der Hausaufgabe 
		 * -------------------------------------------- */
		ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.button_edit_hw);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(listContext, HomeworkEditActivity.class);
		 		i.putExtra("index", listID);
		 		listContext.startActivity(i);
			}
		});


		/* Löschen Button zum Entfernen einer Hausaufgabe
		 * ---------------------------------------------- */
		ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.button_delete_hw);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//AlertDialog-Builder referenzieren
				AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
				//AlertDialog-Builder konfigurieren
				builder.setTitle(R.string.msg_title_hw_delete);         										
				builder.setCancelable(true);											
				//OK/Ja Button
				builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Aufgabe löschen
						try {
							db = new DB_DatabaseHelper();
							db.Delete(listContext.getString(R.string.table_hausaufgaben), listID);
							db.close();
							listCursor.requery();
							DayHomeworkListAdapter.this.notifyDataSetChanged();
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
		return inflater.inflate(R.layout.day_homework_list_item, null);
	}

}
