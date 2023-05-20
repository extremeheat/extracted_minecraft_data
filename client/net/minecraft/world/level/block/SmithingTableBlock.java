package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LegacySmithingMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SmithingTableBlock extends CraftingTableBlock {
   private static final Component CONTAINER_TITLE = Component.translatable("container.upgrade");

   protected SmithingTableBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return new SimpleMenuProvider(
         (var2x, var3x, var4) -> (AbstractContainerMenu)(var2.enabledFeatures().contains(FeatureFlags.UPDATE_1_20)
               ? new SmithingMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3))
               : new LegacySmithingMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3))),
         CONTAINER_TITLE
      );
   }

   @Override
   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         var4.awardStat(Stats.INTERACT_WITH_SMITHING_TABLE);
         return InteractionResult.CONSUME;
      }
   }
}
