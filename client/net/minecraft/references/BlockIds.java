package net.minecraft.references;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class BlockIds {
   public static final ResourceKey<Block> PUMPKIN = createKey("pumpkin");
   public static final ResourceKey<Block> PUMPKIN_STEM = createKey("pumpkin_stem");
   public static final ResourceKey<Block> ATTACHED_PUMPKIN_STEM = createKey("attached_pumpkin_stem");
   public static final ResourceKey<Block> MELON = createKey("melon");
   public static final ResourceKey<Block> MELON_STEM = createKey("melon_stem");
   public static final ResourceKey<Block> ATTACHED_MELON_STEM = createKey("attached_melon_stem");
   public static final ResourceKey<Block> DIRT = createKey("dirt");

   public BlockIds() {
      super();
   }

   private static ResourceKey<Block> createKey(final String name) {
      return ResourceKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(name));
   }
}
