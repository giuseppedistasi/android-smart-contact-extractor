package com.gds.extractor.contacts;


import com.gds.extractor.utils.PhoneNumberNormalizeFormat;

/**
 * Created by Francesco in 21/10/16.
 */
public class ContactItem {
    private String title;
    private String value;
    private Type type;

    public ContactItem(Type type) {
        this.type = type;
    }

    public ContactItem(Type type, String title, String value) {
        this(type);
        this.title = title;
        this.value = value;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ContactItem{" +
                "title='" + title + '\'' +
                ", value='" + value + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object object) {

       if (object != null && object instanceof ContactItem) {
            final PhoneNumberNormalizeFormat phoneNumberNormalizeFormat = new PhoneNumberNormalizeFormat();

            String a = getValue().replaceAll("\\s+", "");
            String normalizedA = phoneNumberNormalizeFormat.format(a);

            ContactItem thing = (ContactItem) object;
            String b = thing.getValue().replaceAll("\\s+", "");
            String normalizedB = phoneNumberNormalizeFormat.format(b);

            return normalizedA.equals(normalizedB);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public enum Type {
        PHONE, EMAIL
    }
}
