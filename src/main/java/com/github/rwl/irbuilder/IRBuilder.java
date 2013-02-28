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

  private int _constantCounter = 0;
  private int _globalCounter = 0;
  private int _globalNameCounter = 0;

  private int _localConstantCounter = 0;

  private final Map<String, Function> funcs = new HashMap<String, Function>();
  private final Map<IValue, String> constants = new HashMap<IValue, String>();

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
    out.write("; ModuleID = '" + moduleId + "'\n");
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

  /**
   * @param name may be null
   */
  public IRBuilder constant(String name, IValue constant) {
    assert constant != null;
    if (name == null || name.isEmpty()) {
      name = getConstantCounter();
    }

    setActiveBuffer(globalBuffer);

    write("@%s = private unnamed_addr constant %s\n", name, constant.ir());
    constants.put(constant, name);
    return this;
  }

  /**
   * @param name may be null
   * @param init may be null
   */
  public IRBuilder global(String name, IType type, IValue init) {
    assert type != null;
    if (name == null || name.isEmpty()) {
      name = getGlobalCounter();
    }

    setActiveBuffer(globalBuffer);

    write("@%s = internal global %s %s\n", name, type.ir(),
        init != null ? init.ir() : "zeroinitializer");
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
      IType argType = argTypes.get(i);
      if (argType instanceof PointerType) {
        gep(argType, constants.get(val), val);
      }
    }
    write(")\n");
    return this;
  }

  private void gep(IType retType, String name, IValue val) {
    write("%s getelementptr inbounds (%s @%s, i32 0, i32 0)",
        retType.ir(), val.type().pointerTo().ir(), name);
  }

  private void setActiveBuffer(StringBuilder buffer) {
    _activeBuffer = buffer;
  }

  private void write(String s, Object... args) {
    _activeBuffer.append(String.format(s, args));
  }

  private void indent(String s, Object... args) {
    write("  " + s, args);
  }

  private String getConstantCounter() {
    int cnt = _constantCounter;
    _constantCounter += 1;
    return String.valueOf(cnt);
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

}
