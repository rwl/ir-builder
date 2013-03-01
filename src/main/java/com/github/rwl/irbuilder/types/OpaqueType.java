package com.github.rwl.irbuilder.types;

public class OpaqueType extends AbstractType {

  public static final OpaqueType INSTANCE = new OpaqueType();

  public OpaqueType() {
  }

  @Override
  public String ir() {
    return "opaque";
  }

}
