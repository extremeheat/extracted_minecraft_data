package net.minecraft.util;

public enum EnumFacing {
   DOWN(0, 1, 0, -1, 0),
   UP(1, 0, 0, 1, 0),
   NORTH(2, 3, 0, 0, -1),
   SOUTH(3, 2, 0, 0, 1),
   EAST(4, 5, -1, 0, 0),
   WEST(5, 4, 1, 0, 0);

   private final int field_82603_g;
   private final int field_82613_h;
   private final int field_82614_i;
   private final int field_82611_j;
   private final int field_82612_k;
   private static final EnumFacing[] field_82609_l = new EnumFacing[6];

   private EnumFacing(int var3, int var4, int var5, int var6, int var7) {
      this.field_82603_g = var3;
      this.field_82613_h = var4;
      this.field_82614_i = var5;
      this.field_82611_j = var6;
      this.field_82612_k = var7;
   }

   public int func_82601_c() {
      return this.field_82614_i;
   }

   public int func_96559_d() {
      return this.field_82611_j;
   }

   public int func_82599_e() {
      return this.field_82612_k;
   }

   public static EnumFacing func_82600_a(int var0) {
      return field_82609_l[var0 % field_82609_l.length];
   }

   static {
      for(EnumFacing var3 : values()) {
         field_82609_l[var3.field_82603_g] = var3;
      }
   }
}
