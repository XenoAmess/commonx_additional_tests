package jdk.java.util.LinkedList;/*
 * Copyright (c) 1998, 1999, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 4163207
 * @summary AddAll was prepending instead of appending!
 */

import com.xenoamess.commons.primitive.collections.lists.DoubleList;
import com.xenoamess.commons.primitive.collections.lists.array_lists.DoubleArrayList;
import com.xenoamess.commons.primitive.collections.lists.linked_lists.DoubleLinkedList;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class DoubleAddAllTest {
    @Test
    public void main() {
        List head = Collections.nCopies(7, (double) 0);
        List tail = Collections.nCopies(4, (double) 1);
        DoubleList l1 = new DoubleArrayList(head);
        DoubleList l2 = new DoubleLinkedList(head);
        l1.addAll(tail);
        l2.addAll(tail);
        if (!l1.equals(l2))
            throw new RuntimeException("addAll is broken.");
    }
}
