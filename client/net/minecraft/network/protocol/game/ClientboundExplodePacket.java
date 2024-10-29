package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

public record ClientboundExplodePacket(Vec3 center, Optional<Vec3> playerKnockback, ParticleOptions explosionParticle, Holder<SoundEvent> explosionSound) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundExplodePacket> STREAM_CODEC;

   public ClientboundExplodePacket(Vec3 var1, Optional<Vec3> var2, ParticleOptions var3, Holder<SoundEvent> var4) {
      super();
      this.center = var1;
      this.playerKnockback = var2;
      this.explosionParticle = var3;
      this.explosionSound = var4;
   }

   public PacketType<ClientboundExplodePacket> type() {
      return GamePacketTypes.CLIENTBOUND_EXPLODE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleExplosion(this);
   }

   public Vec3 center() {
      return this.center;
   }

   public Optional<Vec3> playerKnockback() {
      return this.playerKnockback;
   }

   public ParticleOptions explosionParticle() {
      return this.explosionParticle;
   }

   public Holder<SoundEvent> explosionSound() {
      return this.explosionSound;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Vec3.STREAM_CODEC, ClientboundExplodePacket::center, Vec3.STREAM_CODEC.apply(ByteBufCodecs::optional), ClientboundExplodePacket::playerKnockback, ParticleTypes.STREAM_CODEC, ClientboundExplodePacket::explosionParticle, SoundEvent.STREAM_CODEC, ClientboundExplodePacket::explosionSound, ClientboundExplodePacket::new);
   }
}
