package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;

public class NullValue implements IValue {

  private final IType type;

  public NullValue(IType type) {
    this.type = type;
  }

  @Override
  public String ir() {
    return String.format("%s null", type.ir());
  }

  @Override
  public IType type() {
    return type;
  }

}
