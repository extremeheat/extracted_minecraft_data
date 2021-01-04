package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class PickUpItems extends Behavior<Villager> {
   private List<ItemEntity> items = new ArrayList();

   public PickUpItems() {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, Villager var2) {
      this.items = var1.getEntitiesOfClass(ItemEntity.class, var2.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
      return !this.items.isEmpty();
   }

   protected void start(ServerLevel var1, Villager var2, long var3) {
      ItemEntity var5 = (ItemEntity)this.items.get(var1.random.nextInt(this.items.size()));
      if (var2.wantToPickUp(var5.getItem().getItem())) {
         Vec3 var6 = var5.position();
         var2.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new BlockPosWrapper(new BlockPos(var6))));
         var2.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var6, 0.5F, 0)));
      }

   }
}
