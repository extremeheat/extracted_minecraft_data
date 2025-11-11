package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMarker extends SingleQuadParticle {
   private final SingleQuadParticle.Layer layer;

   BlockMarker(ClientLevel var1, double var2, double var4, double var6, BlockState var8) {
      super(var1, var2, var4, var6, Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(var8));
      this.gravity = 0.0F;
      this.lifetime = 80;
      this.hasPhysics = false;
      this.layer = this.sprite.atlasLocation().equals(TextureAtlas.LOCATION_BLOCKS) ? SingleQuadParticle.Layer.TERRAIN : SingleQuadParticle.Layer.ITEMS;
   }

   public SingleQuadParticle.Layer getLayer() {
      return this.layer;
   }

   public float getQuadSize(float var1) {
      return 0.5F;
   }

   public static class Provider implements ParticleProvider<BlockParticleOption> {
      public Provider() {
         super();
      }

      public Particle createParticle(BlockParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new BlockMarker(var2, var3, var5, var7, var1.getState());
      }
   }
}
