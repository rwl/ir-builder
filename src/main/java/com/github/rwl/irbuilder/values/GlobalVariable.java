package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.ArrayType;
import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.PointerType;

public class GlobalVariable implements IValue {

  private final String name;

  private final PointerType pointerType;

  public GlobalVariable(String name, PointerType type){
    this.name = name;
    this.pointerType = type;
  }

  @Override
  public String ir() {
    if (pointerType.pointsToType() instanceof ArrayType) {
      return String.format("%s getelementptr inbounds (%s @%s, i32 0, i32 0)",
          ((ArrayType) pointerType.pointsToType()).arrayOfType().pointerTo().ir(),
          pointerType.ir(), name);
    } else {
      return pointerType.ir() + " @" + name;
    }
  }

  @Override
  public IType type() {
    if (pointerType.pointsToType() instanceof ArrayType) {
      return ((ArrayType) pointerType.pointsToType()).arrayOfType().pointerTo();
    } else {
      return pointerType;
    }
  }

}
