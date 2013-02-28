package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;

public class ZeroValue implements IValue {

  @Override
  public String ir() {
    return "zeroinitializer";
  }

  @Override
  public IType type() {
    throw new RuntimeException();
  }

}
