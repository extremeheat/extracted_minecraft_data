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

   public GpuBufferSlice writeTransform(final Matrix4fc modelView, final Vector4fc colorModulator, final Vector3fc modelOffset, final Matrix4fc textureMatrix) {
      return this.transforms.writeUniform(new Transform(new Matrix4f(modelView), new Vector4f(colorModulator), new Vector3f(modelOffset), new Matrix4f(textureMatrix)));
   }

   public GpuBufferSlice[] writeTransforms(final Transform... transforms) {
      return this.transforms.writeUniforms(transforms);
   }

   public GpuBufferSlice[] writeChunkSections(final ChunkSectionInfo... infos) {
      return this.chunkSections.writeUniforms(infos);
   }

   public static record Transform(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix) implements DynamicUniformStorage.DynamicUniform {
      public Transform {
         super();
      }

      public void write(final ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix);
      }
   }

   public static record ChunkSectionInfo(Matrix4fc modelView, int x, int y, int z, float visibility, int textureAtlasWidth, int textureAtlasHeight) implements DynamicUniformStorage.DynamicUniform {
      public ChunkSectionInfo {
         super();
      }

      public void write(final ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putFloat(this.visibility).putIVec2(this.textureAtlasWidth, this.textureAtlasHeight).putIVec3(this.x, this.y, this.z);
      }
   }
}
