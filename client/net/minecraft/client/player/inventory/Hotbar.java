package net.minecraft.client.player.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class Hotbar {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SIZE = Inventory.getSelectionSize() - 1;
   public static final Codec<Hotbar> CODEC;
   private static final DynamicOps<Tag> DEFAULT_OPS;
   private static final Dynamic<?> EMPTY_STACK;
   private List<Dynamic<?>> items;

   private Hotbar(final List<Dynamic<?>> items) {
      super();
      this.items = items;
   }

   public Hotbar() {
      this(Collections.nCopies(SIZE, EMPTY_STACK));
   }

   public List<ItemStack> load(final HolderLookup.Provider registries) {
      return this.items.stream().map((dynamic) -> (ItemStack)ItemStack.OPTIONAL_CODEC.parse(RegistryOps.injectRegistryContext(dynamic, registries)).resultOrPartial((error) -> LOGGER.warn("Could not parse hotbar item: {}", error)).orElse(ItemStack.EMPTY)).toList();
   }

   public void storeFrom(final Inventory inventory, final RegistryAccess lookupProvider) {
      RegistryOps<Tag> registryOps = lookupProvider.createSerializationContext(DEFAULT_OPS);
      ImmutableList.Builder<Dynamic<?>> newItems = ImmutableList.builderWithExpectedSize(SIZE);

      for(int i = 0; i < SIZE; ++i) {
         ItemStack item = inventory.getItem(i);
         Optional<Dynamic<?>> result = ItemStack.OPTIONAL_CODEC.encodeStart(registryOps, item).resultOrPartial((error) -> LOGGER.warn("Could not encode hotbar item: {}", error)).map((tag) -> new Dynamic(DEFAULT_OPS, tag));
         newItems.add((Dynamic)result.orElse(EMPTY_STACK));
      }

      this.items = newItems.build();
   }

   public boolean isEmpty() {
      for(Dynamic<?> item : this.items) {
         if (!isEmpty(item)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isEmpty(final Dynamic<?> item) {
      return EMPTY_STACK.equals(item);
   }

   static {
      CODEC = Codec.PASSTHROUGH.listOf().validate((list) -> Util.fixedSize(list, SIZE)).xmap(Hotbar::new, (hotbar) -> hotbar.items);
      DEFAULT_OPS = NbtOps.INSTANCE;
      EMPTY_STACK = new Dynamic(DEFAULT_OPS, (Tag)ItemStack.OPTIONAL_CODEC.encodeStart(DEFAULT_OPS, ItemStack.EMPTY).getOrThrow());
   }
}
