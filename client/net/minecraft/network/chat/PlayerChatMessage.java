package net.minecraft.network.chat;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record PlayerChatMessage(SignedMessageHeader c, MessageSignature d, SignedMessageBody e, Optional<Component> f, FilterMask g) {
   private final SignedMessageHeader signedHeader;
   private final MessageSignature headerSignature;
   private final SignedMessageBody signedBody;
   private final Optional<Component> unsignedContent;
   private final FilterMask filterMask;
   public static final Duration MESSAGE_EXPIRES_AFTER_SERVER = Duration.ofMinutes(5L);
   public static final Duration MESSAGE_EXPIRES_AFTER_CLIENT = MESSAGE_EXPIRES_AFTER_SERVER.plus(Duration.ofMinutes(2L));

   public PlayerChatMessage(FriendlyByteBuf var1) {
      this(
         new SignedMessageHeader(var1),
         new MessageSignature(var1),
         new SignedMessageBody(var1),
         var1.readOptional(FriendlyByteBuf::readComponent),
         FilterMask.read(var1)
      );
   }

   public PlayerChatMessage(SignedMessageHeader var1, MessageSignature var2, SignedMessageBody var3, Optional<Component> var4, FilterMask var5) {
      super();
      this.signedHeader = var1;
      this.headerSignature = var2;
      this.signedBody = var3;
      this.unsignedContent = var4;
      this.filterMask = var5;
   }

   public static PlayerChatMessage system(ChatMessageContent var0) {
      return unsigned(MessageSigner.system(), var0);
   }

   public static PlayerChatMessage unsigned(MessageSigner var0, ChatMessageContent var1) {
      SignedMessageBody var2 = new SignedMessageBody(var1, var0.timeStamp(), var0.salt(), LastSeenMessages.EMPTY);
      SignedMessageHeader var3 = new SignedMessageHeader(null, var0.profileId());
      return new PlayerChatMessage(var3, MessageSignature.EMPTY, var2, Optional.empty(), FilterMask.PASS_THROUGH);
   }

   public void write(FriendlyByteBuf var1) {
      this.signedHeader.write(var1);
      this.headerSignature.write(var1);
      this.signedBody.write(var1);
      var1.writeOptional(this.unsignedContent, FriendlyByteBuf::writeComponent);
      FilterMask.write(var1, this.filterMask);
   }

   public PlayerChatMessage withUnsignedContent(Component var1) {
      Optional var2 = !this.signedContent().decorated().equals(var1) ? Optional.of(var1) : Optional.empty();
      return new PlayerChatMessage(this.signedHeader, this.headerSignature, this.signedBody, var2, this.filterMask);
   }

   public PlayerChatMessage removeUnsignedContent() {
      return this.unsignedContent.isPresent()
         ? new PlayerChatMessage(this.signedHeader, this.headerSignature, this.signedBody, Optional.empty(), this.filterMask)
         : this;
   }

   public PlayerChatMessage filter(FilterMask var1) {
      return this.filterMask.equals(var1) ? this : new PlayerChatMessage(this.signedHeader, this.headerSignature, this.signedBody, this.unsignedContent, var1);
   }

   public PlayerChatMessage filter(boolean var1) {
      return this.filter(var1 ? this.filterMask : FilterMask.PASS_THROUGH);
   }

   public boolean verify(SignatureValidator var1) {
      return this.headerSignature.verify(var1, this.signedHeader, this.signedBody);
   }

   public boolean verify(ProfilePublicKey var1) {
      SignatureValidator var2 = var1.createSignatureValidator();
      return this.verify(var2);
   }

   public boolean verify(ChatSender var1) {
      ProfilePublicKey var2 = var1.profilePublicKey();
      return var2 != null && this.verify(var2);
   }

   public ChatMessageContent signedContent() {
      return this.signedBody.content();
   }

   public Component serverContent() {
      return this.unsignedContent().orElse(this.signedContent().decorated());
   }

   public Instant timeStamp() {
      return this.signedBody.timeStamp();
   }

   public long salt() {
      return this.signedBody.salt();
   }

   public boolean hasExpiredServer(Instant var1) {
      return var1.isAfter(this.timeStamp().plus(MESSAGE_EXPIRES_AFTER_SERVER));
   }

   public boolean hasExpiredClient(Instant var1) {
      return var1.isAfter(this.timeStamp().plus(MESSAGE_EXPIRES_AFTER_CLIENT));
   }

   public MessageSigner signer() {
      return new MessageSigner(this.signedHeader.sender(), this.timeStamp(), this.salt());
   }

   @Nullable
   public LastSeenMessages.Entry toLastSeenEntry() {
      MessageSigner var1 = this.signer();
      return !this.headerSignature.isEmpty() && !var1.isSystem() ? new LastSeenMessages.Entry(var1.profileId(), this.headerSignature) : null;
   }

   public boolean hasSignatureFrom(UUID var1) {
      return !this.headerSignature.isEmpty() && this.signedHeader.sender().equals(var1);
   }

   public boolean isFullyFiltered() {
      return this.filterMask.isFullyFiltered();
   }
}
