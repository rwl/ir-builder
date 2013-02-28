package com.github.rwl.irbuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.rwl.irbuilder.enums.ArchType;
import com.github.rwl.irbuilder.enums.AttrKind;
import com.github.rwl.irbuilder.enums.EnvironmentType;
import com.github.rwl.irbuilder.enums.Linkage;
import com.github.rwl.irbuilder.enums.OSType;
import com.github.rwl.irbuilder.enums.VendorType;
import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.NamedType;
import com.github.rwl.irbuilder.types.PointerType;
import com.github.rwl.irbuilder.types.VoidType;
import com.github.rwl.irbuilder.values.IValue;
import com.github.rwl.irbuilder.values.VoidValue;


public class IRBuilder {

  private static final Logger LOGGER = Logger.getLogger(IRBuilder.class.getName());

  private static class Function {
    Function(IType retType, String sig, List<IType> argTypes) {
      this.retType = retType;
      this.signature = sig;
      this.argTypes = argTypes;
    }
    IType retType;
    String signature;
    List<IType> argTypes;
  }

  private final String moduleId;

  private final StringBuilder targetBuffer = new StringBuilder();
  private final StringBuilder namedTypeBuffer = new StringBuilder();
  private final StringBuilder globalBuffer = new StringBuilder();
  private final StringBuilder funcDefBuffer = new StringBuilder();
  private final StringBuilder funcDeclBuffer = new StringBuilder();
  private final StringBuilder metadataBuffer = new StringBuilder();

  private final StringBuilder[] buffers = {targetBuffer, namedTypeBuffer,
      globalBuffer, funcDefBuffer, funcDeclBuffer, metadataBuffer};

  private StringBuilder _activeBuffer;
;
  private int _globalCounter = 0;
  private int _globalNameCounter = 0;

  private int _localConstantCounter = 0;

  private final Map<String, Function> funcs = new HashMap<String, Function>();
  private final Map<IValue, String> constants = new HashMap<IValue, String>();
  private final Map<String, IType> localTypes = new HashMap<String, IType>();

  private final Set<String> globalNames = new HashSet<String>();

  public IRBuilder(String moduleId) {
    assert moduleId != null;
    this.moduleId = moduleId;
  }

  public String build() {
    StringWriter writer = new StringWriter();
    try {
      build(writer);
    } catch (IOException e) {
      // ignore for StringWriter
    }
    return writer.toString();
  }

  public void build(Writer out) throws IOException {
    out.write("; ModuleID = '" + moduleId + "'\n\n");
    for (StringBuilder buffer : buffers) {
      if (buffer.length() > 0) {
        out.write(buffer.toString());
        out.write("\n");
      }
    }
  }

  public void clear() {
    targetBuffer.delete(0, targetBuffer.length());
    namedTypeBuffer.delete(0, namedTypeBuffer.length());
    globalBuffer.delete(0, globalBuffer.length());
    funcDefBuffer.delete(0, funcDefBuffer.length());
    metadataBuffer.delete(0, metadataBuffer.length());
  }

  public IRBuilder dataLayout(List<String> specifications) {
    assert specifications != null;
    assert specifications.size() > 0;

    setActiveBuffer(targetBuffer);

    write("target datalayout = \"");
    for (int i = 0; i < specifications.size(); i++) {
      write(specifications.get(i));
      if (i != specifications.size() - 1) {
        write("-");
      }
    }
    write("\"\n");
    return this;
  }

  /**
   * @param env may be null
   */
  public IRBuilder triple(ArchType arch, VendorType vendor, OSType os,
      EnvironmentType env) {
    assert arch != null;
    assert vendor != null;
    assert os != null;

    setActiveBuffer(targetBuffer);

    write("target triple = \"%s-%s-%s", arch.arch(), vendor.vendor(), os.os());
    if (env != null) {
      write("-");
      write(env.env());
    }
    write("\"\n");
    return this;
  }

