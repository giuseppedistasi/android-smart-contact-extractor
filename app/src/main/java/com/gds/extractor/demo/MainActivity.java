package com.gds.extractor.demo;

import android.Manifest;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.gds.extractor.R;
import com.gds.extractor.contacts.Contact;
import com.gds.extractor.contacts.ContactsExtractor;
import com.gds.extractor.contacts.ContactsFactory;
import com.gds.extractor.utils.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout v;
    private ListView list;
    private ArrayList<Contact> contactItems = new ArrayList<Contact>();
    private static final int REQUEST_READ_CONTACTS = 777;
    private static final int REQUEST_WRITE_CONTACTS = 888;
    private ContactsAdapter adapter;
    private ProgressBar loadingSpinner;


    @Override
    protected void onResume() {
        super.onResume();

/*        if (checkWriteContactsPermission()) {
            createDummyContacts();
        }*/

        if (checkReadContactsPermission()) {
            startReadinContacts();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        v = (RelativeLayout) findViewById(R.id.main_layout);
        list = (ListView) findViewById(R.id.contacts_list);

        loadingSpinner = (ProgressBar) findViewById(R.id.contacts_progress_bar);

        adapter = new ContactsAdapter(this, contactItems);
        list.setAdapter(adapter);

        setTitle(R.string.my_contacts);

    }

    private boolean checkWriteContactsPermission() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int writePermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, REQUEST_WRITE_CONTACTS);
                return false;
            }
        }

        return true;
    }

    private boolean checkReadContactsPermission() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int readPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            if (readPermission != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]
                        {Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            // Request for camera permission.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                Snackbar.make(v, "Read contacts permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                startReadinContacts();

            }
        }
        else if (requestCode == REQUEST_WRITE_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                Snackbar.make(v, "Write contacts permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                createDummyContacts();
            } else {
                // Permission request was denied.
                Snackbar.make(v, "Write contacts permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void createDummyContacts() {

        loadingSpinner.setVisibility(View.VISIBLE);

        try {
            new ContactsFactory()
                    .createDummyContactsAsync(this)
                    .observeOn(SchedulerProvider.ui())
                    .subscribeOn(SchedulerProvider.io())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            loadingSpinner.setVisibility(View.INVISIBLE);

                            if (checkReadContactsPermission()) {
                                startReadinContacts();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadingSpinner.setVisibility(View.INVISIBLE);

                            if (checkReadContactsPermission()) {
                                startReadinContacts();
                            }
                        }
                    });
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }


    private void startReadinContacts() {

        loadingSpinner.setVisibility(View.VISIBLE);

        ArrayList<Contact> allContacts = new ContactsExtractor(this).getAllContacts();

        new ContactsExtractor(this)
                .getContactsAsync()
                .observeOn(SchedulerProvider.ui())
                .subscribeOn(SchedulerProvider.io())
                .subscribeWith(new SingleObserver<List<Contact>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Contact> contacts) {
                        contactItems.clear();
                        contactItems.addAll(contacts);
                        adapter.notifyDataSetChanged();

                        loadingSpinner.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {

                        loadingSpinner.setVisibility(View.INVISIBLE);

                        Snackbar.make(v, "Cannot read contacts",
                                Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_contact_menu: {
                Intent i = new Intent(this, NewContactActivity.class);
                startActivity(i);
                return true;
            }
        }

        return true;
    }

}
