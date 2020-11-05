package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;

public interface TagContainer {
   TagContainer EMPTY = of(TagCollection.empty(), TagCollection.empty(), TagCollection.empty(), TagCollection.empty());

   TagCollection<Block> getBlocks();

   TagCollection<Item> getItems();

   TagCollection<Fluid> getFluids();

   TagCollection<EntityType<?>> getEntityTypes();

   default void bindToGlobal() {
      StaticTags.resetAll(this);
      Blocks.rebuildCache();
   }

   default void serializeToNetwork(FriendlyByteBuf var1) {
      this.getBlocks().serializeToNetwork(var1, Registry.BLOCK);
      this.getItems().serializeToNetwork(var1, Registry.ITEM);
      this.getFluids().serializeToNetwork(var1, Registry.FLUID);
      this.getEntityTypes().serializeToNetwork(var1, Registry.ENTITY_TYPE);
   }

   static TagContainer deserializeFromNetwork(FriendlyByteBuf var0) {
      TagCollection var1 = TagCollection.loadFromNetwork(var0, Registry.BLOCK);
      TagCollection var2 = TagCollection.loadFromNetwork(var0, Registry.ITEM);
      TagCollection var3 = TagCollection.loadFromNetwork(var0, Registry.FLUID);
      TagCollection var4 = TagCollection.loadFromNetwork(var0, Registry.ENTITY_TYPE);
      return of(var1, var2, var3, var4);
   }

   static TagContainer of(final TagCollection<Block> var0, final TagCollection<Item> var1, final TagCollection<Fluid> var2, final TagCollection<EntityType<?>> var3) {
      return new TagContainer() {
         public TagCollection<Block> getBlocks() {
            return var0;
         }

         public TagCollection<Item> getItems() {
            return var1;
         }

         public TagCollection<Fluid> getFluids() {
            return var2;
         }

         public TagCollection<EntityType<?>> getEntityTypes() {
            return var3;
         }
      };
   }
}
