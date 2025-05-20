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
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CompiledSectionMesh implements SectionMesh {
   public static final SectionMesh UNCOMPILED = new SectionMesh() {
      public boolean facesCanSeeEachother(Direction var1, Direction var2) {
         return false;
      }
   };
   public static final SectionMesh EMPTY = new SectionMesh() {
      public boolean facesCanSeeEachother(Direction var1, Direction var2) {
         return true;
      }
   };
   private final List<BlockEntity> renderableBlockEntities;
   private final VisibilitySet visibilitySet;
   @Nullable
   private final MeshData.SortState transparencyState;
   @Nullable
   private TranslucencyPointOfView translucencyPointOfView;
   private final Map<ChunkSectionLayer, SectionBuffers> buffers = new EnumMap(ChunkSectionLayer.class);

   public CompiledSectionMesh(TranslucencyPointOfView var1, SectionCompiler.Results var2) {
      super();
      this.translucencyPointOfView = var1;
      this.visibilitySet = var2.visibilitySet;
      this.renderableBlockEntities = var2.blockEntities;
      this.transparencyState = var2.transparencyState;
   }

   public void setTranslucencyPointOfView(TranslucencyPointOfView var1) {
      this.translucencyPointOfView = var1;
   }

   public boolean isDifferentPointOfView(TranslucencyPointOfView var1) {
      return !var1.equals(this.translucencyPointOfView);
   }

   public boolean hasRenderableLayers() {
      return !this.buffers.isEmpty();
   }

   public boolean isEmpty(ChunkSectionLayer var1) {
      return !this.buffers.containsKey(var1);
   }

   public List<BlockEntity> getRenderableBlockEntities() {
      return this.renderableBlockEntities;
   }

   public boolean facesCanSeeEachother(Direction var1, Direction var2) {
      return this.visibilitySet.visibilityBetween(var1, var2);
   }

   @Nullable
   public SectionBuffers getBuffers(ChunkSectionLayer var1) {
      return (SectionBuffers)this.buffers.get(var1);
   }

   public void uploadMeshLayer(ChunkSectionLayer var1, MeshData var2, long var3) {
      CommandEncoder var5 = RenderSystem.getDevice().createCommandEncoder();
      SectionBuffers var6 = this.getBuffers(var1);
      if (var6 != null) {
         if (var6.getVertexBuffer().size() < var2.vertexBuffer().remaining()) {
            var6.getVertexBuffer().close();
            var6.setVertexBuffer(RenderSystem.getDevice().createBuffer(() -> {
               String var10000 = var1.label();
               return "Section vertex buffer - layer: " + var10000 + "; cords: " + SectionPos.x(var3) + ", " + SectionPos.y(var3) + ", " + SectionPos.z(var3);
            }, 40, var2.vertexBuffer()));
         } else if (!var6.getVertexBuffer().isClosed()) {
            var5.writeToBuffer(var6.getVertexBuffer().slice(), var2.vertexBuffer());
         }

         ByteBuffer var7 = var2.indexBuffer();
         if (var7 != null) {
            if (var6.getIndexBuffer() != null && var6.getIndexBuffer().size() >= var7.remaining()) {
               if (!var6.getIndexBuffer().isClosed()) {
                  var5.writeToBuffer(var6.getIndexBuffer().slice(), var7);
               }
            } else {
               if (var6.getIndexBuffer() != null) {
                  var6.getIndexBuffer().close();
               }

               var6.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> {
                  String var10000 = var1.label();
                  return "Section index buffer - layer: " + var10000 + "; cords: " + SectionPos.x(var3) + ", " + SectionPos.y(var3) + ", " + SectionPos.z(var3);
               }, 72, var7));
            }
         } else if (var6.getIndexBuffer() != null) {
            var6.getIndexBuffer().close();
            var6.setIndexBuffer((GpuBuffer)null);
         }

         var6.setIndexCount(var2.drawState().indexCount());
         var6.setIndexType(var2.drawState().indexType());
      } else {
         GpuBuffer var11 = RenderSystem.getDevice().createBuffer(() -> {
            String var10000 = var1.label();
            return "Section vertex buffer - layer: " + var10000 + "; cords: " + SectionPos.x(var3) + ", " + SectionPos.y(var3) + ", " + SectionPos.z(var3);
         }, 40, var2.vertexBuffer());
         ByteBuffer var8 = var2.indexBuffer();
         GpuBuffer var9 = var8 != null ? RenderSystem.getDevice().createBuffer(() -> {
            String var10000 = var1.label();
            return "Section index buffer - layer: " + var10000 + "; cords: " + SectionPos.x(var3) + ", " + SectionPos.y(var3) + ", " + SectionPos.z(var3);
         }, 72, var8) : null;
         SectionBuffers var10 = new SectionBuffers(var11, var9, var2.drawState().indexCount(), var2.drawState().indexType());
         this.buffers.put(var1, var10);
      }

   }

   public void uploadLayerIndexBuffer(ChunkSectionLayer var1, ByteBufferBuilder.Result var2, long var3) {
      SectionBuffers var5 = this.getBuffers(var1);
      if (var5 != null) {
         if (var5.getIndexBuffer() == null) {
            var5.setIndexBuffer(RenderSystem.getDevice().createBuffer(() -> {
               String var10000 = var1.label();
               return "Section index buffer - layer: " + var10000 + "; cords: " + SectionPos.x(var3) + ", " + SectionPos.y(var3) + ", " + SectionPos.z(var3);
            }, 72, var2.byteBuffer()));
         } else {
            CommandEncoder var6 = RenderSystem.getDevice().createCommandEncoder();
            if (!var5.getIndexBuffer().isClosed()) {
               var6.writeToBuffer(var5.getIndexBuffer().slice(), var2.byteBuffer());
            }
         }

      }
   }

   public boolean hasTranslucentGeometry() {
      return this.buffers.containsKey(ChunkSectionLayer.TRANSLUCENT);
   }

   @Nullable
   public MeshData.SortState getTransparencyState() {
      return this.transparencyState;
   }

   public void close() {
      this.buffers.values().forEach(SectionBuffers::close);
      this.buffers.clear();
   }
}
