package com.github.rwl.irbuilder.types;

public class StructType extends AbstractType {

  private final IType[] types;

  public StructType(IType[] types) {
    for (IType type : types) {
      assert type != null;
    }
    this.types = types;
  }

  @Override
  public String ir() {
    String ir = "{ ";
    for (int i = 0; i < types.length; i++) {
      IType type = types[i];
      ir += type.ir();
      if (i != types.length - 1) {
        ir += ", ";
      }
    }
    ir += " }";
    return ir;
  }

  public StructType refineAbstractTypeTo(OpaqueType opaque) {
    for (int i = 0; i < types.length; i++) {
      IType type = types[i];
      int n = 0;
      if (type instanceof PointerType) {
        type = baseType((PointerType) type);
        n = pointers((PointerType) type, n);
      }
      if (type.equals(opaque)) {
        IType newType = this;
        for (int j = 0; j < n; j++) {
          newType = newType.pointerTo();
        }
        types[i] = newType;
      }
    }
    return this;
  }

  private IType baseType(PointerType type) {
    if (type.getType() instanceof PointerType) {
      return baseType((PointerType) type.getType());
    }
    return type.getType();
  }

  private int pointers(PointerType type, int i) {
    if (type.getType() instanceof PointerType) {
      return pointers((PointerType) type.getType(), i + 1);
    }
    return i + 1;
  }

}
