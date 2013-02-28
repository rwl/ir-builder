package com.github.rwl.irbuilder.types;

public class StructType extends AbstractType {

  private final IType[] values;

  public StructType(IType[] values) {
    this.values = values;
  }

  @Override
  public String ir() {
    String ir = "{ ";
    for (int i = 0; i < values.length; i++) {
      IType type = values[i];
      ir += type.ir();
      if (i != values.length - 1) {
        ir += ", ";
      }
    }
    ir += " }";
    return ir;
  }

}
