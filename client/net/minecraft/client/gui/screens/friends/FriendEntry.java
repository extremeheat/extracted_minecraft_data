package net.minecraft.client.gui.screens.friends;

import com.mojang.authlib.yggdrasil.response.PresenceStatus;
import com.mojang.authlib.yggdrasil.response.PresenceStatusDto;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PopupScreen;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

class FriendEntry extends AbstractFriendsEntryContainerWidget {
   private static final WidgetSprites REMOVE_SPRITE = new WidgetSprites(Identifier.withDefaultNamespace("friends/remove"));
   private static final Component UNFRIEND = Component.translatable("gui.friends.unfriend");
   private static final Component CONFIRM_TITLE = Component.translatable("gui.friends.confirm_title");
   private static final Component CONFIRM_UNFRIEND = Component.translatable("gui.friends.confirm_unfriend");
   private final SpriteIconButton removeButton;
   private final StringWidget statusWidget;
   private final @Nullable PresenceStatusDto presence;

   public FriendEntry(final Minecraft minecraft, final FriendsOverlayScreen screen, final PlayerSocialManager.PlayerData playerData, final @Nullable PresenceStatusDto presence, final boolean initiallyLoading, final Runnable onAction) {
      super(minecraft, screen, 0, 0, screen.getOverlayWidth() - 16, 28, playerData, true);
      this.presence = presence;
      String var10003 = presence == null ? "offline" : presence.status().toString().toLowerCase(Locale.ROOT);
      this.statusWidget = new StringWidget(Component.translatable("gui.friends.presence.status." + var10003).withColor(presence == null ? -6250336 : -16711936), minecraft.font);
      this.addChild(this.statusWidget);
      Button.CreateNarration narration = getSpriteIconNarration(Component.translatable("gui.friends.narration.button.unfriend", playerData.name()));
      this.removeButton = SpriteIconButton.builder(UNFRIEND, (var2) -> this.confirmRemoveFriend(onAction), true).size(20, 20).sprite((WidgetSprites)REMOVE_SPRITE, 13, 11).tooltip(UNFRIEND).narration(narration).build();
      if (initiallyLoading) {
         this.removeButton.setLoading(true);
      }

      this.addChild(this.removeButton);
   }

   public int presenceStatusSortOrder() {
      PresenceStatus status = this.presence == null ? PresenceStatus.OFFLINE : this.presence.status();
      byte var10000;
      switch (status) {
         case PLAYING_HOSTED_SERVER -> var10000 = 0;
         case PLAYING_SERVER -> var10000 = 1;
         case PLAYING_REALMS -> var10000 = 2;
         case PLAYING_OFFLINE -> var10000 = 3;
         case ONLINE -> var10000 = 4;
         case OFFLINE -> var10000 = 5;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   void disable() {
      this.removeButton.active = false;
   }

   protected Component getEntryNarration() {
      return Component.translatable("gui.friends.narration.entry.friend", this.playerName);
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
      int verticalCenter = this.getY() + (this.getHeight() - 20) / 2;
      int removeX = this.getX() + this.getWidth() - 20;
      this.removeButton.setPosition(removeX, verticalCenter);
      this.removeButton.extractRenderState(graphics, mouseX, mouseY, a);
      int statusWidgetX = this.playerFaceWidget.getRight() + 4;
      int statusWidth = removeX - statusWidgetX - 2;
      this.statusWidget.setMaxWidth(statusWidth, StringWidget.TextOverflow.SCROLLING);
      this.statusWidget.setPosition(statusWidgetX, this.nameWidget.getBottom() + 2);
      this.statusWidget.extractRenderState(graphics, mouseX, mouseY, a);
   }

   private void confirmRemoveFriend(final Runnable action) {
      this.minecraft.gui.setScreen((new PopupScreen.Builder(this.screen, CONFIRM_TITLE)).addMessage(CONFIRM_UNFRIEND).addButton(CommonComponents.GUI_REMOVE, (var2) -> {
         this.removeButton.setLoading(true);
         this.screen.startFriendAction();
         action.run();
         this.minecraft.gui.setScreen(this.screen);
      }).addButton(CommonComponents.GUI_CANCEL, (var1) -> this.minecraft.gui.setScreen(this.screen)).build());
   }
}
