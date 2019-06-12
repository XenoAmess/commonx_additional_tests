/*
 * Copyright (c) 1999, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 4189896
 * @summary AbstractList iterators previously checked for co-modification
 *          *after* the set/add/remove operations were performed.
 */

package jdk.java.util.AbstractList;

import com.xenoamess.commons.primitive.collections.lists.array_lists.IntArrayList;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;

public class IntFailFastIteratorTest {

    @Test
    public void test() throws Exception {
        List orig = new IntArrayList(100);
        for (int i = 0; i < 100; i++)
            orig.add((int) i);

        List copy = new IntArrayList(orig);
        try {
            ListIterator i = copy.listIterator();
            i.next();
            copy.remove(99);
            copy.add((int) 99);
            i.remove();
            throw new Exception("remove: iterator didn't fail fast");
        } catch (ConcurrentModificationException e) {
        }
        if (!copy.equals(orig))
            throw new Exception("remove: iterator didn't fail fast enough");

        try {
            ListIterator i = copy.listIterator();
            i.next();
            copy.remove(99);
            copy.add((int) 99);
            i.set((int) 666);
            throw new Exception("set: iterator didn't fail fast");
        } catch (ConcurrentModificationException e) {
        }
        if (!copy.equals(orig))
            throw new Exception("set: iterator didn't fail fast enough");

        try {
            ListIterator i = copy.listIterator();
            copy.remove(99);
            copy.add((int) 99);
            i.add((int) 666);
            throw new Exception("add: iterator didn't fail fast");
        } catch (ConcurrentModificationException e) {
        }
        if (!copy.equals(orig))
            throw new Exception("add: iterator didn't fail fast enough");
    }
}
