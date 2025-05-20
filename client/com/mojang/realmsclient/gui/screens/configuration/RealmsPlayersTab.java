package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
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
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

class RealmsPlayersTab extends GridLayoutTab implements RealmsConfigurationTab {
   static final Logger LOGGER = LogUtils.getLogger();
   static final Component TITLE = Component.translatable("mco.configure.world.players.title");
   static final Component QUESTION_TITLE = Component.translatable("mco.question");
   private static final int PADDING = 8;
   final RealmsConfigureWorldScreen configurationScreen;
   final Minecraft minecraft;
   RealmsServer serverData;
   private final InvitedObjectSelectionList invitedList;

   RealmsPlayersTab(RealmsConfigureWorldScreen var1, Minecraft var2, RealmsServer var3) {
      super(TITLE);
      this.configurationScreen = var1;
      this.minecraft = var2;
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
      this.invitedList.setSize(this.configurationScreen.width, this.calculateListHeight());
      super.doLayout(var1);
   }

   public void updateData(RealmsServer var1) {
      this.serverData = var1;
      this.invitedList.children().clear();

      for(PlayerInfo var3 : var1.players) {
         this.invitedList.children().add(new Entry(var3));
      }

   }

   class InvitedObjectSelectionList extends ContainerObjectSelectionList<Entry> {
      private static final int ITEM_HEIGHT = 36;

      public InvitedObjectSelectionList(final int var2, final int var3) {
         Minecraft var10001 = Minecraft.getInstance();
         int var10004 = RealmsPlayersTab.this.configurationScreen.getHeaderHeight();
         Objects.requireNonNull(RealmsPlayersTab.this.configurationScreen.getFont());
         super(var10001, var2, var3, var10004, 36, (int)(9.0F * 1.5F));
      }

      protected void renderHeader(GuiGraphics var1, int var2, int var3) {
         String var4 = RealmsPlayersTab.this.serverData.players != null ? Integer.toString(RealmsPlayersTab.this.serverData.players.size()) : "0";
         MutableComponent var5 = Component.translatable("mco.configure.world.invited.number", var4).withStyle(ChatFormatting.UNDERLINE);
         var1.drawString(RealmsPlayersTab.this.configurationScreen.getFont(), (Component)var5, var2 + this.getRowWidth() / 2 - RealmsPlayersTab.this.configurationScreen.getFont().width((FormattedText)var5) / 2, var3, -1);
      }

      protected void renderListBackground(GuiGraphics var1) {
      }

      protected void renderListSeparators(GuiGraphics var1) {
      }

      public int getRowWidth() {
         return 300;
      }
   }

   class Entry extends ContainerObjectSelectionList.Entry<Entry> {
      protected static final int SKIN_FACE_SIZE = 32;
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
         int var3 = RealmsPlayersTab.this.serverData.players.indexOf(this.playerInfo);
         this.makeOpButton = SpriteIconButton.builder(NORMAL_USER_TEXT, (var2x) -> this.op(var3), false).sprite(MAKE_OP_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width((FormattedText)NORMAL_USER_TEXT)).narration((var1x) -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.getName()), (Component)var1x.get(), Component.translatable("narration.cycle_button.usage.focused", OP_TEXT))).build();
         this.removeOpButton = SpriteIconButton.builder(OP_TEXT, (var2x) -> this.deop(var3), false).sprite(REMOVE_OP_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width((FormattedText)OP_TEXT)).narration((var1x) -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.getName()), (Component)var1x.get(), Component.translatable("narration.cycle_button.usage.focused", NORMAL_USER_TEXT))).build();
         this.removeButton = SpriteIconButton.builder(REMOVE_TEXT, (var2x) -> this.uninvite(var3), false).sprite(REMOVE_PLAYER_SPRITE, 8, 7).width(16 + RealmsPlayersTab.this.configurationScreen.getFont().width((FormattedText)REMOVE_TEXT)).narration((var1x) -> CommonComponents.joinForNarration(Component.translatable("mco.invited.player.narration", var2.getName()), (Component)var1x.get())).build();
         this.updateOpButtons();
      }

      private void op(int var1) {
         RealmsClient var2 = RealmsClient.getOrCreate();
         UUID var3 = ((PlayerInfo)RealmsPlayersTab.this.serverData.players.get(var1)).getUuid();

         try {
            this.updateOps(var2.op(RealmsPlayersTab.this.serverData.id, var3));
         } catch (RealmsServiceException var5) {
            RealmsPlayersTab.LOGGER.error("Couldn't op the user", var5);
         }

         this.updateOpButtons();
         this.setFocused(this.removeOpButton);
      }

      private void deop(int var1) {
         RealmsClient var2 = RealmsClient.getOrCreate();
         UUID var3 = ((PlayerInfo)RealmsPlayersTab.this.serverData.players.get(var1)).getUuid();

         try {
            this.updateOps(var2.deop(RealmsPlayersTab.this.serverData.id, var3));
         } catch (RealmsServiceException var5) {
            RealmsPlayersTab.LOGGER.error("Couldn't deop the user", var5);
         }

         this.updateOpButtons();
         this.setFocused(this.makeOpButton);
      }

      private void uninvite(int var1) {
         if (var1 >= 0 && var1 < RealmsPlayersTab.this.serverData.players.size()) {
            PlayerInfo var2 = (PlayerInfo)RealmsPlayersTab.this.serverData.players.get(var1);
            RealmsConfirmScreen var3 = new RealmsConfirmScreen((var3x) -> {
               if (var3x) {
                  RealmsClient var4 = RealmsClient.getOrCreate();

                  try {
                     var4.uninvite(RealmsPlayersTab.this.serverData.id, var2.getUuid());
                  } catch (RealmsServiceException var6) {
                     RealmsPlayersTab.LOGGER.error("Couldn't uninvite user", var6);
                  }

                  RealmsPlayersTab.this.serverData.players.remove(var1);
                  RealmsPlayersTab.this.updateData(RealmsPlayersTab.this.serverData);
               }

               RealmsPlayersTab.this.minecraft.setScreen(RealmsPlayersTab.this.configurationScreen);
            }, RealmsPlayersTab.QUESTION_TITLE, Component.translatable("mco.configure.world.uninvite.player", var2.getName()));
            RealmsPlayersTab.this.minecraft.setScreen(var3);
         }

      }

      private void updateOps(Ops var1) {
         for(PlayerInfo var3 : RealmsPlayersTab.this.serverData.players) {
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
            var11 = -16711936;
         } else {
            var11 = -1;
         }

         int var12 = var3 + var6 / 2 - 16;
         RealmsUtil.renderPlayerFace(var1, var4, var12, 32, this.playerInfo.getUuid());
         int var10000 = var3 + var6 / 2;
         Objects.requireNonNull(RealmsPlayersTab.this.configurationScreen.getFont());
         int var13 = var10000 - 9 / 2;
         var1.drawString(RealmsPlayersTab.this.configurationScreen.getFont(), this.playerInfo.getName(), var4 + 8 + 32, var13, var11);
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
