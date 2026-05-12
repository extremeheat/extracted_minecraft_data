package net.minecraft.client.gui.screens.friends;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlayerFaceWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;

abstract class AbstractFriendsEntryContainerWidget extends AbstractContainerWidget {
   static final int SPRITE_TEXTURE_SIZE = 18;
   static final int BUTTON_SIZE = 20;
   static final int FACE_SIZE = 24;
   static final int PADDING = 4;
   private static final int WINDOW_MARGIN = 8;
   protected final Minecraft minecraft;
   protected final FriendsOverlayScreen screen;
   protected final PlayerFaceWidget playerFaceWidget;
   protected final StringWidget nameWidget;
   protected final String playerName;
   protected final boolean showingStatus;
   private final List<AbstractWidget> children;

   public AbstractFriendsEntryContainerWidget(final Minecraft minecraft, final FriendsOverlayScreen screen, final int x, final int y, final int width, final int height, final PlayerSocialManager.PlayerData playerData, final PlayerSkin playerSkin) {
      this(minecraft, screen, x, y, width, height, playerData, playerSkin, false);
   }

   public AbstractFriendsEntryContainerWidget(final Minecraft minecraft, final FriendsOverlayScreen screen, final int x, final int y, final int width, final int height, final PlayerSocialManager.PlayerData playerData, final PlayerSkin playerSkin, final boolean showingStatus) {
      super(x, y, width, height, Component.empty());
      this.children = new ArrayList();
      this.minecraft = minecraft;
      this.screen = screen;
      this.playerName = playerData.name();
      this.playerFaceWidget = new PlayerFaceWidget(24, playerSkin);
      this.nameWidget = new StringWidget(Component.literal(this.playerName), minecraft.font);
      this.addChild(this.playerFaceWidget);
      this.addChild(this.nameWidget);
      this.showingStatus = showingStatus;
   }

   abstract void disable();

   protected abstract Component getEntryNarration();

   static Button.CreateNarration getSpriteIconNarration(final Component actionDescription) {
      return (var1) -> Component.translatable("narrator.select", actionDescription);
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, this.nameWidget.getMessage());
      GuiEventListener focusedChild = this.getFocused();
      if (focusedChild instanceof AbstractWidget focusedWidget) {
         focusedWidget.updateNarration(output.nest());
      } else {
         output.add(NarratedElementType.USAGE, this.getEntryNarration());
      }

   }

   public Collection<? extends NarratableEntry> getNarratables() {
      List<NarratableEntry> narratables = new ArrayList(this.children.size() + 1);
      narratables.addAll(this.children);
      narratables.add(this);
      return narratables;
   }

   protected final void addChild(final AbstractWidget child) {
      this.children.add(child);
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected int contentHeight() {
      return this.height;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      if ((Boolean)this.minecraft.options.highContrast().get()) {
         graphics.fill(this.getX() + 4, this.getY(), this.getX() + this.getWidth() + 4, this.getY() + this.getHeight(), -16777216);
      }

      this.playerFaceWidget.setPosition(this.getX() + 8, this.getY() + (this.getHeight() - this.playerFaceWidget.getHeight()) / 2);
      this.playerFaceWidget.extractRenderState(graphics, mouseX, mouseY, a);
      int nameY = this.getY() + this.getHeight() / (this.showingStatus ? 3 : 2) - this.nameWidget.getHeight() / 2;
      this.nameWidget.setPosition(this.playerFaceWidget.getRight() + 4, nameY);
      this.nameWidget.extractRenderState(graphics, mouseX, mouseY, a);
   }
}
