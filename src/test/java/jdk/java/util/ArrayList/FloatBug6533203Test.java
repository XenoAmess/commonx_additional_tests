package jdk.java.util.ArrayList;/*
 * Copyright (c) 2007, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 6533203
 * @summary AbstractList.ListItr.add might corrupt iterator state if enclosing add throws
 */

import com.xenoamess.commons.primitive.collections.lists.FloatList;
import com.xenoamess.commons.primitive.collections.lists.array_lists.FloatArrayList;
import org.junit.jupiter.api.Test;

import java.util.ListIterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"serial", "unchecked"})
public class FloatBug6533203Test {

    void test() throws Throwable {
        final FloatList superstitious = new FloatArrayList() {
            @Override
            public void addPrimitive(int index, float i) {
                if ((float) i == (float) 13)
                    throw new Error("unlucky");
                else
                    super.add(index, i);
            }
        };
        final ListIterator it = superstitious.listIterator(0);
        assertEquals(it.nextIndex(), 0);
        THROWS(Error.class, new F() {
            void f() {
                it.add((float) 13);
            }
        });
        assertEquals(it.nextIndex(), 0);
    }

    @Test
    void instanceMain() throws Throwable {
        test();
    }

    abstract class F {
        abstract void f() throws Throwable;
    }

    void THROWS(Class<? extends Throwable> k, F... fs) throws Throwable {
        for (F f : fs)
            try {
                f.f();
                Thread.dumpStack();
            } catch (Throwable t) {
                if (!k.isAssignableFrom(t.getClass()))
                    throw t;
            }
    }
}
