package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class CloudRenderer extends SimplePreparableReloadListener<Optional<TextureData>> implements AutoCloseable {
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
   private RelativeCameraPos prevRelativeCameraPos;
   @Nullable
   private CloudStatus prevType;
   @Nullable
   private TextureData texture;
   @Nullable
   private GpuBuffer vertexBuffer;
   private int indexCount;
   private final RenderSystem.AutoStorageIndexBuffer indices;

   public CloudRenderer() {
      super();
      this.prevRelativeCameraPos = CloudRenderer.RelativeCameraPos.INSIDE_CLOUDS;
      this.vertexBuffer = null;
      this.indexCount = 0;
      this.indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
   }

   protected Optional<TextureData> prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         InputStream var3 = var1.open(TEXTURE_LOCATION);

         Optional var20;
         try (NativeImage var4 = NativeImage.read(var3)) {
            int var5 = var4.getWidth();
            int var6 = var4.getHeight();
            long[] var7 = new long[var5 * var6];

            for(int var8 = 0; var8 < var6; ++var8) {
               for(int var9 = 0; var9 < var5; ++var9) {
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

            var20 = Optional.of(new TextureData(var7, var5, var6));
         } catch (Throwable var18) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var15) {
                  var18.addSuppressed(var15);
               }
            }

            throw var18;
         }

         if (var3 != null) {
            var3.close();
         }

         return var20;
      } catch (IOException var19) {
         LOGGER.error("Failed to load cloud texture", var19);
         return Optional.empty();
      }
   }

   protected void apply(Optional<TextureData> var1, ResourceManager var2, ProfilerFiller var3) {
      this.texture = (TextureData)var1.orElse((Object)null);
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

   public void render(int var1, CloudStatus var2, float var3, Vec3 var4, float var5) {
      if (this.texture != null) {
         float var6 = (float)((double)var3 - var4.y);
         float var7 = var6 + 4.0F;
         RelativeCameraPos var8;
         if (var7 < 0.0F) {
            var8 = CloudRenderer.RelativeCameraPos.ABOVE_CLOUDS;
         } else if (var6 > 0.0F) {
            var8 = CloudRenderer.RelativeCameraPos.BELOW_CLOUDS;
         } else {
            var8 = CloudRenderer.RelativeCameraPos.INSIDE_CLOUDS;
         }

         double var9 = var4.x + (double)(var5 * 0.030000001F);
         double var11 = var4.z + 3.9600000381469727;
         double var13 = (double)this.texture.width * 12.0;
         double var15 = (double)this.texture.height * 12.0;
         var9 -= (double)Mth.floor(var9 / var13) * var13;
         var11 -= (double)Mth.floor(var11 / var15) * var15;
         int var17 = Mth.floor(var9 / 12.0);
         int var18 = Mth.floor(var11 / 12.0);
         float var19 = (float)(var9 - (double)((float)var17 * 12.0F));
         float var20 = (float)(var11 - (double)((float)var18 * 12.0F));
         boolean var21 = var2 == CloudStatus.FANCY;
         RenderPipeline var22 = var21 ? RenderPipelines.CLOUDS : RenderPipelines.FLAT_CLOUDS;
         if (this.needsRebuild || var17 != this.prevCellX || var18 != this.prevCellZ || var8 != this.prevRelativeCameraPos || var2 != this.prevType) {
            this.needsRebuild = false;
            this.prevCellX = var17;
            this.prevCellZ = var18;
            this.prevRelativeCameraPos = var8;
            this.prevType = var2;

            try (MeshData var23 = this.buildMesh(Tesselator.getInstance(), var17, var18, var2, var8, var22)) {
               if (var23 == null) {
                  this.indexCount = 0;
               } else {
                  if (this.vertexBuffer != null && this.vertexBuffer.size >= var23.vertexBuffer().remaining()) {
                     CommandEncoder var24 = RenderSystem.getDevice().createCommandEncoder();
                     var24.writeToBuffer(this.vertexBuffer, var23.vertexBuffer(), 0);
                  } else {
                     if (this.vertexBuffer != null) {
                        this.vertexBuffer.close();
                     }

                     this.vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "Cloud vertex buffer", BufferType.VERTICES, BufferUsage.DYNAMIC_WRITE, var23.vertexBuffer());
                  }

                  this.indexCount = var23.drawState().indexCount();
               }
            }
         }

         if (this.indexCount != 0) {
            RenderSystem.setShaderColor(ARGB.redFloat(var1), ARGB.greenFloat(var1), ARGB.blueFloat(var1), 1.0F);
            if (var21) {
               this.draw(RenderPipelines.CLOUDS_DEPTH_ONLY, var19, var6, var20);
            }

            this.draw(var22, var19, var6, var20);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   private void draw(RenderPipeline var1, float var2, float var3, float var4) {
      RenderSystem.setModelOffset(-var2, var3, -var4);
      RenderTarget var5 = Minecraft.getInstance().getMainRenderTarget();
      RenderTarget var6 = Minecraft.getInstance().levelRenderer.getCloudsTarget();
      GpuTexture var7;
      GpuTexture var8;
      if (var6 != null) {
         var7 = var6.getColorTexture();
         var8 = var6.getDepthTexture();
      } else {
         var7 = var5.getColorTexture();
         var8 = var5.getDepthTexture();
      }

      GpuBuffer var9 = this.indices.getBuffer(this.indexCount);

      try (RenderPass var10 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var7, OptionalInt.empty(), var8, OptionalDouble.empty())) {
         var10.setPipeline(var1);
         var10.setIndexBuffer(var9, this.indices.type());
         var10.setVertexBuffer(0, this.vertexBuffer);
         var10.drawIndexed(0, this.indexCount);
      }

      RenderSystem.resetModelOffset();
   }

   @Nullable
   private MeshData buildMesh(Tesselator var1, int var2, int var3, CloudStatus var4, RelativeCameraPos var5, RenderPipeline var6) {
      float var7 = 0.8F;
      int var8 = ARGB.colorFromFloat(0.8F, 1.0F, 1.0F, 1.0F);
      int var9 = ARGB.colorFromFloat(0.8F, 0.9F, 0.9F, 0.9F);
      int var10 = ARGB.colorFromFloat(0.8F, 0.7F, 0.7F, 0.7F);
      int var11 = ARGB.colorFromFloat(0.8F, 0.8F, 0.8F, 0.8F);
      BufferBuilder var12 = var1.begin(var6.getVertexFormatMode(), var6.getVertexFormat());
      this.buildMesh(var5, var12, var2, var3, var10, var8, var9, var11, var4 == CloudStatus.FANCY);
      return var12.build();
   }

   private void buildMesh(RelativeCameraPos var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      if (this.texture != null) {
         boolean var10 = true;
         long[] var11 = this.texture.cells;
         int var12 = this.texture.width;
         int var13 = this.texture.height;

         for(int var14 = -32; var14 <= 32; ++var14) {
            for(int var15 = -32; var15 <= 32; ++var15) {
               int var16 = Math.floorMod(var3 + var15, var12);
               int var17 = Math.floorMod(var4 + var14, var13);
               long var18 = var11[var16 + var17 * var12];
               if (var18 != 0L) {
                  int var20 = getColor(var18);
                  if (var9) {
                     this.buildExtrudedCell(var1, var2, ARGB.multiply(var5, var20), ARGB.multiply(var6, var20), ARGB.multiply(var7, var20), ARGB.multiply(var8, var20), var15, var14, var18);
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

   private void buildExtrudedCell(RelativeCameraPos var1, BufferBuilder var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9) {
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

   public void close() {
      if (this.vertexBuffer != null) {
         this.vertexBuffer.close();
      }

   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   static enum RelativeCameraPos {
      ABOVE_CLOUDS,
      INSIDE_CLOUDS,
      BELOW_CLOUDS;

      private RelativeCameraPos() {
      }

      // $FF: synthetic method
      private static RelativeCameraPos[] $values() {
         return new RelativeCameraPos[]{ABOVE_CLOUDS, INSIDE_CLOUDS, BELOW_CLOUDS};
      }
   }

   public static record TextureData(long[] cells, int width, int height) {
      final long[] cells;
      final int width;
      final int height;

      public TextureData(long[] var1, int var2, int var3) {
         super();
         this.cells = var1;
         this.width = var2;
         this.height = var3;
      }
   }
}
