package com.github.rwl.irbuilder;

public enum AttrKind {
  ALWAYS_INLINE ("alwaysinline"),
  BY_VAL ("byval"),
  INLINE_HINT ("inlinehint"),
  IN_REG ("inreg"),
  MIN_SIZE ("minsize"),
  NAKED ("naked"),
  NEST ("nest"),
  NO_ALIAS ("noalias"),
  NO_BUILTIN ("nobuiltin"),
  NO_CAPTURE ("nocapture"),
  NO_DUPLICATE ("noduplicate"),
  NO_IMPLICIT_FLOAT ("noimplicitfloat"),
  NO_INLINE ("noinline"),
  NON_LAZY_BIND ("nonlazybind"),
  NO_RED_ZONE ("noredzone"),
  NO_RETURN ("noreturn"),
  NO_UNWIND ("nounwind"),
  OPTIMIZE_FOR_SIZE ("optsize"),
  READ_NONE ("readnone"),
  READ_ONLY ("readonly"),
  RETURNS_TWICE ("returns_twice"),
  //SExt,
  //StackAlignment
  STACK_PROTECT ("ssp"),
  STACK_PROTECT_REQ ("sspreq"),
  STACK_PROTECT_STRONG ("sspstrong"),
  //StructRet
  SANITIZE_ADDRESS ("sanitize_address"),
  SANITIZE_THREAD ("sanitize_thread"),
  SANITIZE_MEMORY ("sanitize_memory"),
  UWTABLE ("uwtable");
  //ZExt

  private final String kind;

  private AttrKind(String kind) {
    this.kind = kind;
  }

  public String kind() {
    return kind;
  }
}
