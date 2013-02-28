package com.github.rwl.irbuilder.enums;

public enum Linkage {
  EXTERNAL ("external"),
  AVAILABLE_EXTERNALLY ("available_externally"),
  LINK_ONCE_ANY ("linkonce"),
  LINK_ONCE_ODR ("linkonce_odr"),
  LINK_ONCE_ODR_AUTO_HIDE ("linkonce_odr_auto_hide"),
  WEAK_ANY ("weak"),
  WEAK_ODR ("weak_odr"),
  APPENDING ("appending"),
  INTERNAL ("internal"),
  PRIVATE ("private"),
  LINKER_PRIVATE ("linker_private"),
  LINKER_PRIVATE_WEAK ("linker_private_weak"),
  DLL_IMPORT ("dllimport"),
  DLL_EXPORT ("dllexport"),
  EXTERNAL_WEAK ("extern_weak"),
  COMMON ("common");

  private final String linkage;

  private Linkage(String linkage) {
    this.linkage = linkage;
  }

  public String linkage() {
    return linkage;
  }
}
