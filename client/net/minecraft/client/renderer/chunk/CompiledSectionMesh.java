package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public class CompiledSectionMesh implements SectionMesh {
   public static final SectionMesh UNCOMPILED = new SectionMesh() {
      public boolean facesCanSeeEachother(final Direction direction1, final Direction direction2) {
         return false;
      }
   };
   public static final SectionMesh EMPTY = new SectionMesh() {
      public boolean facesCanSeeEachother(final Direction direction1, final Direction direction2) {
         return true;
      }
   };
   private final List<BlockEntity> renderableBlockEntities;
   private final VisibilitySet visibilitySet;
   private final MeshData.@Nullable SortState transparencyState;
   private @Nullable TranslucencyPointOfView translucencyPointOfView;
   private final Map<ChunkSectionLayer, SectionBuffers> buffers = new EnumMap(ChunkSectionLayer.class);

   public CompiledSectionMesh(final TranslucencyPointOfView translucencyPointOfView, final SectionCompiler.Results results) {
      super();
      this.translucencyPointOfView = translucencyPointOfView;
      this.visibilitySet = results.visibilitySet;
      this.renderableBlockEntities = results.blockEntities;
      this.transparencyState = results.transparencyState;
   }

   public void setTranslucencyPointOfView(final TranslucencyPointOfView translucencyPointOfView) {
      this.translucencyPointOfView = translucencyPointOfView;
   }

   public boolean isDifferentPointOfView(final TranslucencyPointOfView pointOfView) {
      return !pointOfView.equals(this.translucencyPointOfView);
   }

   public boolean hasRenderableLayers() {
      return !this.buffers.isEmpty();
   }

   public boolean isEmpty(final ChunkSectionLayer layer) {
      return !this.buffers.containsKey(layer);
   }

   public List<BlockEntity> getRenderableBlockEntities() {
      return this.renderableBlockEntities;
   }

   public boolean facesCanSeeEachother(final Direction direction1, final Direction direction2) {
      return this.visibilitySet.visibilityBetween(direction1, direction2);
   }

   public @Nullable SectionBuffers getBuffers(final ChunkSectionLayer layer) {
      return (SectionBuffers)this.buffers.get(layer);
   }

   public void uploadMeshLayer(final ChunkSectionLayer layer, final MeshData mesh, final long sectionNode) {
      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
      SectionBuffers sectionBuffers = this.getBuffers(layer);
      if (sectionBuffers != null) {
         if (sectionBuffers.getVertexBuffer().size() < (long)mesh.vertexBuffer().remaining()) {
            sectionBuffers.getVertexBuffer().close();
            sectionBuffers.setVertexBuffer(RenderSystem.getDevice().createBuffer(() -> {
               String var10000 = layer.label();
               return "Section vertex buffer - layer: " + var10000 + "; cords: " + SectionPos.x(sectionNode) + ", " + SectionPos.y(sectionNode) + ", " + SectionPos.z(sectionNode);
            }, 40, mesh.vertexBuffer()));
         } else if (!sectionBuffers.getVertexBuffer().isClosed()) {
            commandEncoder.writeToBuffer(sectionBuffers.getVertexBuffer().slice(), mesh.vertexBuffer());
         }

         ByteBuffer indexByteBuffer = mesh.indexBuffer();
         if (indexByteBuffer != null) {
            if (sectionBuffers.getIndexBuffer() != null && sectionBuffers.getIndexBuffer().size() >= (long)indexByteBuffer.remaining()) {
               if (!sectionBuffers.getIndexBuffer().isClosed()) {
                  commandEncoder.writeToBuffer(sectionBuffers.getIndexBuffer().slice(), indexByteBuffer);
               }
            } else {
               if (sectionBuffers.getIndexBuffer() != null) {
                  sectionBuffers.getIndexBuffer().close();
               }

               sectionBuffers.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> {
                  String var10000 = layer.label();
                  return "Section index buffer - layer: " + var10000 + "; cords: " + SectionPos.x(sectionNode) + ", " + SectionPos.y(sectionNode) + ", " + SectionPos.z(sectionNode);
               }, 72, indexByteBuffer));
            }
         } else if (sectionBuffers.getIndexBuffer() != null) {
            sectionBuffers.getIndexBuffer().close();
            sectionBuffers.setIndexBuffer((GpuBuffer)null);
         }

         sectionBuffers.setIndexCount(mesh.drawState().indexCount());
         sectionBuffers.setIndexType(mesh.drawState().indexType());
      } else {
         GpuBuffer vertexBuffer = RenderSystem.getDevice().createBuffer(() -> {
            String var10000 = layer.label();
            return "Section vertex buffer - layer: " + var10000 + "; cords: " + SectionPos.x(sectionNode) + ", " + SectionPos.y(sectionNode) + ", " + SectionPos.z(sectionNode);
         }, 40, mesh.vertexBuffer());
         ByteBuffer indexByteBuffer = mesh.indexBuffer();
         GpuBuffer indexBuffer = indexByteBuffer != null ? RenderSystem.getDevice().createBuffer(() -> {
            String var10000 = layer.label();
            return "Section index buffer - layer: " + var10000 + "; cords: " + SectionPos.x(sectionNode) + ", " + SectionPos.y(sectionNode) + ", " + SectionPos.z(sectionNode);
         }, 72, indexByteBuffer) : null;
         SectionBuffers newSectionBuffers = new SectionBuffers(vertexBuffer, indexBuffer, mesh.drawState().indexCount(), mesh.drawState().indexType());
         this.buffers.put(layer, newSectionBuffers);
      }

   }

   public void uploadLayerIndexBuffer(final ChunkSectionLayer layer, final ByteBufferBuilder.Result indexBuffer, final long sectionNode) {
      SectionBuffers target = this.getBuffers(layer);
      if (target != null) {
         if (target.getIndexBuffer() == null) {
            target.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> {
               String var10000 = layer.label();
               return "Section index buffer - layer: " + var10000 + "; cords: " + SectionPos.x(sectionNode) + ", " + SectionPos.y(sectionNode) + ", " + SectionPos.z(sectionNode);
            }, 72, indexBuffer.byteBuffer()));
         } else {
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            if (!target.getIndexBuffer().isClosed()) {
               commandEncoder.writeToBuffer(target.getIndexBuffer().slice(), indexBuffer.byteBuffer());
            }
         }

      }
   }

   public boolean hasTranslucentGeometry() {
      return this.buffers.containsKey(ChunkSectionLayer.TRANSLUCENT);
   }

   public MeshData.@Nullable SortState getTransparencyState() {
      return this.transparencyState;
   }

   public void close() {
      this.buffers.values().forEach(SectionBuffers::close);
      this.buffers.clear();
   }
}
