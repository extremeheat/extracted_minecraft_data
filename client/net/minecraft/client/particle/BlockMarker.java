package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMarker extends TextureSheetParticle {
   BlockMarker(ClientLevel var1, double var2, double var4, double var6, BlockState var8) {
      super(var1, var2, var4, var6);
      this.setSprite(Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(var8));
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

   public static class Provider implements ParticleProvider<BlockParticleOption> {
      public Provider() {
         super();
      }

      public Particle createParticle(BlockParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BlockMarker(var2, var3, var5, var7, var1.getState());
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((BlockParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
