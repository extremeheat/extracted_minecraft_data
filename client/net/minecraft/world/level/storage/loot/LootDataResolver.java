package net.minecraft.world.level.storage.loot;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface LootDataResolver {
   @Nullable
   <T> T getElement(LootDataId<T> var1);

   @Nullable
   default <T> T getElement(LootDataType<T> var1, ResourceLocation var2) {
      return this.getElement(new LootDataId<>(var1, var2));
   }

   default <T> Optional<T> getElementOptional(LootDataId<T> var1) {
      return Optional.ofNullable(this.getElement(var1));
   }

   default <T> Optional<T> getElementOptional(LootDataType<T> var1, ResourceLocation var2) {
      return this.getElementOptional(new LootDataId<>(var1, var2));
   }

   default LootTable getLootTable(ResourceLocation var1) {
      return this.getElementOptional(LootDataType.TABLE, var1).orElse(LootTable.EMPTY);
   }
}
