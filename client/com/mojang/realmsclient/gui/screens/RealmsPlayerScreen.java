package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class RealmsPlayerScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("minecraft", "textures/gui/options_background.png");
   private static final Component QUESTION_TITLE = Component.translatable("mco.question");
   static final Component NORMAL_USER_TOOLTIP = Component.translatable("mco.configure.world.invites.normal.tooltip");
   static final Component OP_TOOLTIP = Component.translatable("mco.configure.world.invites.ops.tooltip");
   static final Component REMOVE_ENTRY_TOOLTIP = Component.translatable("mco.configure.world.invites.remove.tooltip");
   private static final int NO_ENTRY_SELECTED = -1;
   private final RealmsConfigureWorldScreen lastScreen;
   final RealmsServer serverData;
   RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
   int column1X;
   int columnWidth;
   private Button removeButton;
   private Button opdeopButton;
   int playerIndex = -1;
   private boolean stateChanged;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen var1, RealmsServer var2) {
      super(Component.translatable("mco.configure.world.players.title"));
      this.lastScreen = var1;
      this.serverData = var2;
   }

   @Override
   public void init() {
      this.column1X = this.width / 2 - 160;
      this.columnWidth = 150;
      int var1 = this.width / 2 + 12;
      this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
      this.invitedObjectSelectionList.setLeftPos(this.column1X);
      this.addWidget(this.invitedObjectSelectionList);

      for(PlayerInfo var3 : this.serverData.players) {
         this.invitedObjectSelectionList.addEntry(var3);
      }

      this.playerIndex = -1;
      this.addRenderableWidget(
         Button.builder(
               Component.translatable("mco.configure.world.buttons.invite"),
               var1x -> this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData))
            )
            .bounds(var1, row(1), this.columnWidth + 10, 20)
            .build()
      );
      this.removeButton = this.addRenderableWidget(
         Button.builder(Component.translatable("mco.configure.world.invites.remove.tooltip"), var1x -> this.uninvite(this.playerIndex))
            .bounds(var1, row(7), this.columnWidth + 10, 20)
            .build()
      );
      this.opdeopButton = this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.invites.ops.tooltip"), var1x -> {
         if (this.serverData.players.get(this.playerIndex).isOperator()) {
            this.deop(this.playerIndex);
         } else {
            this.op(this.playerIndex);
         }
      }).bounds(var1, row(9), this.columnWidth + 10, 20).build());
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1x -> this.backButtonClicked())
            .bounds(var1 + this.columnWidth / 2 + 2, row(12), this.columnWidth / 2 + 10 - 2, 20)
            .build()
      );
      this.updateButtonStates();
   }

   void updateButtonStates() {
      this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.playerIndex);
      this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.playerIndex);
      this.invitedObjectSelectionList.updateButtons();
   }

   private boolean shouldRemoveAndOpdeopButtonBeVisible(int var1) {
      return var1 != -1;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void backButtonClicked() {
      if (this.stateChanged) {
         this.minecraft.setScreen(this.lastScreen.getNewScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }
   }

   void op(int var1) {
      RealmsClient var2 = RealmsClient.create();
      UUID var3 = this.serverData.players.get(var1).getUuid();

      try {
         this.updateOps(var2.op(this.serverData.id, var3));
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't op the user", var5);
      }

      this.updateButtonStates();
   }

   void deop(int var1) {
      RealmsClient var2 = RealmsClient.create();
      UUID var3 = this.serverData.players.get(var1).getUuid();

      try {
         this.updateOps(var2.deop(this.serverData.id, var3));
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't deop the user", var5);
      }

      this.updateButtonStates();
   }

   private void updateOps(Ops var1) {
      for(PlayerInfo var3 : this.serverData.players) {
         var3.setOperator(var1.ops.contains(var3.getName()));
      }
   }

   void uninvite(int var1) {
      this.updateButtonStates();
      if (var1 >= 0 && var1 < this.serverData.players.size()) {
         PlayerInfo var2 = this.serverData.players.get(var1);
         RealmsConfirmScreen var3 = new RealmsConfirmScreen(var2x -> {
            if (var2x) {
               RealmsClient var3x = RealmsClient.create();

               try {
                  var3x.uninvite(this.serverData.id, var2.getUuid());
               } catch (RealmsServiceException var5) {
                  LOGGER.error("Couldn't uninvite user", var5);
               }

               this.serverData.players.remove(this.playerIndex);
               this.playerIndex = -1;
               this.updateButtonStates();
            }

            this.stateChanged = true;
            this.minecraft.setScreen(this);
         }, QUESTION_TITLE, Component.translatable("mco.configure.world.uninvite.player", var2.getName()));
         this.minecraft.setScreen(var3);
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.invitedObjectSelectionList.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 17, -1);
      int var5 = row(12) + 20;
      var1.setColor(0.25F, 0.25F, 0.25F, 1.0F);
      var1.blit(OPTIONS_BACKGROUND, 0, var5, 0.0F, 0.0F, this.width, this.height - var5, 32, 32);
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      String var6 = this.serverData.players != null ? Integer.toString(this.serverData.players.size()) : "0";
      var1.drawString(this.font, Component.translatable("mco.configure.world.invited.number", var6), this.column1X, row(0), -6250336, false);
   }

   class Entry extends ObjectSelectionList.Entry<RealmsPlayerScreen.Entry> {
      private static final int X_OFFSET = 3;
      private static final int Y_PADDING = 1;
      private static final int BUTTON_WIDTH = 8;
      private static final int BUTTON_HEIGHT = 7;
      private static final WidgetSprites REMOVE_BUTTON_SPRITES = new WidgetSprites(
         new ResourceLocation("player_list/remove_player"), new ResourceLocation("player_list/remove_player_highlighted")
      );
      private static final WidgetSprites MAKE_OP_BUTTON_SPRITES = new WidgetSprites(
         new ResourceLocation("player_list/make_operator"), new ResourceLocation("player_list/make_operator_highlighted")
      );
      private static final WidgetSprites REMOVE_OP_BUTTON_SPRITES = new WidgetSprites(
         new ResourceLocation("player_list/remove_operator"), new ResourceLocation("player_list/remove_operator_highlighted")
      );
      private final PlayerInfo playerInfo;
      private final List<AbstractWidget> children = new ArrayList<>();
      private final ImageButton removeButton;
      private final ImageButton makeOpButton;
      private final ImageButton removeOpButton;

      public Entry(PlayerInfo var2) {
         super();
         this.playerInfo = var2;
         int var3 = RealmsPlayerScreen.this.serverData.players.indexOf(this.playerInfo);
         int var4 = RealmsPlayerScreen.this.invitedObjectSelectionList.getRowRight() - 16 - 9;
         int var5 = RealmsPlayerScreen.this.invitedObjectSelectionList.getRowTop(var3) + 1;
         this.removeButton = new ImageButton(var4, var5, 8, 7, REMOVE_BUTTON_SPRITES, var2x -> RealmsPlayerScreen.this.uninvite(var3), CommonComponents.EMPTY);
         this.removeButton.setTooltip(Tooltip.create(RealmsPlayerScreen.REMOVE_ENTRY_TOOLTIP));
         this.children.add(this.removeButton);
         var4 += 11;
         this.makeOpButton = new ImageButton(var4, var5, 8, 7, MAKE_OP_BUTTON_SPRITES, var2x -> RealmsPlayerScreen.this.op(var3), CommonComponents.EMPTY);
         this.makeOpButton.setTooltip(Tooltip.create(RealmsPlayerScreen.NORMAL_USER_TOOLTIP));
         this.children.add(this.makeOpButton);
         this.removeOpButton = new ImageButton(var4, var5, 8, 7, REMOVE_OP_BUTTON_SPRITES, var2x -> RealmsPlayerScreen.this.deop(var3), CommonComponents.EMPTY);
         this.removeOpButton.setTooltip(Tooltip.create(RealmsPlayerScreen.OP_TOOLTIP));
         this.children.add(this.removeOpButton);
         this.updateButtons();
      }

      public void updateButtons() {
         this.makeOpButton.visible = !this.playerInfo.isOperator();
         this.removeOpButton.visible = !this.makeOpButton.visible;
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         if (!this.makeOpButton.mouseClicked(var1, var3, var5)) {
            this.removeOpButton.mouseClicked(var1, var3, var5);
         }

         this.removeButton.mouseClicked(var1, var3, var5);
         return true;
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11;
         if (!this.playerInfo.getAccepted()) {
            var11 = -6250336;
         } else if (this.playerInfo.getOnline()) {
            var11 = 8388479;
         } else {
            var11 = -1;
         }

         RealmsUtil.renderPlayerFace(var1, RealmsPlayerScreen.this.column1X + 2 + 2, var3 + 1, 8, this.playerInfo.getUuid());
         var1.drawString(RealmsPlayerScreen.this.font, this.playerInfo.getName(), RealmsPlayerScreen.this.column1X + 3 + 12, var3 + 1, var11, false);
         this.children.forEach(var5x -> {
            var5x.setY(var3 + 1);
            var5x.render(var1, var7, var8, var10);
         });
      }

      @Override
      public Component getNarration() {
         return Component.translatable("narrator.select", this.playerInfo.getName());
      }
   }

   class InvitedObjectSelectionList extends RealmsObjectSelectionList<RealmsPlayerScreen.Entry> {
      public InvitedObjectSelectionList() {
         super(RealmsPlayerScreen.this.columnWidth + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
      }

      public void updateButtons() {
         if (RealmsPlayerScreen.this.playerIndex != -1) {
            this.getEntry(RealmsPlayerScreen.this.playerIndex).updateButtons();
         }
      }

      public void addEntry(PlayerInfo var1) {
         this.addEntry(RealmsPlayerScreen.this.new Entry(var1));
      }

      @Override
      public int getRowWidth() {
         return (int)((double)this.width * 1.0);
      }

      @Override
      public void selectItem(int var1) {
         super.selectItem(var1);
         this.selectInviteListItem(var1);
      }

      public void selectInviteListItem(int var1) {
         RealmsPlayerScreen.this.playerIndex = var1;
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsPlayerScreen.Entry var1) {
         super.setSelected(var1);
         RealmsPlayerScreen.this.playerIndex = this.children().indexOf(var1);
         RealmsPlayerScreen.this.updateButtonStates();
      }

      @Override
      public int getScrollbarPosition() {
         return RealmsPlayerScreen.this.column1X + this.width - 5;
      }

      @Override
      public int getMaxPosition() {
         return this.getItemCount() * 13;
      }
   }
}
