package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundLevelParticlesPacket(ParticleOptions particle, boolean overrideLimiter, boolean alwaysShow, double x, double y, double z, float xDist, float yDist, float zDist, float xMaxSpeed, float yMaxSpeed, float zMaxSpeed, int count, RandomizationType randomizationType) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelParticlesPacket> STREAM_CODEC;

   public ClientboundLevelParticlesPacket(final ParticleOptions particle, final boolean overrideLimiter, final boolean alwaysShow, final double x, final double y, final double z, final float xDist, final float yDist, final float zDist, final float maxSpeed, final int count) {
      this(particle, overrideLimiter, alwaysShow, x, y, z, xDist, yDist, zDist, maxSpeed, maxSpeed, maxSpeed, count, ClientboundLevelParticlesPacket.RandomizationType.DEFAULT);
   }

   public ClientboundLevelParticlesPacket {
      super();
   }

   public PacketType<ClientboundLevelParticlesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_LEVEL_PARTICLES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleParticleEvent(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ParticleTypes.STREAM_CODEC, ClientboundLevelParticlesPacket::particle, ByteBufCodecs.BOOL, ClientboundLevelParticlesPacket::overrideLimiter, ByteBufCodecs.BOOL, ClientboundLevelParticlesPacket::alwaysShow, ByteBufCodecs.DOUBLE, ClientboundLevelParticlesPacket::x, ByteBufCodecs.DOUBLE, ClientboundLevelParticlesPacket::y, ByteBufCodecs.DOUBLE, ClientboundLevelParticlesPacket::z, ByteBufCodecs.FLOAT, ClientboundLevelParticlesPacket::xDist, ByteBufCodecs.FLOAT, ClientboundLevelParticlesPacket::yDist, ByteBufCodecs.FLOAT, ClientboundLevelParticlesPacket::zDist, ByteBufCodecs.FLOAT, ClientboundLevelParticlesPacket::xMaxSpeed, ByteBufCodecs.FLOAT, ClientboundLevelParticlesPacket::yMaxSpeed, ByteBufCodecs.FLOAT, ClientboundLevelParticlesPacket::zMaxSpeed, ByteBufCodecs.VAR_INT, ClientboundLevelParticlesPacket::count, ClientboundLevelParticlesPacket.RandomizationType.STREAM_CODEC, ClientboundLevelParticlesPacket::randomizationType, ClientboundLevelParticlesPacket::new);
   }

   public static enum RandomizationType {
      DEFAULT(0),
      ALTERNATIVE(1),
      ALTERNATIVE_WITH_SPEED(2);

      public static final StreamCodec<ByteBuf, RandomizationType> STREAM_CODEC = ByteBufCodecs.enumCodec(RandomizationType.class, (h) -> h.id);
      private final int id;

      private RandomizationType(final int id) {
         this.id = id;
      }

      public boolean isAlternative() {
         return this != DEFAULT;
      }

      // $FF: synthetic method
      private static RandomizationType[] $values() {
         return new RandomizationType[]{DEFAULT, ALTERNATIVE, ALTERNATIVE_WITH_SPEED};
      }
   }
}
