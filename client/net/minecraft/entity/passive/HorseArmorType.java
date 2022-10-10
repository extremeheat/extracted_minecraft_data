package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum HorseArmorType {
   NONE(0),
   IRON(5, "iron", "meo"),
   GOLD(7, "gold", "goo"),
   DIAMOND(11, "diamond", "dio");

   private final String field_188586_e;
   private final String field_188587_f;
   private final int field_188588_g;

   private HorseArmorType(int var3) {
      this.field_188588_g = var3;
      this.field_188586_e = null;
      this.field_188587_f = "";
   }

   private HorseArmorType(int var3, String var4, String var5) {
      this.field_188588_g = var3;
      this.field_188586_e = "textures/entity/horse/armor/horse_armor_" + var4 + ".png";
      this.field_188587_f = var5;
   }

   public int func_188579_a() {
      return this.ordinal();
   }

   public String func_188573_b() {
      return this.field_188587_f;
   }

   public int func_188578_c() {
      return this.field_188588_g;
   }

   @Nullable
   public String func_188574_d() {
      return this.field_188586_e;
   }

   public static HorseArmorType func_188575_a(int var0) {
      return values()[var0];
   }

   public static HorseArmorType func_188580_a(ItemStack var0) {
      return var0.func_190926_b() ? NONE : func_188576_a(var0.func_77973_b());
   }

   public static HorseArmorType func_188576_a(Item var0) {
      if (var0 == Items.field_151138_bX) {
         return IRON;
      } else if (var0 == Items.field_151136_bY) {
         return GOLD;
      } else {
         return var0 == Items.field_151125_bZ ? DIAMOND : NONE;
      }
   }

   public static boolean func_188577_b(Item var0) {
      return func_188576_a(var0) != NONE;
   }
}
