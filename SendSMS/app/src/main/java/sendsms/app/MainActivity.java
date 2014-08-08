package sendsms.app;
/**here the source code hast the same sinks and sources,
 * in one path it displays the alertDialog, and it sends the message later,
 * and in the other branch if the message contains Hi, the SMS will be sent
 * immediately. adopted via www.mkyong.com/android/how-to-send-sms-message-in-android/
 app-00.apk
 **/

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class MainActivity extends Activity {

    Button buttonSend;
    EditText textPhoneNo;
    EditText textSMS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        textSMS = (EditText) findViewById(R.id.editTextSMS);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String phoneNo = textPhoneNo.getText().toString();
                final String sms = textSMS.getText().toString();

                final SmsManager smsManager = SmsManager.getDefault();
                if (sms.contains("Hi")) {
                    smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "Message contains Hi! Sent!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    alertDialogBuilder.setTitle(MainActivity.this.getTitle() + " decision");
                    alertDialogBuilder.setMessage("Are you sure?");
                    // set positive button: Yes message
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                                    try {

                                        smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                                        Toast.makeText(getApplicationContext(), "SMS Sent!",
                                                Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                "SMS faild, please try again later!",
                                                Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                    }
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
            }
        });
    }
}