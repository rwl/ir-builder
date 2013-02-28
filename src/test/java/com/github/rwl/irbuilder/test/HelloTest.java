package com.github.rwl.irbuilder.test;

import com.github.rwl.irbuilder.IRBuilder;
import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.IntType;
import com.github.rwl.irbuilder.values.IValue;
import com.github.rwl.irbuilder.values.StringValue;
import com.google.common.collect.Lists;

import junit.framework.TestCase;

public class HelloTest extends TestCase {

  public void testHelloWorld() {
    StringValue hello = new StringValue("Hello world!\n");

    String ir = new IRBuilder("top")
      .constant(null, hello, null, false)
      .functionDecl(IntType.INT_32, "puts", Lists.<IType>newArrayList(
          IntType.INT_8.pointerTo()), null, false)
      .beginFunction(null, "main", null, null, null, false)
      .call("puts", Lists.<IValue>newArrayList(hello))
      .endFunction(null)
      .build();

    System.out.println(ir);
  }
}
