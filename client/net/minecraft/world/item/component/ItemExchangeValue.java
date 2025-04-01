package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public record ItemExchangeValue(float value) implements TooltipProvider {
   public static final Codec<ItemExchangeValue> CODEC;
   public static final StreamCodec<ByteBuf, ItemExchangeValue> STREAM_CODEC;

   public ItemExchangeValue(float var1) {
      super();
      this.value = var1;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      if (!var5.has(DataComponents.WORLD_MODIFIERS)) {
         float var6 = this.getValue(var4, var5);
         double var7 = (double)Math.round((double)var6 * 1000.0) / 1000.0;
         var2.accept(Component.translatable("item.exchange_value", Component.literal("" + var7).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.YELLOW));
         var2.accept(Component.translatable("world.effect.convert"));
      }
   }

   public float getValue(@Nullable Player var1, ItemStack var2) {
      float var3 = this.value;
      if (var1 != null) {
         for(Holder var5 : BuiltInRegistries.PLAYER_UNLOCK.listElements().toList()) {
            if (var1.isActive(var5)) {
               for(Map.Entry var7 : ((PlayerUnlock)var5.value()).experienceFactorForItemTag().entrySet()) {
                  if (var2.is((TagKey)var7.getKey())) {
                     var3 *= (Float)var7.getValue();
                  }
               }

               for(Map.Entry var11 : ((PlayerUnlock)var5.value()).experienceFactorForItem().entrySet()) {
                  if (var2.is((Item)var11.getKey())) {
                     var3 *= (Float)var11.getValue();
                  }
               }
            }
         }
      }

      var3 += this.getValueSum(var1, ((ItemContainerContents)var2.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).nonEmptyItems());
      var3 += this.getValueSum(var1, ((BundleContents)var2.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)).items());
      return (float)var2.getCount() * var3;
   }

   private float getValueSum(@Nullable Player var1, Iterable<ItemStack> var2) {
      float var3 = 0.0F;

      for(ItemStack var5 : var2) {
         var3 += ((ItemExchangeValue)var5.getOrDefault(DataComponents.EXCHANGE_VALUE, Item.NO_EXCHANGE)).getValue(var1, var5);
      }

      return var3;
   }

   static {
      CODEC = ExtraCodecs.NON_NEGATIVE_FLOAT.xmap(ItemExchangeValue::new, ItemExchangeValue::value);
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.FLOAT, ItemExchangeValue::value, ItemExchangeValue::new);
   }
}
