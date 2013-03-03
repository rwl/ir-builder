package com.github.rwl.irbuilder.values;

import java.util.List;

import com.github.rwl.irbuilder.types.IType;

public class ArrayValue implements IValue {

  private final IType type;

  private final List<IValue> values;

  public ArrayValue(IType type, List<IValue> values) {
    this.type = type;
    this.values = values;
  }

  @Override
  public String ir() {
    String ir = "[";
    for (int i = 0; i < values.size(); i++) {
      IValue value = values.get(i);
      ir += value.ir();
      if (i != values.size() - 1) {
        ir += ", ";
      }
    }
    ir += "]";
    return type().ir() + " " + ir;
  }

  @Override
  public IType type() {
    return type;
  }

}
