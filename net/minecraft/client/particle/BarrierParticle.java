package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class BarrierParticle extends TextureSheetParticle {
   private BarrierParticle(Level var1, double var2, double var4, double var6, ItemLike var8) {
      super(var1, var2, var4, var6);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getItemModelShaper().getParticleIcon(var8));
      this.gravity = 0.0F;
      this.lifetime = 80;
      this.hasPhysics = false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   public float getQuadSize(float var1) {
      return 0.5F;
   }

   // $FF: synthetic method
   BarrierParticle(Level var1, double var2, double var4, double var6, ItemLike var8, Object var9) {
      this(var1, var2, var4, var6, var8);
   }

   public static class Provider implements ParticleProvider {
      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BarrierParticle(var2, var3, var5, var7, Blocks.BARRIER.asItem());
      }
   }
}
