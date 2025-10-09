package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.WeatherRenderState;
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
   private static final float RAIN_PARTICLES_PER_BLOCK = 0.225F;
   private static final int RAIN_RADIUS = 10;
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

   public void extractRenderState(Level var1, int var2, float var3, Vec3 var4, WeatherRenderState var5) {
      var5.intensity = var1.getRainLevel(var3);
      if (!(var5.intensity <= 0.0F)) {
         var5.radius = (Integer)Minecraft.getInstance().options.weatherRadius().get();
         int var6 = Mth.floor(var4.x);
         int var7 = Mth.floor(var4.y);
         int var8 = Mth.floor(var4.z);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
         RandomSource var10 = RandomSource.create();

         for(int var11 = var8 - var5.radius; var11 <= var8 + var5.radius; ++var11) {
            for(int var12 = var6 - var5.radius; var12 <= var6 + var5.radius; ++var12) {
               int var13 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var12, var11);
               int var14 = Math.max(var7 - var5.radius, var13);
               int var15 = Math.max(var7 + var5.radius, var13);
               if (var15 - var14 != 0) {
                  Biome.Precipitation var16 = this.getPrecipitationAt(var1, var9.set(var12, var7, var11));
                  if (var16 != Biome.Precipitation.NONE) {
                     int var17 = var12 * var12 * 3121 + var12 * 45238971 ^ var11 * var11 * 418711 + var11 * 13761;
                     var10.setSeed((long)var17);
                     int var18 = Math.max(var7, var13);
                     int var19 = LevelRenderer.getLightColor(var1, var9.set(var12, var18, var11));
                     if (var16 == Biome.Precipitation.RAIN) {
                        var5.rainColumns.add(this.createRainColumnInstance(var10, var2, var12, var14, var15, var11, var19, var3));
                     } else if (var16 == Biome.Precipitation.SNOW) {
                        var5.snowColumns.add(this.createSnowColumnInstance(var10, var2, var12, var14, var15, var11, var19, var3));
                     }
                  }
               }
            }
         }

      }
   }

   public void render(MultiBufferSource var1, Vec3 var2, WeatherRenderState var3) {
      if (!var3.rainColumns.isEmpty()) {
         RenderType var4 = RenderType.weather(RAIN_LOCATION, Minecraft.useShaderTransparency());
         this.renderInstances(var1.getBuffer(var4), var3.rainColumns, var2, 1.0F, var3.radius, var3.intensity);
      }

      if (!var3.snowColumns.isEmpty()) {
         RenderType var5 = RenderType.weather(SNOW_LOCATION, Minecraft.useShaderTransparency());
         this.renderInstances(var1.getBuffer(var5), var3.snowColumns, var2, 0.8F, var3.radius, var3.intensity);
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
      float var7 = (float)(var5 * var5);

      for(ColumnInstance var9 : var2) {
         float var10 = (float)((double)var9.x + 0.5 - var3.x);
         float var11 = (float)((double)var9.z + 0.5 - var3.z);
         float var12 = (float)Mth.lengthSquared((double)var10, (double)var11);
         float var13 = Mth.lerp(Math.min(var12 / var7, 1.0F), var4, 0.5F) * var6;
         int var14 = ARGB.white(var13);
         int var15 = (var9.z - Mth.floor(var3.z) + 16) * 32 + var9.x - Mth.floor(var3.x) + 16;
         float var16 = this.columnSizeX[var15] / 2.0F;
         float var17 = this.columnSizeZ[var15] / 2.0F;
         float var18 = var10 - var16;
         float var19 = var10 + var16;
         float var20 = (float)((double)var9.topY - var3.y);
         float var21 = (float)((double)var9.bottomY - var3.y);
         float var22 = var11 - var17;
         float var23 = var11 + var17;
         float var24 = var9.uOffset + 0.0F;
         float var25 = var9.uOffset + 1.0F;
         float var26 = (float)var9.bottomY * 0.25F + var9.vOffset;
         float var27 = (float)var9.topY * 0.25F + var9.vOffset;
         var1.addVertex(var18, var20, var22).setUv(var24, var26).setColor(var14).setLight(var9.lightCoords);
         var1.addVertex(var19, var20, var23).setUv(var25, var26).setColor(var14).setLight(var9.lightCoords);
         var1.addVertex(var19, var21, var23).setUv(var25, var27).setColor(var14).setLight(var9.lightCoords);
         var1.addVertex(var18, var21, var22).setUv(var24, var27).setColor(var14).setLight(var9.lightCoords);
      }

   }

   public void tickRainParticles(ClientLevel var1, Camera var2, int var3, ParticleStatus var4, int var5) {
      float var6 = var1.getRainLevel(1.0F);
      if (!(var6 <= 0.0F)) {
         RandomSource var7 = RandomSource.create((long)var3 * 312987231L);
         BlockPos var8 = BlockPos.containing(var2.position());
         BlockPos var9 = null;
         int var10 = 2 * var5 + 1;
         int var11 = var10 * var10;
         int var12 = (int)(0.225F * (float)var11 * var6 * var6) / (var4 == ParticleStatus.DECREASED ? 2 : 1);

         for(int var13 = 0; var13 < var12; ++var13) {
            int var14 = var7.nextInt(var10) - var5;
            int var15 = var7.nextInt(var10) - var5;
            BlockPos var16 = var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var8.offset(var14, 0, var15));
            if (var16.getY() > var1.getMinY() && var16.getY() <= var8.getY() + 10 && var16.getY() >= var8.getY() - 10 && this.getPrecipitationAt(var1, var16) == Biome.Precipitation.RAIN) {
               var9 = var16.below();
               if (var4 == ParticleStatus.MINIMAL) {
                  break;
               }

               double var17 = var7.nextDouble();
               double var19 = var7.nextDouble();
               BlockState var21 = var1.getBlockState(var9);
               FluidState var22 = var1.getFluidState(var9);
               VoxelShape var23 = var21.getCollisionShape(var1, var9);
               double var24 = var23.max(Direction.Axis.Y, var17, var19);
               double var26 = (double)var22.getHeight(var1, var9);
               double var28 = Math.max(var24, var26);
               SimpleParticleType var30 = !var22.is(FluidTags.LAVA) && !var21.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(var21) ? ParticleTypes.RAIN : ParticleTypes.SMOKE;
               var1.addParticle(var30, (double)var9.getX() + var17, (double)var9.getY() + var28, (double)var9.getZ() + var19, 0.0, 0.0, 0.0);
            }
         }

         if (var9 != null && var7.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (var9.getY() > var8.getY() + 1 && var1.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var8).getY() > Mth.floor((float)var8.getY())) {
               var1.playLocalSound(var9, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
            } else {
               var1.playLocalSound(var9, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
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

   public static record ColumnInstance(int x, int z, int bottomY, int topY, float uOffset, float vOffset, int lightCoords) {
      final int x;
      final int z;
      final int bottomY;
      final int topY;
      final float uOffset;
      final float vOffset;
      final int lightCoords;

      public ColumnInstance(int var1, int var2, int var3, int var4, float var5, float var6, int var7) {
         super();
         this.x = var1;
         this.z = var2;
         this.bottomY = var3;
         this.topY = var4;
         this.uOffset = var5;
         this.vOffset = var6;
         this.lightCoords = var7;
      }
   }
}
