package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

public enum ChatVisiblity {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final IntFunction<ChatVisiblity> BY_ID = ByIdMap.<ChatVisiblity>continuous((v) -> v.id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
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
      IntFunction var10001 = BY_ID;
      Objects.requireNonNull(var10001);
      LEGACY_CODEC = var10000.xmap(var10001::apply, (v) -> v.id);
   }
}
