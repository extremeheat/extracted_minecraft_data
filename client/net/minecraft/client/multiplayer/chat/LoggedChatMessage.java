package net.minecraft.client.multiplayer.chat;

import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageHeader;

public interface LoggedChatMessage extends LoggedChatEvent {
   static LoggedChatMessage.Player player(GameProfile var0, Component var1, PlayerChatMessage var2, ChatTrustLevel var3) {
      return new LoggedChatMessage.Player(var0, var1, var2, var3);
   }

   static LoggedChatMessage.System system(Component var0, Instant var1) {
      return new LoggedChatMessage.System(var0, var1);
   }

   Component toContentComponent();

   default Component toNarrationComponent() {
      return this.toContentComponent();
   }

   boolean canReport(UUID var1);

   public static record Player(GameProfile a, Component b, PlayerChatMessage c, ChatTrustLevel d) implements LoggedChatMessage, LoggedChatMessageLink {
      private final GameProfile profile;
      private final Component displayName;
      private final PlayerChatMessage message;
      private final ChatTrustLevel trustLevel;
      private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);

      public Player(GameProfile var1, Component var2, PlayerChatMessage var3, ChatTrustLevel var4) {
         super();
         this.profile = var1;
         this.displayName = var2;
         this.message = var3;
         this.trustLevel = var4;
      }

      @Override
      public Component toContentComponent() {
         if (!this.message.filterMask().isEmpty()) {
            Component var1 = this.message.filterMask().apply(this.message.signedContent());
            return Objects.requireNonNullElse(var1, CommonComponents.EMPTY);
         } else {
            return this.message.serverContent();
         }
      }

      @Override
      public Component toNarrationComponent() {
         Component var1 = this.toContentComponent();
         Component var2 = this.getTimeComponent();
         return Component.translatable("gui.chatSelection.message.narrate", this.displayName, var1, var2);
      }

      public Component toHeadingComponent() {
         Component var1 = this.getTimeComponent();
         return Component.translatable("gui.chatSelection.heading", this.displayName, var1);
      }

      private Component getTimeComponent() {
         LocalDateTime var1 = LocalDateTime.ofInstant(this.message.timeStamp(), ZoneOffset.systemDefault());
         return Component.literal(var1.format(TIME_FORMATTER)).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
      }

      @Override
      public boolean canReport(UUID var1) {
         return this.message.hasSignatureFrom(var1);
      }

      @Override
      public SignedMessageHeader header() {
         return this.message.signedHeader();
      }

      @Override
      public byte[] bodyDigest() {
         return this.message.signedBody().hash().asBytes();
      }

      @Override
      public MessageSignature headerSignature() {
         return this.message.headerSignature();
      }

      public UUID profileId() {
         return this.profile.getId();
      }
   }

   public static record System(Component a, Instant b) implements LoggedChatMessage {
      private final Component message;
      private final Instant timeStamp;

      public System(Component var1, Instant var2) {
         super();
         this.message = var1;
         this.timeStamp = var2;
      }

      @Override
      public Component toContentComponent() {
         return this.message;
      }

      @Override
      public boolean canReport(UUID var1) {
         return false;
      }
   }
}
