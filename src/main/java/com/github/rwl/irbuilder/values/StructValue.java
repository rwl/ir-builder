package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.StructType;

public class StructValue implements IValue {

  private final IValue[] values;

  private final IType type;

  public StructValue(IValue[] values, IType type) {
    this.values = values;
    this.type = type;
  }

  public StructValue(IValue[] values) {
    this.values = values;
    IType[] types = new IType[values.length];
    for (int i = 0; i < values.length; i++) {
      IValue val = values[i];
      types[i] = val.type();
    }
    type = new StructType(types);
  }

  @Override
  public String ir() {
    String ir = "{ ";
    for (int i = 0; i < values.length; i++) {
      IValue value = values[i];
      ir += value.ir();
      if (i != values.length - 1) {
        ir += ", ";
      }
    }
    ir += " }";
    return type().ir() + " " + ir;
  }

  @Override
  public IType type() {
    return type;
  }

}
