package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   private final boolean field_150907_b;
   private final ItemFishFood.FishType field_195971_c;

   public ItemFishFood(ItemFishFood.FishType var1, boolean var2, Item.Properties var3) {
      super(0, 0.0F, false, var3);
      this.field_195971_c = var1;
      this.field_150907_b = var2;
   }

   public int func_150905_g(ItemStack var1) {
      ItemFishFood.FishType var2 = ItemFishFood.FishType.func_150978_a(var1);
      return this.field_150907_b && var2.func_150973_i() ? var2.func_150970_e() : var2.func_150975_c();
   }

   public float func_150906_h(ItemStack var1) {
      return this.field_150907_b && this.field_195971_c.func_150973_i() ? this.field_195971_c.func_150977_f() : this.field_195971_c.func_150967_d();
   }

   protected void func_77849_c(ItemStack var1, World var2, EntityPlayer var3) {
      ItemFishFood.FishType var4 = ItemFishFood.FishType.func_150978_a(var1);
      if (var4 == ItemFishFood.FishType.PUFFERFISH) {
         var3.func_195064_c(new PotionEffect(MobEffects.field_76436_u, 1200, 3));
         var3.func_195064_c(new PotionEffect(MobEffects.field_76438_s, 300, 2));
         var3.func_195064_c(new PotionEffect(MobEffects.field_76431_k, 300, 1));
      }

      super.func_77849_c(var1, var2, var3);
   }

   public static enum FishType {
      COD(2, 0.1F, 5, 0.6F),
      SALMON(2, 0.1F, 6, 0.8F),
      TROPICAL_FISH(1, 0.1F),
      PUFFERFISH(1, 0.1F);

      private final int field_150991_j;
      private final float field_150992_k;
      private final int field_150989_l;
      private final float field_150990_m;
      private final boolean field_150987_n;

      private FishType(int var3, float var4, int var5, float var6) {
         this.field_150991_j = var3;
         this.field_150992_k = var4;
         this.field_150989_l = var5;
         this.field_150990_m = var6;
         this.field_150987_n = var5 != 0;
      }

      private FishType(int var3, float var4) {
         this(var3, var4, 0, 0.0F);
      }

      public int func_150975_c() {
         return this.field_150991_j;
      }

      public float func_150967_d() {
         return this.field_150992_k;
      }

      public int func_150970_e() {
         return this.field_150989_l;
      }

      public float func_150977_f() {
         return this.field_150990_m;
      }

      public boolean func_150973_i() {
         return this.field_150987_n;
      }

      public static ItemFishFood.FishType func_150978_a(ItemStack var0) {
         Item var1 = var0.func_77973_b();
         return var1 instanceof ItemFishFood ? ((ItemFishFood)var1).field_195971_c : COD;
      }
   }
}
