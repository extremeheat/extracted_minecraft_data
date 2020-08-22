package net.minecraft.world.level.storage.loot.entries;

import java.util.function.Consumer;
import net.minecraft.world.level.storage.loot.LootContext;

public interface LootPoolEntry {
   int getWeight(float var1);

   void createItemStack(Consumer var1, LootContext var2);
}
