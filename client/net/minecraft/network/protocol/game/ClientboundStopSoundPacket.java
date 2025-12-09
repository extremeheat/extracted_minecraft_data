package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import org.jspecify.annotations.Nullable;

public class ClientboundStopSoundPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundStopSoundPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundStopSoundPacket>codec(ClientboundStopSoundPacket::write, ClientboundStopSoundPacket::new);
   private static final int HAS_SOURCE = 1;
   private static final int HAS_SOUND = 2;
   private final @Nullable Identifier name;
   private final @Nullable SoundSource source;

   public ClientboundStopSoundPacket(@Nullable Identifier var1, @Nullable SoundSource var2) {
      super();
      this.name = var1;
      this.source = var2;
   }

   private ClientboundStopSoundPacket(FriendlyByteBuf var1) {
      super();
      byte var2 = var1.readByte();
      if ((var2 & 1) > 0) {
         this.source = (SoundSource)var1.readEnum(SoundSource.class);
      } else {
         this.source = null;
      }

      if ((var2 & 2) > 0) {
         this.name = var1.readIdentifier();
      } else {
         this.name = null;
      }

   }

   private void write(FriendlyByteBuf var1) {
      if (this.source != null) {
         if (this.name != null) {
            var1.writeByte(3);
            var1.writeEnum(this.source);
            var1.writeIdentifier(this.name);
         } else {
            var1.writeByte(1);
            var1.writeEnum(this.source);
         }
      } else if (this.name != null) {
         var1.writeByte(2);
         var1.writeIdentifier(this.name);
      } else {
         var1.writeByte(0);
      }

   }

   public PacketType<ClientboundStopSoundPacket> type() {
      return GamePacketTypes.CLIENTBOUND_STOP_SOUND;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleStopSoundEvent(this);
   }

   public @Nullable Identifier getName() {
      return this.name;
   }

   public @Nullable SoundSource getSource() {
      return this.source;
   }
}
