package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BreakingItemParticle extends TextureSheetParticle {
   private final float uo;
   private final float vo;

   private BreakingItemParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, ItemStack var14) {
      this(var1, var2, var4, var6, var14);
      this.xd *= 0.10000000149011612D;
      this.yd *= 0.10000000149011612D;
      this.zd *= 0.10000000149011612D;
      this.xd += var8;
      this.yd += var10;
      this.zd += var12;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.TERRAIN_SHEET;
   }

   protected BreakingItemParticle(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(var8, var1, (LivingEntity)null).getParticleIcon());
      this.gravity = 1.0F;
      this.quadSize /= 2.0F;
      this.uo = this.random.nextFloat() * 3.0F;
      this.vo = this.random.nextFloat() * 3.0F;
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

   // $FF: synthetic method
   BreakingItemParticle(Level var1, double var2, double var4, double var6, double var8, double var10, double var12, ItemStack var14, Object var15) {
      this(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public static class SnowballProvider implements ParticleProvider {
      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, new ItemStack(Items.SNOWBALL));
      }
   }

   public static class SlimeProvider implements ParticleProvider {
      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, new ItemStack(Items.SLIME_BALL));
      }
   }

   public static class Provider implements ParticleProvider {
      public Particle createParticle(ItemParticleOption var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new BreakingItemParticle(var2, var3, var5, var7, var9, var11, var13, var1.getItem());
      }
   }
}
