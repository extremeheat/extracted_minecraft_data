package net.minecraft.world.level.storage.loot;

import net.minecraft.resources.ResourceLocation;

public record LootDataId<T>(LootDataType<T> a, ResourceLocation b) {
   private final LootDataType<T> type;
   private final ResourceLocation location;

   public LootDataId(LootDataType<T> var1, ResourceLocation var2) {
      super();
      this.type = var1;
      this.location = var2;
   }
}
