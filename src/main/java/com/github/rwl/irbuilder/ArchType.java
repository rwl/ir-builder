package com.github.rwl.irbuilder;

public enum ArchType {
  UNKNOWN_ARCH ("UnknownArch"),
  ARM ("arm"),
  AARCH64 ("aarch64"),
  HEXAGON ("hexagon"),
  MIPS ("mips"),
  MIPSEL ("mipsel"),
  MIPS64 ("mips64"),
  MIPS64EL ("mips64el"),
  MSP430 ("msp430"),
  PPC ("ppc"),
  PPC64 ("ppc64"),
  R600 ("r600"),
  SPARC ("sparc"),
  SPARCV9 ("sparcv9"),
  TCE ("tce"),
  THUMB ("thumb"),
  X86 ("x86"),
  X86_64 ("x86_64"),
  XCORE ("xcore"),
  MBLAZE ("mblaze"),
  NVPTX ("nvptx"),
  NVPTX64 ("nvptx64"),
  LE32 ("le32"),
  AMDIL ("amdil"),
  SPIR ("spir"),
  SPIR64 ("spir64");

  private final String arch;

  private ArchType(String arch) {
    this.arch = arch;
  }

  public String arch() {
    return arch;
  }
}
