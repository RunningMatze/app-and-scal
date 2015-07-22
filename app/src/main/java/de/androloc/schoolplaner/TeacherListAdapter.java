package de.androloc.schoolplaner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class TeacherListAdapter extends CursorAdapter {

	private Context listContext;
	private Cursor listCursor;
	private LayoutInflater inflater;
	private int idxID, idxAnrede, idxVorname, idxName;
	private DB_DatabaseHelper db;
	private String anzeigeName;

	public TeacherListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxID = c.getColumnIndex(context.getString(R.string.field_id));
		idxAnrede = c.getColumnIndex(context.getString(R.string.field_anrede));
		idxVorname = c.getColumnIndex(context.getString(R.string.field_vorname));
		idxName = c.getColumnIndex(context.getString(R.string.field_nachname));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		listContext = context;
		listCursor = cursor;
		TextView teacher_Name = (TextView)view.findViewById(R.id.tv_teacher_list_name);
		final long listID = cursor.getLong(idxID);
		anzeigeName = "";
		String anrede = cursor.getString(idxAnrede);
		String vorname = cursor.getString(idxVorname);
		String nachname = cursor.getString(idxName);
		anzeigeName = anrede;
		if (vorname.length() > 0) {
			anzeigeName = anzeigeName + " " + vorname;
		}
		if (nachname.length() > 0) {
			anzeigeName = anzeigeName + " " + nachname;
		}
		teacher_Name.setText(anzeigeName);
	
		/* Info-Button zum Anzeigen eines Lehrerdatensatzes
		 * Daten werden über den Dialog teacher_info.xml angezeigt
		 * ------------------------------------------------------- */
		ImageButton buttonInfo = (ImageButton) view.findViewById(R.id.button_info_teacher);
		buttonInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(listContext, R.style.dialogtheme);
				dialog.setContentView(R.layout.teacher_info);
				dialog.setCancelable(true);
				ClassTeacher actTeacher = new ClassTeacher(listContext);
				actTeacher.FindTeacherByID(listID);
				dialog.setTitle(actTeacher.BuildTeacherName());
	    		((TextView) dialog.findViewById(R.id.teacher_info_kuerzel)).setText(actTeacher.getKuerzel());
				((TextView) dialog.findViewById(R.id.teacher_info_telefon)).setText(actTeacher.getTelefon());
				((TextView) dialog.findViewById(R.id.teacher_info_email)).setText(actTeacher.getEmail());
				((TextView) dialog.findViewById(R.id.teacher_info_bemerkungen)).setText(actTeacher.getBemerkung());
				Button buttonClose = (Button) dialog.findViewById(R.id.button_info_close);
				buttonClose.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				dialog.show();
			}
		});

		
		/* Editieren Button zum Ändern eines Lehrerdatensatzes
		 * --------------------------------------------------- */
		ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.button_edit_teacher);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent i = new Intent(listContext, TeacherEditActivity.class);
		 		i.putExtra("index", listID);
		 		listContext.startActivity(i);
			}
		});
		
		/* Löschen Button zum Entfernen eines Lehrerdatensatzes
		 * ---------------------------------------------------- */
		ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.button_delete_teacher);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//AlertDialog-Builder referenzieren
				AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
				//AlertDialog-Builder konfigurieren
				builder.setTitle(R.string.msg_title_teacher_delete);         										
				builder.setCancelable(true);											
				//OK/Ja Button
				builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Artikel löschen
						try {
							db = new DB_DatabaseHelper();
							db.Delete(listContext.getString(R.string.table_lehrer), listID);
							db.close();
							listCursor.requery();
							TeacherListAdapter.this.notifyDataSetChanged();
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
		return inflater.inflate(R.layout.teacher_list_item, null);
	}
}
