package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeatherEffectRenderer {
   private static final int RAIN_RADIUS = 10;
   private static final int RAIN_DIAMETER = 21;
   private static final ResourceLocation RAIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/rain.png");
   private static final ResourceLocation SNOW_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");
   private static final int RAIN_TABLE_SIZE = 32;
   private static final int HALF_RAIN_TABLE_SIZE = 16;
   private int rainSoundTime;
   private final float[] columnSizeX = new float[1024];
   private final float[] columnSizeZ = new float[1024];

   public WeatherEffectRenderer() {
      super();

      for(int var1 = 0; var1 < 32; ++var1) {
         for(int var2 = 0; var2 < 32; ++var2) {
            float var3 = (float)(var2 - 16);
            float var4 = (float)(var1 - 16);
            float var5 = Mth.length(var3, var4);
            this.columnSizeX[var1 * 32 + var2] = -var4 / var5;
            this.columnSizeZ[var1 * 32 + var2] = var3 / var5;
         }
      }

   }

   public void render(Level var1, MultiBufferSource var2, int var3, float var4, Vec3 var5) {
      float var6 = var1.getRainLevel(var4);
      if (!(var6 <= 0.0F)) {
         int var7 = Minecraft.useFancyGraphics() ? 10 : 5;
         ArrayList var8 = new ArrayList();
         ArrayList var9 = new ArrayList();
         this.collectColumnInstances(var1, var3, var4, var5, var7, var8, var9);
         if (!var8.isEmpty() || !var9.isEmpty()) {
            this.render(var2, var5, var7, var6, var8, var9);
         }

      }
   }

   private void collectColumnInstances(Level var1, int var2, float var3, Vec3 var4, int var5, List<ColumnInstance> var6, List<ColumnInstance> var7) {
      int var8 = Mth.floor(var4.x);
      int var9 = Mth.floor(var4.y);
      int var10 = Mth.floor(var4.z);
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
      RandomSource var12 = RandomSource.create();

      for(int var13 = var10 - var5; var13 <= var10 + var5; ++var13) {
         for(int var14 = var8 - var5; var14 <= var8 + var5; ++var14) {
            int var15 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var14, var13);
            int var16 = Math.max(var9 - var5, var15);
            int var17 = Math.max(var9 + var5, var15);
            if (var17 - var16 != 0) {
               Biome.Precipitation var18 = this.getPrecipitationAt(var1, var11.set(var14, var9, var13));
               if (var18 != Biome.Precipitation.NONE) {
                  int var19 = var14 * var14 * 3121 + var14 * 45238971 ^ var13 * var13 * 418711 + var13 * 13761;
                  var12.setSeed((long)var19);
                  int var20 = Math.max(var9, var15);
                  int var21 = LevelRenderer.getLightColor(var1, var11.set(var14, var20, var13));
                  if (var18 == Biome.Precipitation.RAIN) {
                     var6.add(this.createRainColumnInstance(var12, var2, var14, var16, var17, var13, var21, var3));
                  } else if (var18 == Biome.Precipitation.SNOW) {
                     var7.add(this.createSnowColumnInstance(var12, var2, var14, var16, var17, var13, var21, var3));
                  }
               }
            }
         }
      }

   }

   private void render(MultiBufferSource var1, Vec3 var2, int var3, float var4, List<ColumnInstance> var5, List<ColumnInstance> var6) {
      RenderType var7;
      if (!var5.isEmpty()) {
         var7 = RenderType.weather(RAIN_LOCATION, Minecraft.useShaderTransparency());
         this.renderInstances(var1.getBuffer(var7), var5, var2, 1.0F, var3, var4);
      }

      if (!var6.isEmpty()) {
         var7 = RenderType.weather(SNOW_LOCATION, Minecraft.useShaderTransparency());
         this.renderInstances(var1.getBuffer(var7), var6, var2, 0.8F, var3, var4);
      }

   }

   private ColumnInstance createRainColumnInstance(RandomSource var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8) {
      int var9 = var2 & 131071;
      int var10 = var3 * var3 * 3121 + var3 * 45238971 + var6 * var6 * 418711 + var6 * 13761 & 255;
      float var11 = 3.0F + var1.nextFloat();
      float var12 = -((float)(var9 + var10) + var8) / 32.0F * var11;
      float var13 = var12 % 32.0F;
      return new ColumnInstance(var3, var6, var4, var5, 0.0F, var13, var7);
   }

   private ColumnInstance createSnowColumnInstance(RandomSource var1, int var2, int var3, int var4, int var5, int var6, int var7, float var8) {
      float var9 = (float)var2 + var8;
      float var10 = (float)(var1.nextDouble() + (double)(var9 * 0.01F * (float)var1.nextGaussian()));
      float var11 = (float)(var1.nextDouble() + (double)(var9 * (float)var1.nextGaussian() * 0.001F));
      float var12 = -((float)(var2 & 511) + var8) / 512.0F;
      int var13 = LightTexture.pack((LightTexture.block(var7) * 3 + 15) / 4, (LightTexture.sky(var7) * 3 + 15) / 4);
      return new ColumnInstance(var3, var6, var4, var5, var10, var12 + var11, var13);
   }

   private void renderInstances(VertexConsumer var1, List<ColumnInstance> var2, Vec3 var3, float var4, int var5, float var6) {
      Iterator var7 = var2.iterator();

      while(var7.hasNext()) {
         ColumnInstance var8 = (ColumnInstance)var7.next();
         float var9 = (float)((double)var8.x + 0.5 - var3.x);
         float var10 = (float)((double)var8.z + 0.5 - var3.z);
         float var11 = (float)Mth.lengthSquared((double)var9, (double)var10);
         float var12 = Mth.lerp(var11 / (float)(var5 * var5), var4, 0.5F) * var6;
         int var13 = ARGB.white(var12);
         int var14 = (var8.z - Mth.floor(var3.z) + 16) * 32 + var8.x - Mth.floor(var3.x) + 16;
         float var15 = this.columnSizeX[var14] / 2.0F;
         float var16 = this.columnSizeZ[var14] / 2.0F;
         float var17 = var9 - var15;
         float var18 = var9 + var15;
         float var19 = (float)((double)var8.topY - var3.y);
         float var20 = (float)((double)var8.bottomY - var3.y);
         float var21 = var10 - var16;
         float var22 = var10 + var16;
         float var23 = var8.uOffset + 0.0F;
         float var24 = var8.uOffset + 1.0F;
         float var25 = (float)var8.bottomY * 0.25F + var8.vOffset;
         float var26 = (float)var8.topY * 0.25F + var8.vOffset;
         var1.addVertex(var17, var19, var21).setUv(var23, var25).setColor(var13).setLight(var8.lightCoords);
         var1.addVertex(var18, var19, var22).setUv(var24, var25).setColor(var13).setLight(var8.lightCoords);
         var1.addVertex(var18, var20, var22).setUv(var24, var26).setColor(var13).setLight(var8.lightCoords);
         var1.addVertex(var17, var20, var21).setUv(var23, var26).setColor(var13).setLight(var8.lightCoords);
      }

   }

   public void tickRainParticles(ClientLevel var1, Camera var2, int var3, ParticleStatus var4) {
      float var5 = var1.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
      if (!(var5 <= 0.0F)) {
         RandomSource var6 = RandomSource.create((long)var3 * 312987231L);
         BlockPos var7 = BlockPos.containing(var2.getPosition());
         BlockPos var8 = null;
         int var9 = (int)(100.0F * var5 * var5) / (var4 == ParticleStatus.DECREASED ? 2 : 1);

         for(int var10 = 0; var10 < var9; ++var10) {
            int var11 = var6.nextInt(21) - 10;
            int var12 = var6.nextInt(21) - 10;
            BlockPos var13 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var7.offset(var11, 0, var12));
            if (var13.getY() > var1.getMinY() && var13.getY() <= var7.getY() + 10 && var13.getY() >= var7.getY() - 10 && this.getPrecipitationAt(var1, var13) == Biome.Precipitation.RAIN) {
               var8 = var13.below();
               if (var4 == ParticleStatus.MINIMAL) {
                  break;
               }

               double var14 = var6.nextDouble();
               double var16 = var6.nextDouble();
               BlockState var18 = var1.getBlockState(var8);
               FluidState var19 = var1.getFluidState(var8);
               VoxelShape var20 = var18.getCollisionShape(var1, var8);
               double var21 = var20.max(Direction.Axis.Y, var14, var16);
               double var23 = (double)var19.getHeight(var1, var8);
               double var25 = Math.max(var21, var23);
               SimpleParticleType var27 = !var19.is(FluidTags.LAVA) && !var18.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(var18) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
               var1.addParticle(var27, (double)var8.getX() + var14, (double)var8.getY() + var25, (double)var8.getZ() + var16, 0.0, 0.0, 0.0);
            }
         }

         if (var8 != null && var6.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (var8.getY() > var7.getY() + 1 && var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var7).getY() > Mth.floor((float)var7.getY())) {
               var1.playLocalSound(var8, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
            } else {
               var1.playLocalSound(var8, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   private Biome.Precipitation getPrecipitationAt(Level var1, BlockPos var2) {
      if (!var1.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(var2.getX()), SectionPos.blockToSectionCoord(var2.getZ()))) {
         return Biome.Precipitation.NONE;
      } else {
         Biome var3 = (Biome)var1.getBiome(var2).value();
         return var3.getPrecipitationAt(var2, var1.getSeaLevel());
      }
   }

   private static record ColumnInstance(int x, int z, int bottomY, int topY, float uOffset, float vOffset, int lightCoords) {
      final int x;
      final int z;
      final int bottomY;
      final int topY;
      final float uOffset;
      final float vOffset;
      final int lightCoords;

      ColumnInstance(int var1, int var2, int var3, int var4, float var5, float var6, int var7) {
         super();
         this.x = var1;
         this.z = var2;
         this.bottomY = var3;
         this.topY = var4;
         this.uOffset = var5;
         this.vOffset = var6;
         this.lightCoords = var7;
      }

      public int x() {
         return this.x;
      }

      public int z() {
         return this.z;
      }

      public int bottomY() {
         return this.bottomY;
      }

      public int topY() {
         return this.topY;
      }

      public float uOffset() {
         return this.uOffset;
      }

      public float vOffset() {
         return this.vOffset;
      }

      public int lightCoords() {
         return this.lightCoords;
      }
   }
}
