package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

public class WinterDropEntityTypeTagsProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {
   public WinterDropEntityTypeTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, Registries.ENTITY_TYPE, var2, (var0) -> {
         return var0.builtInRegistryHolder().key();
      });
   }

   protected void addTags(HolderLookup.Provider var1) {
      this.tag(EntityTypeTags.BOAT).add((Object)EntityType.PALE_OAK_BOAT);
   }
}
