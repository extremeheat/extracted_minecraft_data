package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.CloudStatus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.slf4j.Logger;

public class CloudRenderer extends SimplePreparableReloadListener<Optional<CloudRenderer.TextureData>> implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");
   private static final float CELL_SIZE_IN_BLOCKS = 12.0F;
   private static final float HEIGHT_IN_BLOCKS = 4.0F;
   private static final float BLOCKS_PER_SECOND = 0.6F;
   private static final long EMPTY_CELL = 0L;
   private static final int COLOR_OFFSET = 4;
   private static final int NORTH_OFFSET = 3;
   private static final int EAST_OFFSET = 2;
   private static final int SOUTH_OFFSET = 1;
   private static final int WEST_OFFSET = 0;
   private boolean needsRebuild = true;
   private int prevCellX = -2147483648;
   private int prevCellZ = -2147483648;
   private CloudRenderer.RelativeCameraPos prevRelativeCameraPos = CloudRenderer.RelativeCameraPos.INSIDE_CLOUDS;
   @Nullable
   private CloudStatus prevType;
   @Nullable
   private CloudRenderer.TextureData texture;
   private final VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
   private boolean vertexBufferEmpty;

   public CloudRenderer() {
      super();
   }

   protected Optional<CloudRenderer.TextureData> prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         Optional var20;
         try (
            InputStream var3 = var1.open(TEXTURE_LOCATION);
            NativeImage var4 = NativeImage.read(var3);
         ) {
            int var5 = var4.getWidth();
            int var6 = var4.getHeight();
            long[] var7 = new long[var5 * var6];

            for (int var8 = 0; var8 < var6; var8++) {
               for (int var9 = 0; var9 < var5; var9++) {
                  int var10 = var4.getPixel(var9, var8);
                  if (isCellEmpty(var10)) {
                     var7[var9 + var8 * var5] = 0L;
                  } else {
                     boolean var11 = isCellEmpty(var4.getPixel(var9, Math.floorMod(var8 - 1, var6)));
                     boolean var12 = isCellEmpty(var4.getPixel(Math.floorMod(var9 + 1, var6), var8));
                     boolean var13 = isCellEmpty(var4.getPixel(var9, Math.floorMod(var8 + 1, var6)));
                     boolean var14 = isCellEmpty(var4.getPixel(Math.floorMod(var9 - 1, var6), var8));
                     var7[var9 + var8 * var5] = packCellData(var10, var11, var12, var13, var14);
                  }
               }
            }

            var20 = Optional.of(new CloudRenderer.TextureData(var7, var5, var6));
         }

         return var20;
      } catch (IOException var19) {
         LOGGER.error("Failed to load cloud texture", var19);
         return Optional.empty();
      }
   }

   protected void apply(Optional<CloudRenderer.TextureData> var1, ResourceManager var2, ProfilerFiller var3) {
      this.texture = (CloudRenderer.TextureData)var1.orElse(null);
      this.needsRebuild = true;
   }

   private static boolean isCellEmpty(int var0) {
      return ARGB.alpha(var0) < 10;
   }

   private static long packCellData(int var0, boolean var1, boolean var2, boolean var3, boolean var4) {
      return (long)var0 << 4 | (long)((var1 ? 1 : 0) << 3) | (long)((var2 ? 1 : 0) << 2) | (long)((var3 ? 1 : 0) << 1) | (long)((var4 ? 1 : 0) << 0);
   }

   private static int getColor(long var0) {
      return (int)(var0 >> 4 & 4294967295L);
   }

   private static boolean isNorthEmpty(long var0) {
      return (var0 >> 3 & 1L) != 0L;
   }

   private static boolean isEastEmpty(long var0) {
      return (var0 >> 2 & 1L) != 0L;
   }

   private static boolean isSouthEmpty(long var0) {
      return (var0 >> 1 & 1L) != 0L;
   }

   private static boolean isWestEmpty(long var0) {
      return (var0 >> 0 & 1L) != 0L;
   }

   public void render(int var1, CloudStatus var2, float var3, Matrix4f var4, Matrix4f var5, Vec3 var6, float var7) {
      if (this.texture != null) {
         float var8 = (float)((double)var3 - var6.y);
         float var9 = var8 + 4.0F;
         CloudRenderer.RelativeCameraPos var10;
         if (var9 < 0.0F) {
            var10 = CloudRenderer.RelativeCameraPos.ABOVE_CLOUDS;
         } else if (var8 > 0.0F) {
            var10 = CloudRenderer.RelativeCameraPos.BELOW_CLOUDS;
         } else {
            var10 = CloudRenderer.RelativeCameraPos.INSIDE_CLOUDS;
         }

         double var11 = var6.x + (double)(var7 * 0.030000001F);
         double var13 = var6.z + 3.9600000381469727;
         double var15 = (double)this.texture.width * 12.0;
         double var17 = (double)this.texture.height * 12.0;
         var11 -= (double)Mth.floor(var11 / var15) * var15;
         var13 -= (double)Mth.floor(var13 / var17) * var17;
         int var19 = Mth.floor(var11 / 12.0);
         int var20 = Mth.floor(var13 / 12.0);
         float var21 = (float)(var11 - (double)((float)var19 * 12.0F));
         float var22 = (float)(var13 - (double)((float)var20 * 12.0F));
         RenderType var23 = var2 == CloudStatus.FANCY ? RenderType.clouds() : RenderType.flatClouds();
         this.vertexBuffer.bind();
         if (this.needsRebuild || var19 != this.prevCellX || var20 != this.prevCellZ || var10 != this.prevRelativeCameraPos || var2 != this.prevType) {
            this.needsRebuild = false;
            this.prevCellX = var19;
            this.prevCellZ = var20;
            this.prevRelativeCameraPos = var10;
            this.prevType = var2;
            MeshData var24 = this.buildMesh(Tesselator.getInstance(), var19, var20, var2, var10, var23);
            if (var24 != null) {
               this.vertexBuffer.upload(var24);
               this.vertexBufferEmpty = false;
            } else {
               this.vertexBufferEmpty = true;
            }
         }

         if (!this.vertexBufferEmpty) {
            RenderSystem.setShaderColor(
               ARGB.from8BitChannel(ARGB.red(var1)), ARGB.from8BitChannel(ARGB.green(var1)), ARGB.from8BitChannel(ARGB.blue(var1)), 1.0F
            );
            if (var2 == CloudStatus.FANCY) {
               this.drawWithRenderType(RenderType.cloudsDepthOnly(), var4, var5, var21, var8, var22);
            }

            this.drawWithRenderType(var23, var4, var5, var21, var8, var22);
            VertexBuffer.unbind();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   private void drawWithRenderType(RenderType var1, Matrix4f var2, Matrix4f var3, float var4, float var5, float var6) {
      var1.setupRenderState();
      CompiledShaderProgram var7 = RenderSystem.getShader();
      if (var7 != null && var7.MODEL_OFFSET != null) {
         var7.MODEL_OFFSET.set(-var4, var5, -var6);
      }

      this.vertexBuffer.drawWithShader(var2, var3, var7);
      var1.clearRenderState();
   }

   @Nullable
   private MeshData buildMesh(Tesselator var1, int var2, int var3, CloudStatus var4, CloudRenderer.RelativeCameraPos var5, RenderType var6) {
      float var7 = 0.8F;
      int var8 = ARGB.colorFromFloat(0.8F, 1.0F, 1.0F, 1.0F);
      int var9 = ARGB.colorFromFloat(0.8F, 0.9F, 0.9F, 0.9F);
      int var10 = ARGB.colorFromFloat(0.8F, 0.7F, 0.7F, 0.7F);
      int var11 = ARGB.colorFromFloat(0.8F, 0.8F, 0.8F, 0.8F);
      BufferBuilder var12 = var1.begin(var6.mode(), var6.format());
      this.buildMesh(var5, var12, var2, var3, var10, var8, var9, var11, var4 == CloudStatus.FANCY);
      return var12.build();
   }

   private void buildMesh(CloudRenderer.RelativeCameraPos var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      if (this.texture != null) {
         byte var10 = 32;
         long[] var11 = this.texture.cells;
         int var12 = this.texture.width;
         int var13 = this.texture.height;

         for (int var14 = -32; var14 <= 32; var14++) {
            for (int var15 = -32; var15 <= 32; var15++) {
               int var16 = Math.floorMod(var3 + var15, var12);
               int var17 = Math.floorMod(var4 + var14, var13);
               long var18 = var11[var16 + var17 * var12];
               if (var18 != 0L) {
                  int var20 = getColor(var18);
                  if (var9) {
                     this.buildExtrudedCell(
                        var1,
                        var2,
                        ARGB.multiply(var5, var20),
                        ARGB.multiply(var6, var20),
                        ARGB.multiply(var7, var20),
                        ARGB.multiply(var8, var20),
                        var15,
                        var14,
                        var18
                     );
                  } else {
                     this.buildFlatCell(var2, ARGB.multiply(var6, var20), var15, var14);
                  }
               }
            }
         }
      }
   }

   private void buildFlatCell(BufferBuilder var1, int var2, int var3, int var4) {
      float var5 = (float)var3 * 12.0F;
      float var6 = var5 + 12.0F;
      float var7 = (float)var4 * 12.0F;
      float var8 = var7 + 12.0F;
      var1.addVertex(var5, 0.0F, var7).setColor(var2);
      var1.addVertex(var5, 0.0F, var8).setColor(var2);
      var1.addVertex(var6, 0.0F, var8).setColor(var2);
      var1.addVertex(var6, 0.0F, var7).setColor(var2);
   }

   private void buildExtrudedCell(
      CloudRenderer.RelativeCameraPos var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9
   ) {
      float var11 = (float)var7 * 12.0F;
      float var12 = var11 + 12.0F;
      float var13 = 0.0F;
      float var14 = 4.0F;
      float var15 = (float)var8 * 12.0F;
      float var16 = var15 + 12.0F;
      if (var1 != CloudRenderer.RelativeCameraPos.BELOW_CLOUDS) {
         var2.addVertex(var11, 4.0F, var15).setColor(var4);
         var2.addVertex(var11, 4.0F, var16).setColor(var4);
         var2.addVertex(var12, 4.0F, var16).setColor(var4);
         var2.addVertex(var12, 4.0F, var15).setColor(var4);
      }

      if (var1 != CloudRenderer.RelativeCameraPos.ABOVE_CLOUDS) {
         var2.addVertex(var12, 0.0F, var15).setColor(var3);
         var2.addVertex(var12, 0.0F, var16).setColor(var3);
         var2.addVertex(var11, 0.0F, var16).setColor(var3);
         var2.addVertex(var11, 0.0F, var15).setColor(var3);
      }

      if (isNorthEmpty(var9) && var8 > 0) {
         var2.addVertex(var11, 0.0F, var15).setColor(var6);
         var2.addVertex(var11, 4.0F, var15).setColor(var6);
         var2.addVertex(var12, 4.0F, var15).setColor(var6);
         var2.addVertex(var12, 0.0F, var15).setColor(var6);
      }

      if (isSouthEmpty(var9) && var8 < 0) {
         var2.addVertex(var12, 0.0F, var16).setColor(var6);
         var2.addVertex(var12, 4.0F, var16).setColor(var6);
         var2.addVertex(var11, 4.0F, var16).setColor(var6);
         var2.addVertex(var11, 0.0F, var16).setColor(var6);
      }

      if (isWestEmpty(var9) && var7 > 0) {
         var2.addVertex(var11, 0.0F, var16).setColor(var5);
         var2.addVertex(var11, 4.0F, var16).setColor(var5);
         var2.addVertex(var11, 4.0F, var15).setColor(var5);
         var2.addVertex(var11, 0.0F, var15).setColor(var5);
      }

      if (isEastEmpty(var9) && var7 < 0) {
         var2.addVertex(var12, 0.0F, var15).setColor(var5);
         var2.addVertex(var12, 4.0F, var15).setColor(var5);
         var2.addVertex(var12, 4.0F, var16).setColor(var5);
         var2.addVertex(var12, 0.0F, var16).setColor(var5);
      }

      boolean var17 = Math.abs(var7) <= 1 && Math.abs(var8) <= 1;
      if (var17) {
         var2.addVertex(var12, 4.0F, var15).setColor(var4);
         var2.addVertex(var12, 4.0F, var16).setColor(var4);
         var2.addVertex(var11, 4.0F, var16).setColor(var4);
         var2.addVertex(var11, 4.0F, var15).setColor(var4);
         var2.addVertex(var11, 0.0F, var15).setColor(var3);
         var2.addVertex(var11, 0.0F, var16).setColor(var3);
         var2.addVertex(var12, 0.0F, var16).setColor(var3);
         var2.addVertex(var12, 0.0F, var15).setColor(var3);
         var2.addVertex(var12, 0.0F, var15).setColor(var6);
         var2.addVertex(var12, 4.0F, var15).setColor(var6);
         var2.addVertex(var11, 4.0F, var15).setColor(var6);
         var2.addVertex(var11, 0.0F, var15).setColor(var6);
         var2.addVertex(var11, 0.0F, var16).setColor(var6);
         var2.addVertex(var11, 4.0F, var16).setColor(var6);
         var2.addVertex(var12, 4.0F, var16).setColor(var6);
         var2.addVertex(var12, 0.0F, var16).setColor(var6);
         var2.addVertex(var11, 0.0F, var15).setColor(var5);
         var2.addVertex(var11, 4.0F, var15).setColor(var5);
         var2.addVertex(var11, 4.0F, var16).setColor(var5);
         var2.addVertex(var11, 0.0F, var16).setColor(var5);
         var2.addVertex(var12, 0.0F, var16).setColor(var5);
         var2.addVertex(var12, 4.0F, var16).setColor(var5);
         var2.addVertex(var12, 4.0F, var15).setColor(var5);
         var2.addVertex(var12, 0.0F, var15).setColor(var5);
      }
   }

   public void markForRebuild() {
      this.needsRebuild = true;
   }

   @Override
   public void close() {
      this.vertexBuffer.close();
   }

   static enum RelativeCameraPos {
      ABOVE_CLOUDS,
      INSIDE_CLOUDS,
      BELOW_CLOUDS;

      private RelativeCameraPos() {
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
}
