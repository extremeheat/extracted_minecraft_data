package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;

public class MeleeAttack {
   public MeleeAttack() {
      super();
   }

   public static <T extends Mob> OneShot<T> create(int var0) {
      return create((var0x) -> true, var0);
   }

   public static <T extends Mob> OneShot<T> create(Predicate<T> var0, int var1) {
      return BehaviorBuilder.create((Function)((var2) -> var2.group(var2.registered(MemoryModuleType.LOOK_TARGET), var2.present(MemoryModuleType.ATTACK_TARGET), var2.absent(MemoryModuleType.ATTACK_COOLING_DOWN), var2.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(var2, (var3, var4, var5, var6) -> (var7, var8, var9) -> {
               LivingEntity var11 = (LivingEntity)var2.get(var4);
               if (var0.test(var8) && !isHoldingUsableProjectileWeapon(var8) && var8.isWithinMeleeAttackRange(var11) && ((NearestVisibleLivingEntities)var2.get(var6)).contains(var11)) {
                  var3.set(new EntityTracker(var11, true));
                  var8.swing(InteractionHand.MAIN_HAND);
                  var8.doHurtTarget(var7, var11);
                  var5.setWithExpiry(true, (long)var1);
                  return true;
               } else {
                  return false;
               }
            })));
   }

   private static boolean isHoldingUsableProjectileWeapon(Mob var0) {
      return var0.isHolding((var1) -> {
         Item var2 = var1.getItem();
         return var2 instanceof ProjectileWeaponItem && var0.canFireProjectileWeapon((ProjectileWeaponItem)var2);
      });
   }
}
