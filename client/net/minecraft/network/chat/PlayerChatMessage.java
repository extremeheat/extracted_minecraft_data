package net.minecraft.network.chat;

import com.google.common.primitives.Ints;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;

public record PlayerChatMessage(SignedMessageLink link, @Nullable MessageSignature signature, SignedMessageBody signedBody, @Nullable Component unsignedContent, FilterMask filterMask) {
   public static final MapCodec<PlayerChatMessage> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SignedMessageLink.CODEC.fieldOf("link").forGetter(PlayerChatMessage::link), MessageSignature.CODEC.optionalFieldOf("signature").forGetter((var0x) -> {
         return Optional.ofNullable(var0x.signature);
      }), SignedMessageBody.MAP_CODEC.forGetter(PlayerChatMessage::signedBody), ComponentSerialization.CODEC.optionalFieldOf("unsigned_content").forGetter((var0x) -> {
         return Optional.ofNullable(var0x.unsignedContent);
      }), FilterMask.CODEC.optionalFieldOf("filter_mask", FilterMask.PASS_THROUGH).forGetter(PlayerChatMessage::filterMask)).apply(var0, (var0x, var1, var2, var3, var4) -> {
         return new PlayerChatMessage(var0x, (MessageSignature)var1.orElse((Object)null), var2, (Component)var3.orElse((Object)null), var4);
      });
   });
   private static final UUID SYSTEM_SENDER;
   public static final Duration MESSAGE_EXPIRES_AFTER_SERVER;
   public static final Duration MESSAGE_EXPIRES_AFTER_CLIENT;

   public PlayerChatMessage(SignedMessageLink link, @Nullable MessageSignature signature, SignedMessageBody signedBody, @Nullable Component unsignedContent, FilterMask filterMask) {
      super();
      this.link = link;
      this.signature = signature;
      this.signedBody = signedBody;
      this.unsignedContent = unsignedContent;
      this.filterMask = filterMask;
   }

   public static PlayerChatMessage system(String var0) {
      return unsigned(SYSTEM_SENDER, var0);
   }

   public static PlayerChatMessage unsigned(UUID var0, String var1) {
      SignedMessageBody var2 = SignedMessageBody.unsigned(var1);
      SignedMessageLink var3 = SignedMessageLink.unsigned(var0);
      return new PlayerChatMessage(var3, (MessageSignature)null, var2, (Component)null, FilterMask.PASS_THROUGH);
   }

   public PlayerChatMessage withUnsignedContent(Component var1) {
      Component var2 = !var1.equals(Component.literal(this.signedContent())) ? var1 : null;
      return new PlayerChatMessage(this.link, this.signature, this.signedBody, var2, this.filterMask);
   }

   public PlayerChatMessage removeUnsignedContent() {
      return this.unsignedContent != null ? new PlayerChatMessage(this.link, this.signature, this.signedBody, (Component)null, this.filterMask) : this;
   }

   public PlayerChatMessage filter(FilterMask var1) {
      return this.filterMask.equals(var1) ? this : new PlayerChatMessage(this.link, this.signature, this.signedBody, this.unsignedContent, var1);
   }

   public PlayerChatMessage filter(boolean var1) {
      return this.filter(var1 ? this.filterMask : FilterMask.PASS_THROUGH);
   }

   public PlayerChatMessage removeSignature() {
      SignedMessageBody var1 = SignedMessageBody.unsigned(this.signedContent());
      SignedMessageLink var2 = SignedMessageLink.unsigned(this.sender());
      return new PlayerChatMessage(var2, (MessageSignature)null, var1, this.unsignedContent, this.filterMask);
   }

   public static void updateSignature(SignatureUpdater.Output var0, SignedMessageLink var1, SignedMessageBody var2) throws SignatureException {
      var0.update(Ints.toByteArray(1));
      var1.updateSignature(var0);
      var2.updateSignature(var0);
   }

   public boolean verify(SignatureValidator var1) {
      return this.signature != null && this.signature.verify(var1, (var1x) -> {
         updateSignature(var1x, this.link, this.signedBody);
      });
   }

   public String signedContent() {
      return this.signedBody.content();
   }

   public Component decoratedContent() {
      return (Component)Objects.requireNonNullElseGet(this.unsignedContent, () -> {
         return Component.literal(this.signedContent());
      });
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

   public UUID sender() {
      return this.link.sender();
   }

   public boolean isSystem() {
      return this.sender().equals(SYSTEM_SENDER);
   }

   public boolean hasSignature() {
      return this.signature != null;
   }

   public boolean hasSignatureFrom(UUID var1) {
      return this.hasSignature() && this.link.sender().equals(var1);
   }

   public boolean isFullyFiltered() {
      return this.filterMask.isFullyFiltered();
   }

   public SignedMessageLink link() {
      return this.link;
   }

   @Nullable
   public MessageSignature signature() {
      return this.signature;
   }

   public SignedMessageBody signedBody() {
      return this.signedBody;
   }

   @Nullable
   public Component unsignedContent() {
      return this.unsignedContent;
   }

   public FilterMask filterMask() {
      return this.filterMask;
   }

   static {
      SYSTEM_SENDER = Util.NIL_UUID;
      MESSAGE_EXPIRES_AFTER_SERVER = Duration.ofMinutes(5L);
      MESSAGE_EXPIRES_AFTER_CLIENT = MESSAGE_EXPIRES_AFTER_SERVER.plus(Duration.ofMinutes(2L));
   }
}
