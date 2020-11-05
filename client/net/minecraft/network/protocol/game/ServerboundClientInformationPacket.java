package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;

public class ServerboundClientInformationPacket implements Packet<ServerGamePacketListener> {
   private String language;
   private int viewDistance;
   private ChatVisiblity chatVisibility;
   private boolean chatColors;
   private int modelCustomisation;
   private HumanoidArm mainHand;

   public ServerboundClientInformationPacket() {
      super();
   }

   public ServerboundClientInformationPacket(String var1, int var2, ChatVisiblity var3, boolean var4, int var5, HumanoidArm var6) {
      super();
      this.language = var1;
      this.viewDistance = var2;
      this.chatVisibility = var3;
      this.chatColors = var4;
      this.modelCustomisation = var5;
      this.mainHand = var6;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.language = var1.readUtf(16);
      this.viewDistance = var1.readByte();
      this.chatVisibility = (ChatVisiblity)var1.readEnum(ChatVisiblity.class);
      this.chatColors = var1.readBoolean();
      this.modelCustomisation = var1.readUnsignedByte();
      this.mainHand = (HumanoidArm)var1.readEnum(HumanoidArm.class);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.language);
      var1.writeByte(this.viewDistance);
      var1.writeEnum(this.chatVisibility);
      var1.writeBoolean(this.chatColors);
      var1.writeByte(this.modelCustomisation);
      var1.writeEnum(this.mainHand);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleClientInformation(this);
   }

   public ChatVisiblity getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean getChatColors() {
      return this.chatColors;
   }

   public int getModelCustomisation() {
      return this.modelCustomisation;
   }

   public HumanoidArm getMainHand() {
      return this.mainHand;
   }
}
