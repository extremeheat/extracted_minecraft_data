package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.EnumStreamCodec;
import net.minecraft.util.ByIdMap;

public enum ChatVisiblity {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   public static final EnumStreamCodec<ChatVisiblity> STREAM_CODEC = ByteBufCodecs.<ChatVisiblity>enumCodec(ChatVisiblity.class, (v) -> v.id, ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec<ChatVisiblity> LEGACY_CODEC;
   private final int id;
   private final Component caption;

   private ChatVisiblity(final int id, final String key) {
      this.id = id;
      this.caption = Component.translatable(key);
   }

   public Component caption() {
      return this.caption;
   }

   // $FF: synthetic method
   private static ChatVisiblity[] $values() {
      return new ChatVisiblity[]{FULL, SYSTEM, HIDDEN};
   }

   static {
      PrimitiveCodec var10000 = Codec.INT;
      EnumStreamCodec var10001 = STREAM_CODEC;
      Objects.requireNonNull(var10001);
      LEGACY_CODEC = var10000.xmap(var10001::byId, (v) -> v.id);
   }
}
