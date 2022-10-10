package net.minecraft.block;

import net.minecraft.util.math.BlockPos;

public class BlockEventData {
   private final BlockPos field_180329_a;
   private final Block field_151344_d;
   private final int field_151345_e;
   private final int field_151343_f;

   public BlockEventData(BlockPos var1, Block var2, int var3, int var4) {
      super();
      this.field_180329_a = var1;
      this.field_151344_d = var2;
      this.field_151345_e = var3;
      this.field_151343_f = var4;
   }

   public BlockPos func_180328_a() {
      return this.field_180329_a;
   }

   public Block func_151337_f() {
      return this.field_151344_d;
   }

   public int func_151339_d() {
      return this.field_151345_e;
   }

   public int func_151338_e() {
      return this.field_151343_f;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof BlockEventData)) {
         return false;
      } else {
         BlockEventData var2 = (BlockEventData)var1;
         return this.field_180329_a.equals(var2.field_180329_a) && this.field_151345_e == var2.field_151345_e && this.field_151343_f == var2.field_151343_f && this.field_151344_d == var2.field_151344_d;
      }
   }

   public String toString() {
      return "TE(" + this.field_180329_a + ")," + this.field_151345_e + "," + this.field_151343_f + "," + this.field_151344_d;
   }
}
