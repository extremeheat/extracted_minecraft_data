package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BreakingItemParticle extends TextureSheetParticle {
   private final float uo;
   private final float vo;

   BreakingItemParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, ItemStack var14) {
      this(var1, var2, var4, var6, var14);
      this.xd *= 0.10000000149011612;
      this.yd *= 0.10000000149011612;
      this.zd *= 0.10000000149011612;
      this.xd += var8;
      this.yd += var10;
      this.zd += var12;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   protected BreakingItemParticle(ClientLevel var1, double var2, double var4, double var6, ItemStack var8) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(var8, var1, null, 0).getParticleIcon());
      this.gravity = 1.0F;
      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
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

   public static class Provider implements ParticleProvider<ItemParticleOption> {
      public Provider() {
         super();
      }

      public Particle createParticle(ItemParticleOption var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, var9, var11, var13, var1.getItem());
      }
   }

   public static class SlimeProvider implements ParticleProvider<SimpleParticleType> {
      public SlimeProvider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, new ItemStack(Items.SLIME_BALL));
      }
   }

   public static class SnowballProvider implements ParticleProvider<SimpleParticleType> {
      public SnowballProvider() {
         super();
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, new ItemStack(Items.SNOWBALL));
      }
   }
}
