package com.github.rwl.irbuilder.types;

import com.github.rwl.irbuilder.values.IValue;

public class LongType implements IValue {

  private final long value;

  public LongType(long value) {
    this.value = value;
  }

  @Override
  public String ir() {
    return String.format("%s %s", type().ir(), value);
  }

  @Override
  public IType type() {
    return IntType.INT_64;
  }

}
