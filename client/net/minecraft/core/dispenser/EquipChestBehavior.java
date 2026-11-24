package net.minecraft.core.dispenser;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class EquipChestBehavior implements DispenseBehavior {
   public static final EquipChestBehavior INSTANCE = new EquipChestBehavior();
   public static final MapCodec<EquipChestBehavior> MAP_CODEC;

   private EquipChestBehavior() {
      super();
   }

   public DispenseBehavior.DispenseResult execute(final BlockSource source, final ItemStack dispensed) {
      BlockPos pos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));

      for(AbstractChestedHorse abstractChestedHorse : source.level().getEntitiesOfClass(AbstractChestedHorse.class, new AABB(pos), (entity) -> entity.isAlive() && !entity.hasChest())) {
         if (abstractChestedHorse.isTamed()) {
            SlotAccess slot = abstractChestedHorse.getSlot(499);
            if (slot != null && slot.set(dispensed)) {
               SoundEvent sound = abstractChestedHorse.getChestEquipSound();
               RandomSource random = source.level().getRandom();
               source.level().playSound((Entity)null, pos, sound, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
               dispensed.shrink(1);
               return DispenseBehavior.DispenseResult.success(dispensed);
            }
         }
      }

      return DispenseBehavior.DispenseResult.failure();
   }

   public MapCodec<EquipChestBehavior> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = MapCodec.unit(INSTANCE);
   }
}
