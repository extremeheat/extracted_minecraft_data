package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.CraftingGrid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ActionItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CraftingAction extends ActionItem {
   public static final Component CANNOT_PLACE_ERROR = Component.translatable("crafting.table.unable_to_place");

   public CraftingAction(final Item.Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(final UseOnContext context) {
      Player player = context.getPlayer();
      if (player == null) {
         return InteractionResult.PASS;
      } else {
         BlockPos clickedPos = BlockPos.containing(context.getClickLocation());
         AABB box = AABB.encapsulatingFullBlocks(clickedPos.offset(-1, 0, -1), clickedPos.offset(1, 0, 1));
         Level level = context.getLevel();
         List<Entity> grids = level.getEntities(player, box, (entity) -> entity instanceof CraftingGrid);
         boolean hadGrid = !grids.isEmpty();
         if (level instanceof ServerLevel) {
            grids.stream().map((e) -> (CraftingGrid)e).filter((grid) -> grid.isOwnedBy(player)).forEach(Entity::discard);
         }

         if (hadGrid) {
            return InteractionResult.SUCCESS;
         } else if (CraftingGrid.placementOk(level, box)) {
            if (level instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)level;
               serverLevel.getEntities(EntityTypeTest.forClass(CraftingGrid.class), (entity) -> entity.isOwnedBy(player)).forEach(Entity::discard);
               CraftingGrid.createAt(serverLevel, 2, Vec3.atLowerCornerOf(clickedPos), context.getHorizontalDirection(), clickedPos, player);
            }

            return InteractionResult.SUCCESS;
         } else {
            player.sendOverlayMessage(CANNOT_PLACE_ERROR);
            return InteractionResult.FAIL;
         }
      }
   }
}
