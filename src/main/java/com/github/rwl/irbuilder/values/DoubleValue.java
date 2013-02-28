package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.DoubleType;
import com.github.rwl.irbuilder.types.IType;

public class DoubleValue implements IValue {

  private final double value;

  public DoubleValue(double value) {
    this.value = value;
  }

  @Override
  public String ir() {
    return String.format("%s %s", type().ir(), value);
  }

  @Override
  public IType type() {
    return DoubleType.INSTANCE;
  }

}
