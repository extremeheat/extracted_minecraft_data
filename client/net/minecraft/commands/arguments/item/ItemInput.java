package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
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
   private final DataComponentPatch components;

   public ItemInput(Holder<Item> var1, DataComponentPatch var2) {
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
      return (String)this.components.entrySet().stream().flatMap((var1x) -> {
         DataComponentType var2x = (DataComponentType)var1x.getKey();
         ResourceLocation var3 = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(var2x);
         if (var3 == null) {
            return Stream.empty();
         } else {
            Optional var4 = (Optional)var1x.getValue();
            if (var4.isPresent()) {
               TypedDataComponent var5 = TypedDataComponent.createUnchecked(var2x, var4.get());
               return var5.encodeValue(var2).result().stream().map((var1) -> {
                  String var10000 = var3.toString();
                  return var10000 + "=" + String.valueOf(var1);
               });
            } else {
               return Stream.of("!" + var3.toString());
            }
         }
      }).collect(Collectors.joining(String.valueOf(',')));
   }

   private String getItemName() {
      return this.item.unwrapKey().map(ResourceKey::location).orElseGet(() -> {
         return "unknown[" + String.valueOf(this.item) + "]";
      }).toString();
   }
}
