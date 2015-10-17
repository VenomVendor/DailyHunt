package com.venomvendor.dailyhunt.util;

import android.os.Environment;
import android.text.Editable;
import android.widget.EditText;

import com.venomvendor.dailyhunt.core.DHApplication;

import java.io.File;

public class AppUtils {
    public static File getStorageLocation() {
        final String packageName = DHApplication.getContext().getPackageName();
        return new File(Environment.getExternalStorageDirectory(), "." + packageName + "/.imgCache");
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0 || getTrimmedText(str.toString()).length() == 0;
    }

    public static String getTrimmedText(EditText input) {
        return getTrimmedText(input.getText());
    }

    public static String getTrimmedText(Editable input) {
        return input.length() > 0 ? getTrimmedText(input.toString()) : null;
    }

    public static String getTrimmedText(String input) {
        return input != null ? input.trim() : null;
    }
}
