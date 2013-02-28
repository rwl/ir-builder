package com.github.rwl.irbuilder;

public class VoidType extends AbstractType {

  public static final VoidType INSTANCE = new VoidType();

  private VoidType() {
  }

  @Override
  public String ir() {
    return "void";
  }

}
