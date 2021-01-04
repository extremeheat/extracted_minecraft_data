package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TerrainParticle extends TextureSheetParticle {
   private final BlockState blockState;
   private BlockPos pos;
   private final float uo;
   private final float vo;

   public TerrainParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, BlockState var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.blockState = var14;
      this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(var14));
      this.gravity = 1.0F;
      this.rCol = 0.6F;
      this.gCol = 0.6F;
      this.bCol = 0.6F;
      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   public TerrainParticle init(BlockPos var1) {
      this.pos = var1;
      if (this.blockState.getBlock() == Blocks.GRASS_BLOCK) {
         return this;
      } else {
         this.multiplyColor(var1);
         return this;
      }
   }

   public TerrainParticle init() {
      this.pos = new BlockPos(this.x, this.y, this.z);
      Block var1 = this.blockState.getBlock();
      if (var1 == Blocks.GRASS_BLOCK) {
         return this;
      } else {
         this.multiplyColor(this.pos);
         return this;
      }
   }

   protected void multiplyColor(@Nullable BlockPos var1) {
      int var2 = Minecraft.getInstance().getBlockColors().getColor(this.blockState, this.level, var1, 0);
      this.rCol *= (float)(var2 >> 16 & 255) / 255.0F;
      this.gCol *= (float)(var2 >> 8 & 255) / 255.0F;
      this.bCol *= (float)(var2 & 255) / 255.0F;
   }

   protected float getU0() {
      return this.sprite.getU((double)((this.uo + 1.0F) / 4.0F * 16.0F));
   }

   protected float getU1() {
      return this.sprite.getU((double)(this.uo / 4.0F * 16.0F));
   }

   protected float getV0() {
      return this.sprite.getV((double)(this.vo / 4.0F * 16.0F));
   }

   protected float getV1() {
      return this.sprite.getV((double)((this.vo + 1.0F) / 4.0F * 16.0F));
   }

   public int getLightColor(float var1) {
      int var2 = super.getLightColor(var1);
      int var3 = 0;
      if (this.level.hasChunkAt(this.pos)) {
         var3 = this.level.getLightColor(this.pos, 0);
      }

      return var2 == 0 ? var3 : var2;
   }

   public static class Provider implements ParticleProvider<BlockParticleOption> {
      public Provider() {
         super();
      }

      public Particle createParticle(BlockParticleOption var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         BlockState var15 = var1.getState();
         return !var15.isAir() && var15.getBlock() != Blocks.MOVING_PISTON ? (new TerrainParticle(var2, var3, var5, var7, var9, var11, var13, var15)).init() : null;
      }
   }
}
