package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SnekComponent(boolean d) {
   private final boolean revealed;
   public static SnekComponent HIDDEN_SNEK = new SnekComponent(false);
   public static final Codec<SnekComponent> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.BOOL.fieldOf("revealed").forGetter(SnekComponent::revealed)).apply(var0, SnekComponent::new)
   );
   public static final StreamCodec<? super RegistryFriendlyByteBuf, SnekComponent> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.BOOL, SnekComponent::revealed, SnekComponent::new
   );

   public SnekComponent(boolean var1) {
      super();
      this.revealed = var1;
   }
}
