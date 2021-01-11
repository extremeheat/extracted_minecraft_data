package net.minecraft.client.renderer;

import net.minecraft.util.BlockPos;

public class DestroyBlockProgress {
   private final int field_73115_a;
   private final BlockPos field_180247_b;
   private int field_73112_e;
   private int field_82745_f;

   public DestroyBlockProgress(int var1, BlockPos var2) {
      super();
      this.field_73115_a = var1;
      this.field_180247_b = var2;
   }

   public BlockPos func_180246_b() {
      return this.field_180247_b;
   }

   public void func_73107_a(int var1) {
      if (var1 > 10) {
         var1 = 10;
      }

      this.field_73112_e = var1;
   }

   public int func_73106_e() {
      return this.field_73112_e;
   }

   public void func_82744_b(int var1) {
      this.field_82745_f = var1;
   }

   public int func_82743_f() {
      return this.field_82745_f;
   }
}
