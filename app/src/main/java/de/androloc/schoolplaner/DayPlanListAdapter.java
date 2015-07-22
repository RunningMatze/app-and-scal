package de.androloc.schoolplaner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.database.Cursor;

public class DayPlanListAdapter extends CursorAdapter {
	
	/*
	private Context listContext;
	private Cursor listCursor;
	*/
	private LayoutInflater inflater;
	// private int idxID
	private int idxStunde, idxBeginn, idxEnde, idxFach, idxRaum, idxBemerkung;
	private Integer fachID;

	public DayPlanListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		//idxID = c.getColumnIndex(context.getString(R.string.field_id));
		idxStunde = c.getColumnIndex(context.getString(R.string.field_stunde));
		idxBeginn = c.getColumnIndex(context.getString(R.string.field_beginn));
		idxEnde = c.getColumnIndex(context.getString(R.string.field_ende));
		idxFach = c.getColumnIndex(context.getString(R.string.field_fach));
		idxRaum = c.getColumnIndex(context.getString(R.string.field_raum));
		idxBemerkung = c.getColumnIndex(context.getString(R.string.field_bemerkung));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		/*
		listContext = context;
		listCursor = cursor;
		final long listID = cursor.getLong(idxID);
		*/
		
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
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.day_plan_list_item, null);
	}

}
