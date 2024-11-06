package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;

public interface ItemTintSource {
   int calculate(ItemStack var1);

   MapCodec<? extends ItemTintSource> type();
}
