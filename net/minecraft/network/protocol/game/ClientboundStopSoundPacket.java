package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class ClientboundStopSoundPacket implements Packet {
   private ResourceLocation name;
   private SoundSource source;

   public ClientboundStopSoundPacket() {
   }

   public ClientboundStopSoundPacket(@Nullable ResourceLocation var1, @Nullable SoundSource var2) {
      this.name = var1;
      this.source = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      byte var2 = var1.readByte();
      if ((var2 & 1) > 0) {
         this.source = (SoundSource)var1.readEnum(SoundSource.class);
      }

      if ((var2 & 2) > 0) {
         this.name = var1.readResourceLocation();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
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

   @Nullable
   public ResourceLocation getName() {
      return this.name;
   }

   @Nullable
   public SoundSource getSource() {
      return this.source;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleStopSoundEvent(this);
   }
}
