package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.VoidType;

public class VoidValue implements IValue {

  public static final VoidValue INSTANCE = new VoidValue();

  @Override
  public String ir() {
    return type().ir();
  }

  @Override
  public IType type() {
    return VoidType.INSTANCE;
  }

}
