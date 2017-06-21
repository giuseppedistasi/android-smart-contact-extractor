package com.gds.extractor.demo;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.gds.extractor.R;
import com.gds.extractor.contacts.Contact;
import com.gds.extractor.contacts.ContactItem;
import com.gds.extractor.contacts.ContactsExtractor;

public class NewContactActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etMobile;
    private EditText etHomePhone;
    private EditText etHomeEmail;
    private EditText etWorkEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        setTitle(R.string.create_contact);

        etName = (EditText) findViewById(R.id.et_name);

        // Getting reference to Mobile EditText
        etMobile = (EditText) findViewById(R.id.et_mobile_phone);

        // Getting reference to HomePhone EditText
        etHomePhone = (EditText) findViewById(R.id.et_home_phone);

        // Getting reference to HomeEmail EditText
        etHomeEmail = (EditText) findViewById(R.id.et_home_email);

        // Getting reference to WorkEmail EditText
        etWorkEmail = (EditText) findViewById(R.id.et_work_email);

        // Getting reference to "Add Contact" button
        Button btnAdd = (Button) findViewById(R.id.btn_add);

        // Creating a button click listener for the "Add Contact" button
        View.OnClickListener addClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                Contact newContact = new Contact();

                String name = etName.getText().toString();
                String workEmail = etWorkEmail.getText().toString();
                String homeEmail = etHomeEmail.getText().toString();
                String mobile = etMobile.getText().toString();
                String homePhone = etHomePhone.getText().toString();

                if (name.isEmpty()) {
                    Snackbar.make(v, getString(R.string.insert_name),
                            Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                if(mobile.isEmpty() && homePhone.isEmpty()){

                    Snackbar.make(v, getString(R.string.insert_phone),
                            Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }

                newContact.setName(etName.getText().toString());

                if (!workEmail.isEmpty()) {
                    newContact.addContactItem(new ContactItem(ContactItem.Type.EMAIL, "WORK", etWorkEmail.getText().toString()));
                }
                if (!homeEmail.isEmpty()) {
                    newContact.addContactItem(new ContactItem(ContactItem.Type.EMAIL, "HOME", etHomeEmail.getText().toString()));
                }
                if (!mobile.isEmpty()) {
                    newContact.addContactItem(new ContactItem(ContactItem.Type.PHONE, "WORK", etMobile.getText().toString()));
                }
                if (!homePhone.isEmpty()) {
                    newContact.addContactItem(new ContactItem(ContactItem.Type.PHONE, "HOME", etHomePhone.getText().toString()));
                }

                new ContactsExtractor(NewContactActivity.this).insertNewContact(newContact);

                Snackbar.make(v, getString(R.string.contact_created),
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        };

        // Setting click listener for the "Add Contact" button
        btnAdd.setOnClickListener(addClickListener);

    }
}
