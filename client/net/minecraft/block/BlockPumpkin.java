package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPumpkin extends BlockStemGrown {
   protected BlockPumpkin(Block.Properties var1) {
      super(var1);
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      ItemStack var10 = var4.func_184586_b(var5);
      if (var10.func_77973_b() == Items.field_151097_aZ) {
         if (!var2.field_72995_K) {
            EnumFacing var11 = var6.func_176740_k() == EnumFacing.Axis.Y ? var4.func_174811_aO().func_176734_d() : var6;
            var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_199059_fV, SoundCategory.BLOCKS, 1.0F, 1.0F);
            var2.func_180501_a(var3, (IBlockState)Blocks.field_196625_cS.func_176223_P().func_206870_a(BlockCarvedPumpkin.field_196359_a, var11), 11);
            EntityItem var12 = new EntityItem(var2, (double)var3.func_177958_n() + 0.5D + (double)var11.func_82601_c() * 0.65D, (double)var3.func_177956_o() + 0.1D, (double)var3.func_177952_p() + 0.5D + (double)var11.func_82599_e() * 0.65D, new ItemStack(Items.field_151080_bb, 4));
            var12.field_70159_w = 0.05D * (double)var11.func_82601_c() + var2.field_73012_v.nextDouble() * 0.02D;
            var12.field_70181_x = 0.05D;
            var12.field_70179_y = 0.05D * (double)var11.func_82599_e() + var2.field_73012_v.nextDouble() * 0.02D;
            var2.func_72838_d(var12);
            var10.func_77972_a(1, var4);
         }

         return true;
      } else {
         return super.func_196250_a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public BlockStem func_196524_d() {
      return (BlockStem)Blocks.field_150393_bb;
   }

   public BlockAttachedStem func_196523_e() {
      return (BlockAttachedStem)Blocks.field_196711_ds;
   }
}
