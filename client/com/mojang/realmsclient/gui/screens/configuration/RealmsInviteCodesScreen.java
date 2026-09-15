package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.InviteCode;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.AbstractRealmsCodeScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RealmsInviteCodesScreen extends AbstractRealmsCodeScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final Component TITLE = Component.translatable("mco.configure.world.buttons.invite_codes");
   private static final Component LOADING_TEXT = Component.translatable("mco.configure.world.invite_codes.loading");
   private static final Component COPY_LABEL = Component.translatable("mco.configure.world.invite_codes.copy");
   private static final Component DELETE_LABEL = Component.translatable("mco.configure.world.invite_codes.delete");
   private static final Component EDIT_LABEL = Component.translatable("mco.configure.world.invite_codes.edit");
   private static final Component CREATE_LABEL = Component.translatable("mco.configure.world.invite_codes.create");
   private static final int MAX_INVITE_CODES = 5;
   private static final int SPACING = 8;
   private static final int CONTENT_WIDTH = 308;
   private static final int BOTTOM_BUTTON_WIDTH = 150;
   private static final int TOP_BUTTON_WIDTH = 97;
   private static final int FOOTER_HEIGHT = 64;
   private static final int ENTRY_HEIGHT = 28;
   private final RealmsConfigureWorldScreen configureScreen;
   private final long realmId;
   private @Nullable Button copyButton;
   private @Nullable Button editButton;
   private @Nullable Button deleteButton;
   private @Nullable Button createButton;
   private @Nullable Button backButton;
   private @Nullable InviteCodeSelectionList inviteCodeList;
   private List<InviteCode> inviteCodes = List.of();
   private boolean fetchedInviteCodes = false;

   public RealmsInviteCodesScreen(final RealmsConfigureWorldScreen configureScreen, final RealmsServer serverData) {
      super(TITLE);
      this.configureScreen = configureScreen;
      this.realmId = serverData.id;
   }

   public void init() {
      this.layout.removeChildren();
      LinearLayout header = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(8));
      header.defaultCellSetting().alignHorizontallyCenter();
      Component headerTitle = (Component)(this.fetchedInviteCodes ? Component.translatable("mco.configure.world.invite_codes.title", this.inviteCodes.size(), 5) : this.getTitle());
      header.addChild(new StringWidget(headerTitle, this.font));
      int headerHeight = 33;
      if (this.fetchedInviteCodes && this.inviteCodes.isEmpty()) {
         MultiLineTextWidget subtitle = (MultiLineTextWidget)header.addChild((new MultiLineTextWidget(Component.translatable("mco.configure.world.invite_codes.subtitle", 5).withColor(-6250336), this.font)).setCentered(true).setMaxWidth(308));
         headerHeight += subtitle.getHeight();
      }

      this.layout.setHeaderHeight(headerHeight);
      LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.vertical().spacing(8));
      LinearLayout actionRow = (LinearLayout)footer.addChild(LinearLayout.horizontal().spacing(8));
      this.copyButton = (Button)actionRow.addChild(Button.builder(COPY_LABEL, (var1) -> this.onCopy()).width(97).build());
      this.deleteButton = (Button)actionRow.addChild(Button.builder(DELETE_LABEL, (var1) -> this.onDelete()).width(97).build());
      this.editButton = (Button)actionRow.addChild(Button.builder(EDIT_LABEL, (var1) -> this.onEdit()).width(97).build());
      LinearLayout manageRow = (LinearLayout)footer.addChild(LinearLayout.horizontal().spacing(8));
      this.createButton = (Button)manageRow.addChild(Button.builder(CREATE_LABEL, (var1) -> this.onCreate()).width(150).build());
      this.backButton = (Button)manageRow.addChild(Button.builder(CommonComponents.GUI_BACK, (var1) -> this.onClose()).width(150).build());
      this.layout.setFooterHeight(64);
      if (this.fetchedInviteCodes) {
         this.inviteCodeList = (InviteCodeSelectionList)this.layout.addToContents(new InviteCodeSelectionList(this.inviteCodes));
      } else {
         this.inviteCodeList = null;
         this.layout.addToContents(new LoadingDotsWidget(this.font, LOADING_TEXT));
      }

      this.updateButtonStates();
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
      if (!this.fetchedInviteCodes) {
         this.fetchInviteCodes();
      }

   }

   private void fetchInviteCodes() {
      this.setControlsActive(false);
      Screen errorReturnScreen = (Screen)(this.fetchedInviteCodes ? this : this.configureScreen);
      RealmsUtil.supplyAsync((client) -> client.inviteCodes(this.realmId), (exception) -> this.handleFailure(exception, "Couldn't get invite codes", errorReturnScreen)).thenAcceptAsync((result) -> {
         this.inviteCodes = result.inviteCodes();
         this.fetchedInviteCodes = true;
         this.rebuildWidgets();
         this.scheduleNarration();
      }, this.screenExecutor);
   }

   private void updateButtonStates() {
      boolean hasSelection = this.inviteCodeList != null && this.inviteCodeList.getSelectedCode() != null;
      if (this.copyButton != null) {
         this.copyButton.active = hasSelection;
      }

      if (this.editButton != null) {
         this.editButton.active = hasSelection;
      }

      if (this.deleteButton != null) {
         this.deleteButton.active = hasSelection;
      }

      if (this.createButton != null) {
         this.createButton.active = this.fetchedInviteCodes && this.inviteCodes.size() < 5;
      }

   }

   private void setControlsActive(final boolean controlsActive) {
      if (this.inviteCodeList != null) {
         this.inviteCodeList.active = controlsActive;
      }

      if (controlsActive) {
         this.updateButtonStates();
      } else {
         if (this.copyButton != null) {
            this.copyButton.active = false;
         }

         if (this.editButton != null) {
            this.editButton.active = false;
         }

         if (this.deleteButton != null) {
            this.deleteButton.active = false;
         }

         if (this.createButton != null) {
            this.createButton.active = false;
         }

      }
   }

   private void handleFailure(final RealmsServiceException exception, final String message, final Screen errorReturnScreen) {
      LOGGER.error("{}", message, exception);
      this.screenExecutor.execute(() -> {
         this.setControlsActive(true);
         if (this.backButton != null) {
            this.backButton.active = true;
         }

         this.minecraft.gui.setScreen(new RealmsGenericErrorScreen(exception, errorReturnScreen));
      });
   }

   protected void repositionElements() {
      super.repositionElements();
      if (this.inviteCodeList != null) {
         this.inviteCodeList.updateSize(this.width, this.layout);
      }

   }

   protected boolean shouldRenderListBackgroundAndSeparators() {
      return this.inviteCodeList == null;
   }

   public Component getNarrationMessage() {
      return (Component)(this.fetchedInviteCodes ? super.getNarrationMessage() : CommonComponents.joinForNarration(super.getNarrationMessage(), LOADING_TEXT));
   }

   public void onClose() {
      if (this.backButton == null || this.backButton.active) {
         this.minecraft.gui.setScreen(this.configureScreen);
      }

   }

   private void onCopy() {
      if (this.inviteCodeList != null) {
         InviteCode selected = this.inviteCodeList.getSelectedCode();
         if (selected != null) {
            this.minecraft.keyboardHandler.setClipboard(selected.code());
         }

      }
   }

   private void onDelete() {
      if (this.inviteCodeList != null) {
         InviteCode selected = this.inviteCodeList.getSelectedCode();
         if (selected != null) {
            this.setControlsActive(false);
            if (this.backButton != null) {
               this.backButton.active = false;
            }

            RealmsUtil.runAsync((client) -> client.deleteInviteCode(selected.code()), (exception) -> this.handleFailure(exception, "Couldn't delete invite code", this)).thenRunAsync(() -> {
               if (this.backButton != null) {
                  this.backButton.active = true;
               }

               this.refreshInviteCodes();
            }, this.screenExecutor);
         }
      }
   }

   private void onCreate() {
      if (this.fetchedInviteCodes && this.inviteCodes.size() < 5) {
         this.setControlsActive(false);
         if (this.backButton != null) {
            this.backButton.active = false;
         }

         RealmsUtil.runAsync((client) -> client.createInviteCode(this.realmId, true, (Instant)null), (exception) -> this.handleFailure(exception, "Couldn't create invite code", this)).thenRunAsync(() -> {
            if (this.backButton != null) {
               this.backButton.active = true;
            }

            this.refreshInviteCodes();
         }, this.screenExecutor);
      }
   }

   private void onEdit() {
      if (this.inviteCodeList != null) {
         InviteCode selected = this.inviteCodeList.getSelectedCode();
         if (selected != null) {
            this.minecraft.gui.setScreen(new RealmsEditInviteCodeScreen(this, this.realmId, selected));
         }

      }
   }

   void refreshInviteCodes() {
      if (this.inviteCodeList != null) {
         this.inviteCodeList.setSelected((InviteCodeSelectionList.Entry)null);
      }

      this.fetchInviteCodes();
   }

   private class InviteCodeSelectionList extends ObjectSelectionList<Entry> {
      public InviteCodeSelectionList(final List<InviteCode> inviteCodes) {
         Objects.requireNonNull(RealmsInviteCodesScreen.this);
         super(Minecraft.getInstance(), RealmsInviteCodesScreen.this.width, RealmsInviteCodesScreen.this.layout.getContentHeight(), RealmsInviteCodesScreen.this.layout.getHeaderHeight(), 28);
         inviteCodes.forEach((inviteCode) -> this.addEntry(new Entry(inviteCode)));
      }

      public @Nullable InviteCode getSelectedCode() {
         Entry selected = (Entry)this.getSelected();
         return selected == null ? null : selected.inviteCode;
      }

      public void setSelected(final Entry entry) {
         super.setSelected(entry);
         RealmsInviteCodesScreen.this.updateButtonStates();
      }

      public int getRowWidth() {
         return 308;
      }

      private class Entry extends ObjectSelectionList.Entry<Entry> {
         private final InviteCode inviteCode;

         public Entry(final InviteCode inviteCode) {
            Objects.requireNonNull(InviteCodeSelectionList.this);
            super();
            this.inviteCode = inviteCode;
         }

         public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
            InviteCodeSelectionList.this.setSelected(this);
            return true;
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            int var10000 = this.getContentYMiddle();
            Objects.requireNonNull(RealmsInviteCodesScreen.this.font);
            int textY = var10000 - 9 / 2;
            graphics.text(RealmsInviteCodesScreen.this.font, (String)this.inviteCode.code(), this.getContentX() + 8, textY, -1);
         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.inviteCode.code());
         }
      }
   }
}
