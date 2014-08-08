package com.example.myapplication4.app;
/**SendEmail
 * This example, has has two branch, in one branch send SMS to 110,
 * if the message contains Hi,
 * if not: the other branch will send an email after displaying an AlertDialog
 * via: http://www.mkyong.com/android/how-to-send-email-in-android/
 *app-01.apk ~ SendEmail (new name!)
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

    Button buttonSend;
    EditText textTo;
    EditText textSubject;
    EditText textMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textTo = (EditText) findViewById(R.id.editTextTo);
        textSubject = (EditText) findViewById(R.id.editTextSubject);
        textMessage = (EditText) findViewById(R.id.editTextMessage);

        buttonSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                final String to = textTo.getText().toString();
                final String subject = textSubject.getText().toString();
                final String message = textMessage.getText().toString();

                if (message.contains("Hi")) {
                    //to here should be integer.
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("110", null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS contains Hi, sent to 110!",
                            Toast.LENGTH_LONG).show();
                } else {
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