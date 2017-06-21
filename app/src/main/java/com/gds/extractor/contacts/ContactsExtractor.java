package com.gds.extractor.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import com.gds.extractor.utils.Logger;
import com.gds.extractor.utils.PhoneNumberNormalizeFormat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Single;

import static android.provider.ContactsContract.CommonDataKinds.Email;
import static android.provider.ContactsContract.CommonDataKinds.Phone;

/**
 * Created by Francesco in 20/10/16.
 */
@SuppressWarnings("StringBufferReplaceableByString")
public class ContactsExtractor {

    private final Logger logger = Logger.getIntance(getClass());

    private final ContentResolver contentResolver;
    private final Context context;

    private static final String SELECTION = new StringBuilder(ContactsContract.Contacts.DISPLAY_NAME).append(" is not null").toString();

    private static final PhoneNumberNormalizeFormat PHONE_NUMBER_NORMALIZE_FORMAT = new PhoneNumberNormalizeFormat();

    static {
        PHONE_NUMBER_NORMALIZE_FORMAT.loadPrefixes("+39");
    }

    public static String TAG = "GET_CONTACTS";

    private long startTimestamp;

    public ContactsExtractor(Context context) {
        this.context = context;
        contentResolver = context.getContentResolver();
    }

    public Single<ArrayList<Contact>> getContactsAsync() {
        return Single.just(getAllContacts());
    }

    public void insertNewContact(Contact c){
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert display name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, c.getName())
                .build());

        if(c.getEmails().size()>0) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.ADDRESS, c.getEmails().get(0).getValue())
                    .withValue(Email.TYPE, Email.TYPE_WORK)
                    .build());
        }

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        if(c.getPhones().size() > 0) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, c.getPhones().get(0).getValue())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Photo.DATA15,c.getBlob())
                .build());

        try{
            // Executing all the insert operations as a single database transaction
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            Log.d(TAG, "Contact " +  c.getName() + " added");

        }catch (RemoteException e) {
            e.printStackTrace();
        }catch (OperationApplicationException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Contact> getAllContacts(){

        /**log*/
        startTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        Long t0 = System.currentTimeMillis();

        ContentResolver cr = context.getContentResolver();

        Map<Long, Contact> contacts = new LinkedHashMap<Long, Contact>();

        String[] projection = {ContactsContract.Data.CONTACT_ID, ContactsContract.Data.DISPLAY_NAME, ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1, ContactsContract.Data.DATA2, ContactsContract.Data.DATA3, ContactsContract.Data.PHOTO_ID};
        String selection = ContactsContract.Data.MIMETYPE + " IN ('" + Phone.CONTENT_ITEM_TYPE + "', '" + Email.CONTENT_ITEM_TYPE + "')";
        Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, projection, selection, null, ContactsContract.Data.DISPLAY_NAME +" ASC");

        while (cur != null && cur.moveToNext()) {

            long id = cur.getLong(0);
            String name = cur.getString(1);
            String mime = cur.getString(2); // email / phone / company
            String data = cur.getString(3); // the actual info, e.g. +1-212-555-1234
            int type = cur.getInt(4); // a numeric value representing type: e.g. home / office / personal
            String label = cur.getString(5); // a custom label in case type is "TYPE_CUSTOM"
            long photoId = cur.getLong(6);

            // add info to existing list if this contact-id was already found, or create a new list in case it's new
            Contact infos;
            if (contacts.containsKey(id)) {
                infos = contacts.get(id);
            } else {
                infos = new Contact();
                infos.setId(id);
                infos.setName(name);
                Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photoId);
                infos.setPhotoUri(photoUri);
                contacts.put(id, infos);

                Log.d(TAG, "aggiunto" + infos.getName());

            }

            String kind = "unknown";
            String labelStr = "";

            switch (mime) {
                case Phone.CONTENT_ITEM_TYPE:
                    kind = "phone";
                    labelStr = "phone";//Phone.getTypeLabel(context.getResources(), type, label);
                    infos.addContactItem(new ContactItem(ContactItem.Type.PHONE,kind,data));
                    break;
                case Email.CONTENT_ITEM_TYPE:
                    kind = "email";
                    labelStr = "email";//Email.getTypeLabel(getResources(), type, label);
                    infos.addContactItem(new ContactItem(ContactItem.Type.EMAIL,kind,data));
                    break;
            }

        }

        ArrayList<Contact> list = new ArrayList<Contact>();

        for(Contact c1 : contacts.values()){
            list.add(c1);
        }

        long currentTimestamp = new Timestamp(System.currentTimeMillis()).getTime();
        long delay = (currentTimestamp - startTimestamp);
        logger.debug(TAG, " call after " + delay + " milliseconds");

        return list;
    }

}
