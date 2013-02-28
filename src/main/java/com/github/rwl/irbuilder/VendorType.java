package com.github.rwl.irbuilder;

public enum VendorType {
  UNKNOWN_VENDOR ("UnknownVendor"),
  APPLE ("Apple"),
  PC ("PC"),
  SCEI ("SCEI"),
  BGP ("BGP"),
  BGQ ("BGQ"),
  FREESCALE ("Freescale"),
  IBM ("IBM");

  private final String vendor;

  private VendorType(String vendor) {
    this.vendor = vendor;
  }

  public String vendor() {
    return vendor;
  }
}
