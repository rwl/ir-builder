package com.github.rwl.irbuilder.types;

public class NamedType extends AbstractType {

  private final String name;

  private final IType type;

  public NamedType(String name, IType type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String ir() {
    return '%' + name;
  }

  public IType getType() {
    return type;
  }

}
