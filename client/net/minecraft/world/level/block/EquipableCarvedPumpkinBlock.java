package net.minecraft.world.level.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class EquipableCarvedPumpkinBlock extends CarvedPumpkinBlock implements Equipable {
   protected EquipableCarvedPumpkinBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.HEAD;
   }
}
