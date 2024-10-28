package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetExperiencePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetExperiencePacket> STREAM_CODEC = Packet.codec(ClientboundSetExperiencePacket::write, ClientboundSetExperiencePacket::new);
   private final float experienceProgress;
   private final int totalExperience;
   private final int experienceLevel;

   public ClientboundSetExperiencePacket(float var1, int var2, int var3) {
      super();
      this.experienceProgress = var1;
      this.totalExperience = var2;
      this.experienceLevel = var3;
   }

   private ClientboundSetExperiencePacket(FriendlyByteBuf var1) {
      super();
      this.experienceProgress = var1.readFloat();
      this.experienceLevel = var1.readVarInt();
      this.totalExperience = var1.readVarInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeFloat(this.experienceProgress);
      var1.writeVarInt(this.experienceLevel);
      var1.writeVarInt(this.totalExperience);
   }

   public PacketType<ClientboundSetExperiencePacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_EXPERIENCE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetExperience(this);
   }

   public float getExperienceProgress() {
      return this.experienceProgress;
   }

   public int getTotalExperience() {
      return this.totalExperience;
   }

   public int getExperienceLevel() {
      return this.experienceLevel;
   }
}
