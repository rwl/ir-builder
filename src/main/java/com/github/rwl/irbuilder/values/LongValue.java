package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.IntType;

public class LongValue implements IValue {

  private final long value;

  public LongValue(long value) {
    this.value = value;
  }

  @Override
  public String ir() {
    return String.format("%s %d", type().ir(), value);
  }

  @Override
  public IType type() {
    return IntType.INT_64;
  }

}
