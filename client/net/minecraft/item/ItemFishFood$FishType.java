package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public enum ItemFishFood$FishType {
   COD(0, "cod", 2, 0.1F, 5, 0.6F),
   SALMON(1, "salmon", 2, 0.1F, 6, 0.8F),
   CLOWNFISH(2, "clownfish", 1, 0.1F),
   PUFFERFISH(3, "pufferfish", 1, 0.1F);

   private static final Map field_150983_e = Maps.newHashMap();
   private final int field_150980_f;
   private final String field_150981_g;
   private IIcon field_150993_h;
   private IIcon field_150994_i;
   private final int field_150991_j;
   private final float field_150992_k;
   private final int field_150989_l;
   private final float field_150990_m;
   private boolean field_150987_n = false;

   private ItemFishFood$FishType(int var3, String var4, int var5, float var6, int var7, float var8) {
      this.field_150980_f = var3;
      this.field_150981_g = var4;
      this.field_150991_j = var5;
      this.field_150992_k = var6;
      this.field_150989_l = var7;
      this.field_150990_m = var8;
      this.field_150987_n = true;
   }

   private ItemFishFood$FishType(int var3, String var4, int var5, float var6) {
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

   public void func_150968_a(IIconRegister var1) {
      this.field_150993_h = var1.func_94245_a("fish_" + this.field_150981_g + "_raw");
      if (this.field_150987_n) {
         this.field_150994_i = var1.func_94245_a("fish_" + this.field_150981_g + "_cooked");
      }
   }

   public IIcon func_150971_g() {
      return this.field_150993_h;
   }

   public IIcon func_150979_h() {
      return this.field_150994_i;
   }

   public boolean func_150973_i() {
      return this.field_150987_n;
   }

   public static ItemFishFood$FishType func_150974_a(int var0) {
      ItemFishFood$FishType var1 = (ItemFishFood$FishType)field_150983_e.get(var0);
      return var1 == null ? COD : var1;
   }

   public static ItemFishFood$FishType func_150978_a(ItemStack var0) {
      return var0.func_77973_b() instanceof ItemFishFood ? func_150974_a(var0.func_77960_j()) : COD;
   }

   static {
      for(ItemFishFood$FishType var3 : values()) {
         field_150983_e.put(var3.func_150976_a(), var3);
      }
   }
}
