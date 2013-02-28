package com.github.rwl.irbuilder.types;

public class ArrayType extends AbstractType {

  private final IType type;
  private final int length;

  public ArrayType(IType type, int length) {
    this.type = type;
    this.length = length;
  }

  @Override
  public String ir() {
    return String.format("[%d x %s]", length, type.ir());
  }

  public IType getType() {
    return type;
  }

}
