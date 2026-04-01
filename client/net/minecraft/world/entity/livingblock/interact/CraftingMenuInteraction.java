package net.minecraft.world.entity.livingblock.interact;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.CraftingGrid;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;

public class CraftingMenuInteraction implements OnInteract {
   private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

   public CraftingMenuInteraction() {
      super();
   }

   public InteractionResult apply(final Player player, final InteractionHand hand, final Vec3 vec3, final LivingBlock livingBlock) {
      Level var6 = livingBlock.level();
      if (var6 instanceof ServerLevel level) {
         List<? extends CraftingGrid> grids = level.<CraftingGrid>getEntities(EntityTypeTest.forClass(CraftingGrid.class), (g) -> g.isOwnedBy(livingBlock));
         if (grids.isEmpty()) {
            return InteractionResult.FAIL;
         } else {
            CraftingGrid grid = (CraftingGrid)grids.getFirst();
            player.openMenu(new SimpleMenuProvider((containerId, inventory, p) -> new CraftingMenu(containerId, inventory, ContainerLevelAccess.create(level, grid.blockPosition()), grid), CONTAINER_TITLE));
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }
}
