package net.minecraft.client.gui.screens.friends;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.PlayerFaceWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.social.PlayerStatus;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.component.ResolvableProfile;

public class PlayerProfileWidget extends AbstractContainerWidget {
   public static final int FACE_SIZE = 24;
   public static final int FACE_BORDER = 1;
   public static final int LINE_SPACING = 2;
   public static final int NAME_LEFT_PADDING = 5;
   public static final int NAME_TOP_PADDING = 1;
   private final LinearLayout layout;
   private final PlainTextButton profileNameButton;
   private final PresenceStatusWidget statusWidget;

   public PlayerProfileWidget(final Minecraft minecraft, final UUID profileId, final String profileName) {
      super(0, 0, 0, 0, CommonComponents.EMPTY);
      LinearLayout profileInfo = LinearLayout.vertical().spacing(2);
      this.profileNameButton = (PlainTextButton)profileInfo.addChild(this.createProfileNameButton(minecraft, profileName));
      this.statusWidget = (PresenceStatusWidget)profileInfo.addChild(new PresenceStatusWidget(PlayerStatus.DEFAULT, minecraft.font));
      this.layout = LinearLayout.horizontal();
      this.layout.addChild(new PlayerFaceWidget(24, 1, ResolvableProfile.createUnresolved(profileId)));
      this.layout.addChild(profileInfo, (Consumer)((settings) -> settings.paddingLeft(5).paddingTop(1).alignVerticallyMiddle()));
      this.layout.arrangeElements();
      this.setWidth(this.layout.getWidth());
      this.setHeight(this.layout.getHeight());
   }

   private PlainTextButton createProfileNameButton(final Minecraft minecraft, final String profileName) {
      Component profileNameComponent = Component.literal(profileName);
      int profileNameWidth = minecraft.font.width((FormattedText)profileNameComponent);
      Objects.requireNonNull(minecraft.font);
      PlainTextButton button = new PlainTextButton(0, 0, profileNameWidth, 9, profileNameComponent, (var2) -> minecraft.keyboardHandler.setClipboard(profileName), (var1) -> AbstractWidget.wrapDefaultNarrationMessage(Component.translatable("gui.friends.my_profile_name.narration", profileNameComponent)), minecraft.font);
      button.setTooltip(Tooltip.create(CommonComponents.GUI_COPY_TO_CLIPBOARD));
      return button;
   }

   public PresenceStatusWidget statusWidget() {
      return this.statusWidget;
   }

   protected int contentHeight() {
      return this.height;
   }

   public void setX(final int x) {
      super.setX(x);
      this.layout.setX(x);
      this.layout.arrangeElements();
   }

   public void setY(final int y) {
      super.setY(y);
      this.layout.setY(y);
      this.layout.arrangeElements();
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      this.layout.visitWidgets((child) -> child.extractRenderState(graphics, mouseX, mouseY, a));
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
   }

   public Collection<? extends NarratableEntry> getNarratables() {
      return List.of(this.profileNameButton, this.statusWidget);
   }

   public List<? extends GuiEventListener> children() {
      return List.of(this.profileNameButton, this.statusWidget);
   }
}
