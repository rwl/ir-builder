package com.github.rwl.irbuilder;

public enum EnvironmentType {
  UNKNOWN_ENVIRONMENT ("UnknownEnvironment"),
  GNU ("GNU"),
  GNUEABI ("GNUEABI"),
  GNUEABIHF ("GNUEABIHF"),
  GNUX32 ("GNUX32"),
  EABI ("EABI"),
  MACHO ("MachO"),
  ANDROID ("Android"),
  ELF ("ELF");

  private final String env;

  private EnvironmentType(String env) {
    this.env = env;
  }

  public String env() {
    return env;
  }
}
