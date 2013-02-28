package com.github.rwl.irbuilder;

public class OpaqueType extends AbstractType {

  public static final OpaqueType INSTANCE = new OpaqueType();

  private OpaqueType() {
  }

  @Override
  public String ir() {
    return "opaque";
  }

}
