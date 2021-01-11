package net.minecraft.item;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemMinecart extends Item {
   private static final IBehaviorDispenseItem field_96602_b = new BehaviorDefaultDispenseItem() {
      private final BehaviorDefaultDispenseItem field_96465_b = new BehaviorDefaultDispenseItem();

      public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
         EnumFacing var3 = BlockDispenser.func_149937_b(var1.func_82620_h());
         World var4 = var1.func_82618_k();
         double var5 = var1.func_82615_a() + (double)var3.func_82601_c() * 1.125D;
         double var7 = Math.floor(var1.func_82617_b()) + (double)var3.func_96559_d();
         double var9 = var1.func_82616_c() + (double)var3.func_82599_e() * 1.125D;
         BlockPos var11 = var1.func_180699_d().func_177972_a(var3);
         IBlockState var12 = var4.func_180495_p(var11);
         BlockRailBase.EnumRailDirection var13 = var12.func_177230_c() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)var12.func_177229_b(((BlockRailBase)var12.func_177230_c()).func_176560_l()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         double var14;
         if (BlockRailBase.func_176563_d(var12)) {
            if (var13.func_177018_c()) {
               var14 = 0.6D;
            } else {
               var14 = 0.1D;
            }
         } else {
            if (var12.func_177230_c().func_149688_o() != Material.field_151579_a || !BlockRailBase.func_176563_d(var4.func_180495_p(var11.func_177977_b()))) {
               return this.field_96465_b.func_82482_a(var1, var2);
            }

            IBlockState var16 = var4.func_180495_p(var11.func_177977_b());
            BlockRailBase.EnumRailDirection var17 = var16.func_177230_c() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)var16.func_177229_b(((BlockRailBase)var16.func_177230_c()).func_176560_l()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            if (var3 != EnumFacing.DOWN && var17.func_177018_c()) {
               var14 = -0.4D;
            } else {
               var14 = -0.9D;
            }
         }

         EntityMinecart var18 = EntityMinecart.func_180458_a(var4, var5, var7 + var14, var9, ((ItemMinecart)var2.func_77973_b()).field_77841_a);
         if (var2.func_82837_s()) {
            var18.func_96094_a(var2.func_82833_r());
         }

         var4.func_72838_d(var18);
         var2.func_77979_a(1);
         return var2;
      }

      protected void func_82485_a(IBlockSource var1) {
         var1.func_82618_k().func_175718_b(1000, var1.func_180699_d(), 0);
      }
   };
   private final EntityMinecart.EnumMinecartType field_77841_a;

   public ItemMinecart(EntityMinecart.EnumMinecartType var1) {
      super();
      this.field_77777_bU = 1;
      this.field_77841_a = var1;
      this.func_77637_a(CreativeTabs.field_78029_e);
      BlockDispenser.field_149943_a.func_82595_a(this, field_96602_b);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      IBlockState var9 = var3.func_180495_p(var4);
      if (BlockRailBase.func_176563_d(var9)) {
         if (!var3.field_72995_K) {
            BlockRailBase.EnumRailDirection var10 = var9.func_177230_c() instanceof BlockRailBase ? (BlockRailBase.EnumRailDirection)var9.func_177229_b(((BlockRailBase)var9.func_177230_c()).func_176560_l()) : BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            double var11 = 0.0D;
            if (var10.func_177018_c()) {
               var11 = 0.5D;
            }

            EntityMinecart var13 = EntityMinecart.func_180458_a(var3, (double)var4.func_177958_n() + 0.5D, (double)var4.func_177956_o() + 0.0625D + var11, (double)var4.func_177952_p() + 0.5D, this.field_77841_a);
            if (var1.func_82837_s()) {
               var13.func_96094_a(var1.func_82833_r());
            }

            var3.func_72838_d(var13);
         }

         --var1.field_77994_a;
         return true;
      } else {
         return false;
      }
   }
}
