package net.minecraft.client.gui.screens.friends;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.SpriteIconCycleButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.RemoteFriendListUpdateHandler;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

class AddFriendWidget extends AbstractContainerWidget {
   private static final WidgetSprites ADD_SPRITE = new WidgetSprites(Identifier.withDefaultNamespace("friends/send_request"));
   private static final Component ENTER_NICKNAME = Component.translatable("gui.friends.enter_nickname");
   private static final Component SEND_REQUEST = Component.translatable("gui.friends.send_request");
   private static final Component SORT_LIST = Component.translatable("gui.friends.sort");
   private static final int INPUT_SPACING = 4;
   private static final int BUTTON_SIZE = 20;
   private final EditBox editBox;
   private final SpriteIconButton addButton;
   private final SpriteIconCycleButton<FriendsListOrder.SortBy> sortButton;
   private final Minecraft minecraft = Minecraft.getInstance();
   private final LinearLayout layout;

   public AddFriendWidget(final FriendsListFilter filter, final FriendsListOrder order, final int width, final Runnable afterSend) {
      super(0, 0, width, 0, CommonComponents.EMPTY);
      this.editBox = new EditBox(this.minecraft.font, width - 48, 20, ENTER_NICKNAME) {
         {
            Objects.requireNonNull(AddFriendWidget.this);
         }

         public boolean keyPressed(final KeyEvent event) {
            boolean enterPressed = event.shortcutKey() == 13 || event.shortcutKey() == 1073741912;
            boolean elementsActive = this.isActive() && AddFriendWidget.this.addButton.active;
            if (elementsActive && this.isFocused() && enterPressed) {
               AddFriendWidget.this.addButton.playDownSound(AddFriendWidget.this.minecraft.getSoundManager());
               AddFriendWidget.this.addButton.onPress(event);
               return true;
            } else {
               return super.keyPressed(event);
            }
         }
      };
      this.editBox.setHint(ENTER_NICKNAME);
      this.editBox.setResponder((value) -> {
         String trimmedValue = value.trim();
         this.applyState(trimmedValue.isEmpty() ? AddFriendWidget.State.EMPTY_INPUT : AddFriendWidget.State.READY);
         filter.updateSearchFilter(trimmedValue);
      });
      this.addButton = ((SpriteIconButton.Builder)((SpriteIconButton.Builder)((SpriteIconButton.Builder)SpriteIconButton.builder(SEND_REQUEST, (var2) -> this.sendFriendRequest(afterSend), true).sprite((WidgetSprites)ADD_SPRITE, 15, 15).size(20, 20)).tooltip(SEND_REQUEST)).switchToLoadingAfterPress()).build();
      this.sortButton = ((SpriteIconCycleButton.Builder)((SpriteIconCycleButton.Builder)SpriteIconCycleButton.builder(SORT_LIST, FriendsListOrder.SortBy::getTranslation, FriendsListOrder.DEFAULT_SORTING).withValues(FriendsListOrder.SortBy.values())).sprite(FriendsListOrder.SortBy::getSprites).size(20, 20).withTooltip()).build((var1, sorting) -> order.updateSorting(sorting));
      this.applyState(AddFriendWidget.State.EMPTY_INPUT);
      this.layout = LinearLayout.vertical();
      LinearLayout inputRow = LinearLayout.horizontal().spacing(4);
      inputRow.addChild(this.sortButton);
      inputRow.addChild(this.editBox);
      inputRow.addChild(this.addButton);
      this.layout.addChild(inputRow);
      this.layout.arrangeElements();
      this.setHeight(this.layout.getHeight());
   }

   private @Nullable Component getInvalidInputReason(final String name) {
      PlayerSocialManager playerSocialManager = this.minecraft.getPlayerSocialManager();
      if (this.minecraft.getUser().getName().equalsIgnoreCase(name)) {
         return Component.translatable("gui.friends.validation.cannot_add_self");
      } else if (contains(playerSocialManager.getFriends(), name)) {
         return Component.translatable("gui.friends.validation.already_friend", name);
      } else if (contains(playerSocialManager.getOutgoingRequests(), name)) {
         return Component.translatable("gui.friends.validation.already_outgoing", name);
      } else {
         return contains(playerSocialManager.getIncomingRequests(), name) ? Component.translatable("gui.friends.validation.already_incoming", name) : null;
      }
   }

   private static boolean contains(final List<PlayerSocialManager.PlayerData> players, final String playerName) {
      for(PlayerSocialManager.PlayerData playerData : players) {
         if (playerData.name().equalsIgnoreCase(playerName)) {
            return true;
         }
      }

      return false;
   }

   public void applyState(final State newState) {
      switch (newState.ordinal()) {
         case 0:
            this.editBox.setEditable(true);
            this.editBox.active = true;
            this.addButton.setLoading(false);
            this.addButton.active = false;
            break;
         case 1:
            RemoteFriendListUpdateHandler.State friendListState = this.minecraft.getPlayerSocialManager().getFriendListState();
            boolean listReady = friendListState == RemoteFriendListUpdateHandler.State.SUCCESS;
            this.editBox.setEditable(true);
            this.editBox.active = true;
            this.addButton.setLoading(false);
            this.addButton.active = listReady;
            break;
         case 2:
            this.editBox.setEditable(false);
            this.editBox.active = false;
            this.editBox.setFocused(false);
            this.addButton.active = false;
            this.addButton.setLoading(true);
            break;
         case 3:
            this.editBox.setEditable(false);
            this.editBox.active = false;
            this.editBox.setFocused(false);
            this.addButton.active = false;
            this.addButton.setLoading(false);
      }

   }

   private void sendFriendRequest(final Runnable afterSend) {
      String name = this.getValue();
      if (name.isBlank()) {
         this.applyState(AddFriendWidget.State.EMPTY_INPUT);
      } else {
         Component invalidInputReason = this.getInvalidInputReason(name);
         if (invalidInputReason != null) {
            SystemToast.addOrUpdate(this.minecraft.gui.toastManager(), SystemToast.SystemToastId.FRIEND_SYSTEM_NOTIFICATION, invalidInputReason, (Component)null);
            this.applyState(AddFriendWidget.State.READY);
         } else {
            this.applyState(AddFriendWidget.State.SENDING);
            this.minecraft.getPlayerSocialManager().sendFriendRequest(name).thenAcceptAsync((var2) -> {
               this.editBox.setValue("");
               this.applyState(AddFriendWidget.State.EMPTY_INPUT);
               afterSend.run();
            }, this.minecraft);
         }
      }
   }

   public EditBox getEditBox() {
      return this.editBox;
   }

   public String getValue() {
      return this.editBox.getValue().trim();
   }

   public void setValue(final String value) {
      this.editBox.setValue(value);
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
      return List.of(this.sortButton, this.editBox, this.addButton);
   }

   public List<? extends GuiEventListener> children() {
      return List.of(this.sortButton, this.editBox, this.addButton);
   }

   static enum State {
      EMPTY_INPUT,
      READY,
      SENDING,
      DISABLED;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{EMPTY_INPUT, READY, SENDING, DISABLED};
      }
   }
}
