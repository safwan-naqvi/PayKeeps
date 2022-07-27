package com.example.paykeeperfyp.Admin.Management;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.allyants.notifyme.NotifyMe;
import com.example.paykeeperfyp.Admin.Management.Database.DatabaseHelper;

import com.example.paykeeperfyp.BuildConfig;

import com.example.paykeeperfyp.Prevalent.Prevalent;
import com.example.paykeeperfyp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

public class transaction_chat extends AppCompatActivity {

    LinearLayout share_layout;
    ImageView sidebar_icon;
    Intent intent;
    String Friend_id, user_id;
    ArrayList<String> transaction_amount, transaction_time, transaction_remarks, transaction_sender_id, transaction_id;
    Cursor cursor;
    RecyclerView transaction_chat_recycle;
    ChatAdapter chatAdapter;
    MaterialCardView transaction_debit, transaction_credit;
    TextView transaction_date, save_debit, transaction_name, transaction_balance, save_credit, friend_name, error_msg_transaction_amount, error_msg_transaction_date, error_msg_transaction_name;
    //New Added
    Switch transaction_notify;
    MaterialCardView transaction_return_date_layout;
    TextView transaction_return_date, transaction_reminder_date;

    String transaction_date_text, transaction_name_text, transaction_balance_text, Friend_name, transaction_return_date_text;
    DatePickerDialog.OnDateSetListener setListener, setListenerTwo, setListenerthree;
    LocalDate start, end;
    public int notificationID;
    String username, amount, userphone;
    AlarmManager alarmManager;
    long alarmstartTime;
    Calendar startTime;
    String number = "";

