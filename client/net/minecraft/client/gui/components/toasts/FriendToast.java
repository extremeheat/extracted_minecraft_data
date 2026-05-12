package net.minecraft.client.gui.components.toasts;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

public class FriendToast implements Toast {
   private static final WidgetSprites BACKGROUND_SPRITE = new WidgetSprites(Identifier.withDefaultNamespace("friends/toast_background"));
   private static final int FACE_SIZE = 20;
   private static final int TEXT_LEFT_WITH_FACE = 30;
   private static final int TEXT_LEFT_NO_FACE = 7;
   private static final int PADDING_TOP = 7;
   private static final int PADDING_BOTTOM = 3;
   private static final int LINE_SPACING = 11;
   private static final long DEFAULT_DISPLAY_TIME_MS = 5000L;
   private final @Nullable PlayerSkin skin;
   private final List<FormattedCharSequence> messageLines;
   private final long displayTimeMs;
   private Toast.Visibility visibility;

   public FriendToast(final Font font, final @Nullable PlayerSkin skin, final Component message) {
      this(font, skin, message, 5000L);
   }

   public FriendToast(final Font font, final @Nullable PlayerSkin skin, final Component message, final long displayTimeMs) {
      super();
      this.visibility = Toast.Visibility.SHOW;
      this.skin = skin;
      int textLeft = skin != null ? 30 : 7;
      this.messageLines = font.split(message, 160 - textLeft - 4);
      this.displayTimeMs = displayTimeMs;
   }

   public Toast.Visibility getWantedVisibility() {
      return this.visibility;
   }

   public void update(final ToastManager manager, final long fullyVisibleForMs) {
      if ((double)fullyVisibleForMs >= (double)this.displayTimeMs * manager.getNotificationDisplayTimeMultiplier()) {
         this.visibility = Toast.Visibility.HIDE;
      }

   }

   public int height() {
      return 7 + this.contentHeight() + 3;
   }

   private int contentHeight() {
      return Math.max(this.messageLines.size(), 2) * 11;
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final Font font, final long fullyVisibleForMs) {
      int height = this.height();
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)BACKGROUND_SPRITE.get(true, false), 0, 0, this.width(), height);
      int textLeft;
      if (this.skin != null) {
         PlayerFaceExtractor.extractRenderState(graphics, this.skin, 6, 6, 20);
         textLeft = 30;
      } else {
         textLeft = 7;
      }

      int totalTextHeight = this.messageLines.size() * 11;
      int textTop = 7 + (this.contentHeight() - totalTextHeight) / 2;

      for(int i = 0; i < this.messageLines.size(); ++i) {
         graphics.text(font, (FormattedCharSequence)((FormattedCharSequence)this.messageLines.get(i)), textLeft, textTop + i * 11, -1, false);
      }

   }

   public void hide() {
      this.visibility = Toast.Visibility.HIDE;
   }

   public static void add(final ToastManager toastManager, final Font font, final @Nullable PlayerSkin skin, final Component message) {
      toastManager.addToast(new FriendToast(font, skin, message));
   }

   public static void showFriendRequestSent(final Minecraft minecraft, final String nickname) {
      add(minecraft.gui.toastManager(), minecraft.font, (PlayerSkin)null, Component.translatable("gui.friends.toast.request_sent.message", nickname));
   }

   public static void showFriendRequestReceived(final Minecraft minecraft, final String nickname, final PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.request_received.message", nickname));
   }

   public static void showFriendRequestAccepted(final Minecraft minecraft, final String nickname, final PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.request_accepted.message", nickname));
   }

   public static void showFriendAdded(final Minecraft minecraft, final String nickname, final PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.friend_added.message", nickname));
   }

   public static void showFriendJoinRequest(final Minecraft minecraft, final String profileName, final PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.join_request.message", profileName, minecraft.options.keyFriends.getTranslatedKeyMessage()));
   }

   public static void showFriendInvited(final Minecraft minecraft, final String profileName, final PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.friend_invited.message", profileName));
   }

   public static void showInviteFromFriend(final Minecraft minecraft, final String profileName, final PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.invite_from_friend.message", profileName, minecraft.options.keyFriends.getTranslatedKeyMessage()));
   }

   public static void showRequestToJoinFriend(final Minecraft minecraft, final String profileName, final PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.request_to_join_friend.message", profileName));
   }

   public static void showHostInviteExpired(final Minecraft minecraft, final String profileName, final @Nullable PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.host_invite_expired.message", profileName));
   }

   public static void showJoinInviteExpired(final Minecraft minecraft, final String profileName, final @Nullable PlayerSkin skin) {
      add(minecraft.gui.toastManager(), minecraft.font, skin, Component.translatable("gui.friends.toast.join_invite_expired.message", profileName));
   }
}
