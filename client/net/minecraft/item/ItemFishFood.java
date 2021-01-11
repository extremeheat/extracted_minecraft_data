package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   private final boolean field_150907_b;

   public ItemFishFood(boolean var1) {
      super(0, 0.0F, false);
      this.field_150907_b = var1;
   }

   public int func_150905_g(ItemStack var1) {
      ItemFishFood.FishType var2 = ItemFishFood.FishType.func_150978_a(var1);
      return this.field_150907_b && var2.func_150973_i() ? var2.func_150970_e() : var2.func_150975_c();
   }

   public float func_150906_h(ItemStack var1) {
      ItemFishFood.FishType var2 = ItemFishFood.FishType.func_150978_a(var1);
      return this.field_150907_b && var2.func_150973_i() ? var2.func_150977_f() : var2.func_150967_d();
   }

   public String func_150896_i(ItemStack var1) {
      return ItemFishFood.FishType.func_150978_a(var1) == ItemFishFood.FishType.PUFFERFISH ? PotionHelper.field_151423_m : null;
   }

   protected void func_77849_c(ItemStack var1, World var2, EntityPlayer var3) {
      ItemFishFood.FishType var4 = ItemFishFood.FishType.func_150978_a(var1);
      if (var4 == ItemFishFood.FishType.PUFFERFISH) {
         var3.func_70690_d(new PotionEffect(Potion.field_76436_u.field_76415_H, 1200, 3));
         var3.func_70690_d(new PotionEffect(Potion.field_76438_s.field_76415_H, 300, 2));
         var3.func_70690_d(new PotionEffect(Potion.field_76431_k.field_76415_H, 300, 1));
      }

      super.func_77849_c(var1, var2, var3);
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      ItemFishFood.FishType[] var4 = ItemFishFood.FishType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         ItemFishFood.FishType var7 = var4[var6];
         if (!this.field_150907_b || var7.func_150973_i()) {
            var3.add(new ItemStack(this, 1, var7.func_150976_a()));
         }
      }

   }

   public String func_77667_c(ItemStack var1) {
      ItemFishFood.FishType var2 = ItemFishFood.FishType.func_150978_a(var1);
      return this.func_77658_a() + "." + var2.func_150972_b() + "." + (this.field_150907_b && var2.func_150973_i() ? "cooked" : "raw");
   }

   public static enum FishType {
      COD(0, "cod", 2, 0.1F, 5, 0.6F),
      SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
      CLOWNFISH(2, "clownfish", 1, 0.1F),
      PUFFERFISH(3, "pufferfish", 1, 0.1F);

      private static final Map<Integer, ItemFishFood.FishType> field_150983_e = Maps.newHashMap();
      private final int field_150980_f;
      private final String field_150981_g;
      private final int field_150991_j;
      private final float field_150992_k;
      private final int field_150989_l;
      private final float field_150990_m;
      private boolean field_150987_n = false;

      private FishType(int var3, String var4, int var5, float var6, int var7, float var8) {
         this.field_150980_f = var3;
         this.field_150981_g = var4;
         this.field_150991_j = var5;
         this.field_150992_k = var6;
         this.field_150989_l = var7;
         this.field_150990_m = var8;
         this.field_150987_n = true;
      }

      private FishType(int var3, String var4, int var5, float var6) {
         this.field_150980_f = var3;
         this.field_150981_g = var4;
         this.field_150991_j = var5;
         this.field_150992_k = var6;
         this.field_150989_l = 0;
         this.field_150990_m = 0.0F;
         this.field_150987_n = false;
      }

      public int func_150976_a() {
         return this.field_150980_f;
      }

      public String func_150972_b() {
         return this.field_150981_g;
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

      public static ItemFishFood.FishType func_150974_a(int var0) {
         ItemFishFood.FishType var1 = (ItemFishFood.FishType)field_150983_e.get(var0);
         return var1 == null ? COD : var1;
      }

      public static ItemFishFood.FishType func_150978_a(ItemStack var0) {
         return var0.func_77973_b() instanceof ItemFishFood ? func_150974_a(var0.func_77960_j()) : COD;
      }

      static {
         ItemFishFood.FishType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            ItemFishFood.FishType var3 = var0[var2];
            field_150983_e.put(var3.func_150976_a(), var3);
         }

      }
   }
}
