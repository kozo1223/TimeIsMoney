package dev.si.timeismoney.background;

/**
 * Created by Yagi-mac on 2014/07/07.
 */
import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.os.Bundle;

public class CallDialogActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("設定した時間を過ぎています").setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}