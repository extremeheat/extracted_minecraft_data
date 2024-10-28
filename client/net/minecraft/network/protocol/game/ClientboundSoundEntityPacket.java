package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class ClientboundSoundEntityPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSoundEntityPacket> STREAM_CODEC = Packet.codec(ClientboundSoundEntityPacket::write, ClientboundSoundEntityPacket::new);
   private final Holder<SoundEvent> sound;
   private final SoundSource source;
   private final int id;
   private final float volume;
   private final float pitch;
   private final long seed;

   public ClientboundSoundEntityPacket(Holder<SoundEvent> var1, SoundSource var2, Entity var3, float var4, float var5, long var6) {
      super();
      this.sound = var1;
      this.source = var2;
      this.id = var3.getId();
      this.volume = var4;
      this.pitch = var5;
      this.seed = var6;
   }

   private ClientboundSoundEntityPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.sound = (Holder)SoundEvent.STREAM_CODEC.decode(var1);
      this.source = (SoundSource)var1.readEnum(SoundSource.class);
      this.id = var1.readVarInt();
      this.volume = var1.readFloat();
      this.pitch = var1.readFloat();
      this.seed = var1.readLong();
   }

   private void write(RegistryFriendlyByteBuf var1) {
      SoundEvent.STREAM_CODEC.encode(var1, this.sound);
      var1.writeEnum(this.source);
      var1.writeVarInt(this.id);
      var1.writeFloat(this.volume);
      var1.writeFloat(this.pitch);
      var1.writeLong(this.seed);
   }

   public PacketType<ClientboundSoundEntityPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SOUND_ENTITY;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSoundEntityEvent(this);
   }

   public Holder<SoundEvent> getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public int getId() {
      return this.id;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public long getSeed() {
      return this.seed;
   }
}
