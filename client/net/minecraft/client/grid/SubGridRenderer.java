package net.minecraft.client.grid;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.grid.GridCarrier;
import net.minecraft.world.grid.SubGridBlocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SubGridRenderer implements AutoCloseable {
   private static final double CULL_BUFFER_SIZE = 3.0;
   private final Minecraft minecraft = Minecraft.getInstance();
   private final BlockRenderDispatcher blockRenderer;
   private final ClientSubGrid grid;
   private final Reference2ObjectMap<RenderType, VertexBuffer> vertexBuffers = new Reference2ObjectArrayMap();
   @Nullable
   private CompletableFuture<SubGridMeshBuilder.Results> meshFuture;
   private boolean needsRebuild = true;

   public SubGridRenderer(ClientSubGrid var1) {
      super();
      this.grid = var1;
      this.blockRenderer = this.minecraft.getBlockRenderer();
   }

   private void prepareMesh() {
      if (this.meshFuture != null) {
         if (this.meshFuture.isDone()) {
            try (SubGridMeshBuilder.Results var1 = this.meshFuture.join()) {
               var1.uploadTo(this.vertexBuffers);
               VertexBuffer.unbind();
            }

            this.meshFuture = null;
         }
      } else if (this.needsRebuild) {
         this.needsRebuild = false;
         SubGridMeshBuilder.BlockView var6 = SubGridMeshBuilder.BlockView.copyOf(this.grid);
         SubGridMeshBuilder var2 = new SubGridMeshBuilder(this.blockRenderer, var6);
         this.meshFuture = CompletableFuture.supplyAsync(var2::build, Util.backgroundExecutor());
      }
   }

   public void draw(float var1, double var2, double var4, double var6, Frustum var8, Matrix4f var9, Matrix4f var10, boolean var11) {
      GridCarrier var12 = this.grid.carrier();
      Vec3 var13 = new Vec3(
         Mth.lerp((double)var1, var12.xOld, var12.getX()), Mth.lerp((double)var1, var12.yOld, var12.getY()), Mth.lerp((double)var1, var12.zOld, var12.getZ())
      );
      SubGridBlocks var14 = this.grid.getBlocks();
      if (var8.isVisible(
         var13.x - 3.0,
         var13.y - 3.0,
         var13.z - 3.0,
         var13.x + (double)var14.sizeX() + 1.0 + 3.0,
         var13.y + (double)var14.sizeY() + 1.0 + 3.0,
         var13.z + (double)var14.sizeZ() + 1.0 + 3.0
      )) {
         this.prepareMesh();
         if (!this.vertexBuffers.isEmpty()) {
            Window var15 = this.minecraft.getWindow();
            Vector3f var16 = new Vector3f((float)(var13.x - var2), (float)(var13.y - var4), (float)(var13.z - var6));
            if (var11) {
               this.drawLayer(RenderType.translucent(), var16, var9, var10, var15);
               this.drawLayer(RenderType.tripwire(), var16, var9, var10, var15);
            } else {
               this.drawLayer(RenderType.solid(), var16, var9, var10, var15);
               this.drawLayer(RenderType.cutoutMipped(), var16, var9, var10, var15);
               this.drawLayer(RenderType.cutout(), var16, var9, var10, var15);
            }
         }
      }
   }

   private void drawLayer(RenderType var1, Vector3f var2, Matrix4f var3, Matrix4f var4, Window var5) {
      VertexBuffer var6 = (VertexBuffer)this.vertexBuffers.get(var1);
      if (var6 != null) {
         var1.setupRenderState();
         ShaderInstance var7 = RenderSystem.getShader();
         var7.setDefaultUniforms(VertexFormat.Mode.QUADS, var3, var4, var5);
         var7.CHUNK_OFFSET.set(var2.x, var2.y, var2.z);
         var7.apply();
         var6.bind();
         var6.draw();
         VertexBuffer.unbind();
         var7.clear();
         var1.clearRenderState();
      }
   }

   @Override
   public void close() {
      this.vertexBuffers.values().forEach(VertexBuffer::close);
      this.vertexBuffers.clear();
      if (this.meshFuture != null) {
         this.meshFuture.thenAcceptAsync(SubGridMeshBuilder.Results::close, var0 -> RenderSystem.recordRenderCall(var0::run));
         this.meshFuture = null;
      }
   }
}
