package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainParticle extends TextureSheetParticle {
   private final BlockPos pos;
   private final float uo;
   private final float vo;

   public TerrainParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, BlockState var14) {
      this(var1, var2, var4, var6, var8, var10, var12, var14, BlockPos.containing(var2, var4, var6));
   }

   public TerrainParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, BlockState var14, BlockPos var15) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.pos = var15;
      this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(var14));
      this.gravity = 1.0F;
      this.rCol = 0.6F;
      this.gCol = 0.6F;
      this.bCol = 0.6F;
      if (!var14.is(Blocks.GRASS_BLOCK)) {
         int var16 = Minecraft.getInstance().getBlockColors().getColor(var14, var1, var15, 0);
         this.rCol *= (float)(var16 >> 16 & 255) / 255.0F;
         this.gCol *= (float)(var16 >> 8 & 255) / 255.0F;
         this.bCol *= (float)(var16 & 255) / 255.0F;
      }

      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   protected float getU0() {
      return this.sprite.getU((this.uo + 1.0F) / 4.0F);
   }

   protected float getU1() {
      return this.sprite.getU(this.uo / 4.0F);
   }

   protected float getV0() {
      return this.sprite.getV(this.vo / 4.0F);
   }

   protected float getV1() {
      return this.sprite.getV((this.vo + 1.0F) / 4.0F);
   }

   public int getLightColor(float var1) {
      int var2 = super.getLightColor(var1);
      return var2 == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : var2;
   }

   @Nullable
   static TerrainParticle createTerrainParticle(BlockParticleOption var0, ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      BlockState var14 = var0.getState();
      return !var14.isAir() && !var14.is(Blocks.MOVING_PISTON) && var14.shouldSpawnTerrainParticles() ? new TerrainParticle(var1, var2, var4, var6, var8, var10, var12, var14) : null;
   }

   public static class Provider implements ParticleProvider<BlockParticleOption> {
      public Provider() {
         super();
      }

      @Nullable
      public Particle createParticle(BlockParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return TerrainParticle.createTerrainParticle(var1, var2, var3, var5, var7, var9, var11, var13);
      }

      // $FF: synthetic method
      @Nullable
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((BlockParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class DustPillarProvider implements ParticleProvider<BlockParticleOption> {
      public DustPillarProvider() {
         super();
      }

      @Nullable
      public Particle createParticle(BlockParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         TerrainParticle var15 = TerrainParticle.createTerrainParticle(var1, var2, var3, var5, var7, var9, var11, var13);
         if (var15 != null) {
            ((Particle)var15).setParticleSpeed(var2.random.nextGaussian() / 30.0, var11 + var2.random.nextGaussian() / 2.0, var2.random.nextGaussian() / 30.0);
            ((Particle)var15).setLifetime(var2.random.nextInt(20) + 20);
         }

         return var15;
      }

      // $FF: synthetic method
      @Nullable
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((BlockParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class CrumblingProvider implements ParticleProvider<BlockParticleOption> {
      public CrumblingProvider() {
         super();
      }

      @Nullable
      public Particle createParticle(BlockParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         TerrainParticle var15 = TerrainParticle.createTerrainParticle(var1, var2, var3, var5, var7, var9, var11, var13);
         if (var15 != null) {
            ((Particle)var15).setParticleSpeed(0.0, 0.0, 0.0);
            ((Particle)var15).setLifetime(var2.random.nextInt(10) + 1);
         }

         return var15;
      }

      // $FF: synthetic method
      @Nullable
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((BlockParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
