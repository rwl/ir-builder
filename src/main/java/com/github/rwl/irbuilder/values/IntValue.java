package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.IntType;

public class IntValue implements IValue {

  private final int val;

  public IntValue(int val) {
    this.val = val;
  }

  @Override
  public String ir() {
    return String.format("%s %d", type().ir(), val);
  }

  @Override
  public IType type() {
    return IntType.INT_32;
  }

}
