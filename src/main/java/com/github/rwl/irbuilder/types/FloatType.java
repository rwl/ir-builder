package com.github.rwl.irbuilder.types;

public class FloatType extends AbstractType {

  public static final FloatType INSTANCE = new FloatType();

  private FloatType() {
  }

  @Override
  public String ir() {
    return "float";
  }

}
