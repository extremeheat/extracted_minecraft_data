package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {
   private final IBehaviorDispenseItem field_149947_P = new BehaviorDefaultDispenseItem();

   public BlockDropper() {
      super();
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a("furnace_side");
      this.field_149944_M = var1.func_94245_a("furnace_top");
      this.field_149945_N = var1.func_94245_a(this.func_149641_N() + "_front_horizontal");
      this.field_149946_O = var1.func_94245_a(this.func_149641_N() + "_front_vertical");
   }

   @Override
   protected IBehaviorDispenseItem func_149940_a(ItemStack var1) {
      return this.field_149947_P;
   }

   @Override
   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityDropper();
   }

   @Override
   protected void func_149941_e(World var1, int var2, int var3, int var4) {
      BlockSourceImpl var5 = new BlockSourceImpl(var1, var2, var3, var4);
      TileEntityDispenser var6 = (TileEntityDispenser)var5.func_150835_j();
      if (var6 != null) {
         int var7 = var6.func_146017_i();
         if (var7 < 0) {
            var1.func_72926_e(1001, var2, var3, var4, 0);
         } else {
            ItemStack var8 = var6.func_70301_a(var7);
            int var9 = var1.func_72805_g(var2, var3, var4) & 7;
            IInventory var10 = TileEntityHopper.func_145893_b(
               var1, (double)(var2 + Facing.field_71586_b[var9]), (double)(var3 + Facing.field_71587_c[var9]), (double)(var4 + Facing.field_71585_d[var9])
            );
            ItemStack var12;
            if (var10 != null) {
               var12 = TileEntityHopper.func_145889_a(var10, var8.func_77946_l().func_77979_a(1), Facing.field_71588_a[var9]);
               if (var12 == null) {
                  var12 = var8.func_77946_l();
                  if (--var12.field_77994_a == 0) {
                     var12 = null;
                  }
               } else {
                  var12 = var8.func_77946_l();
               }
            } else {
               var12 = this.field_149947_P.func_82482_a(var5, var8);
               if (var12 != null && var12.field_77994_a == 0) {
                  var12 = null;
               }
            }

            var6.func_70299_a(var7, var12);
         }
      }
   }
}
