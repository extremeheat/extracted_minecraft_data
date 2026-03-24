package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;

public record ChargedProjectiles(List<ItemStackTemplate> items) implements TooltipProvider {
   public static final ChargedProjectiles EMPTY = new ChargedProjectiles(List.of());
   public static final Codec<ChargedProjectiles> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ChargedProjectiles> STREAM_CODEC;

   public ChargedProjectiles {
      super();
   }

   public static ChargedProjectiles of(final ItemStackTemplate stack) {
      return new ChargedProjectiles(List.of(stack));
   }

   public static ChargedProjectiles ofNonEmpty(final List<ItemStack> items) {
      return new ChargedProjectiles(List.copyOf(Lists.transform(items, ItemStackTemplate::fromNonEmptyStack)));
   }

   public boolean contains(final Item item) {
      for(ItemStackTemplate projectile : this.items) {
         if (projectile.is(item)) {
            return true;
         }
      }

      return false;
   }

   public List<ItemStack> itemCopies() {
      return Lists.transform(this.items, ItemStackTemplate::create);
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components) {
      ItemStack current = null;
      int count = 0;

      for(ItemStackTemplate projectileTemplate : this.items) {
         ItemStack projectile = projectileTemplate.create();
         if (current == null) {
            current = projectile;
            count = 1;
         } else if (ItemStack.matches(current, projectile)) {
            ++count;
         } else {
            addProjectileTooltip(context, consumer, current, count);
            current = projectile;
            count = 1;
         }
      }

      if (current != null) {
         addProjectileTooltip(context, consumer, current, count);
      }

   }

   private static void addProjectileTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final ItemStack projectile, final int count) {
      if (count == 1) {
         consumer.accept(Component.translatable("item.minecraft.crossbow.projectile.single", projectile.getDisplayName()));
      } else {
         consumer.accept(Component.translatable("item.minecraft.crossbow.projectile.multiple", count, projectile.getDisplayName()));
      }

      TooltipDisplay projectileDisplay = (TooltipDisplay)projectile.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
      projectile.addDetailsToTooltip(context, projectileDisplay, (Player)null, TooltipFlag.NORMAL, (line) -> consumer.accept(Component.literal("  ").append(line).withStyle(ChatFormatting.GRAY)));
   }

   static {
      CODEC = ItemStackTemplate.CODEC.listOf().xmap(ChargedProjectiles::new, (projectiles) -> projectiles.items);
      STREAM_CODEC = ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ChargedProjectiles::new, (projectiles) -> projectiles.items);
   }
}
