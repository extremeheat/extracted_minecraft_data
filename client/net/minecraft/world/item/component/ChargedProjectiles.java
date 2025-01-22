package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class ChargedProjectiles implements TooltipProvider {
   public static final ChargedProjectiles EMPTY = new ChargedProjectiles(List.of());
   public static final Codec<ChargedProjectiles> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ChargedProjectiles> STREAM_CODEC;
   private final List<ItemStack> items;

   private ChargedProjectiles(List<ItemStack> var1) {
      super();
      this.items = var1;
   }

   public static ChargedProjectiles of(ItemStack var0) {
      return new ChargedProjectiles(List.of(var0.copy()));
   }

   public static ChargedProjectiles of(List<ItemStack> var0) {
      return new ChargedProjectiles(List.copyOf(Lists.transform(var0, ItemStack::copy)));
   }

   public boolean contains(Item var1) {
      for(ItemStack var3 : this.items) {
         if (var3.is(var1)) {
            return true;
         }
      }

      return false;
   }

   public List<ItemStack> getItems() {
      return Lists.transform(this.items, ItemStack::copy);
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof ChargedProjectiles) {
            ChargedProjectiles var2 = (ChargedProjectiles)var1;
            if (ItemStack.listMatches(this.items, var2.items)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return ItemStack.hashStackList(this.items);
   }

   public String toString() {
      return "ChargedProjectiles[items=" + String.valueOf(this.items) + "]";
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      for(ItemStack var6 : this.items) {
         var2.accept(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(var6.getDisplayName()));
         TooltipDisplay var7 = (TooltipDisplay)var6.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
         var6.addDetailsToTooltip(var1, var7, (Player)null, TooltipFlag.NORMAL, (var1x) -> var2.accept(Component.literal("  ").append(var1x).withStyle(ChatFormatting.GRAY)));
      }

   }

   static {
      CODEC = ItemStack.CODEC.listOf().xmap(ChargedProjectiles::new, (var0) -> var0.items);
      STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ChargedProjectiles::new, (var0) -> var0.items);
   }
}
