package com.gds.extractor.utils;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;

/**
 * Created by Francesco on 10/06/2016.
 */
@SuppressWarnings("StringBufferReplaceableByString")
public class Logger implements Serializable {

    private static final int TAG_LIMIT = 23;

    private String nameManager;
    private final String tag;

    public static Logger getIntance(String tag) {
        return new Logger(tag);
    }

    public static Logger getIntance(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }

    public static Logger getIntance(String tag, String nameManager) {
        return new Logger(tag, nameManager);
    }

    private Logger(String tag) {
        this(tag, "");
    }

    private Logger(String tag, String nameManager) {
        this.nameManager = nameManager;
        this.tag = getTag(tag);
    }

    private String getTag(String tag) {
        if (tag.length() > TAG_LIMIT) {
            tag = tag.substring(0, TAG_LIMIT);
        }

        return tag;
    }

    public void debug(Object... objects) {
        Log.d(tag, getMessage(objects));
    }

    public void debug(String msg) {
        Log.d(tag, getMessage(msg));
    }

    public void debug(String msg, Object... objects) {
        Log.d(tag, getMessage(msg, objects));
    }

    public void debug(String msg, Type type, Object... objects) {
        String message = getMessage(msg, objects);
        switch (type) {
            case FILE: {
                writeToFile(message);
                break;
            }
            case CONSOLE: {
                Log.d(tag, message);
                break;
            }
            case ALL: {
                Log.d(tag, message);
                writeToFile(message);
                break;
            }
        }
    }

    public void debugFile(String msg, Object... objects) {
        writeToFile(getMessage(msg, objects));
    }

    public void warn(String msg) {
        Log.w(tag, getMessage(msg));
    }

    public void warn(String msg, Throwable e) {
        Log.w(tag, getMessage(msg), e);
    }

    public void warn(Throwable e) {
        Log.w(tag, getMessage(e.getMessage()), e);
    }

    public void warnNoMessage(Throwable e) {
        Log.w(tag, e);
    }

    public void error(String msgError) {
        Log.e(tag, getMessage(msgError));
    }

    public void error(Object... objects) {
        Log.e(tag, getMessage(objects));
    }

    public void error(Throwable e) {
        Log.e(tag, getMessage(e.getMessage()), e);
    }

    public void error(String msg, Throwable e) {
        Log.e(tag, getMessage(msg), e);
    }

    public void error(String msg, Object... objects) {
        Log.e(tag, getMessage(msg, objects));
    }

    public void mock(String message) {
        error(new MockException(message));
    }

    public void mock() {
        error(new MockException());
    }

    public String getNameManager() {
        return nameManager != null ? nameManager : "";
    }

    private String getMessage(Object... objects) {
        return getNameManager() + " - " + getString(objects);
    }

    private String getString(Object... objects) {
        StringBuilder builder = new StringBuilder();
        if (objects != null) {
            for (Object o : objects) {
                builder.append(o).append(" ");
            }

            builder.replace(builder.lastIndexOf(" "), builder.length(), "");

        }
        return builder.toString();
    }

    private String getPrefixMessage() {
        return getNameManager() + " - ";
    }

    private String getMessage(String msg) {
        return new StringBuilder(getPrefixMessage()).append((msg != null ? msg : "")).toString();
    }

    private static final String MARKER = "{}";

    private String getMessage(String msg, Object... objects) {
        StringBuilder message;
        if (msg.contains(MARKER)) {
            message = new StringBuilder(getPrefixMessage()).append(msg);
            for (Object object : objects) {
                int indexOf = message.indexOf(MARKER);
                if (indexOf == -1) {
                    break;
                }
                message.replace(indexOf, indexOf + MARKER.length(), getString(object));
            }

        } else {
            message = new StringBuilder(getMessage(msg)).append(getString(objects));
        }

        return message.toString();

//        return new StringBuilder(getMessage(msg)).append(getString(objects)).toString();
    }

    private void writeToFile(final String value) {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                try {

                    final String localPath = "";
                    final String filename = "";

                    BufferedWriter bos = new BufferedWriter(new FileWriter(
                            localPath + "/" + filename));
                    bos.write(value);
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class MockException extends RuntimeException {


        public MockException() {
            this("Mock exception");
        }

        public MockException(String detailMessage) {
            super(detailMessage);
        }

        public MockException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public MockException(Throwable throwable) {
            super(throwable);
        }
    }

    public enum Type {
        CONSOLE, FILE, ALL
    }
}
