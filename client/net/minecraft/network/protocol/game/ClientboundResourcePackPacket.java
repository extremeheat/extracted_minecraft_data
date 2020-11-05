package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundResourcePackPacket implements Packet<ClientGamePacketListener> {
   private String url;
   private String hash;
   private boolean required;

   public ClientboundResourcePackPacket() {
      super();
   }

   public ClientboundResourcePackPacket(String var1, String var2, boolean var3) {
      super();
      if (var2.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + var2.length() + ")");
      } else {
         this.url = var1;
         this.hash = var2;
         this.required = var3;
      }
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.url = var1.readUtf(32767);
      this.hash = var1.readUtf(40);
      this.required = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUtf(this.url);
      var1.writeUtf(this.hash);
      var1.writeBoolean(this.required);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleResourcePack(this);
   }

   public String getUrl() {
      return this.url;
   }

   public String getHash() {
      return this.hash;
   }

   public boolean isRequired() {
      return this.required;
   }
}
