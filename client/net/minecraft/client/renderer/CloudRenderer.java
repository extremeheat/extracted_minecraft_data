package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

public class CloudRenderer extends SimplePreparableReloadListener<Optional<TextureData>> implements AutoCloseable {
   private static final int FLAG_INSIDE_FACE = 16;
   private static final int FLAG_USE_TOP_COLOR = 32;
   private static final int MAX_RADIUS_CHUNKS = 128;
   private static final float CELL_SIZE_IN_BLOCKS = 12.0F;
   private static final int UBO_SIZE = (new Std140SizeCalculator()).putVec4().putVec3().putVec3().get();
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/clouds.png");
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
   private int quadCount;
   private final RenderSystem.AutoStorageIndexBuffer indices;
   private final MappableRingBuffer ubo;
   @Nullable
   private MappableRingBuffer utb;

   public CloudRenderer() {
      super();
      this.prevRelativeCameraPos = CloudRenderer.RelativeCameraPos.INSIDE_CLOUDS;
      this.quadCount = 0;
      this.indices = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      this.ubo = new MappableRingBuffer(() -> "Cloud UBO", 130, UBO_SIZE);
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

   private static int getSizeForCloudDistance(int var0) {
      boolean var1 = true;
      int var2 = (var0 + 1) * 2 * (var0 + 1) * 2 / 2;
      int var3 = var2 * 4 + 54;
      return var3 * 3;
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
         int var6 = Math.min((Integer)Minecraft.getInstance().options.cloudRange().get(), 128) * 16;
         int var7 = Mth.ceil((float)var6 / 12.0F);
         int var8 = getSizeForCloudDistance(var7);
         if (this.utb == null || this.utb.currentBuffer().size() != var8) {
            if (this.utb != null) {
               this.utb.close();
            }

            this.utb = new MappableRingBuffer(() -> "Cloud UTB", 258, var8);
         }

         float var9 = (float)((double)var3 - var4.y);
         float var10 = var9 + 4.0F;
         RelativeCameraPos var11;
         if (var10 < 0.0F) {
            var11 = CloudRenderer.RelativeCameraPos.ABOVE_CLOUDS;
         } else if (var9 > 0.0F) {
            var11 = CloudRenderer.RelativeCameraPos.BELOW_CLOUDS;
         } else {
            var11 = CloudRenderer.RelativeCameraPos.INSIDE_CLOUDS;
         }

         double var12 = var4.x + (double)(var5 * 0.030000001F);
         double var14 = var4.z + 3.9600000381469727;
         double var16 = (double)this.texture.width * 12.0;
         double var18 = (double)this.texture.height * 12.0;
         var12 -= (double)Mth.floor(var12 / var16) * var16;
         var14 -= (double)Mth.floor(var14 / var18) * var18;
         int var20 = Mth.floor(var12 / 12.0);
         int var21 = Mth.floor(var14 / 12.0);
         float var22 = (float)(var12 - (double)((float)var20 * 12.0F));
         float var23 = (float)(var14 - (double)((float)var21 * 12.0F));
         boolean var24 = var2 == CloudStatus.FANCY;
         RenderPipeline var25 = var24 ? RenderPipelines.CLOUDS : RenderPipelines.FLAT_CLOUDS;
         if (this.needsRebuild || var20 != this.prevCellX || var21 != this.prevCellZ || var11 != this.prevRelativeCameraPos || var2 != this.prevType) {
            this.needsRebuild = false;
            this.prevCellX = var20;
            this.prevCellZ = var21;
            this.prevRelativeCameraPos = var11;
            this.prevType = var2;
            this.utb.rotate();

            try (GpuBuffer.MappedView var26 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.utb.currentBuffer(), false, true)) {
               this.buildMesh(var11, var26.data(), var20, var21, var24, var7);
               this.quadCount = var26.data().position() / 3;
            }
         }

         if (this.quadCount != 0) {
            try (GpuBuffer.MappedView var44 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.ubo.currentBuffer(), false, true)) {
               Std140Builder.intoBuffer(var44.data()).putVec4(ARGB.redFloat(var1), ARGB.greenFloat(var1), ARGB.blueFloat(var1), 1.0F).putVec3(-var22, var9, -var23).putVec3(12.0F, 4.0F, 12.0F);
            }

            GpuBufferSlice var45 = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), new Vector3f(), new Matrix4f(), 0.0F);
            RenderTarget var27 = Minecraft.getInstance().getMainRenderTarget();
            RenderTarget var28 = Minecraft.getInstance().levelRenderer.getCloudsTarget();
            RenderSystem.AutoStorageIndexBuffer var31 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
            GpuBuffer var32 = var31.getBuffer(6 * this.quadCount);
            GpuTextureView var29;
            GpuTextureView var30;
            if (var28 != null) {
               var29 = var28.getColorTextureView();
               var30 = var28.getDepthTextureView();
            } else {
               var29 = var27.getColorTextureView();
               var30 = var27.getDepthTextureView();
            }

            try (RenderPass var33 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Clouds", var29, OptionalInt.empty(), var30, OptionalDouble.empty())) {
               var33.setPipeline(var25);
               RenderSystem.bindDefaultUniforms(var33);
               var33.setUniform("DynamicTransforms", var45);
               var33.setIndexBuffer(var32, var31.type());
               var33.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
               var33.setUniform("CloudInfo", this.ubo.currentBuffer());
               var33.setUniform("CloudFaces", this.utb.currentBuffer());
               var33.setPipeline(var25);
               var33.drawIndexed(0, 0, 6 * this.quadCount, 1);
            }

         }
      }
   }

   private void buildMesh(RelativeCameraPos var1, ByteBuffer var2, int var3, int var4, boolean var5, int var6) {
      if (this.texture != null) {
         long[] var7 = this.texture.cells;
         int var8 = this.texture.width;
         int var9 = this.texture.height;

         for(int var10 = 0; var10 <= 2 * var6; ++var10) {
            for(int var11 = -var10; var11 <= var10; ++var11) {
               int var12 = var10 - Math.abs(var11);
               if (var12 >= 0 && var12 <= var6 && var11 * var11 + var12 * var12 <= var6 * var6) {
                  if (var12 != 0) {
                     this.tryBuildCell(var1, var2, var3, var4, var5, var11, var8, -var12, var9, var7);
                  }

                  this.tryBuildCell(var1, var2, var3, var4, var5, var11, var8, var12, var9, var7);
               }
            }
         }

      }
   }

   private void tryBuildCell(RelativeCameraPos var1, ByteBuffer var2, int var3, int var4, boolean var5, int var6, int var7, int var8, int var9, long[] var10) {
      int var11 = Math.floorMod(var3 + var6, var7);
      int var12 = Math.floorMod(var4 + var8, var9);
      long var13 = var10[var11 + var12 * var7];
      if (var13 != 0L) {
         if (var5) {
            this.buildExtrudedCell(var1, var2, var6, var8, var13);
         } else {
            this.buildFlatCell(var2, var6, var8);
         }

      }
   }

   private void buildFlatCell(ByteBuffer var1, int var2, int var3) {
      this.encodeFace(var1, var2, var3, Direction.DOWN, 32);
   }

   private void encodeFace(ByteBuffer var1, int var2, int var3, Direction var4, int var5) {
      int var6 = var4.get3DDataValue() | var5;
      var6 |= (var2 & 1) << 7;
      var6 |= (var3 & 1) << 6;
      var1.put((byte)(var2 >> 1)).put((byte)(var3 >> 1)).put((byte)var6);
   }

   private void buildExtrudedCell(RelativeCameraPos var1, ByteBuffer var2, int var3, int var4, long var5) {
      if (var1 != CloudRenderer.RelativeCameraPos.BELOW_CLOUDS) {
         this.encodeFace(var2, var3, var4, Direction.UP, 0);
      }

      if (var1 != CloudRenderer.RelativeCameraPos.ABOVE_CLOUDS) {
         this.encodeFace(var2, var3, var4, Direction.DOWN, 0);
      }

      if (isNorthEmpty(var5) && var4 > 0) {
         this.encodeFace(var2, var3, var4, Direction.NORTH, 0);
      }

      if (isSouthEmpty(var5) && var4 < 0) {
         this.encodeFace(var2, var3, var4, Direction.SOUTH, 0);
      }

      if (isWestEmpty(var5) && var3 > 0) {
         this.encodeFace(var2, var3, var4, Direction.WEST, 0);
      }

      if (isEastEmpty(var5) && var3 < 0) {
         this.encodeFace(var2, var3, var4, Direction.EAST, 0);
      }

      boolean var7 = Math.abs(var3) <= 1 && Math.abs(var4) <= 1;
      if (var7) {
         for(Direction var11 : Direction.values()) {
            this.encodeFace(var2, var3, var4, var11, 16);
         }
      }

   }

   public void markForRebuild() {
      this.needsRebuild = true;
   }

   public void endFrame() {
      this.ubo.rotate();
   }

   public void close() {
      this.ubo.close();
      if (this.utb != null) {
         this.utb.close();
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
