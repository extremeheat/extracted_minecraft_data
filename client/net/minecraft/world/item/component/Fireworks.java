package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

public record Fireworks(int flightDuration, List<FireworkExplosion> explosions) implements TooltipProvider {
   public static final int MAX_EXPLOSIONS = 256;
   public static final Codec<Fireworks> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration", 0).forGetter(Fireworks::flightDuration),
               FireworkExplosion.CODEC.sizeLimitedListOf(256).optionalFieldOf("explosions", List.of()).forGetter(Fireworks::explosions)
            )
            .apply(var0, Fireworks::new)
   );
   public static final StreamCodec<ByteBuf, Fireworks> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT, Fireworks::flightDuration, FireworkExplosion.STREAM_CODEC.apply(ByteBufCodecs.list(256)), Fireworks::explosions, Fireworks::new
   );

   public Fireworks(int flightDuration, List<FireworkExplosion> explosions) {
      super();
      if (explosions.size() > 256) {
         throw new IllegalArgumentException("Got " + explosions.size() + " explosions, but maximum is 256");
      } else {
         this.flightDuration = flightDuration;
         this.explosions = explosions;
      }
   }

   @Override
   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      if (this.flightDuration > 0) {
         var2.accept(
            Component.translatable("item.minecraft.firework_rocket.flight")
               .append(CommonComponents.SPACE)
               .append(String.valueOf(this.flightDuration))
               .withStyle(ChatFormatting.GRAY)
         );
      }

      for (FireworkExplosion var5 : this.explosions) {
         var5.addShapeNameTooltip(var2);
         var5.addAdditionalTooltip(var1x -> var2.accept(Component.literal("  ").append(var1x)));
      }
   }
}