package net.minecraft.client.multiplayer.chat;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.ExtraCodecs;

public interface LoggedChatMessage extends LoggedChatEvent {
   static Player player(GameProfile var0, PlayerChatMessage var1, ChatTrustLevel var2) {
      return new Player(var0, var1, var2);
   }

   static System system(Component var0, Instant var1) {
      return new System(var0, var1);
   }

   Component toContentComponent();

   default Component toNarrationComponent() {
      return this.toContentComponent();
   }

   boolean canReport(UUID var1);

   public static record Player(GameProfile profile, PlayerChatMessage message, ChatTrustLevel trustLevel) implements LoggedChatMessage {
      public static final MapCodec<Player> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ExtraCodecs.GAME_PROFILE.fieldOf("profile").forGetter(Player::profile), PlayerChatMessage.MAP_CODEC.forGetter(Player::message), ChatTrustLevel.CODEC.optionalFieldOf("trust_level", ChatTrustLevel.SECURE).forGetter(Player::trustLevel)).apply(var0, Player::new);
      });
      private static final DateTimeFormatter TIME_FORMATTER;

      public Player(GameProfile profile, PlayerChatMessage message, ChatTrustLevel trustLevel) {
         super();
         this.profile = profile;
         this.message = message;
         this.trustLevel = trustLevel;
      }

      public Component toContentComponent() {
         if (!this.message.filterMask().isEmpty()) {
            Component var1 = this.message.filterMask().applyWithFormatting(this.message.signedContent());
            return (Component)(var1 != null ? var1 : Component.empty());
         } else {
            return this.message.decoratedContent();
         }
      }

      public Component toNarrationComponent() {
         Component var1 = this.toContentComponent();
         Component var2 = this.getTimeComponent();
         return Component.translatable("gui.chatSelection.message.narrate", this.profile.getName(), var1, var2);
      }

      public Component toHeadingComponent() {
         Component var1 = this.getTimeComponent();
         return Component.translatable("gui.chatSelection.heading", this.profile.getName(), var1);
      }

      private Component getTimeComponent() {
         LocalDateTime var1 = LocalDateTime.ofInstant(this.message.timeStamp(), ZoneOffset.systemDefault());
         return Component.literal(var1.format(TIME_FORMATTER)).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
      }

      public boolean canReport(UUID var1) {
         return this.message.hasSignatureFrom(var1);
      }

      public UUID profileId() {
         return this.profile.getId();
      }

      public LoggedChatEvent.Type type() {
         return LoggedChatEvent.Type.PLAYER;
      }

      public GameProfile profile() {
         return this.profile;
      }

      public PlayerChatMessage message() {
         return this.message;
      }

      public ChatTrustLevel trustLevel() {
         return this.trustLevel;
      }

      static {
         TIME_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
      }
   }

   public static record System(Component message, Instant timeStamp) implements LoggedChatMessage {
      public static final MapCodec<System> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(ComponentSerialization.CODEC.fieldOf("message").forGetter(System::message), ExtraCodecs.INSTANT_ISO8601.fieldOf("time_stamp").forGetter(System::timeStamp)).apply(var0, System::new);
      });

      public System(Component message, Instant timeStamp) {
         super();
         this.message = message;
         this.timeStamp = timeStamp;
      }

      public Component toContentComponent() {
         return this.message;
      }

      public boolean canReport(UUID var1) {
         return false;
      }

      public LoggedChatEvent.Type type() {
         return LoggedChatEvent.Type.SYSTEM;
      }

      public Component message() {
         return this.message;
      }

      public Instant timeStamp() {
         return this.timeStamp;
      }
   }
}
