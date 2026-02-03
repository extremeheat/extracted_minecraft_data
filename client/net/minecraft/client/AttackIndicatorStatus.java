package net.minecraft.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

public enum AttackIndicatorStatus {
   OFF(0, "options.off"),
   CROSSHAIR(1, "options.attack.crosshair"),
   HOTBAR(2, "options.attack.hotbar");

   private static final IntFunction<AttackIndicatorStatus> BY_ID = ByIdMap.<AttackIndicatorStatus>continuous((s) -> s.id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec<AttackIndicatorStatus> LEGACY_CODEC;
   private final int id;
   private final Component caption;

   private AttackIndicatorStatus(final int id, final String key) {
      this.id = id;
      this.caption = Component.translatable(key);
   }

   public Component caption() {
      return this.caption;
   }

   // $FF: synthetic method
   private static AttackIndicatorStatus[] $values() {
      return new AttackIndicatorStatus[]{OFF, CROSSHAIR, HOTBAR};
   }

   static {
      PrimitiveCodec var10000 = Codec.INT;
      IntFunction var10001 = BY_ID;
      Objects.requireNonNull(var10001);
      LEGACY_CODEC = var10000.xmap(var10001::apply, (s) -> s.id);
   }
}
