package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.RowButton;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsPendingInvitesScreen extends RealmsScreen {
   static final ResourceLocation ACCEPT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/accept_highlighted");
   static final ResourceLocation ACCEPT_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/accept");
   static final ResourceLocation REJECT_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/reject_highlighted");
   static final ResourceLocation REJECT_SPRITE = ResourceLocation.withDefaultNamespace("pending_invite/reject");
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component NO_PENDING_INVITES_TEXT = Component.translatable("mco.invites.nopending");
   static final Component ACCEPT_INVITE = Component.translatable("mco.invites.button.accept");
   static final Component REJECT_INVITE = Component.translatable("mco.invites.button.reject");
   private final Screen lastScreen;
   private final CompletableFuture<List<PendingInvite>> pendingInvites = CompletableFuture.supplyAsync(() -> {
      try {
         return RealmsClient.create().pendingInvites().pendingInvites;
      } catch (RealmsServiceException var1) {
         LOGGER.error("Couldn't list invites", var1);
         return List.of();
      }
   }, Util.ioPool());
   @Nullable
   Component toolTip;
   PendingInvitationSelectionList pendingInvitationSelectionList;
   private Button acceptButton;
   private Button rejectButton;

   public RealmsPendingInvitesScreen(Screen var1, Component var2) {
      super(var2);
      this.lastScreen = var1;
   }

   public void init() {
      RealmsMainScreen.refreshPendingInvites();
      this.pendingInvitationSelectionList = new PendingInvitationSelectionList();
      this.pendingInvites.thenAcceptAsync((var1) -> {
         List var2 = var1.stream().map((var1x) -> {
            return new Entry(var1x);
         }).toList();
         this.pendingInvitationSelectionList.replaceEntries(var2);
         if (var2.isEmpty()) {
            this.minecraft.getNarrator().say(NO_PENDING_INVITES_TEXT);
         }

      }, this.screenExecutor);
      this.addRenderableWidget(this.pendingInvitationSelectionList);
      this.acceptButton = (Button)this.addRenderableWidget(Button.builder(ACCEPT_INVITE, (var1) -> {
         this.handleInvitation(true);
      }).bounds(this.width / 2 - 174, this.height - 32, 100, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1) -> {
         this.onClose();
      }).bounds(this.width / 2 - 50, this.height - 32, 100, 20).build());
      this.rejectButton = (Button)this.addRenderableWidget(Button.builder(REJECT_INVITE, (var1) -> {
         this.handleInvitation(false);
      }).bounds(this.width / 2 + 74, this.height - 32, 100, 20).build());
      this.updateButtonStates();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   void handleInvitation(boolean var1) {
      AbstractSelectionList.Entry var3 = this.pendingInvitationSelectionList.getSelected();
      if (var3 instanceof Entry var2) {
         String var4 = var2.pendingInvite.invitationId;
         CompletableFuture.supplyAsync(() -> {
            try {
               RealmsClient var2 = RealmsClient.create();
               if (var1) {
                  var2.acceptInvitation(var4);
               } else {
                  var2.rejectInvitation(var4);
               }

               return true;
            } catch (RealmsServiceException var3) {
               LOGGER.error("Couldn't handle invite", var3);
               return false;
            }
         }, Util.ioPool()).thenAcceptAsync((var3x) -> {
            if (var3x) {
               this.pendingInvitationSelectionList.removeInvitation(var2);
               this.updateButtonStates();
               RealmsDataFetcher var4 = this.minecraft.realmsDataFetcher();
               if (var1) {
                  var4.serverListUpdateTask.reset();
               }

               var4.pendingInvitesTask.reset();
            }

         }, this.screenExecutor);
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.toolTip = null;
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 12, -1);
      if (this.toolTip != null) {
         var1.renderTooltip(this.font, this.toolTip, var2, var3);
      }

      if (this.pendingInvites.isDone() && this.pendingInvitationSelectionList.hasPendingInvites()) {
         var1.drawCenteredString(this.font, (Component)NO_PENDING_INVITES_TEXT, this.width / 2, this.height / 2 - 20, -1);
      }

   }

   void updateButtonStates() {
      Entry var1 = (Entry)this.pendingInvitationSelectionList.getSelected();
      this.acceptButton.visible = var1 != null;
      this.rejectButton.visible = var1 != null;
   }

   class PendingInvitationSelectionList extends ObjectSelectionList<Entry> {
      public PendingInvitationSelectionList() {
         super(Minecraft.getInstance(), RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.height - 72, 32, 36);
      }

      public int getRowWidth() {
         return 260;
      }

      public void setSelectedIndex(int var1) {
         super.setSelectedIndex(var1);
         RealmsPendingInvitesScreen.this.updateButtonStates();
      }

      public boolean hasPendingInvites() {
         return this.getItemCount() == 0;
      }

      public void removeInvitation(Entry var1) {
         this.removeEntry(var1);
      }
   }

   private class Entry extends ObjectSelectionList.Entry<Entry> {
      private static final int TEXT_LEFT = 38;
      final PendingInvite pendingInvite;
      private final List<RowButton> rowButtons;

      Entry(final PendingInvite var2) {
         super();
         this.pendingInvite = var2;
         this.rowButtons = Arrays.asList(new AcceptRowButton(), new RejectRowButton());
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderPendingInvitationItem(var1, this.pendingInvite, var4, var3, var7, var8);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         RowButton.rowButtonMouseClicked(RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, this, this.rowButtons, var5, var1, var3);
         return super.mouseClicked(var1, var3, var5);
      }

      private void renderPendingInvitationItem(GuiGraphics var1, PendingInvite var2, int var3, int var4, int var5, int var6) {
         var1.drawString(RealmsPendingInvitesScreen.this.font, (String)var2.realmName, var3 + 38, var4 + 1, -1, false);
         var1.drawString(RealmsPendingInvitesScreen.this.font, var2.realmOwnerName, var3 + 38, var4 + 12, 7105644, false);
         var1.drawString(RealmsPendingInvitesScreen.this.font, RealmsUtil.convertToAgePresentationFromInstant(var2.date), var3 + 38, var4 + 24, 7105644, false);
         RowButton.drawButtonsInRow(var1, this.rowButtons, RealmsPendingInvitesScreen.this.pendingInvitationSelectionList, var3, var4, var5, var6);
         RealmsUtil.renderPlayerFace(var1, var3, var4, 32, var2.realmOwnerUuid);
      }

      public Component getNarration() {
         Component var1 = CommonComponents.joinLines(Component.literal(this.pendingInvite.realmName), Component.literal(this.pendingInvite.realmOwnerName), RealmsUtil.convertToAgePresentationFromInstant(this.pendingInvite.date));
         return Component.translatable("narrator.select", var1);
      }

      class AcceptRowButton extends RowButton {
         AcceptRowButton() {
            super(15, 15, 215, 5);
         }

         protected void draw(GuiGraphics var1, int var2, int var3, boolean var4) {
            var1.blitSprite(RenderType::guiTextured, (ResourceLocation)(var4 ? RealmsPendingInvitesScreen.ACCEPT_HIGHLIGHTED_SPRITE : RealmsPendingInvitesScreen.ACCEPT_SPRITE), var2, var3, 18, 18);
            if (var4) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsPendingInvitesScreen.ACCEPT_INVITE;
            }

         }

         public void onClick(int var1) {
            RealmsPendingInvitesScreen.this.handleInvitation(true);
         }
      }

      class RejectRowButton extends RowButton {
         RejectRowButton() {
            super(15, 15, 235, 5);
         }

         protected void draw(GuiGraphics var1, int var2, int var3, boolean var4) {
            var1.blitSprite(RenderType::guiTextured, (ResourceLocation)(var4 ? RealmsPendingInvitesScreen.REJECT_HIGHLIGHTED_SPRITE : RealmsPendingInvitesScreen.REJECT_SPRITE), var2, var3, 18, 18);
            if (var4) {
               RealmsPendingInvitesScreen.this.toolTip = RealmsPendingInvitesScreen.REJECT_INVITE;
            }

         }

         public void onClick(int var1) {
            RealmsPendingInvitesScreen.this.handleInvitation(false);
         }
      }
   }
}
