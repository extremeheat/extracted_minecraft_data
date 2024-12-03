package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ItemTintSource {
   int calculate(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3);

   MapCodec<? extends ItemTintSource> type();
}
