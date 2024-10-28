package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInput {
   private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((var0, var1) -> {
      return Component.translatableEscape("arguments.item.overstacked", var0, var1);
   });
   private final Holder<Item> item;
   private final DataComponentMap components;

   public ItemInput(Holder<Item> var1, DataComponentMap var2) {
      super();
      this.item = var1;
      this.components = var2;
   }

   public Item getItem() {
      return (Item)this.item.value();
   }

   public ItemStack createItemStack(int var1, boolean var2) throws CommandSyntaxException {
      ItemStack var3 = new ItemStack(this.item, var1);
      var3.applyComponents(this.components);
      if (var2 && var1 > var3.getMaxStackSize()) {
         throw ERROR_STACK_TOO_BIG.create(this.getItemName(), var3.getMaxStackSize());
      } else {
         return var3;
      }
   }

   public String serialize(HolderLookup.Provider var1) {
      StringBuilder var2 = new StringBuilder(this.getItemName());
      String var3 = this.serializeComponents(var1);
      if (!var3.isEmpty()) {
         var2.append('[');
         var2.append(var3);
         var2.append(']');
      }

      return var2.toString();
   }

   private String serializeComponents(HolderLookup.Provider var1) {
      RegistryOps var2 = var1.createSerializationContext(NbtOps.INSTANCE);
      return (String)this.components.stream().flatMap((var1x) -> {
         DataComponentType var2x = var1x.type();
         ResourceLocation var3 = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(var2x);
         Optional var4 = var1x.encodeValue(var2).result();
         if (var3 != null && !var4.isEmpty()) {
            String var10000 = var3.toString();
            return Stream.of(var10000 + "=" + String.valueOf(var4.get()));
         } else {
            return Stream.empty();
         }
      }).collect(Collectors.joining(String.valueOf(',')));
   }

   private String getItemName() {
      return this.item.unwrapKey().map(ResourceKey::location).orElseGet(() -> {
         return "unknown[" + String.valueOf(this.item) + "]";
      }).toString();
   }
}
