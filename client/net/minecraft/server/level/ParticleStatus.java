package net.minecraft.server.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.EnumStreamCodec;
import net.minecraft.util.ByIdMap;

public enum ParticleStatus {
   ALL(0, "options.particles.all"),
   DECREASED(1, "options.particles.decreased"),
   MINIMAL(2, "options.particles.minimal");

   public static final EnumStreamCodec<ParticleStatus> STREAM_CODEC = ByteBufCodecs.<ParticleStatus>enumCodec(ParticleStatus.class, (s) -> s.id, ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec<ParticleStatus> LEGACY_CODEC;
   private final int id;
   private final Component caption;

   private ParticleStatus(final int id, final String key) {
      this.id = id;
      this.caption = Component.translatable(key);
   }

   public Component caption() {
      return this.caption;
   }

   // $FF: synthetic method
   private static ParticleStatus[] $values() {
      return new ParticleStatus[]{ALL, DECREASED, MINIMAL};
   }

   static {
      PrimitiveCodec var10000 = Codec.INT;
      EnumStreamCodec var10001 = STREAM_CODEC;
      Objects.requireNonNull(var10001);
      LEGACY_CODEC = var10000.xmap(var10001::byId, (s) -> s.id);
   }
}
