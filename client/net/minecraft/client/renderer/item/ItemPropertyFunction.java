package net.minecraft.client.renderer.item;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** @deprecated */
@Deprecated
public interface ItemPropertyFunction {
   float call(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4);
}
