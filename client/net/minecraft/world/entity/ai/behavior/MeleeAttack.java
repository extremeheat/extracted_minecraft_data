package net.minecraft.world.entity.ai.behavior;

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

   public static OneShot<Mob> create(int var0) {
      return BehaviorBuilder.create((var1) -> {
         return var1.group(var1.registered(MemoryModuleType.LOOK_TARGET), var1.present(MemoryModuleType.ATTACK_TARGET), var1.absent(MemoryModuleType.ATTACK_COOLING_DOWN), var1.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)).apply(var1, (var2, var3, var4, var5) -> {
            return (var6, var7, var8) -> {
               LivingEntity var10 = (LivingEntity)var1.get(var3);
               if (!isHoldingUsableProjectileWeapon(var7) && var7.isWithinMeleeAttackRange(var10) && ((NearestVisibleLivingEntities)var1.get(var5)).contains(var10)) {
                  var2.set(new EntityTracker(var10, true));
                  var7.swing(InteractionHand.MAIN_HAND);
                  var7.doHurtTarget(var10);
                  var4.setWithExpiry(true, (long)var0);
                  return true;
               } else {
                  return false;
               }
            };
         });
      });
   }

   private static boolean isHoldingUsableProjectileWeapon(Mob var0) {
      return var0.isHolding((var1) -> {
         Item var2 = var1.getItem();
         return var2 instanceof ProjectileWeaponItem && var0.canFireProjectileWeapon((ProjectileWeaponItem)var2);
      });
   }
}
