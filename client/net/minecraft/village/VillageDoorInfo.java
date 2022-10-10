package net.minecraft.village;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class VillageDoorInfo {
   private final BlockPos field_179859_a;
   private final BlockPos field_179857_b;
   private final EnumFacing field_179858_c;
   private int field_75475_f;
   private boolean field_75476_g;
   private int field_75482_h;

   public VillageDoorInfo(BlockPos var1, int var2, int var3, int var4) {
      this(var1, func_179854_a(var2, var3), var4);
   }

   private static EnumFacing func_179854_a(int var0, int var1) {
      if (var0 < 0) {
         return EnumFacing.WEST;
      } else if (var0 > 0) {
         return EnumFacing.EAST;
      } else {
         return var1 < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
      }
   }

   public VillageDoorInfo(BlockPos var1, EnumFacing var2, int var3) {
      super();
      this.field_179859_a = var1.func_185334_h();
      this.field_179858_c = var2;
      this.field_179857_b = var1.func_177967_a(var2, 2);
      this.field_75475_f = var3;
   }

   public int func_75474_b(int var1, int var2, int var3) {
      return (int)this.field_179859_a.func_177954_c((double)var1, (double)var2, (double)var3);
   }

   public int func_179848_a(BlockPos var1) {
      return (int)var1.func_177951_i(this.func_179852_d());
   }

   public int func_179846_b(BlockPos var1) {
      return (int)this.field_179857_b.func_177951_i(var1);
   }

   public boolean func_179850_c(BlockPos var1) {
      int var2 = var1.func_177958_n() - this.field_179859_a.func_177958_n();
      int var3 = var1.func_177952_p() - this.field_179859_a.func_177956_o();
      return var2 * this.field_179858_c.func_82601_c() + var3 * this.field_179858_c.func_82599_e() >= 0;
   }

   public void func_75466_d() {
      this.field_75482_h = 0;
   }

   public void func_75470_e() {
      ++this.field_75482_h;
   }

   public int func_75468_f() {
      return this.field_75482_h;
   }

   public BlockPos func_179852_d() {
      return this.field_179859_a;
   }

   public BlockPos func_179856_e() {
      return this.field_179857_b;
   }

   public int func_179847_f() {
      return this.field_179858_c.func_82601_c() * 2;
   }

   public int func_179855_g() {
      return this.field_179858_c.func_82599_e() * 2;
   }

   public int func_75473_b() {
      return this.field_75475_f;
   }

   public void func_179849_a(int var1) {
      this.field_75475_f = var1;
   }

   public boolean func_179851_i() {
      return this.field_75476_g;
   }

   public void func_179853_a(boolean var1) {
      this.field_75476_g = var1;
   }

   public EnumFacing func_188567_j() {
      return this.field_179858_c;
   }
}
