package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.Material;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.AtlasIds;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public class BreakingItemParticle extends SingleQuadParticle {
   private final float uo;
   private final float vo;
   private final SingleQuadParticle.Layer layer;

   private BreakingItemParticle(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za, final TextureAtlasSprite sprite) {
      this(level, x, y, z, sprite);
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      this.xd += xa;
      this.yd += ya;
      this.zd += za;
   }

   protected BreakingItemParticle(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
      super(level, x, y, z, 0.0, 0.0, 0.0, sprite);
      this.gravity = 1.0F;
      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
      this.layer = SingleQuadParticle.Layer.bySprite(sprite);
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

   public SingleQuadParticle.Layer getLayer() {
      return this.layer;
   }

   public abstract static class ItemParticleProvider<T extends ParticleOptions> implements ParticleProvider<T> {
      private final ItemStackRenderState scratchRenderState = new ItemStackRenderState();

      public ItemParticleProvider() {
         super();
      }

      protected TextureAtlasSprite getSprite(final ItemStackTemplate item, final ClientLevel level, final RandomSource random) {
         Minecraft.getInstance().getItemModelResolver().updateForTopItem(this.scratchRenderState, item.create(), ItemDisplayContext.GROUND, level, (ItemOwner)null, 0);
         Material.Baked material = this.scratchRenderState.pickParticleMaterial(random);
         return material != null ? material.sprite() : Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.ITEMS).missingSprite();
      }
   }

   public static class Provider extends ItemParticleProvider<ItemParticleOption> {
      public Provider() {
         super();
      }

      public Particle createParticle(final ItemParticleOption options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         return new BreakingItemParticle(level, x, y, z, xAux, yAux, zAux, this.getSprite(options.getItem(), level, random));
      }
   }

   public static class SlimeProvider extends ItemParticleProvider<SimpleParticleType> {
      public SlimeProvider() {
         super();
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         return new BreakingItemParticle(level, x, y, z, this.getSprite(new ItemStackTemplate(Items.SLIME_BALL), level, random));
      }
   }

   public static class CobwebProvider extends ItemParticleProvider<SimpleParticleType> {
      public CobwebProvider() {
         super();
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         return new BreakingItemParticle(level, x, y, z, this.getSprite(new ItemStackTemplate(Items.COBWEB), level, random));
      }
   }

   public static class SnowballProvider extends ItemParticleProvider<SimpleParticleType> {
      public SnowballProvider() {
         super();
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         return new BreakingItemParticle(level, x, y, z, this.getSprite(new ItemStackTemplate(Items.SNOWBALL), level, random));
      }
   }
}
