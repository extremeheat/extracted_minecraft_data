package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetExperiencePacket implements Packet<ClientGamePacketListener> {
   private final float experienceProgress;
   private final int totalExperience;
   private final int experienceLevel;

   public ClientboundSetExperiencePacket(float var1, int var2, int var3) {
      super();
      this.experienceProgress = var1;
      this.totalExperience = var2;
      this.experienceLevel = var3;
   }

   public ClientboundSetExperiencePacket(FriendlyByteBuf var1) {
      super();
      this.experienceProgress = var1.readFloat();
      this.experienceLevel = var1.readVarInt();
      this.totalExperience = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeFloat(this.experienceProgress);
      var1.writeVarInt(this.experienceLevel);
      var1.writeVarInt(this.totalExperience);
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
