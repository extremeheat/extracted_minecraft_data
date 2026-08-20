package com.mojang.renderpearl.frontend.shaders;

import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.backend.api.SpvModule;
import com.mojang.renderpearl.util.ShaderCompileException;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.spvc.Spvc;
import org.lwjgl.util.spvc.SpvcReflectedResource;

public class SPIRVModule implements SpvModule {
   private final ByteBuffer spv;
   private final ShaderType type;
   private SpvModule.@Nullable Reflection reflection;
   private long spvcContext;

   public SPIRVModule(final ByteBuffer spv, final ShaderType type) {
      super();
      this.spv = spv;
      this.type = type;
   }

   public void close() {
      MemoryUtil.memFree(this.spv);
      if (this.spvcContext != 0L) {
         Spvc.spvc_context_destroy(this.spvcContext);
      }

   }

   public ByteBuffer spv() {
      return this.spv;
   }

   public ShaderType type() {
      return this.type;
   }

   public SpvModule.Reflection reflect() throws ShaderCompileException {
      if (this.reflection == null) {
         this.doReflection();
      }

      return this.reflection;
   }

   public SpvModule.@Nullable Reflection getReflectionInfoIfAvailable() {
      return this.reflection;
   }

   private void doReflection() throws ShaderCompileException {
      IntBuffer spirvBuffer = this.spv.asIntBuffer();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         PointerBuffer pointer = stack.callocPointer(1);
         SpvUtil.throwIfError(Spvc.spvc_context_create(pointer), "Couldn't create spvc context");
         this.spvcContext = pointer.get(0);
      } catch (Throwable var17) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var16) {
               var17.addSuppressed(var16);
            }
         }

         throw var17;
      }

      if (stack != null) {
         stack.close();
      }

      stack = MemoryStack.stackPush();

      try {
         PointerBuffer pointerReturnBuffer = stack.callocPointer(1);
         SpvUtil.throwIfError(Spvc.spvc_context_parse_spirv(this.spvcContext, spirvBuffer, (long)spirvBuffer.remaining(), pointerReturnBuffer), "Couldn't parse spirv");
         long ir = pointerReturnBuffer.get(0);
         SpvUtil.throwIfError(Spvc.spvc_context_create_compiler(this.spvcContext, 0, ir, 0, pointerReturnBuffer), "Couldn't create compiler");
         long compiler = pointerReturnBuffer.get(0);
         SpvUtil.throwIfError(Spvc.spvc_compiler_create_shader_resources(compiler, pointerReturnBuffer), "Couldn't create resource list");
         long spvcResources = pointerReturnBuffer.get(0);
         final List<SpvModule.Reflection.InterfaceVariable> inputs = generateInterfaceVariableList(compiler, spvcResources, spirvBuffer, 3);
         final List<SpvModule.Reflection.InterfaceVariable> outputs = generateInterfaceVariableList(compiler, spvcResources, spirvBuffer, 4);
         final Int2ReferenceMap<List<SpvModule.Reflection.Descriptor>> descriptors = new Int2ReferenceArrayMap();

         for(int i = 0; i < SpvUtil.DESCRIPTOR_TYPES.size(); ++i) {
            int descriptorType = SpvUtil.DESCRIPTOR_TYPES.getInt(i);
            descriptors.put(descriptorType, generateDescriptorList(compiler, spvcResources, spirvBuffer, descriptorType));
         }

         final List<SpvModule.Reflection.Descriptor> allDescriptors = descriptors.values().stream().flatMap(Collection::stream).toList();
         final List<SpvModule.Reflection.PushConstant> pushConstants = generatePushConstantList(compiler, spvcResources);
         this.reflection = new SpvModule.Reflection() {
            {
               Objects.requireNonNull(SPIRVModule.this);
            }

            public List<SpvModule.Reflection.InterfaceVariable> inputs() {
               return inputs;
            }

            public List<SpvModule.Reflection.InterfaceVariable> outputs() {
               return outputs;
            }

            public List<SpvModule.Reflection.Descriptor> descriptors(final int descriptorType) {
               return (List)descriptors.getOrDefault(descriptorType, List.of());
            }

            public List<SpvModule.Reflection.Descriptor> descriptors() {
               return allDescriptors;
            }

            public List<SpvModule.Reflection.PushConstant> pushConstants() {
               return pushConstants;
            }
         };
      } catch (Throwable var18) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var15) {
               var18.addSuppressed(var15);
            }
         }

         throw var18;
      }

      if (stack != null) {
         stack.close();
      }

   }

   private static List<SpvModule.Reflection.InterfaceVariable> generateInterfaceVariableList(final long compiler, final long spvcResources, final IntBuffer spirv, final int resourceType) throws ShaderCompileException {
      ReferenceArrayList<SpvModule.Reflection.InterfaceVariable> list = new ReferenceArrayList();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         PointerBuffer pointerReturnBuffer = stack.callocPointer(1);
         IntBuffer intReturnBuffer = stack.callocInt(1);
         PointerBuffer countPointer = stack.callocPointer(1);
         SpvUtil.throwIfError(Spvc.spvc_resources_get_resource_list_for_type(spvcResources, resourceType, pointerReturnBuffer, countPointer), "Couldn't list input variables");
         long spvcList = pointerReturnBuffer.get(0);
         long spvcCount = countPointer.get(0);
         SpvcReflectedResource.Buffer resources = SpvcReflectedResource.create(spvcList, (int)spvcCount);

         for(int i = 0; (long)i < spvcCount; ++i) {
            final SpvcReflectedResource resource = (SpvcReflectedResource)resources.get(i);
            final String name = resource.nameString();
            if (!Spvc.spvc_compiler_get_binary_offset_for_decoration(compiler, resource.id(), 30, intReturnBuffer)) {
               throw new ShaderCompileException("Couldn't find byte offset for location decoration of " + name);
            }

            final int locationOffset = intReturnBuffer.get(0);
            final TypeHandle typeHandle = new TypeHandle(Spvc.spvc_compiler_get_type_handle(compiler, resource.type_id()));
            list.add(new SpvModule.Reflection.InterfaceVariable() {
               public String name() {
                  return name;
               }

               public SpvModule.Reflection.Type type() {
                  return typeHandle;
               }

               public int location() {
                  return spirv.get(locationOffset);
               }

               public void location(final int location) {
                  spirv.put(locationOffset, location);
               }

               public int decoration(final int decoration) {
                  return Spvc.spvc_compiler_get_decoration(compiler, resource.id(), decoration);
               }
            });
         }
      } catch (Throwable var22) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var21) {
               var22.addSuppressed(var21);
            }
         }

         throw var22;
      }

      if (stack != null) {
         stack.close();
      }

      return ReferenceLists.unmodifiable(list);
   }

   private static List<SpvModule.Reflection.Descriptor> generateDescriptorList(final long compiler, final long spvcResources, final IntBuffer spirv, final int resourceType) throws ShaderCompileException {
      ReferenceArrayList<SpvModule.Reflection.Descriptor> list = new ReferenceArrayList();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         PointerBuffer pointerReturnBuffer = stack.callocPointer(1);
         IntBuffer intReturnBuffer = stack.callocInt(1);
         PointerBuffer countPointer = stack.callocPointer(1);
         SpvUtil.throwIfError(Spvc.spvc_resources_get_resource_list_for_type(spvcResources, resourceType, pointerReturnBuffer, countPointer), "Couldn't list input variables");
         long spvcList = pointerReturnBuffer.get(0);
         long spvcCount = countPointer.get(0);
         SpvcReflectedResource.Buffer resources = SpvcReflectedResource.create(spvcList, (int)spvcCount);

         for(int i = 0; (long)i < spvcCount; ++i) {
            SpvcReflectedResource resource = (SpvcReflectedResource)resources.get(i);
            final String name = resource.nameString();
            if (!Spvc.spvc_compiler_get_binary_offset_for_decoration(compiler, resource.id(), 34, intReturnBuffer)) {
               throw new ShaderCompileException("Couldn't find byte offset for set decoration of " + name);
            }

            final int setOffset = intReturnBuffer.get(0);
            if (!Spvc.spvc_compiler_get_binary_offset_for_decoration(compiler, resource.id(), 33, intReturnBuffer)) {
               throw new ShaderCompileException("Couldn't find byte offset for binding decoration of " + name);
            }

            final TypeHandle typeHandle = new TypeHandle(Spvc.spvc_compiler_get_type_handle(compiler, resource.type_id()));
            final int bindingOffset = intReturnBuffer.get(0);
            list.add(new SpvModule.Reflection.Descriptor() {
               public String name() {
                  return name;
               }

               public SpvModule.Reflection.Type type() {
                  return typeHandle;
               }

               public int resourceType() {
                  return resourceType;
               }

               public int descriptorSetIndex() {
                  return spirv.get(setOffset);
               }

               public void descriptorSetIndex(final int index) {
                  spirv.put(setOffset, index);
               }

               public int binding() {
                  return spirv.get(bindingOffset);
               }

               public void binding(final int binding) {
                  spirv.put(bindingOffset, binding);
               }
            });
         }
      } catch (Throwable var23) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var22) {
               var23.addSuppressed(var22);
            }
         }

         throw var23;
      }

      if (stack != null) {
         stack.close();
      }

      return ReferenceLists.unmodifiable(list);
   }

   private static List<SpvModule.Reflection.PushConstant> generatePushConstantList(final long compiler, final long spvcResources) throws ShaderCompileException {
      ReferenceArrayList<SpvModule.Reflection.PushConstant> list = new ReferenceArrayList();
      MemoryStack stack = MemoryStack.stackPush();

      try {
         PointerBuffer pointerReturnBuffer = stack.callocPointer(1);
         PointerBuffer countPointer = stack.callocPointer(1);
         SpvUtil.throwIfError(Spvc.spvc_resources_get_resource_list_for_type(spvcResources, 9, pointerReturnBuffer, countPointer), "Couldn't list input variables");
         long spvcList = pointerReturnBuffer.get(0);
         long spvcCount = countPointer.get(0);
         SpvcReflectedResource.Buffer resources = SpvcReflectedResource.create(spvcList, (int)spvcCount);

         for(int i = 0; (long)i < spvcCount; ++i) {
            SpvcReflectedResource resource = (SpvcReflectedResource)resources.get(i);
            TypeHandle typeHandle = new TypeHandle(Spvc.spvc_compiler_get_type_handle(compiler, resource.type_id()));
            Spvc.spvc_compiler_get_declared_struct_size(compiler, typeHandle.typeHandle, pointerReturnBuffer);
            int structSize = Math.toIntExact(pointerReturnBuffer.get(0));
            list.add((SpvModule.Reflection.PushConstant)() -> structSize);
         }
      } catch (Throwable var18) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var17) {
               var18.addSuppressed(var17);
            }
         }

         throw var18;
      }

      if (stack != null) {
         stack.close();
      }

      return ReferenceLists.unmodifiable(list);
   }

   private static record TypeHandle(long typeHandle) implements SpvModule.Reflection.Type {
      private TypeHandle {
         super();
      }

      public int baseType() {
         return Spvc.spvc_type_get_basetype(this.typeHandle);
      }

      public int dimensions() {
         int baseType = this.baseType();
         return baseType != 16 && baseType != 17 ? 2147483647 : Spvc.spvc_type_get_image_dimension(this.typeHandle);
      }

      public int vectorSize() {
         return Spvc.spvc_type_get_vector_size(this.typeHandle);
      }

      public int arrayDimensions() {
         return Spvc.spvc_type_get_num_array_dimensions(this.typeHandle);
      }

      public int arrayLength(final int dimensionIndex) {
         return Spvc.spvc_type_get_array_dimension(this.typeHandle, dimensionIndex);
      }
   }
}
