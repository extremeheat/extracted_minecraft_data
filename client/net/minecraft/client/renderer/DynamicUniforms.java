package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class DynamicUniforms implements AutoCloseable {
   public static final int TRANSFORM_UBO_SIZE = (new Std140SizeCalculator()).putMat4f().putVec4().putVec3().putMat4f().get();
   public static final int CHUNK_SECTION_UBO_SIZE = (new Std140SizeCalculator()).putMat4f().putFloat().putIVec2().putIVec3().get();
   private static final int INITIAL_CAPACITY = 2;
   private final DynamicUniformStorage<Transform> transforms;
   private final DynamicUniformStorage<ChunkSectionInfo> chunkSections;

   public DynamicUniforms() {
      super();
      this.transforms = new DynamicUniformStorage<Transform>("Dynamic Transforms UBO", TRANSFORM_UBO_SIZE, 2);
      this.chunkSections = new DynamicUniformStorage<ChunkSectionInfo>("Chunk Sections UBO", CHUNK_SECTION_UBO_SIZE, 2);
   }

   public void reset() {
      this.transforms.endFrame();
      this.chunkSections.endFrame();
   }

   public void close() {
      this.transforms.close();
      this.chunkSections.close();
   }

   public GpuBufferSlice writeTransform(Matrix4fc var1, Vector4fc var2, Vector3fc var3, Matrix4fc var4) {
      return this.transforms.writeUniform(new Transform(new Matrix4f(var1), new Vector4f(var2), new Vector3f(var3), new Matrix4f(var4)));
   }

   public GpuBufferSlice[] writeTransforms(Transform... var1) {
      return this.transforms.writeUniforms(var1);
   }

   public GpuBufferSlice[] writeChunkSections(ChunkSectionInfo... var1) {
      return this.chunkSections.writeUniforms(var1);
   }

   public static record Transform(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix) implements DynamicUniformStorage.DynamicUniform {
      public Transform(Matrix4fc var1, Vector4fc var2, Vector3fc var3, Matrix4fc var4) {
         super();
         this.modelView = var1;
         this.colorModulator = var2;
         this.modelOffset = var3;
         this.textureMatrix = var4;
      }

      public void write(ByteBuffer var1) {
         Std140Builder.intoBuffer(var1).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix);
      }
   }

   public static record ChunkSectionInfo(Matrix4fc modelView, int x, int y, int z, float visibility, int textureAtlasWidth, int textureAtlasHeight) implements DynamicUniformStorage.DynamicUniform {
      public ChunkSectionInfo(Matrix4fc var1, int var2, int var3, int var4, float var5, int var6, int var7) {
         super();
         this.modelView = var1;
         this.x = var2;
         this.y = var3;
         this.z = var4;
         this.visibility = var5;
         this.textureAtlasWidth = var6;
         this.textureAtlasHeight = var7;
      }

      public void write(ByteBuffer var1) {
         Std140Builder.intoBuffer(var1).putMat4f(this.modelView).putFloat(this.visibility).putIVec2(this.textureAtlasWidth, this.textureAtlasHeight).putIVec3(this.x, this.y, this.z);
      }
   }
}
