package net.minecraft.network.protocol.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSoundSequencePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSoundSequencePacket> STREAM_CODEC = Packet.codec(
      ClientboundSoundSequencePacket::write, ClientboundSoundSequencePacket::new
   );
   private final List<ClientboundSoundSequencePacket.DelayedSound> sounds;

   public ClientboundSoundSequencePacket(List<ClientboundSoundSequencePacket.DelayedSound> var1) {
      super();
      this.sounds = var1;
   }

   private ClientboundSoundSequencePacket(RegistryFriendlyByteBuf var1) {
      super();
      int var2 = var1.readInt();
      Builder var3 = ImmutableList.builder();

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.add(new ClientboundSoundSequencePacket.DelayedSound(var1.readInt(), ClientboundSoundPacket.STREAM_CODEC.decode(var1)));
      }

      this.sounds = var3.build();
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeInt(this.sounds.size());

      for(ClientboundSoundSequencePacket.DelayedSound var3 : this.sounds) {
         var1.writeInt(var3.ticks);
         ClientboundSoundPacket.STREAM_CODEC.encode(var1, var3.packet);
      }
   }

   @Override
   public PacketType<ClientboundSoundSequencePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SOUND_SEQUENCE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSoundSequenceEvent(this);
   }

   public List<ClientboundSoundSequencePacket.DelayedSound> getSounds() {
      return this.sounds;
   }

   public static record DelayedSound(int a, ClientboundSoundPacket b) {
      final int ticks;
      final ClientboundSoundPacket packet;

      public DelayedSound(int var1, ClientboundSoundPacket var2) {
         super();
         this.ticks = var1;
         this.packet = var2;
      }
   }
}
