package eventcalendar.app;
/**
 * This example app, has two branches which are based on the input message,
 * if the message contains Berlin, calls the Calendar,put the Device ID as
 * part of title and message input as a description and if it does not contain
 * Berlin, it sends an SMS to device ID as a Number and the message input would be
 * a text which will be sent
 * two sinks, two sources,and each path has its own AlertDialog
 *Via: http://sureshdotariya.blogspot.de/2013/03/how-to-add-events-to-native-calendar.html
 * EventCalendar-EventCalendar.v.1.apk
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

import java.util.Calendar;

public class EventCalendarActivity extends Activity {
    EditText textMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_calendar);

        textMessage = (EditText) findViewById(R.id.editTextMessage);

        Button button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                final String DId = telephonyManager.getDeviceId();
                final String message = textMessage.getText().toString();


                if (message.contains("Berlin")) {

                    //
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventCalendarActivity.this);

                    alertDialogBuilder.setTitle(EventCalendarActivity.this.getTitle() + " decision");
                    alertDialogBuilder.setMessage("Are you sure?");
                    // set positive button: Yes message
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {

                                    Calendar cal = Calendar.getInstance();
                                    Intent intent = new Intent(Intent.ACTION_EDIT);
                                    intent.setType("vnd.android.cursor.item/event");
                                    intent.putExtra("beginTime", cal.getTimeInMillis());
                                    intent.putExtra("allDay", true);
                                    intent.putExtra("rrule", "FREQ=YEARLY");
                                    intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                                    intent.putExtra("title", "Device Id "+DId);
                                    intent.putExtra("description", message);//in my phone calendar goes into Gäste
                                    intent.putExtra("eventLocation", "Bonn");
                                    Toast.makeText(getApplicationContext(), DId + "  !",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(intent);


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

                            EventCalendarActivity.this.finish();
                        }
                    });
                    alertDialogBuilder.show();
                    //

                } else {
                    //
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EventCalendarActivity.this);

                    alertDialogBuilder.setTitle(EventCalendarActivity.this.getTitle() + " decision");
                    alertDialogBuilder.setMessage("Are you sure?");
                    // set positive button: Yes message
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(DId, null, message, null, null);
                                    Toast.makeText(getApplicationContext(), "SMS sent to " + DId,
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

                            EventCalendarActivity.this.finish();
                        }
                    });
                    alertDialogBuilder.show();
                    //
                }
            }

        });

    }
}