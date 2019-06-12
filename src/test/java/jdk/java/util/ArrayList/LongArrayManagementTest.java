package jdk.java.util.ArrayList;/*
 * Copyright 2016 Google, Inc.  All Rights Reserved.
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
 * @bug 8146568
 * @summary brittle white box test of internal array management
 * @modules java.base/java.util:open
 * @run testng ArrayManagement
 */

import com.xenoamess.commons.primitive.collections.lists.AbstractLongList;
import com.xenoamess.commons.primitive.collections.lists.array_lists.LongArrayList;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;


public class LongArrayManagementTest {
    static final int DEFAULT_CAPACITY = 10;
    static final Field ELEMENT_DATA;
    static final Field MODCOUNT;
    static final SplittableRandom rnd = new SplittableRandom();

    static {
        try {
            ELEMENT_DATA = LongArrayList.class.getDeclaredField("elementData");
            ELEMENT_DATA.setAccessible(true);
            MODCOUNT = AbstractLongList.class.getDeclaredField("modCount");
            MODCOUNT.setAccessible(true);
        } catch (ReflectiveOperationException huh) {
            throw new AssertionError(huh);
        }
    }

    static long[] elementData(LongArrayList list) {
        try {
            return (long[]) ELEMENT_DATA.get(list);
        } catch (ReflectiveOperationException huh) {
            throw new AssertionError(huh);
        }
    }

    static int modCount(LongArrayList list) {
        try {
            return MODCOUNT.getInt(list);
        } catch (ReflectiveOperationException huh) {
            throw new AssertionError(huh);
        }
    }

    static int capacity(LongArrayList list) {
        return elementData(list).length;
    }

    static int newCapacity(int oldCapacity) {
        return oldCapacity + (oldCapacity >> 1);
    }

    static void ensureCapacity(LongArrayList list, int capacity) {
        int oldCapacity = capacity(list);
        int oldModCount = modCount(list);
        list.ensureCapacity(capacity);
        assertTrue(capacity(list) >= capacity || capacity(list) == 0);
        assertEquals(modCount(list),
                (capacity(list) == oldCapacity)
                        ? oldModCount
                        : oldModCount + 1);
    }


    @Test
    public void emptyArraysAreShared() {
        assertSame(elementData(new LongArrayList()),
                elementData(new LongArrayList()));
        assertSame(elementData(new LongArrayList(0)),
                elementData(new LongArrayList(0)));
    }

    @Test
    public void emptyArraysDifferBetweenDefaultAndExplicit() {
        assertNotSame(elementData(new LongArrayList()),
                elementData(new LongArrayList(0)));
    }

    @Test
    public void negativeCapacity() {
        for (int capacity : new int[]{-1, Integer.MIN_VALUE}) {
            try {
                new LongArrayList(capacity);
                fail("should throw");
            } catch (IllegalArgumentException success) {
            }
        }
    }
}
