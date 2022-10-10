package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSkullWitherWall extends BlockSkullWall {
   protected BlockSkullWitherWall(Block.Properties var1) {
      super(BlockSkull.Types.WITHER_SKELETON, var1);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, @Nullable EntityLivingBase var4, ItemStack var5) {
      Blocks.field_196705_eO.func_180633_a(var1, var2, var3, var4, var5);
   }
}
