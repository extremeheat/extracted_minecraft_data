package net.minecraft.client.gui.screens.friends;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class IncomingEntry extends AbstractFriendsEntryContainerWidget {
   public static final WidgetSprites ACCEPT_SPRITE = new WidgetSprites(Identifier.withDefaultNamespace("friends/accept"), Identifier.withDefaultNamespace("friends/accept_highlighted"));
   public static final WidgetSprites REJECT_SPRITE = new WidgetSprites(Identifier.withDefaultNamespace("friends/reject"), Identifier.withDefaultNamespace("friends/reject_highlighted"));
   public static final int SPRITE_TEXTURE_SIZE = 18;
   private static final int BUTTON_SPACING = 4;
   public static final Component ACCEPT = Component.translatable("gui.friends.accept");
   public static final Component REJECT = Component.translatable("gui.friends.decline");
   public static final Tooltip ACCEPT_TOOLTIP = Tooltip.create(Component.translatable("gui.friends.accept.tooltip"));
   public static final Tooltip REJECT_TOOLTIP = Tooltip.create(Component.translatable("gui.friends.decline.tooltip"));
   private final SpriteIconButton acceptButton;
   private final SpriteIconButton rejectButton;

   public IncomingEntry(final Minecraft minecraft, final int width, final FriendsListActions friendsListActions, final PlayerSocialManager.PlayerData playerData) {
      super(minecraft, width, playerData.id(), playerData.name());
      Button.CreateNarration acceptNarration = getSpriteIconNarration(Component.translatable("gui.friends.narration.button.accept", playerData.name()));
      Button.CreateNarration rejectNarration = getSpriteIconNarration(Component.translatable("gui.friends.narration.button.decline", playerData.name()));
      this.acceptButton = ((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)SpriteIconButton.builder(ACCEPT, (var2) -> friendsListActions.acceptIncomingFriendRequest(this.playerId), true).size(20, 20)).sprite((WidgetSprites)ACCEPT_SPRITE, 18, 18).tooltip(ACCEPT_TOOLTIP)).createNarration(acceptNarration)).switchToLoadingAfterPress()).build();
      this.addChild(this.acceptButton);
      this.rejectButton = ((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)SpriteIconButton.builder(REJECT, (var2) -> friendsListActions.declineIncomingFriendRequest(this.playerId), true).size(20, 20)).sprite((WidgetSprites)REJECT_SPRITE, 18, 18).tooltip(REJECT_TOOLTIP)).createNarration(rejectNarration)).switchToLoadingAfterPress()).build();
      this.addChild(this.rejectButton);
   }

   public void disable() {
      this.acceptButton.active = false;
      this.rejectButton.active = false;
   }

   protected Component getEntryNarration() {
      return Component.translatable("gui.friends.narration.entry.incoming", this.playerName);
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
      this.rejectButton.setPosition(this.getX() + this.getWidth() - this.rejectButton.getWidth(), this.getY() + (this.getHeight() - this.rejectButton.getHeight()) / 2);
      this.rejectButton.extractRenderState(graphics, mouseX, mouseY, a);
      this.acceptButton.setPosition(this.rejectButton.getX() - this.acceptButton.getWidth() - 4, this.getY() + (this.getHeight() - this.acceptButton.getHeight()) / 2);
      this.acceptButton.extractRenderState(graphics, mouseX, mouseY, a);
   }
}
