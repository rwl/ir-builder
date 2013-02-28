package com.github.rwl.irbuilder.types;

public class IntType extends AbstractType {

  public static final IntType INT_1 = new IntType(1);
  public static final IntType INT_8 = new IntType(8);
  public static final IntType INT_16 = new IntType(16);
  public static final IntType INT_32 = new IntType(32);
  public static final IntType INT_64 = new IntType(64);

  private final int bits;

  public IntType(int bits) {
    this.bits = bits;
  }

  @Override
  public String ir() {
    return String.format("i%d", bits);
  }

}
