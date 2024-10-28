package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class ClientboundStopSoundPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundStopSoundPacket> STREAM_CODEC = Packet.codec(ClientboundStopSoundPacket::write, ClientboundStopSoundPacket::new);
   private static final int HAS_SOURCE = 1;
   private static final int HAS_SOUND = 2;
   @Nullable
   private final ResourceLocation name;
   @Nullable
   private final SoundSource source;

   public ClientboundStopSoundPacket(@Nullable ResourceLocation var1, @Nullable SoundSource var2) {
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
         this.name = var1.readResourceLocation();
      } else {
         this.name = null;
      }

   }

   private void write(FriendlyByteBuf var1) {
      if (this.source != null) {
         if (this.name != null) {
            var1.writeByte(3);
            var1.writeEnum(this.source);
            var1.writeResourceLocation(this.name);
         } else {
            var1.writeByte(1);
            var1.writeEnum(this.source);
         }
      } else if (this.name != null) {
         var1.writeByte(2);
         var1.writeResourceLocation(this.name);
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

   @Nullable
   public ResourceLocation getName() {
      return this.name;
   }

   @Nullable
   public SoundSource getSource() {
      return this.source;
   }
}
