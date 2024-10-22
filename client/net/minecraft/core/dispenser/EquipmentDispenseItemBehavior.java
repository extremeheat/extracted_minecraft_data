package net.minecraft.core.dispenser;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class EquipmentDispenseItemBehavior extends DefaultDispenseItemBehavior {
   public static final EquipmentDispenseItemBehavior INSTANCE = new EquipmentDispenseItemBehavior();

   public EquipmentDispenseItemBehavior() {
      super();
   }

   @Override
   protected ItemStack execute(BlockSource var1, ItemStack var2) {
      return dispenseEquipment(var1, var2) ? var2 : super.execute(var1, var2);
   }

   public static boolean dispenseEquipment(BlockSource var0, ItemStack var1) {
      BlockPos var2 = var0.pos().relative(var0.state().getValue(DispenserBlock.FACING));
      List var3 = var0.level().getEntitiesOfClass(LivingEntity.class, new AABB(var2), var1x -> var1x.canEquipWithDispenser(var1));
      if (var3.isEmpty()) {
         return false;
      } else {
         LivingEntity var4 = (LivingEntity)var3.getFirst();
         EquipmentSlot var5 = var4.getEquipmentSlotForItem(var1);
         ItemStack var6 = var1.split(1);
         var4.setItemSlot(var5, var6);
         if (var4 instanceof Mob var7) {
            var7.setDropChance(var5, 2.0F);
            var7.setPersistenceRequired();
         }

         return true;
      }
   }
}
