package net.minecraft.block;

import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BlockSourceImpl;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockDropper extends BlockDispenser {
   private static final IBehaviorDispenseItem field_149947_P = new BehaviorDefaultDispenseItem();

   public BlockDropper(Block.Properties var1) {
      super(var1);
   }

   protected IBehaviorDispenseItem func_149940_a(ItemStack var1) {
      return field_149947_P;
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityDropper();
   }

   protected void func_176439_d(World var1, BlockPos var2) {
      BlockSourceImpl var3 = new BlockSourceImpl(var1, var2);
      TileEntityDispenser var4 = (TileEntityDispenser)var3.func_150835_j();
      int var5 = var4.func_146017_i();
      if (var5 < 0) {
         var1.func_175718_b(1001, var2, 0);
      } else {
         ItemStack var6 = var4.func_70301_a(var5);
         if (!var6.func_190926_b()) {
            EnumFacing var7 = (EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176441_a);
            IInventory var8 = TileEntityHopper.func_195484_a(var1, var2.func_177972_a(var7));
            ItemStack var9;
            if (var8 == null) {
               var9 = field_149947_P.dispense(var3, var6);
            } else {
               var9 = TileEntityHopper.func_174918_a(var4, var8, var6.func_77946_l().func_77979_a(1), var7.func_176734_d());
               if (var9.func_190926_b()) {
                  var9 = var6.func_77946_l();
                  var9.func_190918_g(1);
               } else {
                  var9 = var6.func_77946_l();
               }
            }

            var4.func_70299_a(var5, var9);
         }
      }
   }
}
