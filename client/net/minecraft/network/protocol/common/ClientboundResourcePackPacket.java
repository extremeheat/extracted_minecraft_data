package net.minecraft.network.protocol.common;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public class ClientboundResourcePackPacket implements Packet<ClientCommonPacketListener> {
   public static final int MAX_HASH_LENGTH = 40;
   private final String url;
   private final String hash;
   private final boolean required;
   @Nullable
   private final Component prompt;

   public ClientboundResourcePackPacket(String var1, String var2, boolean var3, @Nullable Component var4) {
      super();
      if (var2.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + var2.length() + ")");
      } else {
         this.url = var1;
         this.hash = var2;
         this.required = var3;
         this.prompt = var4;
      }
   }

   public ClientboundResourcePackPacket(FriendlyByteBuf var1) {
      super();
      this.url = var1.readUtf();
      this.hash = var1.readUtf(40);
      this.required = var1.readBoolean();
      this.prompt = var1.readNullable(FriendlyByteBuf::readComponent);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.url);
      var1.writeUtf(this.hash);
      var1.writeBoolean(this.required);
      var1.writeNullable(this.prompt, FriendlyByteBuf::writeComponent);
   }

   public void handle(ClientCommonPacketListener var1) {
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

   @Nullable
   public Component getPrompt() {
      return this.prompt;
   }
}
