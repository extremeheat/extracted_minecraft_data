package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignItem extends StandingAndWallBlockItem {
   public SignItem(Item.Properties var1, Block var2, Block var3) {
      super(var2, var3, var1, Direction.DOWN);
   }

   public SignItem(Item.Properties var1, Block var2, Block var3, Direction var4) {
      super(var2, var3, var1, var4);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected boolean updateCustomBlockEntityTag(BlockPos var1, Level var2, @Nullable Player var3, ItemStack var4, BlockState var5) {
      boolean var6 = super.updateCustomBlockEntityTag(var1, var2, var3, var4, var5);
      if (!var2.isClientSide && !var6 && var3 != null) {
         BlockEntity var9 = var2.getBlockEntity(var1);
         if (var9 instanceof SignBlockEntity var7) {
            Block var10 = var2.getBlockState(var1).getBlock();
            if (var10 instanceof SignBlock var8) {
               var8.openTextEdit(var3, (SignBlockEntity)var7, true);
            }
         }
      }

      return var6;
   }
}
