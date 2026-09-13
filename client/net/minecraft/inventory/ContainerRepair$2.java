package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

class ContainerRepair$2 extends Slot {
   ContainerRepair$2(ContainerRepair var1, IInventory var2, int var3, int var4, int var5, World var6, int var7, int var8, int var9) {
      super(var2, var3, var4, var5);
      this.field_135068_e = var1;
      this.field_135071_a = var6;
      this.field_135069_b = var7;
      this.field_135070_c = var8;
      this.field_135067_d = var9;
   }

   @Override
   public boolean func_75214_a(ItemStack var1) {
      return false;
   }

   @Override
   public boolean func_82869_a(EntityPlayer var1) {
      return (var1.field_71075_bZ.field_75098_d || var1.field_71068_ca >= this.field_135068_e.field_82854_e)
         && this.field_135068_e.field_82854_e > 0
         && this.func_75216_d();
   }

   @Override
   public void func_82870_a(EntityPlayer var1, ItemStack var2) {
      if (!var1.field_71075_bZ.field_75098_d) {
         var1.func_82242_a(-this.field_135068_e.field_82854_e);
      }

      ContainerRepair.access$000(this.field_135068_e).func_70299_a(0, null);
      if (ContainerRepair.access$100(this.field_135068_e) > 0) {
         ItemStack var3 = ContainerRepair.access$000(this.field_135068_e).func_70301_a(1);
         if (var3 != null && var3.field_77994_a > ContainerRepair.access$100(this.field_135068_e)) {
            var3.field_77994_a -= ContainerRepair.access$100(this.field_135068_e);
            ContainerRepair.access$000(this.field_135068_e).func_70299_a(1, var3);
         } else {
            ContainerRepair.access$000(this.field_135068_e).func_70299_a(1, null);
         }
      } else {
         ContainerRepair.access$000(this.field_135068_e).func_70299_a(1, null);
      }

      this.field_135068_e.field_82854_e = 0;
      if (!var1.field_71075_bZ.field_75098_d
         && !this.field_135071_a.field_72995_K
         && this.field_135071_a.func_147439_a(this.field_135069_b, this.field_135070_c, this.field_135067_d) == Blocks.field_150467_bQ
         && var1.func_70681_au().nextFloat() < 0.12F) {
         int var6 = this.field_135071_a.func_72805_g(this.field_135069_b, this.field_135070_c, this.field_135067_d);
         int var4 = var6 & 3;
         int var5 = var6 >> 2;
         if (++var5 > 2) {
            this.field_135071_a.func_147468_f(this.field_135069_b, this.field_135070_c, this.field_135067_d);
            this.field_135071_a.func_72926_e(1020, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
         } else {
            this.field_135071_a.func_72921_c(this.field_135069_b, this.field_135070_c, this.field_135067_d, var4 | var5 << 2, 2);
            this.field_135071_a.func_72926_e(1021, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
         }
      } else if (!this.field_135071_a.field_72995_K) {
         this.field_135071_a.func_72926_e(1021, this.field_135069_b, this.field_135070_c, this.field_135067_d, 0);
      }
   }
}
