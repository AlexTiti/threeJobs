/*
* The MIT License (MIT)

* Copyright (c) 2015 Michal Tajchert

* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package com.findtech.threePomelos.musicserver;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

public class Nammu {
    private static Context context;
    private static SharedPreferences sharedPreferences;
    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("pl.tajchert.runtimepermissionhelper", Context.MODE_PRIVATE);
        Nammu.context = context;
    }


    /**
     * Not that needed method but if we override others it is good to keep same.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(String permissionName) {
        if (context == null) {
            throw new RuntimeException("Before comparing permissions you need to call Nammu.initCatchException(context)");
        }
        return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName);
    }
}