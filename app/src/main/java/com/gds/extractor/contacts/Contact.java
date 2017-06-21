package com.gds.extractor.contacts;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.gds.extractor.utils.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Francesco in 20/10/16.
 */
public class Contact implements Serializable, Comparable<Contact> {

    private long id;
    private String name;
    private Drawable photo;
    private byte[] blob;
    private Uri photoUri;

    private List<ContactItem> contactItems = new ArrayList<>();

    public Contact() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPhoto(Drawable photo) {
        this.photo = photo;
    }

    public Drawable getPhoto() {
        return photo;
    }

    public List<ContactItem> getContactItems() {
        return contactItems;
    }

    public void addContactItem(ContactItem contactItem) {
        contactItems.add(contactItem);
    }

    public void addContactItems(List<ContactItem> contactItems) {
        this.contactItems.addAll(contactItems);
    }

    public List<ContactItem> getPhones() {
        return search(ContactItem.Type.PHONE);
    }

    public List<ContactItem> getEmails() {
        return search(ContactItem.Type.EMAIL);
    }

    private List<ContactItem> search(ContactItem.Type type) {
        List<ContactItem> phones = new ArrayList<>();
        for (ContactItem item : contactItems) {
            if (type == item.getType()) {
                phones.add(item);
            }
        }

        return phones;
    }

    public boolean isTypePresent(ContactItem.Type type) {
        boolean ret = false;
        for (ContactItem item : contactItems) {
            if (type == item.getType()) {
                ret = true;
                break;
            }
        }

        return ret;
    }


    public boolean isPhonesPresent() {
        return isTypePresent(ContactItem.Type.PHONE);
    }

    public boolean isEmailsPresent() {
        return isTypePresent(ContactItem.Type.EMAIL);
    }


    @Override
    public int compareTo(Contact contact) {
        if (TextUtils.isEmptyNull(name) && TextUtils.isEmptyNull(contact.getName())) {
            return 0;
        }

        if (TextUtils.isEmptyNull(name) && !TextUtils.isEmptyNull(contact.getName())) {
            return 1;
        }

        if (!TextUtils.isEmptyNull(name) && TextUtils.isEmptyNull(contact.getName())) {
            return -1;
        }

        return name.compareTo(contact.getName());
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", photo=" + photo +
                ", contactItems=" + contactItems +
                '}';
    }

    public byte[] getBlob() {
        return blob;
    }

    public void setBlob(byte[] blob) {
        this.blob = blob;
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }
}
