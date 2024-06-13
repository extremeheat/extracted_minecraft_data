package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.util.StringRepresentable;

public enum ChatTrustLevel implements StringRepresentable {
   SECURE("secure"),
   MODIFIED("modified"),
   NOT_SECURE("not_secure");

   public static final Codec<ChatTrustLevel> CODEC = StringRepresentable.fromEnum(ChatTrustLevel::values);
   private final String serializedName;

   private ChatTrustLevel(String var3) {
      this.serializedName = var3;
   }

   public static ChatTrustLevel evaluate(PlayerChatMessage var0, Component var1, Instant var2) {
      if (!var0.hasSignature() || var0.hasExpiredClient(var2)) {
         return NOT_SECURE;
      } else {
         return isModified(var0, var1) ? MODIFIED : SECURE;
      }
   }

   private static boolean isModified(PlayerChatMessage var0, Component var1) {
      if (!var1.getString().contains(var0.signedContent())) {
         return true;
      } else {
         Component var2 = var0.unsignedContent();
         return var2 == null ? false : containsModifiedStyle(var2);
      }
   }

   private static boolean containsModifiedStyle(Component var0) {
      return var0.<Boolean>visit((var0x, var1) -> isModifiedStyle(var0x) ? Optional.of(true) : Optional.empty(), Style.EMPTY).orElse(false);
   }

   private static boolean isModifiedStyle(Style var0) {
      return !var0.getFont().equals(Style.DEFAULT_FONT);
   }

   public boolean isNotSecure() {
      return this == NOT_SECURE;
   }

   @Nullable
   public GuiMessageTag createTag(PlayerChatMessage var1) {
      return switch (this) {
         case MODIFIED -> GuiMessageTag.chatModified(var1.signedContent());
         case NOT_SECURE -> GuiMessageTag.chatNotSecure();
         default -> null;
      };
   }

   @Override
   public String getSerializedName() {
      return this.serializedName;
   }
}
