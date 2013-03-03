package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.ArrayType;
import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.PointerType;

public class LocalVariable implements IValue {

  private final String name;

  private final IType type;

  public LocalVariable(String name, IType type){
    this.name = name;
    this.type = type;
  }

  @Override
  public String ir() {
    if (type instanceof PointerType &&
        ((PointerType) type).pointsToType() instanceof ArrayType) {
      return String.format("%s getelementptr inbounds (%s @%s, i32 0, i32 0)",
          ((ArrayType) ((PointerType) type).pointsToType()).pointerTo().ir(),
          type.ir(), name);
    } else {
      return type.ir() + " %" + name;
    }
  }

  @Override
  public IType type() {
    return type;
  }

}
