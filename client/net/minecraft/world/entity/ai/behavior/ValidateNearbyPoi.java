package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ValidateNearbyPoi extends Behavior<LivingEntity> {
   private final MemoryModuleType<GlobalPos> memoryType;
   private final Predicate<PoiType> poiPredicate;

   public ValidateNearbyPoi(PoiType var1, MemoryModuleType<GlobalPos> var2) {
      super(ImmutableMap.of(var2, MemoryStatus.VALUE_PRESENT));
      this.poiPredicate = var1.getPredicate();
      this.memoryType = var2;
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, LivingEntity var2) {
      GlobalPos var3 = (GlobalPos)var2.getBrain().getMemory(this.memoryType).get();
      return Objects.equals(var1.getDimension().getType(), var3.dimension()) && var3.pos().closerThan(var2.position(), 5.0D);
   }

   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      Brain var5 = var2.getBrain();
      GlobalPos var6 = (GlobalPos)var5.getMemory(this.memoryType).get();
      ServerLevel var7 = var1.getServer().getLevel(var6.dimension());
      if (this.poiDoesntExist(var7, var6.pos()) || this.bedIsOccupied(var7, var6.pos(), var2)) {
         var5.eraseMemory(this.memoryType);
      }

   }

   private boolean bedIsOccupied(ServerLevel var1, BlockPos var2, LivingEntity var3) {
      BlockState var4 = var1.getBlockState(var2);
      return var4.getBlock().is(BlockTags.BEDS) && (Boolean)var4.getValue(BedBlock.OCCUPIED) && !var3.isSleeping();
   }

   private boolean poiDoesntExist(ServerLevel var1, BlockPos var2) {
      return !var1.getPoiManager().exists(var2, this.poiPredicate);
   }
}
