package com.github.rwl.irbuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.rwl.irbuilder.enums.ArchType;
import com.github.rwl.irbuilder.enums.AttrKind;
import com.github.rwl.irbuilder.enums.EnvironmentType;
import com.github.rwl.irbuilder.enums.Linkage;
import com.github.rwl.irbuilder.enums.OSType;
import com.github.rwl.irbuilder.enums.VendorType;
import com.github.rwl.irbuilder.types.FunctionType;
import com.github.rwl.irbuilder.types.IType;
import com.github.rwl.irbuilder.types.NamedType;
import com.github.rwl.irbuilder.types.PointerType;
import com.github.rwl.irbuilder.values.GlobalVariable;
import com.github.rwl.irbuilder.values.IValue;
import com.github.rwl.irbuilder.values.LocalVariable;
import com.github.rwl.irbuilder.values.VoidValue;


public class IRBuilder {

  private final String moduleId;

  private final StringBuilder targetBuffer = new StringBuilder();
  private final StringBuilder namedTypeBuffer = new StringBuilder();
  private final StringBuilder globalBuffer = new StringBuilder();
  private final StringBuilder funcDefBuffer = new StringBuilder();
  private final StringBuilder funcDeclBuffer = new StringBuilder();
  private final StringBuilder metadataBuffer = new StringBuilder();

  private final StringBuilder[] buffers = {
      targetBuffer,
      namedTypeBuffer,
      globalBuffer,
      funcDefBuffer,
      funcDeclBuffer,
      metadataBuffer
  };

  private StringBuilder _activeBuffer;

  private int _globalCounter = 0;
  private int _globalNameCounter = 0;

