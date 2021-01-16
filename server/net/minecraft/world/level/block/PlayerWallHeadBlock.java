package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerWallHeadBlock extends WallSkullBlock {
   protected PlayerWallHeadBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.PLAYER, var1);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      Blocks.PLAYER_HEAD.setPlacedBy(var1, var2, var3, var4, var5);
   }

   public List<ItemStack> getDrops(BlockState var1, LootContext.Builder var2) {
      return Blocks.PLAYER_HEAD.getDrops(var1, var2);
   }
}
