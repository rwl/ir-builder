package com.github.rwl.irbuilder.types;

public class DoubleType extends AbstractType {

  public static final DoubleType INSTANCE = new DoubleType();

  private DoubleType() {
  }

  @Override
  public String ir() {
    return "double";
  }

}