  private int _localConstantCounter = 1;

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
  public NamedType namedType(String name, IType type) {
    assert type != null;
    assert !"void".equals(name);
    if (name == null || name.isEmpty()) {
      name = getGlobalNameCounter();
    }
    NamedType nt = new NamedType(name, type);
    namedType(nt);
    return nt;
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
  public GlobalVariable constant(String name, IValue var, Linkage linkage,
      boolean unnamedAddr) {
    assert var != null;
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
    write(" constant %s\n", var.ir());
    globalNames.add(name);
    return new GlobalVariable(name, var.type().pointerTo());
  }

  /**
   * @param name may be null
   * @param linkage may be null
   */
  public GlobalVariable global(String name, IValue init, Linkage linkage,
      boolean unnamedAddr) {
    assert init != null;
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
    write(" global %s\n", init.ir());
    globalNames.add(name);
    return new GlobalVariable(name, init.type().pointerTo());
  }

  /**
   * @param name may be null
   * @param linkage may be null
   */
  public GlobalVariable global(String name, IType type, Linkage linkage,
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
    write(" global %s\n", type.ir());
    globalNames.add(name);
    return new GlobalVariable(name, type.pointerTo());
  }

  /**
   * @param retType may be null
   * @param argTypes may be null
   * @param argNames may be null
   * @param attrs may be null
   */
  public LocalVariable[] beginFunction(String name, FunctionType funcType,
      List<String> argNames, List<AttrKind> attrs) {
    assert name != null;
    assert funcType != null;
    int narg = funcType.getArgTypes().size();
    if (argNames != null) {
      assert narg == argNames.size();
    } else {
      argNames = new ArrayList<String>();
      for (Iterator<IType> it = funcType.getArgTypes().iterator(); it
          .hasNext();) {
        argNames.add(getLocalConstantCounter());
      }
    }

    setActiveBuffer(funcDefBuffer);

    write("define %s @%s", funcType.getRetType().ir(), name);
    write("(");
    LocalVariable[] vars = new LocalVariable[narg];
    for (int i = 0; i < narg; i++) {
      IType argType = funcType.getArgTypes().get(i);
      String argName = argNames.get(i);
      write("%s %%%s", argType.ir(), argName);
      if (i != funcType.getArgTypes().size() - 1) {
        write(", ");
      }
      vars[i] = new LocalVariable(argName, argType);
//      localTypes.put(argName, argType);
    }
    if (funcType.isVarArg()) {
      write(", ...");
    }
    write(")");
    if (attrs != null) {
      for (AttrKind attrKind : attrs) {
        write(" %s", attrKind.kind());
      }
    }
    write(" {\n");
    return vars;
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
   */
  public GlobalVariable functionDecl(String name, FunctionType funcType,
      AttrKind... attrs) {
    assert name != null;
    assert funcType != null;

    setActiveBuffer(funcDeclBuffer);

    write("declare %s @%s", funcType.getRetType().ir(), name);
    write("(");
    for (int i = 0; i < funcType.getArgTypes().size(); i++) {
      IType argType = funcType.getArgTypes().get(i);
      write("%s", argType.ir());
      if (i != funcType.getArgTypes().size() - 1) {
        write(", ");
      }
    }
    if (funcType.isVarArg()) {
      write(", ...");
    }
    write(")");
    if (attrs != null) {
      for (AttrKind attrKind : attrs) {
        write(" %s", attrKind.kind());
      }
    }
    write("\n\n");
    return new GlobalVariable(name, funcType.pointerTo());
  }

  public LocalVariable call(IValue funcVar, List<IValue> argVals,
      String varName) {
    assert funcVar != null;
    assert argVals != null;
    if (varName == null) {
      varName = getLocalConstantCounter();
    }

    setActiveBuffer(funcDefBuffer);

    indent("%%%s = call %s(", varName, funcVar.ir());
    for (int i = 0; i < argVals.size(); i++) {
      IValue val = argVals.get(i);
      write("%s", val.ir());
      if (i != argVals.size() - 1) {
        write(", ");
      }
    }
    write(")\n");
    return new LocalVariable(varName, funcVar.type());
  }

  public IRBuilder call(IValue funcVar, List<IValue> argVals) {
    assert funcVar != null;
    assert argVals != null;

    setActiveBuffer(funcDefBuffer);

    indent("call %s(", funcVar.ir());
    for (int i = 0; i < argVals.size(); i++) {
      IValue val = argVals.get(i);
      write("%s", val.ir());
      if (i != argVals.size() - 1) {
        write(", ");
      }
    }
    write(")\n");
    return this;
  }

  /**
   * @param name may be null
   * @param num may be null
   */
  public LocalVariable alloca(IType type, String name, Integer num) {
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
    return new LocalVariable(name, type.pointerTo());
  }

  public IRBuilder store(String assignee, IValue val) {
    assert val != null;
    assert assignee != null;

    setActiveBuffer(funcDefBuffer);

    indent("store %s, %s %%%s\n", val.ir(), val.type().pointerTo().ir(),
        assignee);
    return this;
  }

  public IRBuilder store(LocalVariable assignee, IValue val) {
    assert val != null;
    assert assignee != null;

    setActiveBuffer(funcDefBuffer);

    indent("store %s, %s\n", val.ir(), assignee.ir());
    return this;
  }

  public LocalVariable load(IValue var, String name) {
    assert var != null;
    if (name == null) {
      name = getLocalConstantCounter();
    }

    setActiveBuffer(funcDefBuffer);

    indent("%%%s = load %s", name, var.ir());
    write("\n");
    if (var.type() instanceof PointerType) {
      return new LocalVariable(name, ((PointerType) var.type()).pointsToType());
    } else {
      return new LocalVariable(name, var.type());
    }
  }

  public LocalVariable bitcast(IValue var, IType type, String name) {
    assert var != null;
    if (name == null) {
      name = getLocalConstantCounter();
    }

    setActiveBuffer(funcDefBuffer);

    indent("%%%s = bitcast %s to %s\n", name, var.ir(), type.ir());
    return new LocalVariable(name, type);
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

}
