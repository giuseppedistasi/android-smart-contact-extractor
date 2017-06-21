package com.gds.extractor.demo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gds.extractor.R;
import com.gds.extractor.contacts.Contact;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 24/05/2017.
 */

public class ContactsAdapter extends ArrayAdapter<Contact> {

    private static class ViewHolder {
        TextView nameTv;
        TextView numberTv;
        ImageView photoIV;
    }

    private int lastPosition = -1;

    public ContactsAdapter(Context context, ArrayList<Contact> items) {
        super(context, 0, items);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Contact item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.list_item_name);
            viewHolder.numberTv = (TextView) convertView.findViewById(R.id.list_item_number);
            viewHolder.photoIV = (ImageView) convertView.findViewById(R.id.list_item_picture);
              // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);

        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameTv.setText(item.getName());

        if(item.getPhones().size() >0) {
            viewHolder.numberTv.setText(item.getPhones().get(0).getValue());
        }
        else{
            viewHolder.numberTv.setVisibility(View.GONE);
        }

        Uri photoUri = item.getPhotoUri();
        if(photoUri != null) {
            viewHolder.photoIV.setImageURI(item.getPhotoUri());
        }
        else{
            viewHolder.photoIV.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_placeholder));
        }

        return convertView;
    }

}