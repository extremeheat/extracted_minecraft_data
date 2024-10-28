package net.minecraft.client.player.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class Hotbar {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int SIZE = Inventory.getSelectionSize();
   public static final Codec<Hotbar> CODEC;
   private static final DynamicOps<Tag> DEFAULT_OPS;
   private static final Dynamic<?> EMPTY_STACK;
   private List<Dynamic<?>> items;

   private Hotbar(List<Dynamic<?>> var1) {
      super();
      this.items = var1;
   }

   public Hotbar() {
      this(Collections.nCopies(SIZE, EMPTY_STACK));
   }

   public List<ItemStack> load(HolderLookup.Provider var1) {
      return this.items.stream().map((var1x) -> {
         return (ItemStack)ItemStack.OPTIONAL_CODEC.parse(RegistryOps.injectRegistryContext(var1x, var1)).resultOrPartial((var0) -> {
            LOGGER.warn("Could not parse hotbar item: {}", var0);
         }).orElse(ItemStack.EMPTY);
      }).toList();
   }

   public void storeFrom(Inventory var1, RegistryAccess var2) {
      RegistryOps var3 = var2.createSerializationContext(DEFAULT_OPS);
      ImmutableList.Builder var4 = ImmutableList.builderWithExpectedSize(SIZE);

      for(int var5 = 0; var5 < SIZE; ++var5) {
         ItemStack var6 = var1.getItem(var5);
         Optional var7 = ItemStack.OPTIONAL_CODEC.encodeStart(var3, var6).resultOrPartial((var0) -> {
            LOGGER.warn("Could not encode hotbar item: {}", var0);
         }).map((var0) -> {
            return new Dynamic(DEFAULT_OPS, var0);
         });
         var4.add((Dynamic)var7.orElse(EMPTY_STACK));
      }

      this.items = var4.build();
   }

   public boolean isEmpty() {
      Iterator var1 = this.items.iterator();

      Dynamic var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (Dynamic)var1.next();
      } while(isEmpty(var2));

      return false;
   }

   private static boolean isEmpty(Dynamic<?> var0) {
      return EMPTY_STACK.equals(var0);
   }

   static {
      CODEC = Codec.PASSTHROUGH.listOf().validate((var0) -> {
         return Util.fixedSize(var0, SIZE);
      }).xmap(Hotbar::new, (var0) -> {
         return var0.items;
      });
      DEFAULT_OPS = NbtOps.INSTANCE;
      EMPTY_STACK = new Dynamic(DEFAULT_OPS, (Tag)ItemStack.OPTIONAL_CODEC.encodeStart(DEFAULT_OPS, ItemStack.EMPTY).getOrThrow());
   }
}
