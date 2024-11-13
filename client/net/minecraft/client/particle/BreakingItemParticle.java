package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BreakingItemParticle extends TextureSheetParticle {
   private final float uo;
   private final float vo;

   BreakingItemParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, ItemStackRenderState var14) {
      this(var1, var2, var4, var6, var14);
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      this.xd += var8;
      this.yd += var10;
      this.zd += var12;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   protected BreakingItemParticle(ClientLevel var1, double var2, double var4, double var6, ItemStackRenderState var8) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      TextureAtlasSprite var9 = var8.pickParticleIcon(this.random);
      if (var9 != null) {
         this.setSprite(var9);
      } else {
         this.setSprite((TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation()));
      }

      this.gravity = 1.0F;
      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
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

   public abstract static class ItemParticleProvider<T extends ParticleOptions> implements ParticleProvider<T> {
      private final ItemStackRenderState scratchRenderState = new ItemStackRenderState();

      public ItemParticleProvider() {
         super();
      }

      protected ItemStackRenderState calculateState(ItemStack var1, ClientLevel var2) {
         Minecraft.getInstance().getItemModelResolver().updateForTopItem(this.scratchRenderState, var1, ItemDisplayContext.GROUND, false, var2, (LivingEntity)null, 0);
         return this.scratchRenderState;
      }
   }

   public static class Provider extends ItemParticleProvider<ItemParticleOption> {
      public Provider() {
         super();
      }

      public Particle createParticle(ItemParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, var9, var11, var13, this.calculateState(var1.getItem(), var2));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((ItemParticleOption)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class SlimeProvider extends ItemParticleProvider<SimpleParticleType> {
      public SlimeProvider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, this.calculateState(new ItemStack(Items.SLIME_BALL), var2));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class CobwebProvider extends ItemParticleProvider<SimpleParticleType> {
      public CobwebProvider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, this.calculateState(new ItemStack(Items.COBWEB), var2));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class SnowballProvider extends ItemParticleProvider<SimpleParticleType> {
      public SnowballProvider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, this.calculateState(new ItemStack(Items.SNOWBALL), var2));
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
