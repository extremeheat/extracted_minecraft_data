package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

class RealmsPlayersTab extends GridLayoutTab implements RealmsConfigurationTab {
   static final Logger LOGGER = LogUtils.getLogger();
   static final Component TITLE = Component.translatable("mco.configure.world.players.title");
   static final Component QUESTION_TITLE = Component.translatable("mco.question");
   private static final int PADDING = 8;
   final RealmsConfigureWorldScreen configurationScreen;
   final Minecraft minecraft;
   final Font font;
   RealmsServer serverData;
   final InvitedObjectSelectionList invitedList;

   RealmsPlayersTab(RealmsConfigureWorldScreen var1, Minecraft var2, RealmsServer var3) {
      super(TITLE);
      this.configurationScreen = var1;
      this.minecraft = var2;
      this.font = var1.getFont();
      this.serverData = var3;
      GridLayout.RowHelper var4 = this.layout.spacing(8).createRowHelper(1);
      this.invitedList = (InvitedObjectSelectionList)var4.addChild(new InvitedObjectSelectionList(var1.width, this.calculateListHeight()), LayoutSettings.defaults().alignVerticallyTop().alignHorizontallyCenter());
      var4.addChild(Button.builder(Component.translatable("mco.configure.world.buttons.invite"), (var3x) -> var2.setScreen(new RealmsInviteScreen(var1, var3))).build(), LayoutSettings.defaults().alignVerticallyBottom().alignHorizontallyCenter());
      this.updateData(var3);
   }

   public int calculateListHeight() {
      return this.configurationScreen.getContentHeight() - 20 - 16;
   }

   public void doLayout(ScreenRectangle var1) {
      this.invitedList.updateSizeAndPosition(this.configurationScreen.width, this.calculateListHeight(), this.configurationScreen.layout.getHeaderHeight());
      super.doLayout(var1);
   }

   public void updateData(RealmsServer var1) {
      this.serverData = var1;
      this.invitedList.updateList(var1);
   }

   class InvitedObjectSelectionList extends ContainerObjectSelectionList<Entry> {
      private static final int PLAYER_ENTRY_HEIGHT = 36;

      public InvitedObjectSelectionList(final int var2, final int var3) {
         super(Minecraft.getInstance(), var2, var3, RealmsPlayersTab.this.configurationScreen.getHeaderHeight(), 36);
      }

      void updateList(RealmsServer var1) {
         this.clearEntries();
         this.populateList(var1);
      }

      private void populateList(RealmsServer var1) {
         HeaderEntry var2 = RealmsPlayersTab.this.new HeaderEntry();
         Objects.requireNonNull(RealmsPlayersTab.this.font);
         this.addEntry(var2, var2.height(9));

         for(PlayerEntry var4 : var1.players.stream().map((var1x) -> RealmsPlayersTab.this.new PlayerEntry(var1x)).toList()) {
            this.addEntry(var4);
         }

      }

      protected void renderListBackground(GuiGraphics var1) {
      }

      protected void renderListSeparators(GuiGraphics var1) {
      }

      public int getRowWidth() {
         return 300;
      }
   }

   abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
      Entry() {
         super();
      }
   }

   class PlayerEntry extends Entry {
      protected static final int SKIN_FACE_SIZE = 32;
      private static final Component NORMAL_USER_TEXT = Component.translatable("mco.configure.world.invites.normal.tooltip");
      private static final Component OP_TEXT = Component.translatable("mco.configure.world.invites.ops.tooltip");
      private static final Component REMOVE_TEXT = Component.translatable("mco.configure.world.invites.remove.tooltip");
      private static final Identifier MAKE_OP_SPRITE = Identifier.withDefaultNamespace("player_list/make_operator");
      private static final Identifier REMOVE_OP_SPRITE = Identifier.withDefaultNamespace("player_list/remove_operator");
      private static final Identifier REMOVE_PLAYER_SPRITE = Identifier.withDefaultNamespace("player_list/remove_player");
      private static final int ICON_WIDTH = 8;
      private static final int ICON_HEIGHT = 7;
      private final PlayerInfo playerInfo;
      private final Button removeButton;
      private final Button makeOpButton;
      private final Button removeOpButton;

      public PlayerEntry(final PlayerInfo var2) {
         super();
         this.playerInfo = var2;
         int var3 = RealmsPlayersTab.this.serverData.players.indexOf(this.playerInfo);
         this.makeOpButton = SpriteIconButton.builder(NORMAL_USER_TEXT, (var2x) -> this.op(var3), false).sprite((Identifier)MAKE_OP_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width((FormattedText)NORMAL_USER_TEXT)).narration((var1x) -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.name), (Component)var1x.get(), Component.translatable("narration.cycle_button.usage.focused", OP_TEXT))).build();
         this.removeOpButton = SpriteIconButton.builder(OP_TEXT, (var2x) -> this.deop(var3), false).sprite((Identifier)REMOVE_OP_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width((FormattedText)OP_TEXT)).narration((var1x) -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.name), (Component)var1x.get(), Component.translatable("narration.cycle_button.usage.focused", NORMAL_USER_TEXT))).build();
         this.removeButton = SpriteIconButton.builder(REMOVE_TEXT, (var2x) -> this.uninvite(var3), false).sprite((Identifier)REMOVE_PLAYER_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width((FormattedText)REMOVE_TEXT)).narration((var1x) -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.name), (Component)var1x.get())).build();
         this.updateOpButtons();
      }

      private void op(int var1) {
         UUID var2 = ((PlayerInfo)RealmsPlayersTab.this.serverData.players.get(var1)).uuid;
         RealmsUtil.supplyAsync((var2x) -> var2x.op(RealmsPlayersTab.this.serverData.id, var2), (var0) -> RealmsPlayersTab.LOGGER.error("Couldn't op the user", var0)).thenAcceptAsync((var1x) -> {
            this.updateOps(var1x);
            this.updateOpButtons();
            this.setFocused(this.removeOpButton);
         }, RealmsPlayersTab.this.minecraft);
      }

      private void deop(int var1) {
         UUID var2 = ((PlayerInfo)RealmsPlayersTab.this.serverData.players.get(var1)).uuid;
         RealmsUtil.supplyAsync((var2x) -> var2x.deop(RealmsPlayersTab.this.serverData.id, var2), (var0) -> RealmsPlayersTab.LOGGER.error("Couldn't deop the user", var0)).thenAcceptAsync((var1x) -> {
            this.updateOps(var1x);
            this.updateOpButtons();
            this.setFocused(this.makeOpButton);
         }, RealmsPlayersTab.this.minecraft);
      }

      private void uninvite(int var1) {
         if (var1 >= 0 && var1 < RealmsPlayersTab.this.serverData.players.size()) {
            PlayerInfo var2 = (PlayerInfo)RealmsPlayersTab.this.serverData.players.get(var1);
            RealmsConfirmScreen var3 = new RealmsConfirmScreen((var3x) -> {
               if (var3x) {
                  RealmsUtil.runAsync((var2x) -> var2x.uninvite(RealmsPlayersTab.this.serverData.id, var2.uuid), (var0) -> RealmsPlayersTab.LOGGER.error("Couldn't uninvite user", var0));
                  RealmsPlayersTab.this.serverData.players.remove(var1);
                  RealmsPlayersTab.this.updateData(RealmsPlayersTab.this.serverData);
               }

               RealmsPlayersTab.this.minecraft.setScreen(RealmsPlayersTab.this.configurationScreen);
            }, RealmsPlayersTab.QUESTION_TITLE, Component.translatable("mco.configure.world.uninvite.player", var2.name));
            RealmsPlayersTab.this.minecraft.setScreen(var3);
         }

      }

      private void updateOps(Ops var1) {
         for(PlayerInfo var3 : RealmsPlayersTab.this.serverData.players) {
            var3.operator = var1.ops().contains(var3.name);
         }

      }

      private void updateOpButtons() {
         this.makeOpButton.visible = !this.playerInfo.operator;
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

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         int var6;
         if (!this.playerInfo.accepted) {
            var6 = -6250336;
         } else if (this.playerInfo.online) {
            var6 = -16711936;
         } else {
            var6 = -1;
         }

         int var7 = this.getContentYMiddle() - 16;
         RealmsUtil.renderPlayerFace(var1, this.getContentX(), var7, 32, this.playerInfo.uuid);
         int var10000 = this.getContentYMiddle();
         Objects.requireNonNull(RealmsPlayersTab.this.font);
         int var8 = var10000 - 9 / 2;
         var1.drawString(RealmsPlayersTab.this.font, this.playerInfo.name, this.getContentX() + 8 + 32, var8, var6);
         int var9 = this.getContentYMiddle() - 10;
         int var10 = this.getContentRight() - this.removeButton.getWidth();
         this.removeButton.setPosition(var10, var9);
         this.removeButton.render(var1, var2, var3, var5);
         int var11 = var10 - this.activeOpButton().getWidth() - 8;
         this.makeOpButton.setPosition(var11, var9);
         this.makeOpButton.render(var1, var2, var3, var5);
         this.removeOpButton.setPosition(var11, var9);
         this.removeOpButton.render(var1, var2, var3, var5);
      }
   }

   class HeaderEntry extends Entry {
      private String cachedNumberOfInvites = "";
      private final FocusableTextWidget invitedWidget;

      public HeaderEntry() {
         super();
         MutableComponent var2 = Component.translatable("mco.configure.world.invited.number", "").withStyle(ChatFormatting.UNDERLINE);
         this.invitedWidget = FocusableTextWidget.builder(var2, RealmsPlayersTab.this.font).alwaysShowBorder(false).backgroundFill(FocusableTextWidget.BackgroundFill.ON_FOCUS).build();
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         String var6 = RealmsPlayersTab.this.serverData.players != null ? Integer.toString(RealmsPlayersTab.this.serverData.players.size()) : "0";
         if (!var6.equals(this.cachedNumberOfInvites)) {
            this.cachedNumberOfInvites = var6;
            MutableComponent var7 = Component.translatable("mco.configure.world.invited.number", var6).withStyle(ChatFormatting.UNDERLINE);
            this.invitedWidget.setMessage(var7);
         }

         this.invitedWidget.setPosition(RealmsPlayersTab.this.invitedList.getRowLeft() + RealmsPlayersTab.this.invitedList.getRowWidth() / 2 - this.invitedWidget.getWidth() / 2, this.getY() + this.getHeight() / 2 - this.invitedWidget.getHeight() / 2);
         this.invitedWidget.render(var1, var2, var3, var5);
      }

      int height(int var1) {
         return var1 + this.invitedWidget.getPadding() * 2;
      }

      public List<? extends NarratableEntry> narratables() {
         return List.of(this.invitedWidget);
      }

      public List<? extends GuiEventListener> children() {
         return List.of(this.invitedWidget);
      }
   }
}
