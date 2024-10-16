package com.mojang.blaze3d.vertex;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import org.joml.Vector3f;

public class MeshData implements AutoCloseable {
   private final ByteBufferBuilder.Result vertexBuffer;
   @Nullable
   private ByteBufferBuilder.Result indexBuffer;
   private final MeshData.DrawState drawState;

   public MeshData(ByteBufferBuilder.Result var1, MeshData.DrawState var2) {
      super();
      this.vertexBuffer = var1;
      this.drawState = var2;
   }

   private static Vector3f[] unpackQuadCentroids(ByteBuffer var0, int var1, VertexFormat var2) {
      int var3 = var2.getOffset(VertexFormatElement.POSITION);
      if (var3 == -1) {
         throw new IllegalArgumentException("Cannot identify quad centers with no position element");
      } else {
         FloatBuffer var4 = var0.asFloatBuffer();
         int var5 = var2.getVertexSize() / 4;
         int var6 = var5 * 4;
         int var7 = var1 / 4;
         Vector3f[] var8 = new Vector3f[var7];

         for (int var9 = 0; var9 < var7; var9++) {
            int var10 = var9 * var6 + var3;
            int var11 = var10 + var5 * 2;
            float var12 = var4.get(var10 + 0);
            float var13 = var4.get(var10 + 1);
            float var14 = var4.get(var10 + 2);
            float var15 = var4.get(var11 + 0);
            float var16 = var4.get(var11 + 1);
            float var17 = var4.get(var11 + 2);
            var8[var9] = new Vector3f((var12 + var15) / 2.0F, (var13 + var16) / 2.0F, (var14 + var17) / 2.0F);
         }

         return var8;
      }
   }

   public ByteBuffer vertexBuffer() {
      return this.vertexBuffer.byteBuffer();
   }

   @Nullable
   public ByteBuffer indexBuffer() {
      return this.indexBuffer != null ? this.indexBuffer.byteBuffer() : null;
   }

   public MeshData.DrawState drawState() {
      return this.drawState;
   }

   @Nullable
   public MeshData.SortState sortQuads(ByteBufferBuilder var1, VertexSorting var2) {
      if (this.drawState.mode() != VertexFormat.Mode.QUADS) {
         return null;
      } else {
         Vector3f[] var3 = unpackQuadCentroids(this.vertexBuffer.byteBuffer(), this.drawState.vertexCount(), this.drawState.format());
         MeshData.SortState var4 = new MeshData.SortState(var3, this.drawState.indexType());
         this.indexBuffer = var4.buildSortedIndexBuffer(var1, var2);
         return var4;
      }
   }

   @Override
   public void close() {
      this.vertexBuffer.close();
      if (this.indexBuffer != null) {
         this.indexBuffer.close();
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
