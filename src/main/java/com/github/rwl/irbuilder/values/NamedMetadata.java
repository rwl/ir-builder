package com.github.rwl.irbuilder.values;

public class NamedMetadata {

  private final String name;

  public NamedMetadata(String name) {
    this.name = name;
  }

  public String ir() {
    return String.format("!%s", name);
  }

}
