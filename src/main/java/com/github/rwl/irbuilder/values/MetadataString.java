package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;

public class MetadataString implements IValue {

  private final String string;

  public MetadataString(String string) {
    this.string = string;
  }

  @Override
  public String ir() {
    return String.format("metadata !\"%s\"", string);
  }

  @Override
  public IType type() {
    throw new RuntimeException("A metadata string does not have a type");
  }

}
