package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundSetTitlesPacket implements Packet {
   private ClientboundSetTitlesPacket.Type type;
   private Component text;
   private int fadeInTime;
   private int stayTime;
   private int fadeOutTime;

   public ClientboundSetTitlesPacket() {
   }

   public ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type var1, Component var2) {
      this(var1, var2, -1, -1, -1);
   }

   public ClientboundSetTitlesPacket(int var1, int var2, int var3) {
      this(ClientboundSetTitlesPacket.Type.TIMES, (Component)null, var1, var2, var3);
   }

   public ClientboundSetTitlesPacket(ClientboundSetTitlesPacket.Type var1, @Nullable Component var2, int var3, int var4, int var5) {
      this.type = var1;
      this.text = var2;
      this.fadeInTime = var3;
      this.stayTime = var4;
      this.fadeOutTime = var5;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.type = (ClientboundSetTitlesPacket.Type)var1.readEnum(ClientboundSetTitlesPacket.Type.class);
      if (this.type == ClientboundSetTitlesPacket.Type.TITLE || this.type == ClientboundSetTitlesPacket.Type.SUBTITLE || this.type == ClientboundSetTitlesPacket.Type.ACTIONBAR) {
         this.text = var1.readComponent();
      }

      if (this.type == ClientboundSetTitlesPacket.Type.TIMES) {
         this.fadeInTime = var1.readInt();
         this.stayTime = var1.readInt();
         this.fadeOutTime = var1.readInt();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.type);
      if (this.type == ClientboundSetTitlesPacket.Type.TITLE || this.type == ClientboundSetTitlesPacket.Type.SUBTITLE || this.type == ClientboundSetTitlesPacket.Type.ACTIONBAR) {
         var1.writeComponent(this.text);
      }

      if (this.type == ClientboundSetTitlesPacket.Type.TIMES) {
         var1.writeInt(this.fadeInTime);
         var1.writeInt(this.stayTime);
         var1.writeInt(this.fadeOutTime);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetTitles(this);
   }

   public ClientboundSetTitlesPacket.Type getType() {
      return this.type;
   }

   public Component getText() {
      return this.text;
   }

   public int getFadeInTime() {
      return this.fadeInTime;
   }

   public int getStayTime() {
      return this.stayTime;
   }

   public int getFadeOutTime() {
      return this.fadeOutTime;
   }

   public static enum Type {
      TITLE,
      SUBTITLE,
      ACTIONBAR,
      TIMES,
      CLEAR,
      RESET;
   }
}
