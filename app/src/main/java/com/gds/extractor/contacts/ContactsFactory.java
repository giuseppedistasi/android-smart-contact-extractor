package com.gds.extractor.contacts;

import android.content.Context;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import io.reactivex.Completable;
import io.reactivex.functions.Action;

/**
 * Created by giuseppedistasi on 30/03/17.
 */

public class ContactsFactory {

    private static byte[] image;
    private static final String TAG = "CONTACTS FACTORY";

    public Completable createDummyContactsAsync(final Context context) throws RemoteException, OperationApplicationException {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                createDummyContacts(context);
            }
        });
    }

    /**
     * Called when the activity is first created.
     */
    public void createDummyContacts(Context context) throws RemoteException, OperationApplicationException {

        try {
            InputStream bitmap = context.getAssets().open("picture.jpg");
            Bitmap bit = BitmapFactory.decodeStream(bitmap);
            image = toByteArray(bit);

        } catch (IOException e1) {
            e1.printStackTrace();
        }


        for (int i = 0; i < 300; i++) {
            String s = "a" + i;
            String phoneNumber = "4561237890" + i;

            Contact c = new Contact();
            c.setName(s);
            c.setBlob(image);
            ContactItem contactItem = new ContactItem(ContactItem.Type.PHONE, "", phoneNumber);
            c.addContactItem(contactItem);

            new ContactsExtractor(context).insertNewContact(c);

        }

    }

    public byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }
}
