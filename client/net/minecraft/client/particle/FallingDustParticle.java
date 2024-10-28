package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class FallingDustParticle extends TextureSheetParticle {
   private final float rotSpeed;
   private final SpriteSet sprites;

   FallingDustParticle(ClientLevel var1, double var2, double var4, double var6, float var8, float var9, float var10, SpriteSet var11) {
      super(var1, var2, var4, var6);
      this.sprites = var11;
      this.rCol = var8;
      this.gCol = var9;
      this.bCol = var10;
      float var12 = 0.9F;
      this.quadSize *= 0.67499995F;
      int var13 = (int)(32.0 / (Math.random() * 0.8 + 0.2));
      this.lifetime = (int)Math.max((float)var13 * 0.9F, 1.0F);
      this.setSpriteFromAge(var11);
      this.rotSpeed = ((float)Math.random() - 0.5F) * 0.1F;
      this.roll = (float)Math.random() * 6.2831855F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.oRoll = this.roll;
         this.roll += 3.1415927F * this.rotSpeed * 2.0F;
         if (this.onGround) {
            this.oRoll = this.roll = 0.0F;
         }

         this.move(this.xd, this.yd, this.zd);
         this.yd -= 0.003000000026077032;
         this.yd = Math.max(this.yd, -0.14000000059604645);
      }
   }

   public static class Provider implements ParticleProvider<BlockParticleOption> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      @Nullable
      public Particle createParticle(BlockParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         BlockState var15 = var1.getState();
         if (!var15.isAir() && var15.getRenderShape() == RenderShape.INVISIBLE) {
            return null;
         } else {
            BlockPos var16 = BlockPos.containing(var3, var5, var7);
            int var17 = Minecraft.getInstance().getBlockColors().getColor(var15, var2, var16);
            if (var15.getBlock() instanceof FallingBlock) {
               var17 = ((FallingBlock)var15.getBlock()).getDustColor(var15, var2, var16);
            }

            float var18 = (float)(var17 >> 16 & 255) / 255.0F;
            float var19 = (float)(var17 >> 8 & 255) / 255.0F;
            float var20 = (float)(var17 & 255) / 255.0F;
            return new FallingDustParticle(var2, var3, var5, var7, var18, var19, var20, this.sprite);
         }
      }

      // $FF: synthetic method
      @Nullable
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((BlockParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
