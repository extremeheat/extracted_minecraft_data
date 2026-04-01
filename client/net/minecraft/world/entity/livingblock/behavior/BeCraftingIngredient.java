package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
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
import org.jspecify.annotations.Nullable;

public class BeCraftingIngredient implements LivingBlockBehavior {
   private static final double MOVE_THRESHOLD = 0.1;
   private static final int STUCK_TICK_THRESHOLD = 20;
   private final Intent<Target> move;
   private @Nullable BlockPos pos;
   private @Nullable CraftingGrid grid;
   private int slotX;
   private int slotY;
   private Vec3 lastMovePos;
   private int lastMoveTick;
   public static LivingBlockBehaviorType BECOME_CRAFTING_INGREDIENT = LivingBlockBehaviorType.behaviorType((Function)((b) -> new BeCraftingIngredient(b.withIntentTo(LivingBlock.MOVE_TOWARDS))));

   public BeCraftingIngredient(final Intent<Target> move) {
      super();
      this.lastMovePos = Vec3.ZERO;
      this.move = move;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      Level var3 = entity.level();
      if (!(var3 instanceof ServerLevel level)) {
         return false;
      } else if (!entity.hopesAndDreams.hasDesire(Desires.APPROACH) && entity.isIdle()) {
         ItemStack me = entity.getItemStack();
         Vec3 pos = entity.position();
         ContextMap context = SlotDisplayContext.fromLevel(level);

         for(CraftingGrid grid : level.getEntities(EntityTypeTest.forClass(CraftingGrid.class), AABB.around(pos, 20.0), CraftingGrid::isAutomatic)) {
            int size = grid.getSize();

            for(int y = 0; y < size; ++y) {
               for(int x = 0; x < size; ++x) {
                  SlotDisplay ghost = grid.getGhostItem(x, y);
                  ItemStack ingredient = grid.getIngredient(x, y);
                  LivingBlock trying = grid.getIngredientBlockThatIsTrying(x, y);
                  if (ingredient.isEmpty() && trying == null && ghost.matches(context, me)) {
                     this.slotX = x;
                     this.slotY = y;
                     this.pos = grid.getSlotPosition(x, y);
                     this.grid = grid;
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void onStart(final LivingBlock entity) {
      this.move.update(Target.exactlyAt(this.pos.getBottomCenter()));
      this.grid.startTrying(entity, this.slotX, this.slotY);
      this.lastMovePos = entity.position();
      this.lastMoveTick = entity.tickCount;
   }

   public void onStop(final LivingBlock entity) {
      this.move.clear();
      this.grid.stopTrying(entity, this.slotX, this.slotY);
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (entity.hopesAndDreams.hasDesire(Desires.APPROACH)) {
         return false;
      } else if (!this.grid.isAlive()) {
         this.grid = null;
         return false;
      } else {
         LivingBlock ingredientInSlot = this.grid.getIngredientBlock(this.slotX, this.slotY);
         SlotDisplay ghostItem = this.grid.getGhostItem(this.slotX, this.slotY);
         ContextMap context = SlotDisplayContext.fromLevel(level);
         if (ingredientInSlot != null && ingredientInSlot != entity && ghostItem.matches(context, ingredientInSlot.getItemStack())) {
            return false;
         } else {
            SlotDisplay ghost = this.grid.getGhostItem(this.slotX, this.slotY);
            if (!ghost.matches(context, entity.getItemStack())) {
               return false;
            } else {
               Vec3 remainingTravel = Vec3.atBottomCenterOf(this.pos).subtract(entity.position());
               if (!(remainingTravel.length() < 0.1) && (!(remainingTravel.horizontalDistance() < 0.1) || !(remainingTravel.y() < 1.0))) {
                  if (this.lastMovePos.distanceTo(entity.position()) > 0.1) {
                     this.lastMoveTick = entity.tickCount;
                     this.lastMovePos = entity.position();
                  }

                  return entity.tickCount - this.lastMoveTick <= 20;
               } else {
                  return true;
               }
            }
         }
      }
   }
}
