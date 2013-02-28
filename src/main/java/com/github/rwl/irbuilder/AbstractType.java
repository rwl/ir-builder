package com.github.rwl.irbuilder;

public abstract class AbstractType implements IType {

  @Override
  public PointerType pointerTo() {
    return new PointerType(this);
  }

}
