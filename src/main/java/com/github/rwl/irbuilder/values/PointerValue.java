package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.ArrayType;
import com.github.rwl.irbuilder.types.IType;

public class PointerValue implements IValue {

  private final String identifier;

  private final ArrayType arrayType;

  private final IType type;

  public PointerValue(String ident, ArrayType type) {
    this.identifier = ident;
    this.arrayType = type;
    this.type = null;
  }

  public PointerValue(IType type, String identifier) {
    this.type = type;
    this.identifier = identifier;
    this.arrayType = null;
  }

  @Override
  public String ir() {
    if (arrayType != null) {
      return String.format("%s getelementptr inbounds (%s @%s, i32 0, i32 0)",
          arrayType.getType().pointerTo().ir(), arrayType.pointerTo().ir(), identifier);
    } else {
      return type.pointerTo().ir() + " @" + identifier;
    }
  }

  @Override
  public IType type() {
    return arrayType;
  }

}
