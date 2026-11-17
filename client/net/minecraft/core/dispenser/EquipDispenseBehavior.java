package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class EquipDispenseBehavior implements DispenseBehavior {
   public static final EquipDispenseBehavior INSTANCE = new EquipDispenseBehavior();
   public static final MapCodec<EquipDispenseBehavior> MAP_CODEC;

   private EquipDispenseBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      BlockPos pos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
      List<LivingEntity> entities = source.level().getEntitiesOfClass(LivingEntity.class, new AABB(pos), (entity) -> entity.canEquipWithDispenser(dispensed));
      if (entities.isEmpty()) {
         return DispenseBehavior.DispenseResult.failure();
      } else {
         LivingEntity target = (LivingEntity)entities.getFirst();
         EquipmentSlot slot = target.getEquipmentSlotForItem(dispensed);
         ItemStack equip = dispensed.split(1);
         target.setItemSlot(slot, equip);
         if (target instanceof Mob) {
            Mob targetMob = (Mob)target;
            targetMob.setGuaranteedDrop(slot);
            targetMob.setPersistenceRequired();
         }

         return DispenseBehavior.DispenseResult.success(dispensed);
      }
   }

   public MapCodec<EquipDispenseBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
