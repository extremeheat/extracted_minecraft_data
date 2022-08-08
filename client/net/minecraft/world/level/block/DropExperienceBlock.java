package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class DropExperienceBlock extends Block {
   private final IntProvider xpRange;

   public DropExperienceBlock(BlockBehaviour.Properties var1) {
      this(var1, ConstantInt.of(0));
   }

   public DropExperienceBlock(BlockBehaviour.Properties var1, IntProvider var2) {
      super(var1);
      this.xpRange = var2;
   }

   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5) {
         this.tryDropExperience(var2, var3, var4, this.xpRange);
      }

   }
}
