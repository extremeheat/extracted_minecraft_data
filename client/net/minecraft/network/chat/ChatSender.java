package net.minecraft.network.chat;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;

public record ChatSender(UUID a, Component b, @Nullable Component c) {
   private final UUID uuid;
   private final Component name;
   @Nullable
   private final Component teamName;

   public ChatSender(UUID var1, Component var2) {
      this(var1, var2, null);
   }

   public ChatSender(FriendlyByteBuf var1) {
      this(var1.readUUID(), var1.readComponent(), var1.readNullable(FriendlyByteBuf::readComponent));
   }

   public ChatSender(UUID var1, Component var2, @Nullable Component var3) {
      super();
      this.uuid = var1;
      this.name = var2;
      this.teamName = var3;
   }

   public static ChatSender system(Component var0) {
      return new ChatSender(Util.NIL_UUID, var0);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUUID(this.uuid);
      var1.writeComponent(this.name);
      var1.writeNullable(this.teamName, FriendlyByteBuf::writeComponent);
   }

   public ChatSender withTeamName(Component var1) {
      return new ChatSender(this.uuid, this.name, var1);
   }
}
