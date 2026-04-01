package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.phys.AABB;

public class EquipOnEntityBehavior implements LivingBlockBehavior {
   private static final int EQUIP_INTERVAL = 17;
   public static final LivingBlockBehaviorType BEHAVIOR = LivingBlockBehaviorType.behaviorType((Function)((var0) -> new EquipOnEntityBehavior()));
   private int lastTriggeredTick = 0;

   public EquipOnEntityBehavior() {
      super();
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.tickCount - this.lastTriggeredTick >= 51;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      ItemStack stack = entity.getItemStack();
      if (stack.has(DataComponents.EQUIPPABLE)) {
         LivingEntity equipOn = (LivingEntity)level.getNearestEntity(LivingEntity.class, entity.getX(), entity.getY(), entity.getZ(), (new AABB(entity.blockPosition())).inflate(0.5));
         Equippable equippable = (Equippable)stack.get(DataComponents.EQUIPPABLE);
         if (equipOn != null && equippable != null) {
            EquipmentSlot slot = equippable.slot();
            if (equipOn.isEquippableInSlot(stack, slot) && equipOn.getItemBySlot(slot).isEmpty()) {
               equipOn.setItemSlot(slot, stack.split(1));
               level.playSound((Entity)null, equipOn.getX(), equipOn.getY(), equipOn.getZ(), (SoundEvent)equippable.equipSound().value(), equipOn.getSoundSource());
               entity.discard();
               return true;
            }
         }
      }

      return false;
   }
}
