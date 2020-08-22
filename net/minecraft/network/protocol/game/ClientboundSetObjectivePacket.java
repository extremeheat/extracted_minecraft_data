package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ClientboundSetObjectivePacket implements Packet {
   private String objectiveName;
   private Component displayName;
   private ObjectiveCriteria.RenderType renderType;
   private int method;

   public ClientboundSetObjectivePacket() {
   }

   public ClientboundSetObjectivePacket(Objective var1, int var2) {
      this.objectiveName = var1.getName();
      this.displayName = var1.getDisplayName();
      this.renderType = var1.getRenderType();
      this.method = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.objectiveName = var1.readUtf(16);
      this.method = var1.readByte();
      if (this.method == 0 || this.method == 2) {
         this.displayName = var1.readComponent();
         this.renderType = (ObjectiveCriteria.RenderType)var1.readEnum(ObjectiveCriteria.RenderType.class);
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.objectiveName);
      var1.writeByte(this.method);
      if (this.method == 0 || this.method == 2) {
         var1.writeComponent(this.displayName);
         var1.writeEnum(this.renderType);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddObjective(this);
   }

   public String getObjectiveName() {
      return this.objectiveName;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public int getMethod() {
      return this.method;
   }

   public ObjectiveCriteria.RenderType getRenderType() {
      return this.renderType;
   }
}
