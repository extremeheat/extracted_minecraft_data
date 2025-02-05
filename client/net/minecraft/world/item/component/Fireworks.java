package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record Fireworks(int flightDuration, List<FireworkExplosion> explosions) implements TooltipProvider {
   public static final int MAX_EXPLOSIONS = 256;
   public static final Codec<Fireworks> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration", 0).forGetter(Fireworks::flightDuration), FireworkExplosion.CODEC.sizeLimitedListOf(256).optionalFieldOf("explosions", List.of()).forGetter(Fireworks::explosions)).apply(var0, Fireworks::new));
   public static final StreamCodec<ByteBuf, Fireworks> STREAM_CODEC;

   public Fireworks(int var1, List<FireworkExplosion> var2) {
      super();
      if (var2.size() > 256) {
         throw new IllegalArgumentException("Got " + var2.size() + " explosions, but maximum is 256");
      } else {
         this.flightDuration = var1;
         this.explosions = var2;
      }
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, DataComponentGetter var4) {
      if (this.flightDuration > 0) {
         var2.accept(Component.translatable("item.minecraft.firework_rocket.flight").append(CommonComponents.SPACE).append(String.valueOf(this.flightDuration)).withStyle(ChatFormatting.GRAY));
      }

      FireworkExplosion var5 = null;
      int var6 = 0;

      for(FireworkExplosion var8 : this.explosions) {
         if (var5 == null) {
            var5 = var8;
            var6 = 1;
         } else if (var5.equals(var8)) {
            ++var6;
         } else {
            addExplosionTooltip(var2, var5, var6);
            var5 = var8;
            var6 = 1;
         }
      }

      if (var5 != null) {
         addExplosionTooltip(var2, var5, var6);
      }

   }

   private static void addExplosionTooltip(Consumer<Component> var0, FireworkExplosion var1, int var2) {
      MutableComponent var3 = var1.shape().getName();
      if (var2 == 1) {
         var0.accept(Component.translatable("item.minecraft.firework_rocket.single_star", var3).withStyle(ChatFormatting.GRAY));
      } else {
         var0.accept(Component.translatable("item.minecraft.firework_rocket.multiple_stars", var2, var3).withStyle(ChatFormatting.GRAY));
      }

      var1.addAdditionalTooltip((var1x) -> var0.accept(Component.literal("  ").append(var1x)));
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, Fireworks::flightDuration, FireworkExplosion.STREAM_CODEC.apply(ByteBufCodecs.list(256)), Fireworks::explosions, Fireworks::new);
   }
}
