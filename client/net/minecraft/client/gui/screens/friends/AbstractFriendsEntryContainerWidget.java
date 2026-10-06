package net.minecraft.client.gui.screens.friends;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ResolvableProfile;

public abstract class AbstractFriendsEntryContainerWidget extends AbstractContainerWidget {
   public static final int BUTTON_SIZE = 20;
   public static final int VERTICAL_PADDING = 3;
   protected final Minecraft minecraft;
   protected final PlayerFaceWidget playerFaceWidget;
   protected final StringWidget nameWidget;
   protected final String playerName;
   protected final UUID playerId;
   private final List<AbstractWidget> children = new ArrayList();

   public AbstractFriendsEntryContainerWidget(final Minecraft minecraft, final int width, final UUID playerId, final String playerName) {
      super(0, 0, width, 29, CommonComponents.EMPTY);
      this.minecraft = minecraft;
      this.playerName = playerName;
      this.playerId = playerId;
      this.playerFaceWidget = new PlayerFaceWidget(24, 1, ResolvableProfile.createUnresolved(playerId));
      this.nameWidget = new StringWidget(Component.literal(playerName), minecraft.font);
      this.addChild(this.playerFaceWidget);
      this.addChild(this.nameWidget);
   }

   public abstract void disable();

   public UUID playerId() {
      return this.playerId;
   }

   public String playerName() {
      return this.playerName;
   }

   protected abstract Component getEntryNarration();

   protected static Button.CreateNarration getSpriteIconNarration(final Component actionDescription) {
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

   protected int getProfileInfoHeight() {
      Objects.requireNonNull(this.minecraft.font);
      return 9;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      this.extractPlayerFace(graphics, mouseX, mouseY, a);
      int nameX = this.playerFaceWidget.getRight() + 5;
      this.nameWidget.setPosition(nameX, this.getY() + (this.getHeight() - this.getProfileInfoHeight()) / 2 + 1);
      this.nameWidget.extractRenderState(graphics, mouseX, mouseY, a);
   }

   protected void extractPlayerFace(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      this.playerFaceWidget.setPosition(this.getX(), this.getY() + (this.getHeight() - this.playerFaceWidget.getHeight()) / 2);
      this.playerFaceWidget.extractRenderState(graphics, mouseX, mouseY, a);
   }
}
