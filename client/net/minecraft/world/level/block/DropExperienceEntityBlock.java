package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public abstract class DropExperienceEntityBlock extends BaseEntityBlock {
   private final IntProvider xpRange;

   protected DropExperienceEntityBlock(final IntProvider xpRange, final BlockBehaviour.Properties properties) {
      super(properties);
      this.xpRange = xpRange;
   }

   protected void spawnAfterBreak(final BlockState state, final ServerLevel level, final BlockPos pos, final ItemStack tool, final boolean dropExperience, final @Nullable Entity breaker) {
      super.spawnAfterBreak(state, level, pos, tool, dropExperience, breaker);
      if (dropExperience) {
         this.tryDropExperience(level, pos, tool, this.xpRange);
      }

   }
}
