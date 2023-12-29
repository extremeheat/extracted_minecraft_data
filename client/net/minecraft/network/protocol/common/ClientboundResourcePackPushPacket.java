package net.minecraft.network.protocol.common;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

public record ClientboundResourcePackPushPacket(UUID b, String c, String d, boolean e, @Nullable Component f) implements Packet<ClientCommonPacketListener> {
   private final UUID id;
   private final String url;
   private final String hash;
   private final boolean required;
   @Nullable
   private final Component prompt;
   public static final int MAX_HASH_LENGTH = 40;

   public ClientboundResourcePackPushPacket(UUID var1, String var2, String var3, boolean var4, @Nullable Component var5) {
      super();
      if (var3.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + var3.length() + ")");
      } else {
         this.id = var1;
         this.url = var2;
         this.hash = var3;
         this.required = var4;
         this.prompt = var5;
      }
   }

   public ClientboundResourcePackPushPacket(FriendlyByteBuf var1) {
      this(var1.readUUID(), var1.readUtf(), var1.readUtf(40), var1.readBoolean(), var1.readNullable(FriendlyByteBuf::readComponentTrusted));
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.id);
      var1.writeUtf(this.url);
      var1.writeUtf(this.hash);
      var1.writeBoolean(this.required);
      var1.writeNullable(this.prompt, FriendlyByteBuf::writeComponent);
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleResourcePackPush(this);
   }
}
