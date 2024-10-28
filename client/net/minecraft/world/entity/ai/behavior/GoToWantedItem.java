package net.minecraft.world.entity.ai.behavior;

import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;

public class GoToWantedItem {
   public GoToWantedItem() {
      super();
   }

   public static BehaviorControl<LivingEntity> create(float var0, boolean var1, int var2) {
      return create((var0x) -> {
         return true;
      }, var0, var1, var2);
   }

   public static <E extends LivingEntity> BehaviorControl<E> create(Predicate<E> var0, float var1, boolean var2, int var3) {
      return BehaviorBuilder.create((var4) -> {
         BehaviorBuilder var5 = var2 ? var4.registered(MemoryModuleType.WALK_TARGET) : var4.absent(MemoryModuleType.WALK_TARGET);
         return var4.group(var4.registered(MemoryModuleType.LOOK_TARGET), var5, var4.present(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM), var4.registered(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS)).apply(var4, (var4x, var5x, var6, var7) -> {
            return (var8, var9, var10) -> {
               ItemEntity var12 = (ItemEntity)var4.get(var6);
               if (var4.tryGet(var7).isEmpty() && var0.test(var9) && var12.closerThan(var9, (double)var3) && var9.level().getWorldBorder().isWithinBounds(var12.blockPosition())) {
                  WalkTarget var13 = new WalkTarget(new EntityTracker(var12, false), var1, 0);
                  var4x.set(new EntityTracker(var12, true));
                  var5x.set(var13);
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }
}
