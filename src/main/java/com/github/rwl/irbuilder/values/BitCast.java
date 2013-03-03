package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.FunctionType;
import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.PointerType;

public class BitCast implements IValue {

  private final IValue value;

  private final IType type;

  public BitCast(IValue value, IType type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public String ir() {
    if (type instanceof PointerType &&
        ((PointerType) type).pointsToType() instanceof FunctionType) {
      return String.format("%s bitcast(%s to %s)",
          ((FunctionType) ((PointerType) type).pointsToType()).getRetType().ir(),
          value.ir(), type.ir());
    } else {
      return String.format("%s bitcast(%s to %s)", type.ir(), value.ir(),
          type.ir());
    }
  }

  @Override
  public IType type() {
    if (type instanceof FunctionType) {
      return ((FunctionType) type).getRetType();
    } else {
      return type;
    }
  }

}
