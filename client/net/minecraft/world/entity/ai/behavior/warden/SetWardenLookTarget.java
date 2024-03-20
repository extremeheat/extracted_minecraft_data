package net.minecraft.world.entity.ai.behavior.warden;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class SetWardenLookTarget {
   public SetWardenLookTarget() {
      super();
   }

   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create(
         var0 -> var0.group(
                  var0.registered(MemoryModuleType.LOOK_TARGET),
                  var0.registered(MemoryModuleType.DISTURBANCE_LOCATION),
                  var0.registered(MemoryModuleType.ROAR_TARGET),
                  var0.absent(MemoryModuleType.ATTACK_TARGET)
               )
               .apply(var0, (var1, var2, var3, var4) -> (var4x, var5, var6) -> {
                     Optional var8 = var0.<LivingEntity>tryGet(var3).map(Entity::blockPosition).or(() -> var0.tryGet(var2));
                     if (var8.isEmpty()) {
                        return false;
                     } else {
                        var1.set(new BlockPosTracker((BlockPos)var8.get()));
                        return true;
                     }
                  })
      );
   }
}