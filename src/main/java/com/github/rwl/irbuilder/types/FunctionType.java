package com.github.rwl.irbuilder.types;

import java.util.List;

public class FunctionType extends AbstractType {

  private final IType retType;

  private final List<IType> argTypes;

  public FunctionType(IType retType, List<IType> argTypes) {
    this.retType = retType;
    this.argTypes = argTypes;
  }

  @Override
  public String ir() {
    String ir = retType.ir() + " (";
    for (int i = 0; i < argTypes.size(); i++) {
      IType argType = argTypes.get(i);
      ir += argType.ir();
      if (i != argTypes.size() - 1) {
        ir += ", ";
      }
    }
    return ir + ")";
  }

  public IType getRetType() {
    return retType;
  }

  public List<IType> getArgTypes() {
    return argTypes;
  }

}
