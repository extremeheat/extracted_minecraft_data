package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsPendingInvitesScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component NO_PENDING_INVITES_TEXT = Component.translatable("mco.invites.nopending");
   private final Screen lastScreen;
   private final CompletableFuture<List<PendingInvite>> pendingInvites = CompletableFuture.supplyAsync(() -> {
      try {
         return RealmsClient.getOrCreate().pendingInvites().pendingInvites();
      } catch (RealmsServiceException var1) {
         LOGGER.error("Couldn't list invites", var1);
         return List.of();
      }
   }, Util.ioPool());
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   @Nullable
   PendingInvitationSelectionList pendingInvitationSelectionList;

   public RealmsPendingInvitesScreen(Screen var1, Component var2) {
      super(var2);
      this.lastScreen = var1;
   }

   public void init() {
      RealmsMainScreen.refreshPendingInvites();
      this.layout.addTitleHeader(this.title, this.font);
      this.pendingInvitationSelectionList = (PendingInvitationSelectionList)this.layout.addToContents(new PendingInvitationSelectionList(this.minecraft));
      this.pendingInvites.thenAcceptAsync((var1) -> {
         List var2 = var1.stream().map((var1x) -> new Entry(var1x)).toList();
         this.pendingInvitationSelectionList.replaceEntries(var2);
         if (var2.isEmpty()) {
            this.minecraft.getNarrator().saySystemQueued(NO_PENDING_INVITES_TEXT);
         }

      }, this.screenExecutor);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.pendingInvitationSelectionList != null) {
         this.pendingInvitationSelectionList.updateSize(this.width, this.layout);
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.pendingInvites.isDone() && this.pendingInvitationSelectionList.hasPendingInvites()) {
         var1.drawCenteredString(this.font, (Component)NO_PENDING_INVITES_TEXT, this.width / 2, this.height / 2 - 20, -1);
      }

   }

   class PendingInvitationSelectionList extends ContainerObjectSelectionList<Entry> {
      public static final int ITEM_HEIGHT = 36;

      public PendingInvitationSelectionList(final Minecraft var2) {
         super(var2, RealmsPendingInvitesScreen.this.width, RealmsPendingInvitesScreen.this.layout.getContentHeight(), RealmsPendingInvitesScreen.this.layout.getHeaderHeight(), 36);
      }

      public int getRowWidth() {
         return 280;
      }

      public boolean hasPendingInvites() {
         return this.getItemCount() == 0;
      }

      public void removeInvitation(Entry var1) {
         this.removeEntry(var1);
      }
   }

   class Entry extends ContainerObjectSelectionList.Entry<Entry> {
      private static final Component ACCEPT_INVITE = Component.translatable("mco.invites.button.accept");
      private static final Component REJECT_INVITE = Component.translatable("mco.invites.button.reject");
      private static final WidgetSprites ACCEPT_SPRITE = new WidgetSprites(ResourceLocation.withDefaultNamespace("pending_invite/accept"), ResourceLocation.withDefaultNamespace("pending_invite/accept_highlighted"));
      private static final WidgetSprites REJECT_SPRITE = new WidgetSprites(ResourceLocation.withDefaultNamespace("pending_invite/reject"), ResourceLocation.withDefaultNamespace("pending_invite/reject_highlighted"));
      private static final int SPRITE_TEXTURE_SIZE = 18;
      private static final int SPRITE_SIZE = 21;
      private static final int TEXT_LEFT = 38;
      private final PendingInvite pendingInvite;
      private final List<AbstractWidget> children = new ArrayList();
      private final SpriteIconButton acceptButton;
      private final SpriteIconButton rejectButton;
      private final StringWidget realmName;
      private final StringWidget realmOwnerName;
      private final StringWidget inviteDate;

      Entry(final PendingInvite var2) {
         super();
         this.pendingInvite = var2;
         int var3 = RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.getRowWidth() - 32 - 32 - 42;
         this.realmName = (new StringWidget(Component.literal(var2.realmName()), RealmsPendingInvitesScreen.this.font)).setMaxWidth(var3);
         this.realmOwnerName = (new StringWidget(Component.literal(var2.realmOwnerName()).withColor(-6250336), RealmsPendingInvitesScreen.this.font)).setMaxWidth(var3);
         this.inviteDate = (new StringWidget(ComponentUtils.mergeStyles(RealmsUtil.convertToAgePresentationFromInstant(var2.date()), Style.EMPTY.withColor(-6250336)), RealmsPendingInvitesScreen.this.font)).setMaxWidth(var3);
         Button.CreateNarration var4 = this.getCreateNarration(var2);
         this.acceptButton = SpriteIconButton.builder(ACCEPT_INVITE, (var1x) -> this.handleInvitation(true), false).sprite((WidgetSprites)ACCEPT_SPRITE, 18, 18).size(21, 21).narration(var4).withTootip().build();
         this.rejectButton = SpriteIconButton.builder(REJECT_INVITE, (var1x) -> this.handleInvitation(false), false).sprite((WidgetSprites)REJECT_SPRITE, 18, 18).size(21, 21).narration(var4).withTootip().build();
         this.children.addAll(List.of(this.acceptButton, this.rejectButton));
      }

      private Button.CreateNarration getCreateNarration(PendingInvite var1) {
         return (var1x) -> {
            MutableComponent var2 = CommonComponents.joinForNarration((Component)var1x.get(), Component.literal(var1.realmName()), Component.literal(var1.realmOwnerName()), RealmsUtil.convertToAgePresentationFromInstant(var1.date()));
            return Component.translatable("narrator.select", var2);
         };
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         int var6 = this.getContentX();
         int var7 = this.getContentY();
         int var8 = var6 + 38;
         RealmsUtil.renderPlayerFace(var1, var6, var7, 32, this.pendingInvite.realmOwnerUuid());
         this.realmName.setPosition(var8, var7 + 1);
         this.realmName.renderWidget(var1, var2, var3, (float)var6);
         this.realmOwnerName.setPosition(var8, var7 + 12);
         this.realmOwnerName.renderWidget(var1, var2, var3, (float)var6);
         this.inviteDate.setPosition(var8, var7 + 24);
         this.inviteDate.renderWidget(var1, var2, var3, (float)var6);
         int var9 = var7 + this.getContentHeight() / 2 - 10;
         this.acceptButton.setPosition(var6 + this.getContentWidth() - 16 - 42, var9);
         this.acceptButton.render(var1, var2, var3, var5);
         this.rejectButton.setPosition(var6 + this.getContentWidth() - 8 - 21, var9);
         this.rejectButton.render(var1, var2, var3, var5);
      }

      private void handleInvitation(boolean var1) {
         String var2 = this.pendingInvite.invitationId();
         CompletableFuture.supplyAsync(() -> {
            try {
               RealmsClient var2x = RealmsClient.getOrCreate();
               if (var1) {
                  var2x.acceptInvitation(var2);
               } else {
                  var2x.rejectInvitation(var2);
               }

               return true;
            } catch (RealmsServiceException var3) {
               RealmsPendingInvitesScreen.LOGGER.error("Couldn't handle invite", var3);
               return false;
            }
         }, Util.ioPool()).thenAcceptAsync((var2x) -> {
            if (var2x) {
               RealmsPendingInvitesScreen.this.pendingInvitationSelectionList.removeInvitation(this);
               RealmsDataFetcher var3 = RealmsPendingInvitesScreen.this.minecraft.realmsDataFetcher();
               if (var1) {
                  var3.serverListUpdateTask.reset();
               }

               var3.pendingInvitesTask.reset();
            }

         }, RealmsPendingInvitesScreen.this.screenExecutor);
      }
   }
}
