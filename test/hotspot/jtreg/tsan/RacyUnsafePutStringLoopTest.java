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

/* @test RacyUnsafePutStringLoopTest
 * @summary Test a racy sun.misc.Unsafe.putObject.
 * @library /test/lib
 * @build AbstractLoop TsanRunner UnsafeUtil
 * @run main RacyUnsafePutStringLoopTest
 */

import java.io.IOException;
import jdk.test.lib.process.OutputAnalyzer;
import jdk.test.lib.process.ProcessTools;

public class RacyUnsafePutStringLoopTest {
  public static void main(String[] args) throws IOException {
    TsanRunner.runTsanTestExpectFailure(RacyUnsafePutStringLoopRunner.class)
        .shouldMatch("(Read|Write) of size (4|8) at 0x[0-9a-fA-F]+ by thread T[0-9]+")
        .shouldContain(" #0 RacyUnsafePutStringLoopRunner.run(I)V RacyUnsafePutStringLoopTest.java:")
        .shouldContain(" #0 (Generated Stub) <null>")
        .shouldContain(" #1 sun.misc.Unsafe.putObject(Ljava/lang/Object;JLjava/lang/Object;)V Unsafe.java:");
  }
}

class RacyUnsafePutStringLoopRunner extends AbstractLoop {
  private String x = "a";
  private static final long X_OFFSET = UnsafeUtil.objectFieldOffset(RacyUnsafePutStringLoopRunner.class, "x");

  @Override
  protected synchronized void run(int i) {
    char c = (char) (x.charAt(0) + 1);
    x = Character.toString(c);
  }

  @Override
  protected void run2(int i) {
    String s;
    synchronized (this) {
      s = this.x;
    }
    char c = (char) (s.charAt(0) + 1);
    UnsafeUtil.UNSAFE.putObject(this, X_OFFSET, Character.toString(c));
  }

  public static void main(String[] args) throws InterruptedException {
    RacyUnsafePutStringLoopRunner loop = new RacyUnsafePutStringLoopRunner();
    loop.runInTwoThreads();
    System.out.println("x = " + loop.x);
  }
}
