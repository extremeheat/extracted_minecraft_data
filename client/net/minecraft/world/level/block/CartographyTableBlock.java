package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CartographyMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CartographyTableBlock extends Block {
   private static final TranslatableComponent CONTAINER_TITLE = new TranslatableComponent("container.cartography_table", new Object[0]);

   protected CartographyTableBlock(Block.Properties var1) {
      super(var1);
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      var4.openMenu(var1.getMenuProvider(var2, var3));
      var4.awardStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
      return true;
   }

   @Nullable
   public MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return new SimpleMenuProvider((var2x, var3x, var4) -> {
         return new CartographyMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3));
      }, CONTAINER_TITLE);
   }
}
