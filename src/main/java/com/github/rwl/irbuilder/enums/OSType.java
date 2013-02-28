package com.github.rwl.irbuilder.enums;

public enum OSType {
  UNKNOWN_OS ("UnknownOS"),
  AURORAUX ("AuroraUX"),
  CYGWIN ("Cygwin"),
  DARWIN ("Darwin"),
  DRAGON_FLY ("DragonFly"),
  FREE_BSD ("FreeBSD"),
  IOS ("IOS"),
  KFREE_BSD ("KFreeBSD"),
  LINUX ("Linux"),
  LV2 ("Lv2"),
  MAC_OSX ("MacOSX"),
  MINGW32 ("MinGW32"),
  NET_BSD ("NetBSD"),
  OPEN_BSD ("OpenBSD"),
  SOLARIS ("Solaris"),
  WIN32 ("Win32"),
  HAIKU ("Haiku"),
  MINIX ("Minix"),
  RTEMS ("RTEMS"),
  NACL ("NaCl"),
  CNK ("CNK"),
  BITRIG ("Bitrig"),
  AIX ("AIX");

  private final String os;

  private OSType(String os) {
    this.os = os;
  }

  public String os() {
    return os;
  }
}
