# android-smart-contact-extractor

- A smart, fast and easy to use contact extractor for android 

- Extracts contacts with a single instruction in a very fast and optimized way

- Works sync or async (RxJava)

# Usage

Edit Manifest adding this line

    <uses-permission android:name="android.permission.READ_CONTACTS" />
    
    #Add also this line only if you want to use the contact creator factory included in project
    <uses-permission android:name="android.permission.WRITE_CONTACTS" />
  
  
Edit Build.gradle adding the following dependencies

    compile 'com.android.support:appcompat-v7:25.3.1'
    compile 'com.android.support:design:25.3.1'
    compile 'io.reactivex.rxjava2:rxjava:2.0.0'
    compile 'io.reactivex.rxjava2:rxandroid:2.0.0'
    
Add all content of package com.gds.extractor in your project

For Android 6.+ for access contacts you need to ask runtime permission 

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
            else {
                // Permission request was denied.
                Snackbar.make(v, "Read contacts permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
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


Invoke Extractor handling asynchronously response with rxJava

    private void startReadinContacts() {

        loadingSpinner.setVisibility(View.VISIBLE);

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


If you prefer you can also invoke Extractor synchronously (warning:extractor synchronously will freeze ui thread until task will be completed)

           ArrayList<Contact> allContacts = new ContactsExtractor(this).getAllContacts();
           
           
         
