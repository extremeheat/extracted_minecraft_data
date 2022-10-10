package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private static final IBehaviorDispenseItem field_96602_b = new BehaviorDefaultDispenseItem() {
      private final BehaviorDefaultDispenseItem field_96465_b = new BehaviorDefaultDispenseItem();

      public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
         EnumFacing var3 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
         World var4 = var1.func_197524_h();
         double var5 = var1.func_82615_a() + (double)var3.func_82601_c() * 1.125D;
         double var7 = Math.floor(var1.func_82617_b()) + (double)var3.func_96559_d();
         double var9 = var1.func_82616_c() + (double)var3.func_82599_e() * 1.125D;
         BlockPos var11 = var1.func_180699_d().func_177972_a(var3);
         IBlockState var12 = var4.func_180495_p(var11);
         RailShape var13 = var12.func_177230_c() instanceof BlockRailBase ? (RailShape)var12.func_177229_b(((BlockRailBase)var12.func_177230_c()).func_176560_l()) : RailShape.NORTH_SOUTH;
         double var14;
         if (var12.func_203425_a(BlockTags.field_203437_y)) {
            if (var13.func_208092_c()) {
               var14 = 0.6D;
            } else {
               var14 = 0.1D;
            }
         } else {
            if (!var12.func_196958_f() || !var4.func_180495_p(var11.func_177977_b()).func_203425_a(BlockTags.field_203437_y)) {
               return this.field_96465_b.dispense(var1, var2);
            }

            IBlockState var16 = var4.func_180495_p(var11.func_177977_b());
            RailShape var17 = var16.func_177230_c() instanceof BlockRailBase ? (RailShape)var16.func_177229_b(((BlockRailBase)var16.func_177230_c()).func_176560_l()) : RailShape.NORTH_SOUTH;
            if (var3 != EnumFacing.DOWN && var17.func_208092_c()) {
               var14 = -0.4D;
            } else {
               var14 = -0.9D;
            }
         }

         EntityMinecart var18 = EntityMinecart.func_184263_a(var4, var5, var7 + var14, var9, ((ItemMinecart)var2.func_77973_b()).field_77841_a);
         if (var2.func_82837_s()) {
            var18.func_200203_b(var2.func_200301_q());
         }

         var4.func_72838_d(var18);
         var2.func_190918_g(1);
         return var2;
      }

      protected void func_82485_a(IBlockSource var1) {
         var1.func_197524_h().func_175718_b(1000, var1.func_180699_d(), 0);
      }
   };
   private final EntityMinecart.Type field_77841_a;

   public ItemMinecart(EntityMinecart.Type var1, Item.Properties var2) {
      super(var2);
      this.field_77841_a = var1;
      BlockDispenser.func_199774_a(this, field_96602_b);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      IBlockState var4 = var2.func_180495_p(var3);
      if (!var4.func_203425_a(BlockTags.field_203437_y)) {
         return EnumActionResult.FAIL;
      } else {
         ItemStack var5 = var1.func_195996_i();
         if (!var2.field_72995_K) {
            RailShape var6 = var4.func_177230_c() instanceof BlockRailBase ? (RailShape)var4.func_177229_b(((BlockRailBase)var4.func_177230_c()).func_176560_l()) : RailShape.NORTH_SOUTH;
            double var7 = 0.0D;
            if (var6.func_208092_c()) {
               var7 = 0.5D;
            }

            EntityMinecart var9 = EntityMinecart.func_184263_a(var2, (double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 0.0625D + var7, (double)var3.func_177952_p() + 0.5D, this.field_77841_a);
            if (var5.func_82837_s()) {
               var9.func_200203_b(var5.func_200301_q());
            }

            var2.func_72838_d(var9);
         }

         var5.func_190918_g(1);
         return EnumActionResult.SUCCESS;
      }
   }
}
