/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zuriquebolos.songsofclassicgames;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class MyDownloaderService extends DownloaderService {
	
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkuC/YAqY0KhwBBmGpshN53NbnhgYeQuuMNhRXBpIDtqQAWvZgMMTt1J3gQjlR5LGXrPmQQh+YIJAjL9CNcnwg1NIkmnFfGj8F1ljo/5F4zCVa6y4lD/eFuiKJLxFWWvBQXaSy5na9FJXdrWHyCpD1lFfSTXwCQf5cNZs+rZAkHzYCaF4JQxtSSRsk8x7RXw3j+oOa32V+xOUuP6qzBLvn8ozB+/jqOyDu2v6O3SpKBayp/pIHgHX2r6C11FME2nPqKX/qBj3UiZZyF3mNy+vgpKu3lwjsy8L4IH7+tqgQTX+xY8lux5SDkxQX5weLOq2gFszm7Nw3yX6HMo1n/iZcQIDAQAB";
    private static final byte[] SALT = new byte[] {1, 43, -12, -1, 54, 98, -100, -12, 43, 2, -8, -4, 9, 5, -106, -108, -33, 45, -1, 84};
    
    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }
    
    @Override
    public byte[] getSALT() {
        return SALT;
    }
    
    @Override
    public String getAlarmReceiverClassName() {
        return MyAlarmReceiver.class.getName();
    }
}