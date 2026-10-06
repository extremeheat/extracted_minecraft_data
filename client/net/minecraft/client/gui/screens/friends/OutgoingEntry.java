package net.minecraft.client.gui.screens.friends;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class OutgoingEntry extends AbstractFriendsEntryContainerWidget {
   public static final WidgetSprites REVOKE_SPRITE = new WidgetSprites(Identifier.withDefaultNamespace("friends/cancel"));
   public static final int SPRITE_TEXTURE_SIZE = 12;
   private static final Component REVOKE_INVITE = Component.translatable("gui.friends.cancel_request");
   private final SpriteIconButton revokeButton;

   public OutgoingEntry(final Minecraft minecraft, final int width, final FriendsListActions friendsListActions, final PlayerSocialManager.PlayerData playerData) {
      super(minecraft, width, playerData.id(), playerData.name());
      Button.CreateNarration narration = getSpriteIconNarration(Component.translatable("gui.friends.narration.button.cancel_request", playerData.name()));
      this.revokeButton = ((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)SpriteIconButton.builder(REVOKE_INVITE, (var2) -> friendsListActions.revokeOutgoingFriendRequest(this.playerId), true).size(20, 20)).sprite((WidgetSprites)REVOKE_SPRITE, 12, 12).tooltip(REVOKE_INVITE)).createNarration(narration)).switchToLoadingAfterPress()).build();
      this.addChild(this.revokeButton);
   }

   public void disable() {
      this.revokeButton.active = false;
   }

   protected Component getEntryNarration() {
      return Component.translatable("gui.friends.narration.entry.outgoing", this.playerName);
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
      this.revokeButton.setPosition(this.getX() + this.getWidth() - this.revokeButton.getWidth(), this.getY() + (this.getHeight() - this.revokeButton.getHeight()) / 2);
      this.revokeButton.extractRenderState(graphics, mouseX, mouseY, a);
   }
}
