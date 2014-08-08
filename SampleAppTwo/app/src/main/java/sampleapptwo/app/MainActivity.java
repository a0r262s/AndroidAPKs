package sampleapptwo.app;
/**
 * Here one AD. two sinks two sources, just one sink to just one source, via one AD-yes button.
 * SMS has the device ID as message, and Email has the input message from user as a message body of email application
 * 
 * */
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

    Button buttonSend;

    EditText textMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.buttonSend);

        textMessage = (EditText) findViewById(R.id.editTextMessage);

        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                final String DId = telephonyManager.getDeviceId();

                final String to = "asad60@yahoo.com";
                final String subject = "New Email From MyApp";
                final String message = textMessage.getText().toString();


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                alertDialogBuilder.setTitle(MainActivity.this.getTitle() + " decision");
                alertDialogBuilder.setMessage("Are you sure?");
                // set positive button: Yes message
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                try {
                                    Intent email = new Intent(Intent.ACTION_SEND);
                                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                                    //email.putExtra(Intent.EXTRA_CC, new String[]{ to});
                                    //email.putExtra(Intent.EXTRA_BCC, new String[]{to});
                                    email.putExtra(Intent.EXTRA_SUBJECT, subject);
                                    email.putExtra(Intent.EXTRA_TEXT, message);

                                    //need this to prompts email client only
                                    email.setType("message/rfc822");

                                    startActivity(Intent.createChooser(email, "Choose an Email client :"));

                                    Toast.makeText(getApplicationContext(), "Email is sending!",
                                            Toast.LENGTH_LONG).show();
                                }//try
                                catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),
                                            "SMS faild, please try again later!",
                                            Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }//catch

                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage("911", null, DId, null, null);
                                Toast.makeText(getApplicationContext(), "Device ID via SMS sent.. ",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                );
                // set negative button: No message
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // cancel the alert box and put a Toast to the user
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "You chose a negative answer",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                // set neutral button: Exit the app message
                alertDialogBuilder.setNeutralButton("Exit the app", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // exit the app and go to the HOME

                        MainActivity.this.finish();
                    }
                });
                alertDialogBuilder.show();
            }

        });
    }
}