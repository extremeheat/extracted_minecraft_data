package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.CraftingGrid;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.Target;
import net.minecraft.world.entity.livingblock.cognition.Desires;
import net.minecraft.world.entity.livingblock.cognition.Intent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AvoidBeingInTheWayOfCrafting implements LivingBlockBehavior {
   public static final double BACK_AWAY_DISTANCE = 5.0;
   private final Intent<Target> move;
   private Vec3 pos;
   public static LivingBlockBehaviorType AVOID_DISRUPTING_CRAFTING = LivingBlockBehaviorType.behaviorType((Function)((b) -> new AvoidBeingInTheWayOfCrafting(b.withIntentTo(LivingBlock.MOVE_TOWARDS))));

   public AvoidBeingInTheWayOfCrafting(final Intent<Target> move) {
      super();
      this.move = move;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      Level var3 = entity.level();
      if (!(var3 instanceof ServerLevel level)) {
         return false;
      } else if (!entity.hopesAndDreams.hasDesire(Desires.APPROACH) && entity.isIdle()) {
         Vec3 pos = entity.position();
         List<CraftingGrid> grids = level.getEntities(EntityTypeTest.forClass(CraftingGrid.class), AABB.around(pos, 20.0), CraftingGrid::isAutomatic);
         ItemStack me = entity.getItemStack();
         ContextMap context = SlotDisplayContext.fromLevel(entity.level());

         for(CraftingGrid grid : grids) {
            if (grid.hasGhostItems()) {
               int size = grid.getSize();
               Vec3 gridPos = grid.position();
               if (!(Math.max(Math.abs(gridPos.x() - pos.x()), Math.abs(gridPos.z() - pos.z())) > (double)size / 2.0 + 0.5) && !(Math.abs(gridPos.y() - pos.y()) > 5.0)) {
                  for(int y = 0; y < size; ++y) {
                     for(int x = 0; x < size; ++x) {
                        Vec3 slotPos = Vec3.atBottomCenterOf(grid.getSlotPosition(x, y));
                        if (!(Math.max(Math.abs(slotPos.x() - pos.x()), Math.abs(slotPos.z() - pos.z())) > 1.0)) {
                           SlotDisplay ghost = grid.getGhostItem(x, y);
                           LivingBlock ingredient = grid.getIngredientBlock(x, y);
                           if (ingredient != null && ingredient != entity || !ghost.matches(context, me)) {
                              this.pos = getTargetPosition(entity.position(), grid.position());
                              return true;
                           }
                        }
                     }
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static Vec3 getTargetPosition(final Vec3 entityPos, final Vec3 gridPos) {
      double dx = entityPos.x() - gridPos.x();
      double dz = entityPos.z() - gridPos.z();
      double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
      if (horizontalDistance < 0.01) {
         return new Vec3(entityPos.x() + 5.0, entityPos.y(), entityPos.z());
      } else {
         double scale = 5.0 / horizontalDistance;
         return new Vec3(entityPos.x() + dx * scale, entityPos.y(), entityPos.z() + dz * scale);
      }
   }

   public void onStart(final LivingBlock entity) {
      this.move.update(Target.near(this.pos, 1.0));
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      return false;
   }
}
