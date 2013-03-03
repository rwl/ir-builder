package com.github.rwl.irbuilder.types;

import java.util.Arrays;
import java.util.List;

public class FunctionType extends AbstractType {

  private final IType retType;

  private final List<IType> argTypes;

  private final boolean varArg;

  public FunctionType(IType retType, IType... argTypes) {
    this.retType = retType;
    this.argTypes = Arrays.asList(argTypes);
    this.varArg = false;
  }

  public FunctionType(IType retType, List<IType> argTypes, boolean varArg) {
    this.retType = retType;
    this.argTypes = argTypes;
    this.varArg = varArg;
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
    if (varArg) {
      ir += ", ...";
    }
    return ir + ")";
  }

  public IType getRetType() {
    return retType;
  }

  public List<IType> getArgTypes() {
    return argTypes;
  }

  public boolean isVarArg() {
    return varArg;
  }

}
