package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.Iterator;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsPlayerScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private String toolTip;
   private final RealmsConfigureWorldScreen lastScreen;
   private final RealmsServer serverData;
   private RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
   private int column1_x;
   private int column_width;
   private int column2_x;
   private RealmsButton removeButton;
   private RealmsButton opdeopButton;
   private int selectedInvitedIndex = -1;
   private String selectedInvited;
   private int player = -1;
   private boolean stateChanged;
   private RealmsLabel titleLabel;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen var1, RealmsServer var2) {
      this.lastScreen = var1;
      this.serverData = var2;
   }

   public void tick() {
      super.tick();
   }

   public void init() {
      this.column1_x = this.width() / 2 - 160;
      this.column_width = 150;
      this.column2_x = this.width() / 2 + 12;
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(1, this.column2_x, RealmsConstants.row(1), this.column_width + 10, 20, getLocalizedString("mco.configure.world.buttons.invite")) {
         public void onPress() {
            Realms.setScreen(new RealmsInviteScreen(RealmsPlayerScreen.this.lastScreen, RealmsPlayerScreen.this, RealmsPlayerScreen.this.serverData));
         }
      });
      this.buttonsAdd(this.removeButton = new RealmsButton(4, this.column2_x, RealmsConstants.row(7), this.column_width + 10, 20, getLocalizedString("mco.configure.world.invites.remove.tooltip")) {
         public void onPress() {
            RealmsPlayerScreen.this.uninvite(RealmsPlayerScreen.this.player);
         }
      });
      this.buttonsAdd(this.opdeopButton = new RealmsButton(5, this.column2_x, RealmsConstants.row(9), this.column_width + 10, 20, getLocalizedString("mco.configure.world.invites.ops.tooltip")) {
         public void onPress() {
            if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(RealmsPlayerScreen.this.player)).isOperator()) {
               RealmsPlayerScreen.this.deop(RealmsPlayerScreen.this.player);
            } else {
               RealmsPlayerScreen.this.op(RealmsPlayerScreen.this.player);
            }

         }
      });
      this.buttonsAdd(new RealmsButton(0, this.column2_x + this.column_width / 2 + 2, RealmsConstants.row(12), this.column_width / 2 + 10 - 2, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsPlayerScreen.this.backButtonClicked();
         }
      });
      this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
      this.invitedObjectSelectionList.setLeftPos(this.column1_x);
      this.addWidget(this.invitedObjectSelectionList);
      Iterator var1 = this.serverData.players.iterator();

      while(var1.hasNext()) {
         PlayerInfo var2 = (PlayerInfo)var1.next();
         this.invitedObjectSelectionList.addEntry(var2);
      }

      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.players.title"), this.width() / 2, 17, 16777215));
      this.narrateLabels();
      this.updateButtonStates();
   }

   private void updateButtonStates() {
      this.removeButton.setVisible(this.shouldRemoveAndOpdeopButtonBeVisible(this.player));
      this.opdeopButton.setVisible(this.shouldRemoveAndOpdeopButtonBeVisible(this.player));
   }

   private boolean shouldRemoveAndOpdeopButtonBeVisible(int var1) {
      return var1 != -1;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

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
         Realms.setScreen(this.lastScreen.getNewScreen());
      } else {
         Realms.setScreen(this.lastScreen);
      }

   }

   private void op(int var1) {
      this.updateButtonStates();
      RealmsClient var2 = RealmsClient.createRealmsClient();
      String var3 = ((PlayerInfo)this.serverData.players.get(var1)).getUuid();

      try {
         this.updateOps(var2.op(this.serverData.id, var3));
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't op the user");
      }

   }

   private void deop(int var1) {
      this.updateButtonStates();
      RealmsClient var2 = RealmsClient.createRealmsClient();
      String var3 = ((PlayerInfo)this.serverData.players.get(var1)).getUuid();

      try {
         this.updateOps(var2.deop(this.serverData.id, var3));
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't deop the user");
      }

   }

   private void updateOps(Ops var1) {
      Iterator var2 = this.serverData.players.iterator();

      while(var2.hasNext()) {
         PlayerInfo var3 = (PlayerInfo)var2.next();
         var3.setOperator(var1.ops.contains(var3.getName()));
      }

   }

   private void uninvite(int var1) {
      this.updateButtonStates();
      if (var1 >= 0 && var1 < this.serverData.players.size()) {
         PlayerInfo var2 = (PlayerInfo)this.serverData.players.get(var1);
         this.selectedInvited = var2.getUuid();
         this.selectedInvitedIndex = var1;
         RealmsConfirmScreen var3 = new RealmsConfirmScreen(this, "Question", getLocalizedString("mco.configure.world.uninvite.question") + " '" + var2.getName() + "' ?", 2);
         Realms.setScreen(var3);
      }

   }

   public void confirmResult(boolean var1, int var2) {
      if (var2 == 2) {
         if (var1) {
            RealmsClient var3 = RealmsClient.createRealmsClient();

            try {
               var3.uninvite(this.serverData.id, this.selectedInvited);
            } catch (RealmsServiceException var5) {
               LOGGER.error("Couldn't uninvite user");
            }

            this.deleteFromInvitedList(this.selectedInvitedIndex);
            this.player = -1;
            this.updateButtonStates();
         }

         this.stateChanged = true;
         Realms.setScreen(this);
      }

   }

   private void deleteFromInvitedList(int var1) {
      this.serverData.players.remove(var1);
   }

   public void render(int var1, int var2, float var3) {
      this.toolTip = null;
      this.renderBackground();
      if (this.invitedObjectSelectionList != null) {
         this.invitedObjectSelectionList.render(var1, var2, var3);
      }

      int var4 = RealmsConstants.row(12) + 20;
      Tezzelator var5 = Tezzelator.instance;
      bind("textures/gui/options_background.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var6 = 32.0F;
      var5.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
      var5.vertex(0.0D, (double)this.height(), 0.0D).tex(0.0F, (float)(this.height() - var4) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      var5.vertex((double)this.width(), (double)this.height(), 0.0D).tex((float)this.width() / 32.0F, (float)(this.height() - var4) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      var5.vertex((double)this.width(), (double)var4, 0.0D).tex((float)this.width() / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      var5.vertex(0.0D, (double)var4, 0.0D).tex(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      var5.end();
      this.titleLabel.render(this);
      if (this.serverData != null && this.serverData.players != null) {
         this.drawString(getLocalizedString("mco.configure.world.invited") + " (" + this.serverData.players.size() + ")", this.column1_x, RealmsConstants.row(0), 10526880);
      } else {
         this.drawString(getLocalizedString("mco.configure.world.invited"), this.column1_x, RealmsConstants.row(0), 10526880);
      }

      super.render(var1, var2, var3);
      if (this.serverData != null) {
         if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, var1, var2);
         }

      }
   }

   protected void renderMousehoverTooltip(String var1, int var2, int var3) {
      if (var1 != null) {
         int var4 = var2 + 12;
         int var5 = var3 - 12;
         int var6 = this.fontWidth(var1);
         this.fillGradient(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(var1, var4, var5, 16777215);
      }
   }

   private void drawRemoveIcon(int var1, int var2, int var3, int var4) {
      boolean var5 = var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 9 && var4 < RealmsConstants.row(12) + 20 && var4 > RealmsConstants.row(1);
      bind("realms:textures/gui/realms/cross_player_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, var5 ? 7.0F : 0.0F, 8, 7, 8, 14);
      RenderSystem.popMatrix();
      if (var5) {
         this.toolTip = getLocalizedString("mco.configure.world.invites.remove.tooltip");
      }

   }

   private void drawOpped(int var1, int var2, int var3, int var4) {
      boolean var5 = var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 9 && var4 < RealmsConstants.row(12) + 20 && var4 > RealmsConstants.row(1);
      bind("realms:textures/gui/realms/op_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, var5 ? 8.0F : 0.0F, 8, 8, 8, 16);
      RenderSystem.popMatrix();
      if (var5) {
         this.toolTip = getLocalizedString("mco.configure.world.invites.ops.tooltip");
      }

   }

   private void drawNormal(int var1, int var2, int var3, int var4) {
      boolean var5 = var3 >= var1 && var3 <= var1 + 9 && var4 >= var2 && var4 <= var2 + 9 && var4 < RealmsConstants.row(12) + 20 && var4 > RealmsConstants.row(1);
      bind("realms:textures/gui/realms/user_icon.png");
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.pushMatrix();
      RealmsScreen.blit(var1, var2, 0.0F, var5 ? 8.0F : 0.0F, 8, 8, 8, 16);
      RenderSystem.popMatrix();
      if (var5) {
         this.toolTip = getLocalizedString("mco.configure.world.invites.normal.tooltip");
      }

   }

   class InvitedObjectSelectionListEntry extends RealmListEntry {
      final PlayerInfo mPlayerInfo;

      public InvitedObjectSelectionListEntry(PlayerInfo var2) {
         this.mPlayerInfo = var2;
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.renderInvitedItem(this.mPlayerInfo, var3, var2, var6, var7);
      }

      private void renderInvitedItem(PlayerInfo var1, int var2, int var3, int var4, int var5) {
         int var6;
         if (!var1.getAccepted()) {
            var6 = 10526880;
         } else if (var1.getOnline()) {
            var6 = 8388479;
         } else {
            var6 = 16777215;
         }

         RealmsPlayerScreen.this.drawString(var1.getName(), RealmsPlayerScreen.this.column1_x + 3 + 12, var3 + 1, var6);
         if (var1.isOperator()) {
            RealmsPlayerScreen.this.drawOpped(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, var3 + 1, var4, var5);
         } else {
            RealmsPlayerScreen.this.drawNormal(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, var3 + 1, var4, var5);
         }

         RealmsPlayerScreen.this.drawRemoveIcon(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, var3 + 2, var4, var5);
         RealmsPlayerScreen.this.drawString(RealmsScreen.getLocalizedString("mco.configure.world.activityfeed.disabled"), RealmsPlayerScreen.this.column2_x, RealmsConstants.row(5), 10526880);
         RealmsTextureManager.withBoundFace(var1.getUuid(), () -> {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, var3 + 1, 8.0F, 8.0F, 8, 8, 8, 8, 64, 64);
            RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, var3 + 1, 40.0F, 8.0F, 8, 8, 8, 8, 64, 64);
         });
      }
   }

   class InvitedObjectSelectionList extends RealmsObjectSelectionList {
      public InvitedObjectSelectionList() {
         super(RealmsPlayerScreen.this.column_width + 10, RealmsConstants.row(12) + 20, RealmsConstants.row(1), RealmsConstants.row(12) + 20, 13);
      }

      public void addEntry(PlayerInfo var1) {
         this.addEntry(RealmsPlayerScreen.this.new InvitedObjectSelectionListEntry(var1));
      }

      public int getRowWidth() {
         return (int)((double)this.width() * 1.0D);
      }

      public boolean isFocused() {
         return RealmsPlayerScreen.this.isFocused(this);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 == 0 && var1 < (double)this.getScrollbarPosition() && var3 >= (double)this.y0() && var3 <= (double)this.y1()) {
            int var6 = RealmsPlayerScreen.this.column1_x;
            int var7 = RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width;
            int var8 = (int)Math.floor(var3 - (double)this.y0()) - this.headerHeight() + this.getScroll() - 4;
            int var9 = var8 / this.itemHeight();
            if (var1 >= (double)var6 && var1 <= (double)var7 && var9 >= 0 && var8 >= 0 && var9 < this.getItemCount()) {
               this.selectItem(var9);
               this.itemClicked(var8, var9, var1, var3, this.width());
            }

            return true;
         } else {
            return super.mouseClicked(var1, var3, var5);
         }
      }

      public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
         if (var2 >= 0 && var2 <= RealmsPlayerScreen.this.serverData.players.size() && RealmsPlayerScreen.this.toolTip != null) {
            if (!RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip")) && !RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip"))) {
               if (RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip"))) {
                  RealmsPlayerScreen.this.uninvite(var2);
               }
            } else if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(var2)).isOperator()) {
               RealmsPlayerScreen.this.deop(var2);
            } else {
               RealmsPlayerScreen.this.op(var2);
            }

         }
      }

      public void selectItem(int var1) {
         this.setSelected(var1);
         if (var1 != -1) {
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(var1)).getName()));
         }

         this.selectInviteListItem(var1);
      }

      public void selectInviteListItem(int var1) {
         RealmsPlayerScreen.this.player = var1;
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void renderBackground() {
         RealmsPlayerScreen.this.renderBackground();
      }

      public int getScrollbarPosition() {
         return RealmsPlayerScreen.this.column1_x + this.width() - 5;
      }

      public int getItemCount() {
         return RealmsPlayerScreen.this.serverData == null ? 1 : RealmsPlayerScreen.this.serverData.players.size();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 13;
      }
   }
}
