package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class WorldBorderRenderer {
   public static final ResourceLocation FORCEFIELD_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png");
   private boolean needsRebuild = true;
   private double lastMinX;
   private double lastMinZ;
   private double lastBorderMinX;
   private double lastBorderMaxX;
   private double lastBorderMinZ;
   private double lastBorderMaxZ;
   private VertexBuffer[] worldBorderBuffers = new VertexBuffer[4];

   public WorldBorderRenderer() {
      super();

      for(Direction var2 : Direction.Plane.HORIZONTAL) {
         this.worldBorderBuffers[var2.get2DDataValue()] = new VertexBuffer(BufferUsage.STATIC_WRITE);
      }

   }

   private void rebuildWorldBorderBuffer(WorldBorder var1, double var2, double var4, double var6, float var8, float var9, float var10) {
      try (ByteBufferBuilder var11 = new ByteBufferBuilder(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4)) {
         double var12 = var1.getMinX();
         double var14 = var1.getMaxX();
         double var16 = var1.getMinZ();
         double var18 = var1.getMaxZ();
         double var20 = Math.max((double)Mth.floor(var4 - var2), var16);
         double var22 = Math.min((double)Mth.ceil(var4 + var2), var18);
         float var24 = (float)(Mth.floor(var20) & 1) * 0.5F;
         float var25 = (float)(var22 - var20) / 2.0F;
         double var26 = Math.max((double)Mth.floor(var6 - var2), var12);
         double var28 = Math.min((double)Mth.ceil(var6 + var2), var14);
         float var30 = (float)(Mth.floor(var26) & 1) * 0.5F;
         float var31 = (float)(var28 - var26) / 2.0F;

         for(Direction var33 : Direction.Plane.HORIZONTAL) {
            BufferBuilder var34 = new BufferBuilder(var11, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            switch (var33) {
               case EAST:
                  var34.addVertex((float)(var14 - var26), -var8, (float)(var22 - var20)).setUv(var24, var9);
                  var34.addVertex((float)(var14 - var26), -var8, 0.0F).setUv(var25 + var24, var9);
                  var34.addVertex((float)(var14 - var26), var8, 0.0F).setUv(var25 + var24, var10);
                  var34.addVertex((float)(var14 - var26), var8, (float)(var22 - var20)).setUv(var24, var10);
                  break;
               case WEST:
                  var34.addVertex(0.0F, -var8, 0.0F).setUv(var24, var9);
                  var34.addVertex(0.0F, -var8, (float)(var22 - var20)).setUv(var25 + var24, var9);
                  var34.addVertex(0.0F, var8, (float)(var22 - var20)).setUv(var25 + var24, var10);
                  var34.addVertex(0.0F, var8, 0.0F).setUv(var24, var10);
                  break;
               case SOUTH:
                  var34.addVertex(0.0F, -var8, (float)(var18 - var20)).setUv(var30, var9);
                  var34.addVertex((float)(var28 - var26), -var8, (float)(var18 - var20)).setUv(var31 + var30, var9);
                  var34.addVertex((float)(var28 - var26), var8, (float)(var18 - var20)).setUv(var31 + var30, var10);
                  var34.addVertex(0.0F, var8, (float)(var18 - var20)).setUv(var30, var10);
                  break;
               case NORTH:
                  var34.addVertex((float)(var28 - var26), -var8, 0.0F).setUv(var30, var9);
                  var34.addVertex(0.0F, -var8, 0.0F).setUv(var31 + var30, var9);
                  var34.addVertex(0.0F, var8, 0.0F).setUv(var31 + var30, var10);
                  var34.addVertex((float)(var28 - var26), var8, 0.0F).setUv(var30, var10);
            }

            this.worldBorderBuffers[var33.get2DDataValue()].bind();
            this.worldBorderBuffers[var33.get2DDataValue()].upload(var34.buildOrThrow());
         }

         VertexBuffer.unbind();
         this.lastBorderMinX = var12;
         this.lastBorderMaxX = var14;
         this.lastBorderMinZ = var16;
         this.lastBorderMaxZ = var18;
         this.lastMinX = var26;
         this.lastMinZ = var20;
         this.needsRebuild = false;
      }

   }

   public void render(WorldBorder var1, Vec3 var2, double var3, double var5) {
      double var7 = var1.getMinX();
      double var9 = var1.getMaxX();
      double var11 = var1.getMinZ();
      double var13 = var1.getMaxZ();
      if ((!(var2.x < var9 - var3) || !(var2.x > var7 + var3) || !(var2.z < var13 - var3) || !(var2.z > var11 + var3)) && !(var2.x < var7 - var3) && !(var2.x > var9 + var3) && !(var2.z < var11 - var3) && !(var2.z > var13 + var3)) {
         double var15 = 1.0 - var1.getDistanceToBorder(var2.x, var2.z) / var3;
         var15 = Math.pow(var15, 4.0);
         var15 = Mth.clamp(var15, 0.0, 1.0);
         double var17 = var2.x;
         double var19 = var2.z;
         float var21 = (float)var5;
         RenderType var22 = RenderType.worldBorder();
         int var23 = var1.getStatus().getColor();
         float var24 = (float)ARGB.red(var23) / 255.0F;
         float var25 = (float)ARGB.green(var23) / 255.0F;
         float var26 = (float)ARGB.blue(var23) / 255.0F;
         RenderSystem.setShaderColor(var24, var25, var26, (float)var15);
         float var27 = (float)(Util.getMillis() % 3000L) / 3000.0F;
         RenderSystem.setTextureMatrix((new Matrix4f()).translation(var27, var27, 0.0F));
         float var28 = (float)(-Mth.frac(var2.y * 0.5));
         float var29 = var28 + var21;
         if (this.shouldRebuildWorldBorderBuffer(var1)) {
            this.rebuildWorldBorderBuffer(var1, var3, var19, var17, var21, var29, var28);
         }

         RenderSystem.setModelOffset((float)(this.lastMinX - var17), (float)(-var2.y), (float)(this.lastMinZ - var19));

         for(WorldBorder.DistancePerDirection var31 : var1.closestBorder(var17, var19)) {
            if (var31.distance() < var3) {
               this.worldBorderBuffers[var31.direction().get2DDataValue()].drawWithRenderType(var22);
            }
         }

         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.resetTextureMatrix();
         RenderSystem.resetModelOffset();
      }
   }

   public void invalidate() {
      this.needsRebuild = true;
   }

   private boolean shouldRebuildWorldBorderBuffer(WorldBorder var1) {
      return this.needsRebuild || var1.getMinX() != this.lastBorderMinX || var1.getMinZ() != this.lastBorderMinZ || var1.getMaxX() != this.lastBorderMaxX || var1.getMaxZ() != this.lastBorderMaxZ;
   }
}
