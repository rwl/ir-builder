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

  /**
   * @param newType may be null
   */
  public StructType refineAbstractTypeTo(OpaqueType opaque,
      IType newType) {
    for (int i = 0; i < types.length; i++) {
      IType type = types[i];
      int n = 0;
      if (type instanceof PointerType) {
        n = pointers((PointerType) type, n);
        type = baseType((PointerType) type);
      }
      if (type.equals(opaque)) {
        IType replacement = newType == null ? this : newType;
        for (int j = 0; j < n; j++) {
          replacement = replacement.pointerTo();
        }
        types[i] = replacement;
      }
    }
    return this;
  }

  private IType baseType(PointerType type) {
    if (type.pointsToType() instanceof PointerType) {
      return baseType((PointerType) type.pointsToType());
    }
    return type.pointsToType();
  }

  private int pointers(PointerType type, int i) {
    if (type.pointsToType() instanceof PointerType) {
      return pointers((PointerType) type.pointsToType(), i + 1);
    }
    return i + 1;
  }

}