  /**
   * @param name may be null
   */
  public IRBuilder namedType(String name, IType type) {
    assert type != null;
    assert !"void".equals(name);
    if (name == null || name.isEmpty()) {
      name = getGlobalNameCounter();
    }

    setActiveBuffer(namedTypeBuffer);

    write("%%%s = type %s\n", name, type.ir());
    return this;
  }

  public IRBuilder namedType(NamedType namedType) {
    assert namedType != null;

    setActiveBuffer(namedTypeBuffer);

    write("%s = type %s\n", namedType.ir(), namedType.getType().ir());
    return this;
  }

  /**
   * @param name may be null
   * @param linkage may be null
   */
  public IRBuilder constant(String name, IValue constant, Linkage linkage,
      boolean unnamedAddr) {
    assert constant != null;
    if (name == null || name.isEmpty()) {
      name = getGlobalCounter();
    } else if (globalNames.contains(name)) {
      name += getGlobalCounter();
    }
    if (linkage == null) {
      linkage = Linkage.PRIVATE;
    }

    setActiveBuffer(globalBuffer);

    write("@%s = %s", name, linkage.linkage());
    if (unnamedAddr) {
      write(" unnamed_addr");
    }
    write(" constant %s\n", constant.ir());
    constants.put(constant, name);
    globalNames.add(name);
    return this;
  }

  /**
   * @param name may be null
   * @param init may be null
   * @param linkage may be null
   */
  public IRBuilder global(String name, IType type, IValue init, Linkage linkage,
      boolean unnamedAddr) {
    assert type != null;
    if (name == null || name.isEmpty()) {
      name = getGlobalCounter();
    } else if (globalNames.contains(name)) {
      name += getGlobalCounter();
    }
    if (linkage == null) {
      linkage = Linkage.INTERNAL;
    }

    setActiveBuffer(globalBuffer);

    write("@%s = %s", name, linkage.linkage());
    if (unnamedAddr) {
      write(" unnamed_addr");
    }
    write(" global %s", type.ir());
    if (init != null) {
      write(" %s", init.ir());
    }
    write("\n");
    globalNames.add(name);
    return this;
  }

  /**
   * @param retType may be null
   * @param argTypes may be null
   * @param argNames may be null
   * @param attrs may be null
   */
  public IRBuilder beginFunction(IType retType, String name, List<IType> argTypes,
      List<String> argNames, List<AttrKind> attrs, boolean varArgs) {
    if (retType == null) {
      retType = VoidType.INSTANCE;
    }
    assert name != null;
    if (argTypes == null) {
      argTypes = new ArrayList<IType>();
    }
    if (argNames != null) {
      assert argTypes != null;
      assert argTypes.size() == argNames.size();
    } else {
      argNames = new ArrayList<String>();
      for (Iterator<IType> it = argTypes.iterator(); it.hasNext();) {
        argNames.add(getLocalConstantCounter());
      }
    }

    setActiveBuffer(funcDefBuffer);

    write("define %s @%s", retType.ir(), name);
    String sig = "(";
    for (int i = 0; i < argTypes.size(); i++) {
      IType argType = argTypes.get(i);
      String argName = argNames.get(i);
      sig += String.format("%s %%%s", argType.ir(), argName);
      if (i != argTypes.size() - 1) {
        sig += ", ";
      }
      localTypes.put(argName, argType);
    }
    if (varArgs) {
      sig += ", ...";
    }
    sig += ")";
    write(sig);
    if (attrs != null) {
      for (AttrKind attrKind : attrs) {
        write(" %s", attrKind.kind());
      }
    }
    write(" {\n");
    funcs.put(name, new Function(retType, sig, argTypes));
    return this;
  }

  /**
   * @param retVal may be null
   */
  public IRBuilder endFunction(IValue retVal) {
    if (retVal == null) {
      retVal = VoidValue.INSTANCE;
    }
    setActiveBuffer(funcDefBuffer);

    indent("ret %s", retVal.ir());
    write("\n");
    write("}");
    write("\n");
    return this;
  }

