package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface ItemPropertyFunction {
   float call(ItemStack var1, @Nullable Level var2, @Nullable LivingEntity var3);
}
