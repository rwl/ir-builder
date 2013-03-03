package com.github.rwl.irbuilder.values;

import com.github.rwl.irbuilder.types.ArrayType;
import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.IntType;

public class StringValue implements IValue {

  private final String value;

  public StringValue(String value) {
    this.value = value;
  }

  @Override
  public String ir() {
    if (value.length() > 0) {
      return String.format("%s c\"%s\\00\"", type().ir(), StringValue.escape(value));
    } else {
      return String.format("%s zeroinitializer", type().ir());
    }
  }

  @Override
  public IType type() {
    return new ArrayType(IntType.INT_8, value.length() + 1);
  }

  public static String escape(String s) {
    final StringBuilder sb = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++){
      char c = s.charAt(i);
      if (!Character.isLetterOrDigit(c)) {
        sb.append(String.format("\\%02x", (int) c));
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

}
