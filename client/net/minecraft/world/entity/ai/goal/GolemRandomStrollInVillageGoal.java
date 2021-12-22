package net.minecraft.world.entity.ai.goal;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class GolemRandomStrollInVillageGoal extends RandomStrollGoal {
   private static final int POI_SECTION_SCAN_RADIUS = 2;
   private static final int VILLAGER_SCAN_RADIUS = 32;
   private static final int RANDOM_POS_XY_DISTANCE = 10;
   private static final int RANDOM_POS_Y_DISTANCE = 7;

   public GolemRandomStrollInVillageGoal(PathfinderMob var1, double var2) {
      super(var1, var2, 240, false);
   }

   @Nullable
   protected Vec3 getPosition() {
      float var2 = this.mob.level.random.nextFloat();
      if (this.mob.level.random.nextFloat() < 0.3F) {
         return this.getPositionTowardsAnywhere();
      } else {
         Vec3 var1;
         if (var2 < 0.7F) {
            var1 = this.getPositionTowardsVillagerWhoWantsGolem();
            if (var1 == null) {
               var1 = this.getPositionTowardsPoi();
            }
         } else {
            var1 = this.getPositionTowardsPoi();
            if (var1 == null) {
               var1 = this.getPositionTowardsVillagerWhoWantsGolem();
            }
         }

         return var1 == null ? this.getPositionTowardsAnywhere() : var1;
      }
   }

   @Nullable
   private Vec3 getPositionTowardsAnywhere() {
      return LandRandomPos.getPos(this.mob, 10, 7);
   }

   @Nullable
   private Vec3 getPositionTowardsVillagerWhoWantsGolem() {
      ServerLevel var1 = (ServerLevel)this.mob.level;
      List var2 = var1.getEntities(EntityType.VILLAGER, this.mob.getBoundingBox().inflate(32.0D), this::doesVillagerWantGolem);
      if (var2.isEmpty()) {
         return null;
      } else {
         Villager var3 = (Villager)var2.get(this.mob.level.random.nextInt(var2.size()));
         Vec3 var4 = var3.position();
         return LandRandomPos.getPosTowards(this.mob, 10, 7, var4);
      }
   }

   @Nullable
   private Vec3 getPositionTowardsPoi() {
      SectionPos var1 = this.getRandomVillageSection();
      if (var1 == null) {
         return null;
      } else {
         BlockPos var2 = this.getRandomPoiWithinSection(var1);
         return var2 == null ? null : LandRandomPos.getPosTowards(this.mob, 10, 7, Vec3.atBottomCenterOf(var2));
      }
   }

   @Nullable
   private SectionPos getRandomVillageSection() {
      ServerLevel var1 = (ServerLevel)this.mob.level;
      List var2 = (List)SectionPos.cube(SectionPos.method_73(this.mob), 2).filter((var1x) -> {
         return var1.sectionsToVillage(var1x) == 0;
      }).collect(Collectors.toList());
      return var2.isEmpty() ? null : (SectionPos)var2.get(var1.random.nextInt(var2.size()));
   }

   @Nullable
   private BlockPos getRandomPoiWithinSection(SectionPos var1) {
      ServerLevel var2 = (ServerLevel)this.mob.level;
      PoiManager var3 = var2.getPoiManager();
      List var4 = (List)var3.getInRange((var0) -> {
         return true;
      }, var1.center(), 8, PoiManager.Occupancy.IS_OCCUPIED).map(PoiRecord::getPos).collect(Collectors.toList());
      return var4.isEmpty() ? null : (BlockPos)var4.get(var2.random.nextInt(var4.size()));
   }

   private boolean doesVillagerWantGolem(Villager var1) {
      return var1.wantsToSpawnGolem(this.mob.level.getGameTime());
   }
}
