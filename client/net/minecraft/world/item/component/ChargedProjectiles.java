package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
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

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      ItemStack var6 = null;
      int var7 = 0;

      for(ItemStack var9 : this.items) {
         if (var6 == null) {
            var6 = var9;
            var7 = 1;
         } else if (ItemStack.matches(var6, var9)) {
            ++var7;
         } else {
            addProjectileTooltip(var1, var2, var6, var7);
            var6 = var9;
            var7 = 1;
         }
      }

      if (var6 != null) {
         addProjectileTooltip(var1, var2, var6, var7);
      }

   }

   private static void addProjectileTooltip(Item.TooltipContext var0, Consumer<Component> var1, ItemStack var2, int var3) {
      if (var3 == 1) {
         var1.accept(Component.translatable("item.minecraft.crossbow.projectile.single", var2.getDisplayName()));
      } else {
         var1.accept(Component.translatable("item.minecraft.crossbow.projectile.multiple", var3, var2.getDisplayName()));
      }

      TooltipDisplay var4 = (TooltipDisplay)var2.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
      var2.addDetailsToTooltip(var0, var4, (Player)null, TooltipFlag.NORMAL, (var1x) -> var1.accept(Component.literal("  ").append(var1x).withStyle(ChatFormatting.GRAY)));
   }

   static {
      CODEC = ItemStack.CODEC.listOf().xmap(ChargedProjectiles::new, (var0) -> var0.items);
      STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ChargedProjectiles::new, (var0) -> var0.items);
   }
}
