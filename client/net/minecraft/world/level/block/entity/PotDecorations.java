package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.ItemLike;

public record PotDecorations(Optional<Item> back, Optional<Item> left, Optional<Item> right, Optional<Item> front) implements TooltipProvider {
   public static final PotDecorations EMPTY = new PotDecorations(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
   public static final Codec<PotDecorations> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, PotDecorations> STREAM_CODEC;

   private PotDecorations(List<Item> var1) {
      this(getItem(var1, 0), getItem(var1, 1), getItem(var1, 2), getItem(var1, 3));
   }

   public PotDecorations(Item var1, Item var2, Item var3, Item var4) {
      this(List.of(var1, var2, var3, var4));
   }

   public PotDecorations(Optional<Item> var1, Optional<Item> var2, Optional<Item> var3, Optional<Item> var4) {
      super();
      this.back = var1;
      this.left = var2;
      this.right = var3;
      this.front = var4;
   }

   private static Optional<Item> getItem(List<Item> var0, int var1) {
      if (var1 >= var0.size()) {
         return Optional.empty();
      } else {
         Item var2 = (Item)var0.get(var1);
         return var2 == Items.BRICK ? Optional.empty() : Optional.of(var2);
      }
   }

   public List<Item> ordered() {
      return Stream.of(this.back, this.left, this.right, this.front).map((var0) -> (Item)var0.orElse(Items.BRICK)).toList();
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      if (!this.equals(EMPTY)) {
         var2.accept(CommonComponents.EMPTY);
         addSideDetailsToTooltip(var2, this.front);
         addSideDetailsToTooltip(var2, this.left);
         addSideDetailsToTooltip(var2, this.right);
         addSideDetailsToTooltip(var2, this.back);
      }
   }

   private static void addSideDetailsToTooltip(Consumer<Component> var0, Optional<Item> var1) {
      var0.accept((new ItemStack((ItemLike)var1.orElse(Items.BRICK), 1)).getHoverName().plainCopy().withStyle(ChatFormatting.GRAY));
   }

   static {
      CODEC = BuiltInRegistries.ITEM.byNameCodec().sizeLimitedListOf(4).xmap(PotDecorations::new, PotDecorations::ordered);
      STREAM_CODEC = ByteBufCodecs.registry(Registries.ITEM).apply(ByteBufCodecs.list(4)).map(PotDecorations::new, PotDecorations::ordered);
   }
}