    private DatabaseReference SaveDebitRef;
    private DatabaseReference SaveCreditRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_chat);
        //Setup Up whole scenario for date selection
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        createNotificationChannel();

        transaction_chat_recycle = findViewById(R.id.transaction_chat_recycle);
        transaction_debit = findViewById(R.id.transaction_debit);
        transaction_credit = findViewById(R.id.transaction_credit);
        friend_name = findViewById(R.id.friend_name);
        share_layout = findViewById(R.id.share_layout);

        //Problem is here
        String DB_name = Prevalent.currentOnlineUser.getPhoneNo();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(DB_name, Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("Id", "");
        intent = getIntent();
        Friend_id = intent.getStringExtra("Friend_id");

        DatabaseHelper myDB = new DatabaseHelper(this);
        Cursor cursor = myDB.get_user_details(Friend_id);
        if (cursor == null) {
            Toast.makeText(getApplicationContext(), "No Data available", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                Friend_name = cursor.getString(1);
                number = cursor.getString(0);
            }

            friend_name.setText(Friend_name + number);

            transaction_sender_id = new ArrayList<>();
            transaction_amount = new ArrayList<>();
            transaction_remarks = new ArrayList<>();
            transaction_time = new ArrayList<>();
            transaction_id = new ArrayList<>();
        }

        transaction_debit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(transaction_chat.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_debit_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(true);

                transaction_balance = bottomSheetDialog.findViewById(R.id.transaction_amount);
                transaction_name = bottomSheetDialog.findViewById(R.id.transaction_name);
                transaction_date = bottomSheetDialog.findViewById(R.id.transaction_date);
                transaction_notify = bottomSheetDialog.findViewById(R.id.switchNotifier);
                transaction_return_date_layout = bottomSheetDialog.findViewById(R.id.transaction_return_date_layout);
                transaction_return_date = bottomSheetDialog.findViewById(R.id.transaction_return_date);
                transaction_reminder_date = bottomSheetDialog.findViewById(R.id.transaction_reminder_date);

                //Initially Both are hidden
                transaction_reminder_date.setVisibility(View.GONE);
                transaction_return_date.setVisibility(View.GONE);
                transaction_return_date_layout.setVisibility(View.GONE);

                error_msg_transaction_amount = bottomSheetDialog.findViewById(R.id.error_msg_transaction_amount);
                error_msg_transaction_date = bottomSheetDialog.findViewById(R.id.error_msg_transaction_date);
                error_msg_transaction_name = bottomSheetDialog.findViewById(R.id.error_msg_transaction_name);
                save_debit = bottomSheetDialog.findViewById(R.id.save_debit);

                checkNotifierEnable();

                transaction_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(transaction_chat.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();
                    }
                });

                setListener = new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String month_name = new DateFormatSymbols().getMonths()[month];
                        String temp_date = String.valueOf(dayOfMonth) + " - " + month_name.substring(0, 3) + " - " + String.valueOf(year);
                        transaction_date.setText(temp_date);
                    }
                };

                transaction_return_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(transaction_chat.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListenerTwo, year, month, day);
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();
                    }
                });

                setListenerTwo = new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String month_name = new DateFormatSymbols().getMonths()[month];
                        String temp_date = String.valueOf(dayOfMonth) + " - " + month_name.substring(0, 3) + " - " + String.valueOf(year);
                        transaction_return_date.setText(temp_date);

                    }
                };

                save_debit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        transaction_balance_text = transaction_balance.getText().toString();
                        transaction_name_text = transaction_name.getText().toString();
                        transaction_date_text = transaction_date.getText().toString();
                        transaction_return_date_text = transaction_return_date.getText().toString();

                        int count1 = 1, count2 = 1, count3 = 1, count4 = 1;

                        error_msg_transaction_name.setVisibility(View.GONE);
                        error_msg_transaction_amount.setVisibility(View.GONE);
                        error_msg_transaction_date.setVisibility(View.GONE);

                        if (transaction_name_text.isEmpty()) {
                            count1 = 0;
                            error_msg_transaction_name.setVisibility(View.VISIBLE);
                            error_msg_transaction_name.setText("This is required");
                        }
                        if (transaction_balance_text.isEmpty()) {
                            count2 = 0;
                            error_msg_transaction_amount.setVisibility(View.VISIBLE);
                            error_msg_transaction_amount.setText("This is required");
                        }
                        if (transaction_date_text.isEmpty()) {
                            count3 = 0;
                            count4 = 0;
                            error_msg_transaction_date.setVisibility(View.VISIBLE);
                            error_msg_transaction_date.setText("This is required");
                        }


                        if (count1 == 1 && count2 == 1 && count3 == 1 && count4 == 1) {
                            DatabaseHelper myDB = new DatabaseHelper(getApplicationContext());
                            String tost_message = null;
                            if (myDB.storeNewDebitTransaction(user_id, Friend_id, transaction_balance_text, transaction_name_text, transaction_date_text)) {
                                Cursor cursor = myDB.get_user_details(Friend_id);
                                while (cursor.moveToNext()) {
                                    username = cursor.getString(1);
                                    amount = transaction_balance_text;
                                    userphone = cursor.getString(0);
                                }
                                createNotificationChannel();
                                tost_message = "Transaction Added";

                            } else {
                                tost_message = "Something went wrong";
                            }

                            //Sending Message to user
                            String myMessage = "Debit is added of " + transaction_balance_text + " on reason of " + transaction_name_text + " at " + transaction_date_text + " and returned date is " + transaction_return_date_text;
                            //region Firstly Sending Message to user
                            SmsManager mySms = SmsManager.getDefault();
                            String numx = "+" + number;
                            mySms.sendTextMessage(numx, null, myMessage, null, null);
                            //Saving Debit to database
                            String business;
                            if (Paper.book().exist("UserBusiness")) {
                                business = Paper.book().read("UserBusiness");
                            } else {
                                business = Prevalent.UserChosenBusiness;
                            }
                            SaveDebitRef = FirebaseDatabase.getInstance().getReference("Company").child(business).child("User").child(numx).child("Debit");
                            String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
                            String currentDate = new SimpleDateFormat("EEE,d MMM , yyyy", Locale.getDefault()).format(new Date());
                            String rid = currentDate + " " + currentTime;
                            Map<String, Object> debitUpdates = new HashMap<>();
                            debitUpdates.put("debit_id", rid);
                            debitUpdates.put("debit_name", transaction_name_text);
                            debitUpdates.put("debit_balance", transaction_balance_text);
                            debitUpdates.put("debit_date", transaction_date_text);
                            debitUpdates.put("debit_return", transaction_return_date_text);

                            //reference.updateChildren(hopperUpdates);
                            SaveDebitRef.child(rid).setValue(debitUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(transaction_chat.this, "Debit Added", Toast.LENGTH_SHORT).show();
                                }
                            });


                            transaction_sender_id = new ArrayList<>();
                            transaction_amount = new ArrayList<>();
                            transaction_remarks = new ArrayList<>();
                            transaction_time = new ArrayList<>();
                            transaction_id = new ArrayList<>();
                            Fetch_Transaction(user_id, Friend_id);
                            chatAdapter = new ChatAdapter(getApplicationContext(), user_id, transaction_sender_id, transaction_amount, transaction_remarks, transaction_time, transaction_id);
                            transaction_chat_recycle.setAdapter(chatAdapter);
                            transaction_chat_recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            transaction_chat_recycle.smoothScrollToPosition(transaction_amount.size());
                            Toast.makeText(transaction_chat.this, tost_message, Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.hide();


                            startTime = Calendar.getInstance();
                            startTime.set(Calendar.HOUR_OF_DAY, LocalDateTime.now().getHour());
                            startTime.set(Calendar.MINUTE, LocalDateTime.now().getMinute() + 1);
                            startTime.set(Calendar.SECOND, 0);
                            startTime.set(Calendar.DAY_OF_MONTH, day);
                            startTime.set(Calendar.MONTH, month);
                            startTime.set(Calendar.YEAR, year);
                            alarmstartTime = startTime.getTimeInMillis();
                            Log.i("work", String.valueOf(alarmstartTime) + "    " + LocalDateTime.now().getMinute());

                            setRemainder();

                        }
                    }
                });
                bottomSheetDialog.show();
            }
        });
        transaction_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(transaction_chat.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_credit_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(true);

                transaction_balance = bottomSheetDialog.findViewById(R.id.transaction_amount);
                transaction_name = bottomSheetDialog.findViewById(R.id.transaction_name);
                transaction_date = bottomSheetDialog.findViewById(R.id.transaction_date2);


                error_msg_transaction_amount = bottomSheetDialog.findViewById(R.id.error_msg_transaction_amount);
                error_msg_transaction_date = bottomSheetDialog.findViewById(R.id.error_msg_transaction_date);
                error_msg_transaction_name = bottomSheetDialog.findViewById(R.id.error_msg_transaction_name);
                save_credit = bottomSheetDialog.findViewById(R.id.save_credit);


                transaction_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(transaction_chat.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListenerthree, year, month, day);
                        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        datePickerDialog.show();
                    }
                });
                setListenerthree = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String month_name = new DateFormatSymbols().getMonths()[month];
                        String temp_date = String.valueOf(dayOfMonth) + " - " + month_name.substring(0, 3) + " - " + String.valueOf(year);
                        transaction_date.setText(temp_date);
                    }
                };

                save_credit.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        transaction_balance_text = transaction_balance.getText().toString();
                        transaction_name_text = transaction_name.getText().toString();
                        transaction_date_text = transaction_date.getText().toString();

                        int count1 = 1, count2 = 1, count3 = 1;

                        error_msg_transaction_name.setText("");
                        error_msg_transaction_amount.setText("");
                        error_msg_transaction_date.setText("");

                        if (transaction_name_text.isEmpty()) {
                            count1 = 0;
                            error_msg_transaction_name.setText("This is required");
                        }
                        if (transaction_balance_text.isEmpty()) {
                            count2 = 0;
                            error_msg_transaction_amount.setText("This is required");
                        }
                        if (transaction_date_text.isEmpty()) {
                            count3 = 0;
                            error_msg_transaction_date.setText("This is required");
                        }


                        if (count1 == 1 && count2 == 1 && count3 == 1) {
                            DatabaseHelper myDB = new DatabaseHelper(getApplicationContext());
                            String tost_message = null;
                            if (myDB.storeNewCreditTransaction(user_id, Friend_id, transaction_balance_text, transaction_name_text, transaction_date_text)) {
                                tost_message = "Transaction Added";


                            } else {
                                tost_message = "Something went wrong";
                            }

                            String business2;
                            if (Paper.book().exist("UserBusiness")) {
                                business2 = Paper.book().read("UserBusiness");
                            } else {
                                business2 = Prevalent.UserChosenBusiness;
                            }
                            String num = "+" + number;
                            SaveCreditRef = FirebaseDatabase.getInstance().getReference("Company").child(business2).child("User").child(num).child("Credit");
                            String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
                            String currentDate = new SimpleDateFormat("EEE,d MMM , yyyy", Locale.getDefault()).format(new Date());
                            String rid = currentDate + " " + currentTime;

                            Map<String, Object> creditUpdates = new HashMap<>();
                            creditUpdates.put("credit_id", rid);
                            creditUpdates.put("credit_name", transaction_name.getText().toString());
                            creditUpdates.put("credit_balance", transaction_balance.getText().toString());
                            creditUpdates.put("credit_date", transaction_date.getText().toString());

                            //reference.updateChildren(hopperUpdates);
                            SaveCreditRef.child(rid).setValue(creditUpdates);

                            String myMessage = "Credit is added of " + transaction_balance.getText() + " on reason of " + transaction_name.getText() + " at " + transaction_date.getText();
                            //region Firstly Sending Message to user
                            SmsManager mySms = SmsManager.getDefault();

                            mySms.sendTextMessage(num, null, myMessage, null, null);


                            Log.i("testData", num);
                            transaction_sender_id = new ArrayList<>();
                            transaction_amount = new ArrayList<>();
                            transaction_remarks = new ArrayList<>();
                            transaction_time = new ArrayList<>();
                            transaction_id = new ArrayList<>();
                            Fetch_Transaction(user_id, Friend_id);
                            chatAdapter = new ChatAdapter(getApplicationContext(), user_id, transaction_sender_id, transaction_amount, transaction_remarks, transaction_time, transaction_id);
                            transaction_chat_recycle.setAdapter(chatAdapter);
                            transaction_chat_recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            transaction_chat_recycle.smoothScrollToPosition(transaction_amount.size());
                            Toast.makeText(transaction_chat.this, tost_message, Toast.LENGTH_SHORT).show();
                            bottomSheetDialog.hide();
                        }
                    }
                });
                bottomSheetDialog.show();
            }
        });


        Fetch_Transaction(user_id, Friend_id);
        chatAdapter = new ChatAdapter(getApplicationContext(), user_id, transaction_sender_id, transaction_amount, transaction_remarks, transaction_time, transaction_id);
        transaction_chat_recycle.setAdapter(chatAdapter);
        transaction_chat_recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        transaction_chat_recycle.smoothScrollToPosition(transaction_amount.size());

    }

    private void createNotificationChannel() {
        //Generating New ID's

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PayKeeperReminderChannel";
            String Description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("payKeeper", name, importance);
            channel.setDescription(Description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void checkNotifierEnable() {

        transaction_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (transaction_notify.isChecked()) {
                    transaction_reminder_date.setVisibility(View.VISIBLE);
                    transaction_return_date.setVisibility(View.VISIBLE);
                    transaction_return_date_layout.setVisibility(View.VISIBLE);
                } else if (!transaction_notify.isChecked()) {
                    transaction_reminder_date.setVisibility(View.GONE);
                    transaction_return_date.setVisibility(View.GONE);
                    transaction_return_date_layout.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setRemainder() {
        NotifyMe notifyMe = new NotifyMe.Builder(getApplicationContext())
                .title("PayKeeper: Transaction Reminder")
                .content(username + " (" + userphone + ")" + " Have to Pay Pending Amount of " + amount)
                .led_color(255, 255, 255, 255)
                .time(startTime)
                .addAction(new Intent(), "Open")
                .key("test")
                .large_icon(R.mipmap.ic_launcher_round)
                .build();


    }


    public void onBackPressed(View view) {
        super.onBackPressed();
    }

    public void Fetch_Transaction(String userid, String friendid) {
        Date oneWayTripDate = null;
        DatabaseHelper myDB = new DatabaseHelper(this);
        cursor = myDB.user_friend_transaction(userid, friendid);
        while (cursor.moveToNext()) {
            transaction_sender_id.add(cursor.getString(0));
            transaction_amount.add(cursor.getString(1));
            transaction_remarks.add(cursor.getString(2));
            String date = cursor.getString(3);
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy hh:mm a");
            try {
                oneWayTripDate = input.parse(date);  // parse input
            } catch (ParseException e) {
                e.printStackTrace();
            }
            transaction_time.add(output.format(oneWayTripDate));
            transaction_id.add(cursor.getString(4));
        }
    }


    public void share_transaction(View view) {
        final DatabaseHelper myDB = new DatabaseHelper(this);
        //If User Want to delete that transaction
        AlertDialog.Builder builder = new AlertDialog.Builder(transaction_chat.this);
        builder.setMessage("What you want to do with this Transaction?")
                .setCancelable(true)
                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doShareFunction(view);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Delete that transaction
                        String transaction_id;
                        LinearLayout card = (LinearLayout) view;
                        transaction_id = card.getTag().toString();

                        Boolean cursor = myDB.deleteNote(transaction_id);
                        if (cursor) {
                            Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                            recreate();
                        } else {
                            Toast.makeText(getApplicationContext(), "Delete Failure", Toast.LENGTH_SHORT).show();
                        }

                        //End of open transaction
                    }
                }).show();


    }

    private void doShareFunction(View view) {
        //If User Press Want to Open Transaction
        String transaction_id;
        String transaction_sender_id = null;
        String transaction_receiver_id = null;
        String transaction_amount_text = null;
        String transaction_remarks_text = null;
        String transaction_date_text = null;
        String sender_id;
        String customer_phone_number_alert_text = null;
        final String[] user_name = new String[1];
        final String[] user_business_name = new String[1];
        Bitmap customer_image_alert_text = null;
        final ImageView close_alert, customer_image_alert, share_icon;
        TextView transaction_amount, transaction_remarks, transaction_time, customer_phone_number_alert, transactionamountsymbol;
        final MaterialCardView alert_dialog;
        final LinearLayout share_layout;

        LinearLayout card = (LinearLayout) view;
        transaction_id = card.getTag().toString();
        final DatabaseHelper myDB = new DatabaseHelper(this);
        Cursor cursor = myDB.get_transaction_details(transaction_id);

        while (cursor.moveToNext()) {
            transaction_sender_id = cursor.getString(1);
            transaction_receiver_id = cursor.getString(2);
            transaction_amount_text = cursor.getString(3);
            transaction_remarks_text = cursor.getString(5);
            transaction_date_text = cursor.getString(7);
        }

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_transaction_alert);

        alert_dialog = dialog.findViewById(R.id.alert_dialog);
        close_alert = dialog.findViewById(R.id.close_alert);
        customer_image_alert = dialog.findViewById(R.id.customer_image);
        customer_phone_number_alert = dialog.findViewById(R.id.customer_contact_number);
        transactionamountsymbol = dialog.findViewById(R.id.transactionamountsymbol);
        transaction_amount = dialog.findViewById(R.id.transaction_amount);
        transaction_remarks = dialog.findViewById(R.id.transaction_remarks);
        transaction_time = dialog.findViewById(R.id.transaction_time);
        share_icon = dialog.findViewById(R.id.share_icon);
        share_layout = dialog.findViewById(R.id.share_layout);

        if (transaction_sender_id.compareTo(user_id) == 0) {
            sender_id = transaction_receiver_id;
            transaction_amount.setText("- " + transaction_amount_text);
            transaction_amount.setTextColor(getApplicationContext().getResources().getColor(R.color.warning));
            transactionamountsymbol.setText("Rs");
            close_alert.setImageResource(R.drawable.alertbox_cross_icon_debit);
            share_icon.setImageResource(R.drawable.credit_share_icon);
        } else {
            sender_id = transaction_sender_id;
            transaction_amount.setText("+ " + transaction_amount_text);
            transaction_amount.setTextColor(getApplicationContext().getResources().getColor(R.color.sucess));
            transactionamountsymbol.setText("Rs");
            close_alert.setImageResource(R.drawable.alertbox_cross_icon_credit);
            share_icon.setImageResource(R.drawable.credit_share_icon);
        }

        Cursor cursor1 = myDB.get_user_details(sender_id);

        while (cursor1.moveToNext()) {
            customer_phone_number_alert_text = "+92-" + cursor1.getString(0).substring(2);
            customer_image_alert_text = BitmapFactory.decodeByteArray(cursor1.getBlob(4), 0, cursor1.getBlob(4).length);
        }

        transaction_remarks.setText(transaction_remarks_text);
        transaction_time.setText(transaction_date_text);
        customer_phone_number_alert.setText(customer_phone_number_alert_text);
        customer_image_alert.setImageBitmap(customer_image_alert_text);

        close_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                share_layout.setVisibility(View.INVISIBLE);
                close_alert.setVisibility(View.INVISIBLE);
                Bitmap bitmap = Bitmap.createBitmap(alert_dialog.getWidth(), alert_dialog.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                alert_dialog.draw(canvas);
                Cursor cursor2 = myDB.get_user_details(user_id);
                while (cursor2.moveToNext()) {
                    user_name[0] = cursor2.getString(1);
                    user_business_name[0] = cursor2.getString(2);
                }

                try {
                    File file = new File(getApplicationContext().getExternalCacheDir(), File.separator + user_name[0] + "_" + user_business_name[0] + ".jpg");
                    Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_SHORT).show();
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/jpg");
                    startActivity(Intent.createChooser(intent, "Share image via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


}