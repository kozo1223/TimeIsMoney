package dev.si.timeismoney.main;

import dev.si.timeismoney.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RegisterActivity extends Activity {
private View layout; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activityregister);
	}

	public void onSetbuttonClick(View v) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.popupdialog,
				(ViewGroup) findViewById(R.id.layout_root));
		AlertDialog.Builder D = new AlertDialog.Builder(this);
		D.setTitle("アプリの登録");
		D.setView(layout);
		D.setPositiveButton("OK", null);
		D.show();
	}

	public void onBackButtonClick(View v) {
		finish();
	}

}
