package com.github.rwl.irbuilder.test;

import com.github.rwl.irbuilder.IRBuilder;
import com.github.rwl.irbuilder.IType;
import com.github.rwl.irbuilder.IntType;
import com.google.common.collect.Lists;

import junit.framework.TestCase;

public class HelloTest extends TestCase {

  public void testHelloWorld() {

    String ir = new IRBuilder("top")
      .constant(null, "Hello world!\n")
      .functionDecl(IntType.INT_32, "puts", Lists.<IType>newArrayList(
          IntType.INT_8.pointerTo()), null, false)
      .beginFunction(null, "main", null, null, null, false)
      .call("puts")
      .endFunction(null)
      .build();

    System.out.println(ir);
  }
}
