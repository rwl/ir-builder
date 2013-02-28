package com.github.rwl.irbuilder;

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