  /**
   * @param retType may be null
   * @param argTypes may be null
   * @param attrs may be null
   */
  public IRBuilder functionDecl(IType retType, String name, List<IType> argTypes,
      List<AttrKind> attrs, boolean varArgs) {
    if (retType == null) {
      retType = VoidType.INSTANCE;
    }
    assert name != null;

    setActiveBuffer(funcDeclBuffer);

    write("declare %s @%s", retType.ir(), name);
    String sig = "(";
    if (argTypes != null) {
      for (int i = 0; i < argTypes.size(); i++) {
        IType argType = argTypes.get(i);
        sig += String.format("%s", argType.ir());
      }
      if (varArgs) {
        sig += ", ...";
      }
    }
    sig += ")";
    write(sig);
    if (attrs != null) {
      for (AttrKind attrKind : attrs) {
        write(" %s", attrKind.kind());
      }
    }
    write("\n");
    funcs.put(name, new Function(retType, sig, argTypes));
    return this;
  }

  public IRBuilder call(String name, List<IValue> argVals) {
    assert name != null;
    assert !name.isEmpty();
    Function func = funcs.get(name);
    assert func != null;
    IType retType = func.retType;
    String fnty = func.signature + '*';
    List<IType> argTypes = func.argTypes;

    setActiveBuffer(funcDefBuffer);

    indent("call %s %s @%s(", retType.ir(), fnty, name);
    for (int i = 0; i < argVals.size(); i++) {
      IValue val = argVals.get(i);
      //IType argType = argTypes.get(i);
//      if (argType instanceof PointerType) {
//        gep(argType, constants.get(val), val);
//      }
      write("%s", val.ir());
      if (i != argVals.size() - 1) {
        write(", ");
      }
    }
    write(")\n");
    return this;
  }

//  private void gep(IType retType, String name, IValue val) {
//    write("%s getelementptr inbounds (%s @%s, i32 0, i32 0)",
//        retType.ir(), val.type().pointerTo().ir(), name);
//  }

  /**
   * @param name may be null
   * @param num may be null
   */
  public IRBuilder alloca(IType type, String name, Integer num) {
    assert type != null;
    if (name == null) {
      name = getLocalConstantCounter();
    }

    setActiveBuffer(funcDefBuffer);

    indent("%%%s = alloca %s", name, type.ir());
    if (num != null) {
      indent(", %s %d", type.ir(), num);
    }
    write("\n");
    localTypes.put(name, type);
    return this;
  }

  public IRBuilder store(String assignee, String constant) {
    assert constant != null;
    assert assignee != null;
    IType type = localTypes.get(constant);
    assert type != null;

    setActiveBuffer(funcDefBuffer);

    indent("store %s %%%s, %s %%%s", type.ir(), constant,
        type.pointerTo().ir(), assignee);
    write("\n");
    return this;
  }

  public IRBuilder store(String assignee, IValue val) {
    assert val != null;
    assert assignee != null;

    setActiveBuffer(funcDefBuffer);

    indent("store %s, %s %%%s", val.ir(), val.type().pointerTo().ir(),
        assignee);
    write("\n");
    return this;
  }

  private void setActiveBuffer(StringBuilder buffer) {
    _activeBuffer = buffer;
  }

  private void write(String s, Object... args) {
    _activeBuffer.append(String.format(s, args));
  }

  private void write(String s) {
    _activeBuffer.append(s);
  }

  private void indent(String s, Object... args) {
    write("  " + s, args);
  }

  private String getGlobalNameCounter() {
    int cnt = _globalNameCounter;
    _globalNameCounter += 1;
    return String.valueOf(cnt);
  }

  private String getGlobalCounter() {
    int cnt = _globalCounter;
    _globalCounter += 1;
    return String.valueOf(cnt);
  }

  private String getLocalConstantCounter() {
    int cnt = _localConstantCounter;
    _localConstantCounter += 1;
    return String.valueOf(cnt);
  }

  public String uniqueGlobalName(String candidate) {
    if (globalNames.contains(candidate)) {
      candidate += getGlobalCounter();
    }
    return candidate;
  }

}
