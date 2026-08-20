package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import java.nio.ByteBuffer;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public class DynamicGpuData implements AutoCloseable {
   private static final Vector4fc WHITE = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static final Vector3fc NO_OFFSET = new Vector3f();
   private static final Matrix4fc IDENTITY_TEXTURE_TRANSFORM = new Matrix4f();
   public static final int TRANSFORM_UBO_SIZE = (new Std140SizeCalculator()).putMat4f().putMat4f().putVec4().putVec3().get();
   public static final int TERRAIN_TRANSFORM_UBO_SIZE = (new Std140SizeCalculator()).putMat4f().putIVec2().get();
   public static final int CHUNK_SECTION_UBO_SIZE = (new Std140SizeCalculator()).putIVec3().putFloat().get();
   private static final int INITIAL_CAPACITY = 2;
   private final DynamicGpuDataStorage<Transform> transforms;
   private final DynamicGpuDataStorage<TerrainTransform> terrain;
   private @Nullable DynamicGpuDataStorage<ChunkSectionInfo> chunkSections = null;
   private @Nullable DynamicGpuDataStorage<IndexedDraw> chunkSectionsCommandBuffer = null;

   public DynamicGpuData() {
      super();
      this.transforms = new DynamicGpuDataStorage<Transform>("Dynamic Transforms UBO", TRANSFORM_UBO_SIZE, 128, 2);
      this.terrain = new DynamicGpuDataStorage<TerrainTransform>("Terrain UBO", TERRAIN_TRANSFORM_UBO_SIZE, 128, 1);
   }

   public void reset() {
      this.transforms.endFrame();
      this.terrain.endFrame();
      if (this.chunkSections != null) {
         this.chunkSections.endFrame();
      }

      if (this.chunkSectionsCommandBuffer != null) {
         this.chunkSectionsCommandBuffer.endFrame();
      }

   }

   public void close() {
      this.transforms.close();
      this.terrain.close();
      if (this.chunkSections != null) {
         this.chunkSections.close();
      }

      if (this.chunkSectionsCommandBuffer != null) {
         this.chunkSectionsCommandBuffer.close();
      }

   }

   public GpuBufferSlice writeTransform(final Matrix4f modelView) {
      return this.writeTransform(new Transform(modelView, WHITE, NO_OFFSET, IDENTITY_TEXTURE_TRANSFORM));
   }

   public GpuBufferSlice writeTransform(final Matrix4f modelView, final Vector4f colorModulator) {
      return this.writeTransform(new Transform(modelView, colorModulator, NO_OFFSET, IDENTITY_TEXTURE_TRANSFORM));
   }

   public GpuBufferSlice writeTransform(final Matrix4f modelView, final Matrix4f textureMatrix) {
      return this.writeTransform(new Transform(modelView, WHITE, NO_OFFSET, textureMatrix));
   }

   public GpuBufferSlice writeTransform(final Matrix4f modelView, final Vector4f colorModulator, final Vector3f modelOffset, final Matrix4f textureMatrix) {
      return this.writeTransform(new Transform(modelView, colorModulator, modelOffset, textureMatrix));
   }

   public GpuBufferSlice writeTransform(final Transform transform) {
      return this.transforms.writeData(transform);
   }

   public GpuBufferSlice[] writeTransforms(final Transform... transforms) {
      return this.transforms.writeData(transforms);
   }

   public GpuBufferSlice writeTerrainTransform(final Matrix4fc modelView, final int textureAtlasWidth, final int textureAtlasHeight) {
      return this.terrain.writeData(new TerrainTransform(modelView, textureAtlasWidth, textureAtlasHeight));
   }

   public GpuBufferSlice[] writeChunkSections(final ChunkSectionInfo... infos) {
      if (this.chunkSections != null && (this.chunkSections.usage() & 128) == 0) {
         this.chunkSections.close();
         this.chunkSections = null;
      }

      if (this.chunkSections == null) {
         this.chunkSections = new DynamicGpuDataStorage<ChunkSectionInfo>("Chunk Sections UBO", CHUNK_SECTION_UBO_SIZE, 128, 2);
      }

      return this.chunkSections.writeData(infos);
   }

   public GpuBufferSlice writeChunkSectionsInstanced(final List<ChunkSectionInfo> infos) {
      if (this.chunkSections != null && (this.chunkSections.usage() & 32) == 0) {
         this.chunkSections.close();
         this.chunkSections = null;
      }

      if (this.chunkSections == null) {
         this.chunkSections = new DynamicGpuDataStorage<ChunkSectionInfo>("Chunk Sections Instanced", CHUNK_SECTION_UBO_SIZE, 32, 2);
      }

      return this.chunkSections.writeDataBatched(infos);
   }

   public GpuBufferSlice[] writeChunkSectionCommands(final List<List<IndexedDraw>> draws) {
      if (this.chunkSectionsCommandBuffer == null) {
         this.chunkSectionsCommandBuffer = new DynamicGpuDataStorage<IndexedDraw>("Chunk Sections Command Buffer", 20, 512, 2);
      }

      return this.chunkSectionsCommandBuffer.writeDataBatchedMultiple(draws);
   }

   public static record Transform(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix) implements DynamicGpuDataStorage.DynamicGpuData {
      public Transform {
         super();
      }

      public void write(final ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putMat4f(this.textureMatrix).putVec4(this.colorModulator).putVec3(this.modelOffset);
      }
   }

   public static record TerrainTransform(Matrix4fc modelView, int textureAtlasWidth, int textureAtlasHeight) implements DynamicGpuDataStorage.DynamicGpuData {
      public TerrainTransform {
         super();
      }

      public void write(final ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putMat4f(this.modelView).putIVec2(this.textureAtlasWidth, this.textureAtlasHeight);
      }
   }

   public static record ChunkSectionInfo(int x, int y, int z, float visibility) implements DynamicGpuDataStorage.DynamicGpuData {
      public ChunkSectionInfo {
         super();
      }

      public void write(final ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putIVec3(this.x, this.y, this.z).putFloat(this.visibility);
      }
   }

   public static record IndexedDraw(int indexCount, int instanceCount, int firstIndex, int baseVertex, int baseInstance) implements DynamicGpuDataStorage.DynamicGpuData {
      public IndexedDraw {
         super();
      }

      public void write(final ByteBuffer buffer) {
         buffer.putInt(this.indexCount).putInt(this.instanceCount).putInt(this.firstIndex).putInt(this.baseVertex).putInt(this.baseInstance);
      }
   }
}
