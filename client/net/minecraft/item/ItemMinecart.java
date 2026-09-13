package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private static final IBehaviorDispenseItem field_96602_b = new ItemMinecart$1();
   public int field_77841_a;

   public ItemMinecart(int var1) {
      super();
      this.field_77777_bU = 1;
      this.field_77841_a = var1;
      this.func_77637_a(CreativeTabs.field_78029_e);
      BlockDispenser.field_149943_a.func_82595_a(this, field_96602_b);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (BlockRailBase.func_150051_a(var3.func_147439_a(var4, var5, var6))) {
         if (!var3.field_72995_K) {
            EntityMinecart var11 = EntityMinecart.func_94090_a(
               var3, (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F), (double)((float)var6 + 0.5F), this.field_77841_a
            );
            if (var1.func_82837_s()) {
               var11.func_96094_a(var1.func_82833_r());
            }

            var3.func_72838_d(var11);
         }

         --var1.field_77994_a;
         return true;
      } else {
         return false;
      }
   }
}
