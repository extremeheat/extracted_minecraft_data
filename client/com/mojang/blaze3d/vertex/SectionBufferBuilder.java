package com.mojang.blaze3d.vertex;

import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.ARGB;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

public class SectionBufferBuilder {
   private static final int MAX_VERTEX_COUNT = 16777215;
   private static final int NO_QUAD = -1;
   private final ByteBufferBuilder buffer;
   private int vertices;
   private final ChunkSectionLayer layer;
   private final PrimitiveTopology primitiveTopology;
   private final Object2IntMap<BakedQuad> bakedQuadCache = new Object2IntOpenHashMap();
   private final ArrayList<BakedQuad> bakedQuads = new ArrayList();
   private boolean building = true;

   public SectionBufferBuilder(final ByteBufferBuilder buffer, final PrimitiveTopology primitiveTopology, final ChunkSectionLayer layer) {
      super();
      this.buffer = buffer;
      this.primitiveTopology = primitiveTopology;
      this.layer = layer;
      this.bakedQuadCache.defaultReturnValue(-1);
   }

   public @Nullable MeshData build() {
      this.ensureBuilding();

      for(BakedQuad bakedQuad : this.bakedQuads) {
         this.writeBakedQuad(bakedQuad);
      }

      MeshData mesh = this.storeMesh();
      this.building = false;
      return mesh;
   }

   private void ensureBuilding() {
      if (!this.building) {
         throw new IllegalStateException("Not building!");
      }
   }

   private @Nullable MeshData storeMesh() {
      if (this.vertices == 0) {
         return null;
      } else {
         ByteBufferBuilder.Result vertexBuffer = this.buffer.build();
         if (vertexBuffer == null) {
            return null;
         } else {
            int indices = this.primitiveTopology.indexCount(this.vertices);
            IndexType indexType = IndexType.least(this.vertices);
            return new MeshData(vertexBuffer, new MeshData.DrawState(this.vertices, indices, this.primitiveTopology, indexType));
         }
      }
   }

   private int getOrRegisterBakedQuad(final BakedQuad quad) {
      int newQuadIndex = this.bakedQuads.size();
      int existingQuadIndex = this.bakedQuadCache.putIfAbsent(quad, newQuadIndex);
      if (existingQuadIndex != -1) {
         return existingQuadIndex;
      } else {
         this.bakedQuads.add(quad);
         return newQuadIndex;
      }
   }

   public void addQuad(final float x, final float y, final float z, final BakedQuad quad, final QuadInstance instance) {
      int lightEmission = quad.materialInfo().lightEmission();
      int brightnessPacked = 0;
      long lightCoordsPacked = 0L;

      for(int vertex = 0; vertex < 4; ++vertex) {
         int light = instance.getLightCoordsWithEmission(vertex, lightEmission);
         long lightPackedRG8 = (long)(light & 255 | light >>> 8 & '\uff00');
         long lightCoordShiftVal = (long)(16 * vertex);
         lightCoordsPacked |= (lightPackedRG8 & 65535L) << (int)lightCoordShiftVal;
         int brightnessShiftVal = 8 * vertex;
         brightnessPacked |= (instance.getBrightness(vertex) & 255) << brightnessShiftVal;
      }

      this.addQuad(quad, x, y, z, instance.getColor(), brightnessPacked, lightCoordsPacked);
   }

   public void addFluidQuad(final float x, final float y, final float z, final BakedQuad quad, final int color, final int brightness, final int lightCoords) {
      int brightnessPacked = brightness << 24 | brightness << 16 | brightness << 8 | brightness;
      long lightPackedRG8 = (long)((lightCoords & 255 | lightCoords >>> 8 & '\uff00') & '\uffff');
      long lightCoordsPacked = lightPackedRG8 << 48 | lightPackedRG8 << 32 | lightPackedRG8 << 16 | lightPackedRG8;
      this.addQuad(quad, x, y, z, color, brightnessPacked, lightCoordsPacked);
   }

   private static int packPositionChannel(final float f) {
      return Math.round(f * 15.0F) + 7;
   }

   private void addQuad(final BakedQuad quad, final float x, final float y, final float z, final int color, final int brightnessPacked, final long lightCoordsPacked) {
      this.ensureBuilding();
      if (this.vertices >= 16777215) {
         throw new IllegalStateException("Trying to write too many vertices (>16777215) into BufferBuilder");
      } else {
         this.vertices += 4;
         int bakedQuadIndex = this.getOrRegisterBakedQuad(quad);
         long pointer = this.buffer.reserve(this.layer.vertexBufferFaceSize());
         int positionPacked = (packPositionChannel(z) & 255) << 16 | (packPositionChannel(y) & 255) << 8 | packPositionChannel(x) & 255;
         MemoryUtil.memPutInt(pointer, positionPacked);
         MemoryUtil.memPutShort(pointer + 3L, (short)bakedQuadIndex);
         int abgr = ARGB.toABGR(color);
         MemoryUtil.memPutInt(pointer + 5L, abgr);
         MemoryUtil.memPutInt(pointer + 8L, brightnessPacked);
         MemoryUtil.memPutLong(pointer + 12L, lightCoordsPacked);
      }
   }

   private void writeBakedQuad(final BakedQuad bakedQuad) {
      this.ensureBuilding();
      long pointer = this.buffer.reserve(this.layer.vertexBufferBakedFaceSize());
      int vertexBufferBakedFaceCornerSize = this.layer.vertexBufferBakedFaceSize() / 4;

      for(int i = 0; i < 4; ++i) {
         Vector3fc pos = bakedQuad.position(i);
         MemoryUtil.memPutFloat(pointer, pos.x());
         MemoryUtil.memPutFloat(pointer + 4L, pos.y());
         MemoryUtil.memPutFloat(pointer + 8L, pos.z());
         MemoryUtil.memPutLong(pointer + 12L, bakedQuad.packedUV(i));
         pointer += (long)vertexBufferBakedFaceCornerSize;
      }

   }
}
