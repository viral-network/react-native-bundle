/*
Permission is hereby granted, perpetual, worldwide, non-exclusive, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:



1. The Software cannot be used in any form or in any substantial portions for development, maintenance and for any other purposes, in the military sphere and in relation to military products, including, but not limited to:

a. any kind of armored force vehicles, missile weapons, warships, artillery weapons, air military vehicles (including military aircrafts, combat helicopters, military drones aircrafts), air defense systems, rifle armaments, small arms, firearms and side arms, melee weapons, chemical weapons, weapons of mass destruction;

b. any special software for development technical documentation for military purposes;

c. any special equipment for tests of prototypes of any subjects with military purpose of use;

d. any means of protection for conduction of acts of a military nature;

e. any software or hardware for determining strategies, reconnaissance, troop positioning, conducting military actions, conducting special operations;

f. any dual-use products with possibility to use the product in military purposes;

g. any other products, software or services connected to military activities;

h. any auxiliary means related to abovementioned spheres and products.



2. The Software cannot be used as described herein in any connection to the military activities. A person, a company, or any other entity, which wants to use the Software, shall take all reasonable actions to make sure that the purpose of use of the Software cannot be possibly connected to military purposes.



3. The Software cannot be used by a person, a company, or any other entity, activities of which are connected to military sphere in any means. If a person, a company, or any other entity, during the period of time for the usage of Software, would engage in activities, connected to military purposes, such person, company, or any other entity shall immediately stop the usage of Software and any its modifications or alterations.



4. Abovementioned restrictions should apply to all modification, alteration, merge, and to other actions, related to the Software, regardless of how the Software was changed due to the abovementioned actions.



The above copyright notice and this permission notice shall be included in all copies or substantial portions, modifications and alterations of the Software.



THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.viralnetworkreactnativebundle;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = ReactNativeBundleModule.NAME)
public class ReactNativeBundleModule extends ReactContextBaseJavaModule {
    public static final String NAME = "ReactNativeBundle";


    public ReactNativeBundleModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    @ReactMethod
    public void updateTransactionNonce(final ReadableArray trits, final int type, final int headFlag, final int tailFlag, final int security, Promise promise) {
        final byte[] tritsCopy = new byte[Bundle.TRANSACTION_LENGTH];
        for (int i = 0; i < Bundle.TRANSACTION_LENGTH; i++) {
          tritsCopy[i] = (byte) trits.getInt(i);
        }

        final byte[] hash = Bundle.updateTransactionNonce(tritsCopy, type, headFlag, tailFlag, security);

        final WritableArray hashCopy = Arguments.createArray();
        for (int i = 0; i < Curl_729_27.HASH_LENGTH; i++) {
            hashCopy.pushInt(hash[i]);
        }
        final WritableArray tritsCopy2 = Arguments.createArray();
        for (int i = 0; i < Bundle.TRANSACTION_LENGTH; i++) {
            tritsCopy2.pushInt(tritsCopy[i]);
        }

        WritableMap map = Arguments.createMap();
        map.putArray("hash", hashCopy);
        map.putArray("trits", tritsCopy2);
        promise.resolve(map);
    }

    @ReactMethod
    public void updateBundleNonce(final ReadableArray transactions, final int security, Promise promise) {
        final byte[][] transactionsCopy = new byte[transactions.size()][Bundle.TRANSACTION_LENGTH];

        for (int i = 0; i < transactionsCopy.length; i++) {
            final ReadableArray buffer = transactions.getArray(i);
            for (int j = 0; j < Bundle.TRANSACTION_LENGTH; j++) {
                transactionsCopy[i][j] = (byte) buffer.getInt(j);
            }
        }

        final byte[] bundle = Bundle.updateBundleNonce(transactionsCopy, security);

        final WritableArray bundleCopy = Arguments.createArray();
        for (int i = 0; i < Curl_729_27.HASH_LENGTH; i++) {
            bundleCopy.pushInt(bundle[i]);
        }
        final WritableArray transactionsCopy2 = Arguments.createArray();
        for (int i = 0; i < transactionsCopy.length; i++) {
            final WritableArray buffer = Arguments.createArray();
            for (int j = 0; j < Bundle.TRANSACTION_LENGTH; j++) {
                buffer.pushInt(transactionsCopy[i][j]);
            }
            transactionsCopy2.pushArray(buffer);
        }

        WritableMap map = Arguments.createMap();
        map.putArray("bundle", bundleCopy);
        map.putArray("transactions", transactionsCopy2);
        promise.resolve(map);
    }
}
