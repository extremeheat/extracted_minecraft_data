package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainParticle extends TextureSheetParticle {
   private final BlockPos pos;
   private final float uo;
   private final float vo;

   public TerrainParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, BlockState var14) {
      this(var1, var2, var4, var6, var8, var10, var12, var14, new BlockPos(var2, var4, var6));
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
         this.rCol *= (float)(var16 >> 16 & 0xFF) / 255.0F;
         this.gCol *= (float)(var16 >> 8 & 0xFF) / 255.0F;
         this.bCol *= (float)(var16 & 0xFF) / 255.0F;
      }

      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   @Override
   protected float getU0() {
      return this.sprite.getU((double)((this.uo + 1.0F) / 4.0F * 16.0F));
   }

   @Override
   protected float getU1() {
      return this.sprite.getU((double)(this.uo / 4.0F * 16.0F));
   }

   @Override
   protected float getV0() {
      return this.sprite.getV((double)(this.vo / 4.0F * 16.0F));
   }

   @Override
   protected float getV1() {
      return this.sprite.getV((double)((this.vo + 1.0F) / 4.0F * 16.0F));
   }

   @Override
   public int getLightColor(float var1) {
      int var2 = super.getLightColor(var1);
      return var2 == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : var2;
   }

   public static class Provider implements ParticleProvider<BlockParticleOption> {
      public Provider() {
         super();
      }

      public Particle createParticle(
         BlockParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13
      ) {
         BlockState var15 = var1.getState();
         return !var15.isAir() && !var15.is(Blocks.MOVING_PISTON) ? new TerrainParticle(var2, var3, var5, var7, var9, var11, var13, var15) : null;
      }
   }
}
