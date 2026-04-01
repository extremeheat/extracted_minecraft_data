package net.minecraft.world.entity.livingblock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class LivingBlockTypeRegistry {
   private final LivingBlockType defaultBlockType;
   private final LivingBlockType defaultItemType;
   private final List<@Nullable LivingBlockType> itemLookup;

   private LivingBlockTypeRegistry(final LivingBlockType defaultBlockType, final LivingBlockType defaultItemType, final List<@Nullable LivingBlockType> itemLookup) {
      super();
      this.defaultBlockType = defaultBlockType;
      this.defaultItemType = defaultItemType;
      this.itemLookup = itemLookup;
   }

   public LivingBlockType get(final Item item) {
      LivingBlockType type = (LivingBlockType)this.itemLookup.get(Item.getId(item));
      if (type != null) {
         return type;
      } else {
         return item instanceof BlockItem && !item.builtInRegistryHolder().is(ItemTags.BLOCK_PLACERS) ? this.defaultBlockType : this.defaultItemType;
      }
   }

   public LivingBlockType get(final ItemStack itemStack) {
      return this.get(itemStack.getItem());
   }

   public LivingBlockType get(final BlockState state) {
      return this.get(state.getBlock().asItem());
   }

   public static Builder builderWithDefaultType(final LivingBlockType defaultBlockType, final LivingBlockType defaultItemType) {
      return new Builder(defaultBlockType, defaultItemType);
   }

   public static final class Builder {
      private final LivingBlockType defaultBlockType;
      private final LivingBlockType defaultItemType;
      private final List<@Nullable LivingBlockType> itemLookup;

      private Builder(final LivingBlockType defaultBlockType, final LivingBlockType defaultItemType) {
         super();
         this.defaultBlockType = defaultBlockType;
         this.defaultItemType = defaultItemType;
         this.itemLookup = new ArrayList(Collections.nCopies(BuiltInRegistries.ITEM.size(), (Object)null));
      }

      public Builder register(final Item item, final LivingBlockType type) {
         int id = Item.getId(item);
         if (this.itemLookup.get(id) != null) {
            throw new IllegalStateException("Tried to register living block type twice: " + String.valueOf(item.builtInRegistryHolder().key().identifier()));
         } else {
            this.itemLookup.set(id, type);
            return this;
         }
      }

      public Builder register(final Block block, final LivingBlockType type) {
         return this.register(block.asItem(), type);
      }

      public Builder register(final Item item, final LivingBlockType.Builder builder) {
         return this.register(item, builder.build());
      }

      public Builder register(final Iterable<Item> items, final LivingBlockType type) {
         items.forEach((item) -> this.register(item, type));
         return this;
      }

      public Builder register(final Iterable<Item> items, final LivingBlockType.Builder builder) {
         return this.register(items, builder.build());
      }

      public Builder registerBlocks(final Iterable<Block> blocks, final LivingBlockType.Builder builder) {
         LivingBlockType livingBlockType = builder.build();
         blocks.forEach((item) -> this.register(item, livingBlockType));
         return this;
      }

      public Builder register(final Block block, final LivingBlockType.Builder builder) {
         return this.register(block, builder.build());
      }

      public LivingBlockTypeRegistry build() {
         return new LivingBlockTypeRegistry(this.defaultBlockType, this.defaultItemType, this.itemLookup);
      }
   }
}
