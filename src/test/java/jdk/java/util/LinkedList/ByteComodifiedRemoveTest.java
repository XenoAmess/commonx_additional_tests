package jdk.java.util.LinkedList;/*
 * Copyright (c) 2000, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 4308549
 * @summary Due to a bug in LinkedList's ListIterator's remove(),
 *     the ListIterator would not check for comodification before remove.
 * @author Konstantin Kladko
 */

import com.xenoamess.commons.primitive.collections.lists.linked_lists.ByteLinkedList;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;

public class ByteComodifiedRemoveTest {
    @Test
    public void main() {
        ByteLinkedList list = new ByteLinkedList();
        Byte o1 = new Byte((byte) 1);
        list.add(o1);
        ListIterator e = list.listIterator();
        e.next();
        Byte o2 = new Byte((byte) 0);
        list.add(o2);

        try {
            e.remove();
        } catch (ConcurrentModificationException cme) {
            return;
        }

        throw new RuntimeException(
                "LinkedList ListIterator.remove() comodification check failed.");
    }
}
