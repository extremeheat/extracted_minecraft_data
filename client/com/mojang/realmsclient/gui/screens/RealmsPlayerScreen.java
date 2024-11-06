package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsPlayerScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.configure.world.players.title");
   static final Component QUESTION_TITLE = Component.translatable("mco.question");
   private static final int PADDING = 8;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final RealmsConfigureWorldScreen lastScreen;
   final RealmsServer serverData;
   @Nullable
   private InvitedObjectSelectionList invitedList;
   boolean stateChanged;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen var1, RealmsServer var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.serverData = var2;
   }

   public void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.invitedList = (InvitedObjectSelectionList)this.layout.addToContents(new InvitedObjectSelectionList());
      this.repopulateInvitedList();
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      var1.addChild(Button.builder(Component.translatable("mco.configure.world.buttons.invite"), (var1x) -> {
         this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData));
      }).build());
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.invitedList != null) {
         this.invitedList.updateSize(this.width, this.layout);
      }

   }

   void repopulateInvitedList() {
      if (this.invitedList != null) {
         this.invitedList.children().clear();
         Iterator var1 = this.serverData.players.iterator();

         while(var1.hasNext()) {
            PlayerInfo var2 = (PlayerInfo)var1.next();
            this.invitedList.children().add(new Entry(var2));
         }

      }
   }

   public void onClose() {
      this.backButtonClicked();
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.minecraft.setScreen(this.lastScreen.getNewScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   class InvitedObjectSelectionList extends ContainerObjectSelectionList<Entry> {
      private static final int ITEM_HEIGHT = 36;

      public InvitedObjectSelectionList() {
         Minecraft var10001 = Minecraft.getInstance();
         int var10002 = RealmsPlayerScreen.this.width;
         int var10003 = RealmsPlayerScreen.this.layout.getContentHeight();
         int var10004 = RealmsPlayerScreen.this.layout.getHeaderHeight();
         Objects.requireNonNull(RealmsPlayerScreen.this.font);
         super(var10001, var10002, var10003, var10004, 36, (int)(9.0F * 1.5F));
      }

      protected void renderHeader(GuiGraphics var1, int var2, int var3) {
         String var4 = RealmsPlayerScreen.this.serverData.players != null ? Integer.toString(RealmsPlayerScreen.this.serverData.players.size()) : "0";
         MutableComponent var5 = Component.translatable("mco.configure.world.invited.number", var4).withStyle(ChatFormatting.UNDERLINE);
         var1.drawString(RealmsPlayerScreen.this.font, (Component)var5, var2 + this.getRowWidth() / 2 - RealmsPlayerScreen.this.font.width((FormattedText)var5) / 2, var3, -1);
      }

      public int getRowWidth() {
         return 300;
      }
   }

   private class Entry extends ContainerObjectSelectionList.Entry<Entry> {
      private static final Component NORMAL_USER_TEXT = Component.translatable("mco.configure.world.invites.normal.tooltip");
      private static final Component OP_TEXT = Component.translatable("mco.configure.world.invites.ops.tooltip");
      private static final Component REMOVE_TEXT = Component.translatable("mco.configure.world.invites.remove.tooltip");
      private static final ResourceLocation MAKE_OP_SPRITE = ResourceLocation.withDefaultNamespace("player_list/make_operator");
      private static final ResourceLocation REMOVE_OP_SPRITE = ResourceLocation.withDefaultNamespace("player_list/remove_operator");
      private static final ResourceLocation REMOVE_PLAYER_SPRITE = ResourceLocation.withDefaultNamespace("player_list/remove_player");
      private static final int ICON_WIDTH = 8;
      private static final int ICON_HEIGHT = 7;
      private final PlayerInfo playerInfo;
      private final Button removeButton;
      private final Button makeOpButton;
      private final Button removeOpButton;

      public Entry(final PlayerInfo var2) {
         super();
         this.playerInfo = var2;
         int var3 = RealmsPlayerScreen.this.serverData.players.indexOf(this.playerInfo);
         this.makeOpButton = SpriteIconButton.builder(NORMAL_USER_TEXT, (var2x) -> {
            this.op(var3);
         }, false).sprite(MAKE_OP_SPRITE, 8, 7).width(16 + RealmsPlayerScreen.this.font.width((FormattedText)NORMAL_USER_TEXT)).narration((var1x) -> {
            return CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.getName()), (Component)var1x.get(), Component.translatable("narration.cycle_button.usage.focused", OP_TEXT));
         }).build();
         this.removeOpButton = SpriteIconButton.builder(OP_TEXT, (var2x) -> {
            this.deop(var3);
         }, false).sprite(REMOVE_OP_SPRITE, 8, 7).width(16 + RealmsPlayerScreen.this.font.width((FormattedText)OP_TEXT)).narration((var1x) -> {
            return CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.getName()), (Component)var1x.get(), Component.translatable("narration.cycle_button.usage.focused", NORMAL_USER_TEXT));
         }).build();
         this.removeButton = SpriteIconButton.builder(REMOVE_TEXT, (var2x) -> {
            this.uninvite(var3);
         }, false).sprite(REMOVE_PLAYER_SPRITE, 8, 7).width(16 + RealmsPlayerScreen.this.font.width((FormattedText)REMOVE_TEXT)).narration((var1x) -> {
            return CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.getName()), (Component)var1x.get());
         }).build();
         this.updateOpButtons();
      }

      private void op(int var1) {
         RealmsClient var2 = RealmsClient.create();
         UUID var3 = ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(var1)).getUuid();

         try {
            this.updateOps(var2.op(RealmsPlayerScreen.this.serverData.id, var3));
         } catch (RealmsServiceException var5) {
            RealmsPlayerScreen.LOGGER.error("Couldn't op the user", var5);
         }

         this.updateOpButtons();
      }

      private void deop(int var1) {
         RealmsClient var2 = RealmsClient.create();
         UUID var3 = ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(var1)).getUuid();

         try {
            this.updateOps(var2.deop(RealmsPlayerScreen.this.serverData.id, var3));
         } catch (RealmsServiceException var5) {
            RealmsPlayerScreen.LOGGER.error("Couldn't deop the user", var5);
         }

         this.updateOpButtons();
      }

      private void uninvite(int var1) {
         if (var1 >= 0 && var1 < RealmsPlayerScreen.this.serverData.players.size()) {
            PlayerInfo var2 = (PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(var1);
            RealmsConfirmScreen var3 = new RealmsConfirmScreen((var3x) -> {
               if (var3x) {
                  RealmsClient var4 = RealmsClient.create();

                  try {
                     var4.uninvite(RealmsPlayerScreen.this.serverData.id, var2.getUuid());
                  } catch (RealmsServiceException var6) {
                     RealmsPlayerScreen.LOGGER.error("Couldn't uninvite user", var6);
                  }

                  RealmsPlayerScreen.this.serverData.players.remove(var1);
                  RealmsPlayerScreen.this.repopulateInvitedList();
               }

               RealmsPlayerScreen.this.stateChanged = true;
               RealmsPlayerScreen.this.minecraft.setScreen(RealmsPlayerScreen.this);
            }, RealmsPlayerScreen.QUESTION_TITLE, Component.translatable("mco.configure.world.uninvite.player", var2.getName()));
            RealmsPlayerScreen.this.minecraft.setScreen(var3);
         }

      }

      private void updateOps(Ops var1) {
         Iterator var2 = RealmsPlayerScreen.this.serverData.players.iterator();

         while(var2.hasNext()) {
            PlayerInfo var3 = (PlayerInfo)var2.next();
            var3.setOperator(var1.ops.contains(var3.getName()));
         }

      }

      private void updateOpButtons() {
         this.makeOpButton.visible = !this.playerInfo.isOperator();
         this.removeOpButton.visible = !this.makeOpButton.visible;
      }

      private Button activeOpButton() {
         return this.makeOpButton.visible ? this.makeOpButton : this.removeOpButton;
      }

      public List<? extends GuiEventListener> children() {
         return ImmutableList.of(this.activeOpButton(), this.removeButton);
      }

      public List<? extends NarratableEntry> narratables() {
         return ImmutableList.of(this.activeOpButton(), this.removeButton);
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11;
         if (!this.playerInfo.getAccepted()) {
            var11 = -6250336;
         } else if (this.playerInfo.getOnline()) {
            var11 = 8388479;
         } else {
            var11 = -1;
         }

         int var12 = var3 + var6 / 2 - 16;
         RealmsUtil.renderPlayerFace(var1, var4, var12, 32, this.playerInfo.getUuid());
         int var10000 = var3 + var6 / 2;
         Objects.requireNonNull(RealmsPlayerScreen.this.font);
         int var13 = var10000 - 9 / 2;
         var1.drawString(RealmsPlayerScreen.this.font, this.playerInfo.getName(), var4 + 8 + 32, var13, var11);
         int var14 = var3 + var6 / 2 - 10;
         int var15 = var4 + var5 - this.removeButton.getWidth();
         this.removeButton.setPosition(var15, var14);
         this.removeButton.render(var1, var7, var8, var10);
         int var16 = var15 - this.activeOpButton().getWidth() - 8;
         this.makeOpButton.setPosition(var16, var14);
         this.makeOpButton.render(var1, var7, var8, var10);
         this.removeOpButton.setPosition(var16, var14);
         this.removeOpButton.render(var1, var7, var8, var10);
      }
   }
}
