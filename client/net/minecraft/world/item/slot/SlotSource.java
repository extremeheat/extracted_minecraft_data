package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;

public interface SlotSource extends Validatable {
   MapCodec<? extends SlotSource> codec();

   SlotCollection provide(LootContext context);
}
