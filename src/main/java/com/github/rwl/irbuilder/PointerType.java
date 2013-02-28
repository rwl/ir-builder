package com.github.rwl.irbuilder;

public class PointerType extends AbstractType {

  private final IType type;

  public PointerType(IType type) {
    this.type = type;
  }

  @Override
  public String ir() {
    return String.format("%s*", type.ir());
  }

}
