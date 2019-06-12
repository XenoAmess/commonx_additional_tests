/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 7121314
 * @summary AbstractCollection.toArrayPrimitive(T[]) doesn't return the given array
 *           in concurrent modification.
 * @author Ulf Zibis, David Holmes
 */

package jdk.java.util.AbstractCollection;

import com.xenoamess.commons.primitive.collections.AbstractDoubleCollection;
import com.xenoamess.commons.primitive.iterators.DoubleIterator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoubleToArrayTest {

    static class DoubleTestCollection implements AbstractDoubleCollection {
        private final double[] elements;
        private int[] sizes;
        private int nextSize;

        public DoubleTestCollection(double[] elements) {
            this.elements = elements;
            setSizeSequence(new int[]{elements.length});
        }

        /*
         * Sets the values that size() will return on each use. The next
         * call to size will return sizes[0], then sizes[1] etc. This allows us
         * to emulate a concurrent change to the contents of the collection
         * without having to perform concurrent changes. If sizes[n+1] contains
         * a larger value, the collection will appear to have shrunk when
         * iterated; if a smaller value then the collection will appear to have
         * grown when iterated.
         */
        void setSizeSequence(int... sizes) {
            this.sizes = sizes;
            nextSize = 0;
        }

        /* can change collection's size after each invocation */
        @Override
        public int size() {
            return sizes[nextSize == sizes.length - 1 ? nextSize : nextSize++];
        }

        @Override
        public DoubleIterator iterator() {
            return new DoubleIterator() {
                int pos = 0;

                public boolean hasNext() {
                    return pos < sizes[nextSize];
                }

                public double nextPrimitive() {
                    return elements[pos++];
                }

                public void remove() {
                    throw new UnsupportedOperationException(
                            "Not supported yet.");
                }
            };
        }
    }

    static final double[] OBJECTS = {(double) 0, (double) 1, (double) 2};
    static final DoubleTestCollection CANDIDATE = new DoubleTestCollection(OBJECTS);
    static final int CAP = OBJECTS.length; // capacity of the CANDIDATE
    static final int LAST = CAP - 1; // last possible array index
    double[] a;
    double[] res;

    int last() {
        return a.length - 1;
    }

    @Test
    protected void test() throws Throwable {
        // Check array type conversion
        res = new DoubleTestCollection(new double[]{1, 2}).toArrayPrimitive(new double[0]);
        assertTrue(res instanceof double[]);
        assertTrue(res.length == 2);
        assertTrue(res[1] == (double) 2);

        // Check incompatible type of target array

        res = CANDIDATE.toArrayPrimitive(new double[CAP]);

        // Check more elements than a.length
        a = new double[CAP - 1]; // appears too small
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res != a);
        assertTrue(res[LAST] != (double) 0);

        // Check equal elements as a.length
        a = new double[CAP]; // appears to match
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res == a);
        assertTrue(res[last()] != (double) 0);

        // Check equal elements as a.length
        a = new double[CAP + 1]; // appears too big
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res == a);
        assertTrue(res[last()] == (double) 0);

        // Check less elements than expected, but more than a.length
        a = new double[CAP - 2]; // appears too small
        CANDIDATE.setSizeSequence(CAP, CAP - 1);
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res != a);
        assertTrue(res.length == CAP - 1);
        assertTrue(res[LAST - 1] != (double) 0);

        // Check less elements than expected, but equal as a.length
        a = Arrays.copyOf(OBJECTS, CAP); // appears to match
        CANDIDATE.setSizeSequence(CAP, CAP - 1);
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res == a);
        assertTrue(res[last()] == (double) 0);

        // Check more elements than expected and more than a.length
        a = new double[CAP - 1]; // appears to match
        CANDIDATE.setSizeSequence(CAP - 1, CAP);
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res != a);
        assertTrue(res[LAST] != (double) 0);

        // Check more elements than expected, but equal as a.length
        a = new double[CAP - 1]; // appears to match
        CANDIDATE.setSizeSequence(CAP - 2, CAP - 1);
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res == a);
        assertTrue(res[last()] != (double) 0);

        // Check more elements than expected, but less than a.length
        a = Arrays.copyOf(OBJECTS, CAP); // appears to match
        CANDIDATE.setSizeSequence(CAP - 2, CAP - 1);
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res == a);
        assertTrue(res[last()] == (double) 0);

        test_7121314();
    }

    /*
     * Major target of this testcase, bug 7121314.
     */
    protected void test_7121314() throws Throwable {
        // Check equal elements as a.length, but less than expected
        a = new double[CAP - 1]; // appears too small
        CANDIDATE.setSizeSequence(CAP, CAP - 1);
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res == a);
        assertTrue(res[last()] != (double) 0);

        // Check less elements than a.length and less than expected
        a = Arrays.copyOf(OBJECTS, CAP - 1); // appears too small
        CANDIDATE.setSizeSequence(CAP, CAP - 2);
        res = CANDIDATE.toArrayPrimitive(a);
        assertTrue(res == a);
        assertTrue(res[last()] == (double) 0);
    }
}
