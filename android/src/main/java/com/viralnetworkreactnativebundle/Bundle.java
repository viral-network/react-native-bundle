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

import java.util.Arrays;

public class Bundle {

    public static final int TRANSACTION_LENGTH = 8019;
    private static final int BUNDLE_FRAGMENT_LENGTH = Curl_729_27.HASH_LENGTH / 3;
    private static final int TYPE_OFFSET = 0;
    private static final int HEAD_FLAG_OFFSET = 1;
    private static final int TAIL_FLAG_OFFSET = 2;
    private static final int TRANSACTION_NONCE_OFFSET = 7938;
    private static final int TRANSACTION_NONCE_END = 8019;
    private static final int BUNDLE_ESSENCE_OFFSET = 6561;
    private static final int BUNDLE_ESSENCE_LENGTH = 729;
    private static final int BUNDLE_ESSENCE_END = BUNDLE_ESSENCE_OFFSET + BUNDLE_ESSENCE_LENGTH;
    private static final int BUNDLE_NONCE_OFFSET = 7209;
    private static final int BUNDLE_NONCE_LENGTH = 81;

    private static int hammingWeight(final byte[] hash, final int offset) {
        int w = 0;
        for (int i = 0; i < BUNDLE_FRAGMENT_LENGTH; i++) {
            w += hash[offset + i];
        }
        return w;
    }

    private static byte[] essence(byte[][] transactions) {
        final byte[] essenceTrits = new byte[transactions.length * BUNDLE_ESSENCE_LENGTH];

        for (int i = 0; i < transactions.length; i++) {
            final byte[] transactionEssence = Arrays.copyOfRange(transactions[i], BUNDLE_ESSENCE_OFFSET, BUNDLE_ESSENCE_END);
            for (int j = 0; j < BUNDLE_ESSENCE_LENGTH; j++) {
                essenceTrits[i * BUNDLE_ESSENCE_LENGTH + j] = transactionEssence[j];
            }
        }

        return essenceTrits;
    }

    public static byte[] updateTransactionNonce(byte[] trits, int type, int headFlag, int tailFlag, int security) {
        final byte[] hash = new byte[Curl_729_27.HASH_LENGTH];

        do {
            Curl_729_27.getDigest(trits, 0, TRANSACTION_LENGTH, hash, 0);

            boolean weightValidityFlag = true;
            for (int i = 0; i < security; i++) {
                if (hammingWeight(hash, i * BUNDLE_FRAGMENT_LENGTH) != 0) {
                    weightValidityFlag = false;
                    break;
                }
            }

            if (
                weightValidityFlag &&
                hash[TYPE_OFFSET] == type &&
                hash[HEAD_FLAG_OFFSET] == headFlag &&
                hash[TAIL_FLAG_OFFSET] == tailFlag
            ) {
                break;
            }

            for (int i = TRANSACTION_NONCE_OFFSET; i < TRANSACTION_NONCE_END; i++) {
                if (++trits[i] > 1) {
                    trits[i] = -1;
                } else {
                    break;
                }
            }
        } while (true);

        return hash;
    }

  public static byte[] updateBundleNonce(byte[][] transactions, int security) {
      final byte[] essenceTrits = essence(transactions);
      final byte[] bundle = new byte[Curl_729_27.HASH_LENGTH];
      final Curl_729_27 curl = new Curl_729_27(essenceTrits.length);

      do {
          curl.absorb(essenceTrits, 0, essenceTrits.length);
          curl.squeeze(bundle, 0, bundle.length);

          boolean weightValidityFlag = true;
          for (int i = 0; i < security; i++) {
              if (hammingWeight(bundle, i * BUNDLE_FRAGMENT_LENGTH) != 0) {
                  weightValidityFlag = false;
                  break;
              }
          }

          if (weightValidityFlag) {
              break;
          }

          for (int i = 0; i < BUNDLE_NONCE_LENGTH; i++) {
              if (++essenceTrits[BUNDLE_NONCE_OFFSET - BUNDLE_ESSENCE_OFFSET + i] > 1) {
                  essenceTrits[BUNDLE_NONCE_OFFSET - BUNDLE_ESSENCE_OFFSET + i] = -1;
              } else {
                  break;
              }
          }

          final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
          Converter.copy(essenceTrits.length, lengthTrits, 0, lengthTrits.length);
          curl.reset(lengthTrits);
      } while (true);

      final byte[] bundleNonce = Arrays.copyOfRange(
          essenceTrits,
          BUNDLE_NONCE_OFFSET - BUNDLE_ESSENCE_OFFSET,
          BUNDLE_NONCE_OFFSET - BUNDLE_ESSENCE_OFFSET + BUNDLE_NONCE_LENGTH
      );

      for (int i = 0; i < BUNDLE_NONCE_LENGTH; i++) {
          transactions[0][BUNDLE_NONCE_OFFSET + i] = bundleNonce[i];
      }

      return bundle;
    }
}

