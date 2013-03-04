package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.IType;

public class MetadataNode implements IValue {

  private final String identifier;

  public MetadataNode(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String ir() {
    return String.format("metadata !%s", identifier);
  }

  @Override
  public IType type() {
    throw new RuntimeException("A metadata node does not have a type");
  }

  public String getIdentifier() {
    return identifier;
  }

}
