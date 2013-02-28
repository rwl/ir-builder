package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;

public class BitCast implements IValue {

  private final IValue value;

  private final IType type;

  public BitCast(IValue value, IType type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public String ir() {
    return String.format("%s bitcast(%s to %s)", type.ir(), value.ir(), type.ir());
  }

  @Override
  public IType type() {
    throw new RuntimeException("bitcast has no type");
  }

}
