package de.androloc.schoolplaner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

public class PlanListAdapter extends CursorAdapter {

	private Context listContext;
	private Cursor listCursor;
	private LayoutInflater inflater;
	private int idxID, idxStunde, idxBeginn, idxEnde, idxFach, idxRaum, idxBemerkung;
	private Integer fachID;

	public PlanListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxID = c.getColumnIndex(context.getString(R.string.field_id));
		idxStunde = c.getColumnIndex(context.getString(R.string.field_stunde));
		idxBeginn = c.getColumnIndex(context.getString(R.string.field_beginn));
		idxEnde = c.getColumnIndex(context.getString(R.string.field_ende));
		idxFach = c.getColumnIndex(context.getString(R.string.field_fach));
		idxRaum = c.getColumnIndex(context.getString(R.string.field_raum));
		idxBemerkung = c.getColumnIndex(context.getString(R.string.field_bemerkung));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		listContext = context;
		listCursor = cursor;
		final long listID = cursor.getLong(idxID);
		
		/* Layouts für Freistunde bzw. Unterrichtsstunde */
		RelativeLayout layoutFrei = (RelativeLayout)view.findViewById(R.id.layout_plan_noedu);
		RelativeLayout layoutUnterricht = (RelativeLayout)view.findViewById(R.id.layout_plan_hour);
		
		/* Fach, wenn 0 dann kein Unterricht 
		 * ---------------------------------*/
		fachID = cursor.getInt(idxFach);
		if (fachID == 0) {
			layoutUnterricht.setVisibility(View.GONE);
			layoutFrei.setVisibility(View.VISIBLE);
		} else {
			layoutUnterricht.setVisibility(View.VISIBLE);
			layoutFrei.setVisibility(View.GONE);
			/* Uhrzeit - Beginn der Schulstunde */
			TextView tvBeginn = (TextView)view.findViewById(R.id.tv_plan_stunde_beginn);
			tvBeginn.setText(cursor.getString(idxBeginn));
			/* Uhrzeit - Ende der Schulstunde */
			TextView tvEnde = (TextView)view.findViewById(R.id.tv_plan_stunde_ende);
			tvEnde.setText(cursor.getString(idxEnde));
			/* Fach */
			ClassFaecher fach = new ClassFaecher(context);
			TextView tvFach = (TextView)view.findViewById(R.id.tv_plan_list_fach);
			if (fach.findFachByID(fachID) == true) {
				tvFach.setText(fach.getFach());
			} else {
				tvFach.setText(context.getString(R.string.tv_undefined));
			}
			/* Raum */
			TextView tvRaum = (TextView)view.findViewById(R.id.tv_plan_list_raum);
			tvRaum.setText(cursor.getString(idxRaum));
			/* Bemerkungen */
			TextView tvBemerkung = (TextView)view.findViewById(R.id.tv_plan_list_bemerkung);
			tvBemerkung.setText(cursor.getString(idxBemerkung));
		} 
		
		/* Anzeige des Images für die lfd. Stunde */
		int stundeNr = cursor.getInt(idxStunde);
		switch (stundeNr) {
		case 1:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_1);
			break;
		case 2:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_2);
			break;
		case 3:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_3);
			break;
		case 4:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_4);
			break;
		case 5:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_5);
			break;
		case 6:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_6);
			break;
		case 7:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_7);
			break;
		case 8:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_8);
			break;
		case 9:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_9);
			break;
		case 10:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_10);
			break;
		case 11:
			((ImageView)view.findViewById(R.id.image_plan_stunde)).setImageResource(R.drawable.stunde_11);
			break;
		}

		/* Löschen Button zum Zurücksetzen einer Unterrichtsstunde
		 * ------------------------------------------------------- */
		ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.button_delete_hour);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//AlertDialog-Builder referenzieren
				AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
				//AlertDialog-Builder konfigurieren
				builder.setTitle(R.string.msg_title_hour_delete);         										
				builder.setCancelable(true);											
				//OK/Ja Button
				builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Artikel löschen
						try {
							ClassPlan tmpPlan = new ClassPlan(listContext);
							tmpPlan.findHourByID(listID);
							tmpPlan.setFach(0);
							tmpPlan.setBeginn("");
							tmpPlan.setEnde("");
							tmpPlan.setRaum("");
							tmpPlan.setBemerkung("");
							tmpPlan.updatePlan(listID);
							tmpPlan=null;
							listCursor.requery();
							PlanListAdapter.this.notifyDataSetChanged();
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

		/* Bearbeiten-Button zum Ändern einer Unterrichtsstunde
		 * --------------------------------------------------- */
		ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.button_settings_hour);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Activity zur Erfassung eines neuen Lehrers starten
				Intent i = new Intent(listContext, PlanEditActivity.class);
		 		i.putExtra("index", listID);
				listContext.startActivity(i);
			}
		});
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.plan_list_item, null);
	}

	/*
	//Lehrernamen anhand der Lehrer-ID anzeigen
	//-----------------------------------------
	private void SetTeacherName(long teacherID, View view, Context context) {
		TextView tvTeacher = (TextView)view.findViewById(R.id.tv_faecher_list_teacher);
		if (teacherID == 0) {
			tvTeacher.setText(R.string.tv_faecher_noteacher);
		} else {
			ClassTeacher actTeacher = new ClassTeacher(context);
			actTeacher.FindTeacherByID(teacherID);
			tvTeacher.setText(actTeacher.BuildTeacherName());
		}
	}
	*/

}
