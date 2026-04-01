package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.CraftingGrid;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ShowCraftingGridBehavior implements LivingBlockBehavior {
   private static final int STATIONARY_WAIT_TIME = 5;
   private static final int NO_SPACE_PARTICLE_TIME = 60;
   private int stationaryTicks;

   public ShowCraftingGridBehavior() {
      super();
   }

   private boolean shouldRun(final LivingBlock entity) {
      boolean stationary = entity.onGround() && entity.getDeltaMovement().horizontalDistance() < 9.999999747378752E-6;
      if (stationary) {
         ++this.stationaryTicks;
      } else {
         this.stationaryTicks = 0;
      }

      return this.stationaryTicks > 5;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.shouldRun(entity);
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (!this.shouldRun(entity)) {
         return false;
      } else {
         if (getOwnedCraftingGrids(entity, level).isEmpty() && this.stationaryTicks % 60 == 20) {
            Vec3 p = entity.position();
            level.sendParticles(ParticleTypes.ANGRY_VILLAGER, p.x(), p.y() + 1.0, p.z(), 1, 0.0, 0.0, 0.0, 0.0);
         }

         return true;
      }
   }

   public void onStart(final LivingBlock entity) {
      Level var3 = entity.level();
      if (var3 instanceof ServerLevel level) {
         if (getOwnedCraftingGrids(entity, level).isEmpty() && !entity.isDeadOrDying()) {
            Direction direction = Direction.fromYRot((double)(entity.getRandom().nextFloat() * 360.0F));

            for(int attempt = 0; attempt < 4; ++attempt) {
               BlockPos pos = entity.blockPosition().relative((Direction)direction, 3);
               AABB box = AABB.encapsulatingFullBlocks(pos.offset(-1, 0, -1), pos.offset(1, 0, 1));
               if (CraftingGrid.placementOk(level, box)) {
                  CraftingGrid.createAt(level, 3, Vec3.atBottomCenterOf(pos), direction.getOpposite(), pos, entity);
                  return;
               }

               direction = direction.getClockWise();
            }

         }
      }
   }

   private static List<? extends CraftingGrid> getOwnedCraftingGrids(final LivingBlock entity, final ServerLevel level) {
      return level.<CraftingGrid>getEntities(EntityTypeTest.forClass(CraftingGrid.class), (g) -> g.isOwnedBy(entity));
   }

   public static LivingBlockBehaviorType showCraftingTable() {
      return LivingBlockBehaviorType.behaviorType(ShowCraftingGridBehavior::new);
   }
}
