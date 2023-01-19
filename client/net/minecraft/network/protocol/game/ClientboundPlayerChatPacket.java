package net.minecraft.network.protocol.game;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatSender;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;

public record ClientboundPlayerChatPacket(Component a, Optional<Component> b, int c, ChatSender d, Instant e, Crypt.SaltSignaturePair f)
   implements Packet<ClientGamePacketListener> {
   private final Component signedContent;
   private final Optional<Component> unsignedContent;
   private final int typeId;
   private final ChatSender sender;
   private final Instant timeStamp;
   private final Crypt.SaltSignaturePair saltSignature;
   private static final Duration MESSAGE_EXPIRES_AFTER = ServerboundChatPacket.MESSAGE_EXPIRES_AFTER.plus(Duration.ofMinutes(2L));

   public ClientboundPlayerChatPacket(FriendlyByteBuf var1) {
      this(
         var1.readComponent(),
         var1.readOptional(FriendlyByteBuf::readComponent),
         var1.readVarInt(),
         new ChatSender(var1),
         var1.readInstant(),
         new Crypt.SaltSignaturePair(var1)
      );
   }

   public ClientboundPlayerChatPacket(Component var1, Optional<Component> var2, int var3, ChatSender var4, Instant var5, Crypt.SaltSignaturePair var6) {
      super();
      this.signedContent = var1;
      this.unsignedContent = var2;
      this.typeId = var3;
      this.sender = var4;
      this.timeStamp = var5;
      this.saltSignature = var6;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeComponent(this.signedContent);
      var1.writeOptional(this.unsignedContent, FriendlyByteBuf::writeComponent);
      var1.writeVarInt(this.typeId);
      this.sender.write(var1);
      var1.writeInstant(this.timeStamp);
      Crypt.SaltSignaturePair.write(var1, this.saltSignature);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerChat(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }

   public PlayerChatMessage getMessage() {
      MessageSignature var1 = new MessageSignature(this.sender.uuid(), this.timeStamp, this.saltSignature);
      return new PlayerChatMessage(this.signedContent, var1, this.unsignedContent);
   }

   private Instant getExpiresAt() {
      return this.timeStamp.plus(MESSAGE_EXPIRES_AFTER);
   }

   public boolean hasExpired(Instant var1) {
      return var1.isAfter(this.getExpiresAt());
   }

   public ChatType resolveType(Registry<ChatType> var1) {
      return Objects.requireNonNull((ChatType)var1.byId(this.typeId), "Invalid chat type");
   }
}
