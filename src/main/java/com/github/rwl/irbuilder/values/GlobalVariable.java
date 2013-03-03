package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.ArrayType;
import com.github.rwl.irbuilder.types.IType;

public class GlobalVariable implements IValue {

  private final String name;

  private final IType type;

  public GlobalVariable(String name, IType type){
    this.name = name;
    this.type = type;
  }

  @Override
  public String ir() {
    if (type instanceof ArrayType) {
      return String.format("%s getelementptr inbounds (%s @%s, i32 0, i32 0)",
          ((ArrayType) type).getType().pointerTo().ir(), type.pointerTo().ir(),
          name);
    } else {
      return type.pointerTo().ir() + " @" + name;
    }
  }

  @Override
  public IType type() {
    if (type instanceof ArrayType) {
      return ((ArrayType) type).getType().pointerTo();
    } else {
      return type;
    }
  }

}
