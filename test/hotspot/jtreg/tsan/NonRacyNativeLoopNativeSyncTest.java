/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Google and/or its affiliates. All rights reserved.
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

/* @test NonRacyNativeLoopNativeSyncTest
 * @summary Test that native code protected by native synchronization is not racy.
 * @library /test/lib
 * @build AbstractLoop AbstractNativeLoop TsanRunner
 * @run main/othervm/native -XX:+ThreadSanitizer NonRacyNativeLoopNativeSyncTest
 */

import java.io.IOException;
import jdk.test.lib.process.OutputAnalyzer;
import jdk.test.lib.process.ProcessTools;

/**
 * Test that TSAN doesn't report native code as racy
 * when protected by Java synchronization.
 */
public class NonRacyNativeLoopNativeSyncTest {
  public static void main(String[] args) throws IOException {
    TsanRunner.runTsanTestExpectSuccess(NonRacyNativeLoopNativeSyncRunner.class);
  }
}

class NonRacyNativeLoopNativeSyncRunner extends AbstractNativeLoop {
  @Override
  protected void run(int i) {
    writeNativeGlobalSync();
  }

  public static void main(String[] args) throws InterruptedException {
    NonRacyNativeLoopNativeSyncRunner loop = new NonRacyNativeLoopNativeSyncRunner();
    loop.runInTwoThreads();
    System.out.format("native_global = %d\n", loop.readNativeGlobal());
  }
}
