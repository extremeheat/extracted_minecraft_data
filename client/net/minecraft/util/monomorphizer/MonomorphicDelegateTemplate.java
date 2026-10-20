package net.minecraft.util.monomorphizer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassHierarchyResolver;
import java.lang.classfile.ClassModel;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.CodeElement;
import java.lang.classfile.Instruction;
import java.lang.classfile.MethodModel;
import java.lang.classfile.MethodTransform;
import java.lang.classfile.Opcode;
import java.lang.classfile.TypeKind;
import java.lang.classfile.ClassFile.ClassHierarchyResolverOption;
import java.lang.classfile.constantpool.ConstantDynamicEntry;
import java.lang.classfile.constantpool.ConstantPoolBuilder;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.classfile.instruction.LoadInstruction;
import java.lang.classfile.instruction.LocalVariable;
import java.lang.classfile.instruction.LocalVariableType;
import java.lang.classfile.instruction.StoreInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.DynamicConstantDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup.ClassOption;
import java.lang.reflect.AccessFlag;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;

record MonomorphicDelegateTemplate<Target>(byte[] classBytes) {
   private static final MethodType CONSTRUCTOR_TYPE;

   MonomorphicDelegateTemplate {
      super();
   }

   public static <Target> MonomorphicDelegateTemplate<Target> createTemplate(final Class<Target> targetClass) {
      validateTargetClass(targetClass);
      ClassModel targetClassModel = ClassFile.of().parse(loadClassBytes(targetClass));
      validateTargetClassModel(targetClassModel);
      byte[] transformedClassBytes = transformClassToDelegate(targetClassModel, targetClass.getClassLoader());
      if (SharedConstants.DEBUG_DUMP_RUNTIME_MONOMORPHIC_CLASSES) {
         try {
            Path path = Path.of("monomorphizer", targetClass.getName() + ".class");
            Files.createDirectories(path.getParent());
            Files.write(path, transformedClassBytes, new OpenOption[0]);
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

      return new MonomorphicDelegateTemplate<Target>(transformedClassBytes);
   }

   private static void validateTargetClass(final Class<?> targetClass) {
      if (targetClass.isHidden()) {
         throw new IllegalArgumentException(String.valueOf(targetClass) + " may not be hidden");
      } else if (!targetClass.isRecord()) {
         throw new IllegalArgumentException(String.valueOf(targetClass) + " must be a record");
      } else {
         Stream<Class<?>> innerClasses = Arrays.stream(targetClass.getNestMembers()).filter((nestMember) -> targetClass.equals(nestMember.getEnclosingClass()));
         List<Class<?>> instanceInnerClasses = innerClasses.filter((innerClass) -> (innerClass.getModifiers() & 8) == 0).toList();
         if (!instanceInnerClasses.isEmpty()) {
            String var10002 = String.valueOf(targetClass);
            throw new IllegalArgumentException(var10002 + " has non-static inner classes: " + String.valueOf(instanceInnerClasses));
         }
      }
   }

   private static void validateTargetClassModel(final ClassModel model) {
      for(MethodModel method : model.methods()) {
         boolean hasSuperInvoke = hasAnyInstructionMatching(method, (instruction) -> {
            boolean var10000;
            if (instruction instanceof InvokeInstruction invoke) {
               if (invoke.opcode() == Opcode.INVOKESPECIAL && !invoke.method().name().equalsString("<init>")) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         });
         if (hasSuperInvoke) {
            throw new IllegalArgumentException(model.thisClass().asInternalName() + " may not use super. calls");
         }
      }

   }

   private static boolean hasAnyInstructionMatching(final MethodModel method, final Predicate<Instruction> predicate) {
      return (Boolean)method.code().map((code) -> code.elementStream().anyMatch((element) -> {
            boolean var10000;
            if (element instanceof Instruction instruction) {
               if (predicate.test(instruction)) {
                  var10000 = true;
                  return var10000;
               }
            }

            var10000 = false;
            return var10000;
         })).orElse(false);
   }

   private static byte[] loadClassBytes(final Class<?> clazz) {
      String var10000 = clazz.getName();
      String path = "/" + var10000.replace('.', '/') + ".class";

      try {
         InputStream input = clazz.getResourceAsStream(path);

         byte[] var3;
         try {
            if (input == null) {
               String var10002 = String.valueOf(clazz);
               throw new IllegalArgumentException("Could not find resource for class " + var10002 + " at " + path);
            }

            var3 = input.readAllBytes();
         } catch (Throwable var6) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (input != null) {
            input.close();
         }

         return var3;
      } catch (IOException e) {
         throw new IllegalArgumentException("Could not load bytes for class " + String.valueOf(clazz) + " at " + path, e);
      }
   }

   private static byte[] transformClassToDelegate(final ClassModel targetClassModel, final ClassLoader targetClassLoader) {
      ClassDesc targetClassDesc = targetClassModel.thisClass().asSymbol();
      ClassDesc thisClassDesc = ClassDesc.of(targetClassDesc.packageName(), "$MonomorphicDelegate$" + targetClassDesc.displayName());
      ConstantPoolBuilder constantPool = ConstantPoolBuilder.of(targetClassModel);
      ConstantDynamicEntry targetInstanceCondy = constantPool.constantDynamicEntry(DynamicConstantDesc.ofNamed(ConstantDescs.BSM_CLASS_DATA, "_", targetClassDesc));
      DelegateClassTransform transform = new DelegateClassTransform(thisClassDesc, targetClassDesc, targetInstanceCondy);
      ClassHierarchyResolver classHierarchyResolver = ClassHierarchyResolver.ofClassLoading(targetClassLoader);
      return ClassFile.of().withOptions(ClassHierarchyResolverOption.of(classHierarchyResolver)).transformClass(targetClassModel, thisClassDesc, transform);
   }

   public GeneratedDelegate create(final MethodHandles.Lookup lookup, final Target target) {
      try {
         MethodHandles.Lookup newLookup = lookup.defineHiddenClassWithClassData(this.classBytes, target, true, new MethodHandles.Lookup.ClassOption[]{ClassOption.NESTMATE});
         return newLookup.findConstructor(newLookup.lookupClass(), CONSTRUCTOR_TYPE).invoke();
      } catch (Throwable e) {
         throw new IllegalStateException("Failed to create monomorphic delegate for: " + String.valueOf(target), e);
      }
   }

   static {
      CONSTRUCTOR_TYPE = MethodType.methodType(Void.TYPE);
   }

   private static final class DelegateClassTransform implements ClassTransform {
      private static final ClassDesc CD_GENERATED_DELEGATE = (ClassDesc)GeneratedDelegate.class.describeConstable().orElseThrow();
      private final ClassDesc thisClassDesc;
      private final ClassDesc targetClassDesc;
      private final ConstantDynamicEntry targetInstanceCondy;
      private final Set<DispatcherStub> usedDispatcherStubs = new HashSet();

      public DelegateClassTransform(final ClassDesc thisClassDesc, final ClassDesc targetClassDesc, final ConstantDynamicEntry targetInstanceCondy) {
         super();
         this.thisClassDesc = thisClassDesc;
         this.targetClassDesc = targetClassDesc;
         this.targetInstanceCondy = targetInstanceCondy;
      }

      public void accept(final ClassBuilder builder, final ClassElement element) {
         Objects.requireNonNull(element);
         byte var4 = 0;
         //$FF: var4->value
         //0->java/lang/classfile/attribute/RecordAttribute
         //1->java/lang/classfile/Superclass
         //2->java/lang/classfile/FieldModel
         //3->java/lang/classfile/MethodModel
         switch (element.typeSwitch<invokedynamic>(element, var4)) {
            case 0:
            case 2:
               break;
            case 1:
               builder.withSuperclass(CD_GENERATED_DELEGATE);
               break;
            case 3:
               MethodModel method = (MethodModel)element;
               this.acceptMethod(builder, method);
               break;
            default:
               builder.with(element);
         }

      }

      private void acceptMethod(final ClassBuilder builder, final MethodModel method) {
         String methodName = method.methodName().stringValue();
         MethodTypeDesc methodDesc = method.methodTypeSymbol();
         if (!methodName.equals("<init>") && !methodName.equals("<clinit>")) {
            if (method.flags().has(AccessFlag.STATIC)) {
               builder.with(method);
            } else if (!methodName.equals("equals") || methodDesc.parameterCount() != 1 || !methodDesc.parameterType(0).equals(ConstantDescs.CD_Object)) {
               if (!methodName.equals("hashCode") || methodDesc.parameterCount() != 0) {
                  if (!methodName.equals("toString") || methodDesc.parameterCount() != 0) {
                     builder.transformMethod(method, MethodTransform.transformingCode(this::transformCodeElement));
                  }
               }
            }
         }
      }

      private void transformCodeElement(final CodeBuilder code, final CodeElement element) {
         Objects.requireNonNull(element);
         CodeElement var3 = element;
         byte var4 = 0;

         while(true) {
            //$FF: var4->value
            //0->java/lang/classfile/instruction/LoadInstruction
            //1->java/lang/classfile/instruction/StoreInstruction
            //2->java/lang/classfile/instruction/InvokeInstruction
            //3->java/lang/classfile/instruction/LocalVariable
            //4->java/lang/classfile/instruction/LocalVariableType
            switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
               case 0:
                  LoadInstruction load = (LoadInstruction)var3;
                  if (load.slot() != code.receiverSlot()) {
                     var4 = 1;
                     break;
                  }

                  code.ldc(this.targetInstanceCondy);
                  return;
               case 1:
                  StoreInstruction store = (StoreInstruction)var3;
                  if (store.slot() == code.receiverSlot()) {
                     throw new IllegalStateException("Encountered store to receiver slot (how?): " + String.valueOf(store));
                  }

                  var4 = 2;
                  break;
               case 2:
                  InvokeInstruction invoke = (InvokeInstruction)var3;
                  if (!invoke.owner().matches(this.targetClassDesc)) {
                     var4 = 3;
                     break;
                  }

                  this.transformInvokeOnThisType(code, invoke);
                  return;
               case 3:
               case 4:
                  if (var3 instanceof LocalVariable || var3 instanceof LocalVariableType) {
                     return;
                  }

                  var4 = 5;
                  break;
               default:
                  code.with(element);
                  return;
            }
         }
      }

      private void transformInvokeOnThisType(final CodeBuilder code, final InvokeInstruction invoke) {
         String methodName = invoke.name().stringValue();
         MethodTypeDesc methodDesc = invoke.typeSymbol();
         if (invoke.opcode() == Opcode.INVOKESTATIC) {
            code.invokestatic(this.thisClassDesc, methodName, methodDesc);
         } else if (methodName.equals("<init>")) {
            code.with(invoke);
         } else {
            this.invokeDispatcher(code, methodName, methodDesc);
         }
      }

      private void invokeDispatcher(final CodeBuilder code, final String methodName, final MethodTypeDesc methodDesc) {
         DispatcherStub stub = new DispatcherStub(methodName, methodDesc);
         this.usedDispatcherStubs.add(stub);
         code.aload(code.receiverSlot());
         code.invokestatic(this.thisClassDesc, stub.dispatcherName(), stub.dispatcherDesc(this.targetClassDesc));
      }

      public void atEnd(final ClassBuilder builder) {
         for(DispatcherStub stub : this.usedDispatcherStubs) {
            this.buildDispatcher(builder, stub);
         }

         builder.withMethodBody("<init>", MethodTypeDesc.of(ConstantDescs.CD_void), AccessFlag.PUBLIC.mask(), (code) -> {
            code.aload(code.receiverSlot());
            code.ldc(this.targetInstanceCondy);
            code.invokespecial(CD_GENERATED_DELEGATE, "<init>", MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_Object));
            code.return_();
         });
      }

      private void buildDispatcher(final ClassBuilder builder, final DispatcherStub stub) {
         String targetName = stub.targetName();
         MethodTypeDesc targetDesc = stub.targetDesc();
         MethodTypeDesc dispatcherDesc = stub.dispatcherDesc(this.targetClassDesc);
         builder.withMethodBody(stub.dispatcherName(), dispatcherDesc, AccessFlag.PRIVATE.mask() | AccessFlag.STATIC.mask(), (code) -> {
            code.aload(code.parameterSlot(0));
            code.ldc(this.targetInstanceCondy);
            code.ifThenElse(Opcode.IF_ACMPEQ, (ifCode) -> {
               ifCode.aload(ifCode.parameterSlot(dispatcherDesc.parameterCount() - 1));
               ifCode.checkcast(this.thisClassDesc);
               loadParameters(ifCode, 1, targetDesc);
               ifCode.invoke(Opcode.INVOKEVIRTUAL, this.thisClassDesc, targetName, targetDesc, false);
            }, (elseCode) -> {
               elseCode.aload(elseCode.parameterSlot(0));
               loadParameters(elseCode, 1, targetDesc);
               elseCode.invoke(Opcode.INVOKEVIRTUAL, this.targetClassDesc, targetName, targetDesc, false);
            });
            code.return_(asTypeKind(targetDesc.returnType()));
         });
      }

      private static void loadParameters(final CodeBuilder code, final int baseIndex, final MethodTypeDesc targetDesc) {
         for(int i = 0; i < targetDesc.parameterCount(); ++i) {
            ClassDesc parameterDesc = targetDesc.parameterType(i);
            code.loadLocal(asTypeKind(parameterDesc), code.parameterSlot(i + baseIndex));
         }

      }

      private static TypeKind asTypeKind(final ClassDesc typeDesc) {
         if (!typeDesc.isClassOrInterface() && !typeDesc.isArray()) {
            if (typeDesc == ConstantDescs.CD_boolean) {
               return TypeKind.BOOLEAN;
            } else if (typeDesc == ConstantDescs.CD_byte) {
               return TypeKind.BYTE;
            } else if (typeDesc == ConstantDescs.CD_char) {
               return TypeKind.CHAR;
            } else if (typeDesc == ConstantDescs.CD_short) {
               return TypeKind.SHORT;
            } else if (typeDesc == ConstantDescs.CD_int) {
               return TypeKind.INT;
            } else if (typeDesc == ConstantDescs.CD_long) {
               return TypeKind.LONG;
            } else if (typeDesc == ConstantDescs.CD_float) {
               return TypeKind.FLOAT;
            } else if (typeDesc == ConstantDescs.CD_double) {
               return TypeKind.DOUBLE;
            } else if (typeDesc == ConstantDescs.CD_void) {
               return TypeKind.VOID;
            } else {
               throw new UnsupportedOperationException("Unsupported type: " + String.valueOf(typeDesc));
            }
         } else {
            return TypeKind.REFERENCE;
         }
      }

      private static record DispatcherStub(String targetName, MethodTypeDesc targetDesc) {
         private DispatcherStub {
            super();
         }

         public String dispatcherName() {
            return "dispatcher$" + this.targetName;
         }

         public MethodTypeDesc dispatcherDesc(final ClassDesc targetClassDesc) {
            return this.targetDesc.insertParameterTypes(this.targetDesc.parameterCount(), new ClassDesc[]{ConstantDescs.CD_Object}).insertParameterTypes(0, new ClassDesc[]{targetClassDesc});
         }
      }
   }
}
