package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class GoToClosestVillage extends Behavior<Villager> {
   private final float speedModifier;
   private final int closeEnoughDistance;

   public GoToClosestVillage(float var1, int var2) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = var1;
      this.closeEnoughDistance = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      return !var1.isVillage(var2.blockPosition());
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      PoiManager var5 = var1.getPoiManager();
      int var6 = var5.sectionsToVillage(SectionPos.of(var2.blockPosition()));
      Vec3 var7 = null;

      for(int var8 = 0; var8 < 5; ++var8) {
         Vec3 var9 = LandRandomPos.getPos(var2, 15, 7, (var1x) -> {
            return (double)(-var5.sectionsToVillage(SectionPos.of(var1x)));
         });
         if (var9 != null) {
            int var10 = var5.sectionsToVillage(SectionPos.of(new BlockPos(var9)));
            if (var10 < var6) {
               var7 = var9;
               break;
            }

            if (var10 == var6) {
               var7 = var9;
            }
         }
      }

      if (var7 != null) {
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var7, this.speedModifier, this.closeEnoughDistance)));
      }

   }
}
