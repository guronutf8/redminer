package info.guron.redminer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Guron on 20.09.13.
 */
public class LoginError_Dialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState){
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.LoginError_Dialog)
                .setPositiveButton(R.string.LoginError_Dialog_ok,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginError_Dialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

}
