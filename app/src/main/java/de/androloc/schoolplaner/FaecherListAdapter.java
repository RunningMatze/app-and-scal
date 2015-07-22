package de.androloc.schoolplaner;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

public class FaecherListAdapter extends CursorAdapter {

	private Context listContext;
	private Cursor listCursor;
	private DB_DatabaseHelper db;
	private LayoutInflater inflater;
	private int idxID, idxFach, idxTeacher;
	

	public FaecherListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		inflater = LayoutInflater.from(context);
		idxID = c.getColumnIndex(context.getString(R.string.field_id));
		idxFach = c.getColumnIndex(context.getString(R.string.field_fach));
		idxTeacher = c.getColumnIndex(context.getString(R.string.field_teacher));
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		listContext = context;
		listCursor = cursor;
		final long listID = cursor.getLong(idxID);
		String fach = cursor.getString(idxFach);
		TextView tvFach = (TextView)view.findViewById(R.id.tv_faecher_list_fach);
		tvFach.setText(fach);
		long teacherID = cursor.getLong(idxTeacher);
		SetTeacherName(teacherID, view, context);

		/* Info-Button zum Anzeigen eines Fach-Datensatzes
		 * Daten werden über den Dialog faecher_info.xml angezeigt
		 * ------------------------------------------------------- */
		ImageButton buttonInfo = (ImageButton) view.findViewById(R.id.button_info_fach);
		buttonInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(listContext, R.style.dialogtheme);
				dialog.setContentView(R.layout.faecher_info);
				dialog.setCancelable(true);
				ClassFaecher actFach = new ClassFaecher(listContext);
				actFach.findFachByID(listID);
				dialog.setTitle(actFach.getFach());
	    		((TextView) dialog.findViewById(R.id.fach_info_kurzcode)).setText(actFach.getKurzcode());
				((TextView) dialog.findViewById(R.id.fach_info_bemerkungen)).setText(actFach.getBemerkung());
				ClassTeacher actTeacher = new ClassTeacher(listContext);
				if (actFach.getLehrer() == 0) {
					((TextView) dialog.findViewById(R.id.fach_info_teacher)).setText(R.string.tv_faecher_noteacher);
				} else {
					actTeacher.FindTeacherByID(actFach.getLehrer());
					((TextView) dialog.findViewById(R.id.fach_info_teacher)).setText(actTeacher.BuildTeacherName());
				}
				if (actTeacher.getAnrede().equals("Frau")) {
					((ImageView) dialog.findViewById(R.id.fach_info_mw)).setImageResource(R.drawable.teacher_woman);
				}
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
		
		/* Editieren Button zum Ändern eines fach-Datensatzes
		 * -------------------------------------------------- */
		ImageButton buttonEdit = (ImageButton) view.findViewById(R.id.button_edit_fach);
		buttonEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		 		Intent i = new Intent(listContext, FaecherEditActivity.class);
		 		i.putExtra("index", listID);
		 		listContext.startActivity(i);
			}
		});

		/* Löschen Button zum Entfernen eines Fach-Datensatzes
		 * --------------------------------------------------- */
		ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.button_delete_fach);
		buttonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//AlertDialog-Builder referenzieren
				AlertDialog.Builder builder = new AlertDialog.Builder(listContext);
				//AlertDialog-Builder konfigurieren
				builder.setTitle(R.string.msg_title_fach_delete);         										
				builder.setCancelable(true);											
				//OK/Ja Button
				builder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() { 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// Artikel löschen
						try {
							db = new DB_DatabaseHelper();
							db.Delete(listContext.getString(R.string.table_faecher), listID);
							db.close();
							listCursor.requery();
							FaecherListAdapter.this.notifyDataSetChanged();
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
		return inflater.inflate(R.layout.faecher_list_item, null);
	}

	//Lehrernamen anhand der Lehrer-ID anzeigen
	//-----------------------------------------
	private void SetTeacherName(long teacherID, View view, Context context) {
		TextView tvTeacher = (TextView)view.findViewById(R.id.tv_faecher_list_teacher);
		if (teacherID == 0) {
			tvTeacher.setText(R.string.tv_faecher_noteacher);
		} else {
			ClassTeacher actTeacher = new ClassTeacher(context);
			if (actTeacher.FindTeacherByID(teacherID) == true) {
				tvTeacher.setText(actTeacher.BuildTeacherName());
			} else {
				tvTeacher.setText(R.string.tv_faecher_noteacher);
			}
		}
	}
}
