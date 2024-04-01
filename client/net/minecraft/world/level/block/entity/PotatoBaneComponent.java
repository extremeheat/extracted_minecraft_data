package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record PotatoBaneComponent(float c) implements TooltipProvider {
   private final float damageBoost;
   public static final Codec<PotatoBaneComponent> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.FLOAT.fieldOf("damage_boost").forGetter(PotatoBaneComponent::damageBoost)).apply(var0, PotatoBaneComponent::new)
   );
   public static final StreamCodec<ByteBuf, PotatoBaneComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

   public PotatoBaneComponent(float var1) {
      super();
      this.damageBoost = var1;
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      var1.accept(Component.translatable("potato_bane.tooltip.damage_boost", this.damageBoost).withStyle(ChatFormatting.GREEN));
   }

   public static float getPotatoDamageBoost(ItemStack var0, Entity var1) {
      if (var1.isPotato()) {
         PotatoBaneComponent var2 = var0.get(DataComponents.POTATO_BANE);
         if (var2 != null) {
            return var2.damageBoost;
         }
      }

      return 0.0F;
   }
}
