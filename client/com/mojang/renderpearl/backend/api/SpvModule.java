package com.mojang.renderpearl.backend.api;

import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.util.ShaderCompileException;
import java.nio.ByteBuffer;
import java.util.List;
import org.jspecify.annotations.Nullable;

public interface SpvModule extends AutoCloseable {
   void close();

   ByteBuffer spv();

   ShaderType type();

   Reflection reflect() throws ShaderCompileException;

   @Nullable Reflection getReflectionInfoIfAvailable();

   public interface Reflection {
      List<InterfaceVariable> inputs();

      List<InterfaceVariable> outputs();

      List<Descriptor> descriptors(int resourceType);

      List<Descriptor> descriptors();

      List<PushConstant> pushConstants();

      public interface InterfaceVariable {
         String name();

         Type type();

         int location();

         void location(int location);

         default boolean hasDecoration(final int decoration) {
            return this.decoration(decoration) != 0;
         }

         int decoration(int decoration);
      }

      public interface Descriptor {
         String name();

         Type type();

         int resourceType();

         int descriptorSetIndex();

         void descriptorSetIndex(int index);

         int binding();

         void binding(int location);
      }

      public interface PushConstant {
         int size();
      }

      public interface Type {
         int baseType();

         int dimensions();

         int vectorSize();

         int arrayDimensions();

         int arrayLength(int dimensionIndex);
      }
   }
}
