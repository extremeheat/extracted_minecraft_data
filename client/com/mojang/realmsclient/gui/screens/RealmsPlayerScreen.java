package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsPlayerScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation OP_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/op_icon.png");
   private static final ResourceLocation USER_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/user_icon.png");
   private static final ResourceLocation CROSS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/cross_player_icon.png");
   private static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation("minecraft", "textures/gui/options_background.png");
   private static final Component NORMAL_USER_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.normal.tooltip");
   private static final Component OP_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.ops.tooltip");
   private static final Component REMOVE_ENTRY_TOOLTIP = new TranslatableComponent("mco.configure.world.invites.remove.tooltip");
   private static final Component INVITED_LABEL = new TranslatableComponent("mco.configure.world.invited");
   @Nullable
   private Component toolTip;
   private final RealmsConfigureWorldScreen lastScreen;
   final RealmsServer serverData;
   private RealmsPlayerScreen.InvitedObjectSelectionList invitedObjectSelectionList;
   int column1X;
   int columnWidth;
   private int column2X;
   private Button removeButton;
   private Button opdeopButton;
   private int selectedInvitedIndex = -1;
   private String selectedInvited;
   int player = -1;
   private boolean stateChanged;
   RealmsPlayerScreen.UserAction hoveredUserAction;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen var1, RealmsServer var2) {
      super(new TranslatableComponent("mco.configure.world.players.title"));
      this.hoveredUserAction = RealmsPlayerScreen.UserAction.NONE;
      this.lastScreen = var1;
      this.serverData = var2;
   }

   public void init() {
      this.column1X = this.width / 2 - 160;
      this.columnWidth = 150;
      this.column2X = this.width / 2 + 12;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.invitedObjectSelectionList = new RealmsPlayerScreen.InvitedObjectSelectionList();
      this.invitedObjectSelectionList.setLeftPos(this.column1X);
      this.addWidget(this.invitedObjectSelectionList);
      Iterator var1 = this.serverData.players.iterator();

      while(var1.hasNext()) {
         PlayerInfo var2 = (PlayerInfo)var1.next();
         this.invitedObjectSelectionList.addEntry(var2);
      }

      this.addRenderableWidget(new Button(this.column2X, row(1), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.buttons.invite"), (var1x) -> {
         this.minecraft.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData));
      }));
      this.removeButton = (Button)this.addRenderableWidget(new Button(this.column2X, row(7), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.invites.remove.tooltip"), (var1x) -> {
         this.uninvite(this.player);
      }));
      this.opdeopButton = (Button)this.addRenderableWidget(new Button(this.column2X, row(9), this.columnWidth + 10, 20, new TranslatableComponent("mco.configure.world.invites.ops.tooltip"), (var1x) -> {
         if (((PlayerInfo)this.serverData.players.get(this.player)).isOperator()) {
            this.deop(this.player);
         } else {
            this.method_23(this.player);
         }

      }));
      this.addRenderableWidget(new Button(this.column2X + this.columnWidth / 2 + 2, row(12), this.columnWidth / 2 + 10 - 2, 20, CommonComponents.GUI_BACK, (var1x) -> {
         this.backButtonClicked();
      }));
      this.updateButtonStates();
   }

   void updateButtonStates() {
      this.removeButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
      this.opdeopButton.visible = this.shouldRemoveAndOpdeopButtonBeVisible(this.player);
   }

   private boolean shouldRemoveAndOpdeopButtonBeVisible(int var1) {
      return var1 != -1;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
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
         this.minecraft.setScreen(this.lastScreen.getNewScreen());
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   // $FF: renamed from: op (int) void
   void method_23(int var1) {
      this.updateButtonStates();
      RealmsClient var2 = RealmsClient.create();
      String var3 = ((PlayerInfo)this.serverData.players.get(var1)).getUuid();

      try {
         this.updateOps(var2.method_51(this.serverData.field_121, var3));
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't op the user");
      }

   }

   void deop(int var1) {
      this.updateButtonStates();
      RealmsClient var2 = RealmsClient.create();
      String var3 = ((PlayerInfo)this.serverData.players.get(var1)).getUuid();

      try {
         this.updateOps(var2.deop(this.serverData.field_121, var3));
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

   void uninvite(int var1) {
      this.updateButtonStates();
      if (var1 >= 0 && var1 < this.serverData.players.size()) {
         PlayerInfo var2 = (PlayerInfo)this.serverData.players.get(var1);
         this.selectedInvited = var2.getUuid();
         this.selectedInvitedIndex = var1;
         RealmsConfirmScreen var3 = new RealmsConfirmScreen((var1x) -> {
            if (var1x) {
               RealmsClient var2 = RealmsClient.create();

               try {
                  var2.uninvite(this.serverData.field_121, this.selectedInvited);
               } catch (RealmsServiceException var4) {
                  LOGGER.error("Couldn't uninvite user");
               }

               this.deleteFromInvitedList(this.selectedInvitedIndex);
               this.player = -1;
               this.updateButtonStates();
            }

            this.stateChanged = true;
            this.minecraft.setScreen(this);
         }, new TextComponent("Question"), (new TranslatableComponent("mco.configure.world.uninvite.question")).append(" '").append(var2.getName()).append("' ?"));
         this.minecraft.setScreen(var3);
      }

   }

   private void deleteFromInvitedList(int var1) {
      this.serverData.players.remove(var1);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.toolTip = null;
      this.hoveredUserAction = RealmsPlayerScreen.UserAction.NONE;
      this.renderBackground(var1);
      if (this.invitedObjectSelectionList != null) {
         this.invitedObjectSelectionList.render(var1, var2, var3, var4);
      }

      drawCenteredString(var1, this.font, this.title, this.width / 2, 17, 16777215);
      int var5 = row(12) + 20;
      Tesselator var6 = Tesselator.getInstance();
      BufferBuilder var7 = var6.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float var8 = 32.0F;
      var7.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      var7.vertex(0.0D, (double)this.height, 0.0D).method_7(0.0F, (float)(this.height - var5) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      var7.vertex((double)this.width, (double)this.height, 0.0D).method_7((float)this.width / 32.0F, (float)(this.height - var5) / 32.0F + 0.0F).color(64, 64, 64, 255).endVertex();
      var7.vertex((double)this.width, (double)var5, 0.0D).method_7((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      var7.vertex(0.0D, (double)var5, 0.0D).method_7(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
      var6.end();
      if (this.serverData != null && this.serverData.players != null) {
         this.font.draw(var1, (Component)(new TextComponent("")).append(INVITED_LABEL).append(" (").append(Integer.toString(this.serverData.players.size())).append(")"), (float)this.column1X, (float)row(0), 10526880);
      } else {
         this.font.draw(var1, INVITED_LABEL, (float)this.column1X, (float)row(0), 10526880);
      }

      super.render(var1, var2, var3, var4);
      if (this.serverData != null) {
         this.renderMousehoverTooltip(var1, this.toolTip, var2, var3);
      }
   }

   protected void renderMousehoverTooltip(PoseStack var1, @Nullable Component var2, int var3, int var4) {
      if (var2 != null) {
         int var5 = var3 + 12;
         int var6 = var4 - 12;
         int var7 = this.font.width((FormattedText)var2);
         this.fillGradient(var1, var5 - 3, var6 - 3, var5 + var7 + 3, var6 + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(var1, var2, (float)var5, (float)var6, 16777215);
      }
   }

   void drawRemoveIcon(PoseStack var1, int var2, int var3, int var4, int var5) {
      boolean var6 = var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 9 && var5 < row(12) + 20 && var5 > row(1);
      RenderSystem.setShaderTexture(0, CROSS_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = var6 ? 7.0F : 0.0F;
      GuiComponent.blit(var1, var2, var3, 0.0F, var7, 8, 7, 8, 14);
      if (var6) {
         this.toolTip = REMOVE_ENTRY_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.UserAction.REMOVE;
      }

   }

   void drawOpped(PoseStack var1, int var2, int var3, int var4, int var5) {
      boolean var6 = var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 9 && var5 < row(12) + 20 && var5 > row(1);
      RenderSystem.setShaderTexture(0, OP_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = var6 ? 8.0F : 0.0F;
      GuiComponent.blit(var1, var2, var3, 0.0F, var7, 8, 8, 8, 16);
      if (var6) {
         this.toolTip = OP_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.UserAction.TOGGLE_OP;
      }

   }

   void drawNormal(PoseStack var1, int var2, int var3, int var4, int var5) {
      boolean var6 = var4 >= var2 && var4 <= var2 + 9 && var5 >= var3 && var5 <= var3 + 9 && var5 < row(12) + 20 && var5 > row(1);
      RenderSystem.setShaderTexture(0, USER_ICON_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = var6 ? 8.0F : 0.0F;
      GuiComponent.blit(var1, var2, var3, 0.0F, var7, 8, 8, 8, 16);
      if (var6) {
         this.toolTip = NORMAL_USER_TOOLTIP;
         this.hoveredUserAction = RealmsPlayerScreen.UserAction.TOGGLE_OP;
      }

   }

   static enum UserAction {
      TOGGLE_OP,
      REMOVE,
      NONE;

      private UserAction() {
      }

      // $FF: synthetic method
      private static RealmsPlayerScreen.UserAction[] $values() {
         return new RealmsPlayerScreen.UserAction[]{TOGGLE_OP, REMOVE, NONE};
      }
   }

   class InvitedObjectSelectionList extends RealmsObjectSelectionList<RealmsPlayerScreen.Entry> {
      public InvitedObjectSelectionList() {
         super(RealmsPlayerScreen.this.columnWidth + 10, RealmsPlayerScreen.row(12) + 20, RealmsPlayerScreen.row(1), RealmsPlayerScreen.row(12) + 20, 13);
      }

      public void addEntry(PlayerInfo var1) {
         this.addEntry(RealmsPlayerScreen.this.new Entry(var1));
      }

      public int getRowWidth() {
         return (int)((double)this.width * 1.0D);
      }

      public boolean isFocused() {
         return RealmsPlayerScreen.this.getFocused() == this;
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 == 0 && var1 < (double)this.getScrollbarPosition() && var3 >= (double)this.y0 && var3 <= (double)this.y1) {
            int var6 = RealmsPlayerScreen.this.column1X;
            int var7 = RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth;
            int var8 = (int)Math.floor(var3 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int var9 = var8 / this.itemHeight;
            if (var1 >= (double)var6 && var1 <= (double)var7 && var9 >= 0 && var8 >= 0 && var9 < this.getItemCount()) {
               this.selectItem(var9);
               this.itemClicked(var8, var9, var1, var3, this.width);
            }

            return true;
         } else {
            return super.mouseClicked(var1, var3, var5);
         }
      }

      public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
         if (var2 >= 0 && var2 <= RealmsPlayerScreen.this.serverData.players.size() && RealmsPlayerScreen.this.hoveredUserAction != RealmsPlayerScreen.UserAction.NONE) {
            if (RealmsPlayerScreen.this.hoveredUserAction == RealmsPlayerScreen.UserAction.TOGGLE_OP) {
               if (((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(var2)).isOperator()) {
                  RealmsPlayerScreen.this.deop(var2);
               } else {
                  RealmsPlayerScreen.this.method_23(var2);
               }
            } else if (RealmsPlayerScreen.this.hoveredUserAction == RealmsPlayerScreen.UserAction.REMOVE) {
               RealmsPlayerScreen.this.uninvite(var2);
            }

         }
      }

      public void selectItem(int var1) {
         super.selectItem(var1);
         this.selectInviteListItem(var1);
      }

      public void selectInviteListItem(int var1) {
         RealmsPlayerScreen.this.player = var1;
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsPlayerScreen.Entry var1) {
         super.setSelected(var1);
         RealmsPlayerScreen.this.player = this.children().indexOf(var1);
         RealmsPlayerScreen.this.updateButtonStates();
      }

      public void renderBackground(PoseStack var1) {
         RealmsPlayerScreen.this.renderBackground(var1);
      }

      public int getScrollbarPosition() {
         return RealmsPlayerScreen.this.column1X + this.width - 5;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 13;
      }
   }

   private class Entry extends ObjectSelectionList.Entry<RealmsPlayerScreen.Entry> {
      private final PlayerInfo playerInfo;

      public Entry(PlayerInfo var2) {
         super();
         this.playerInfo = var2;
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderInvitedItem(var1, this.playerInfo, var4, var3, var7, var8);
      }

      private void renderInvitedItem(PoseStack var1, PlayerInfo var2, int var3, int var4, int var5, int var6) {
         int var7;
         if (!var2.getAccepted()) {
            var7 = 10526880;
         } else if (var2.getOnline()) {
            var7 = 8388479;
         } else {
            var7 = 16777215;
         }

         RealmsPlayerScreen.this.font.draw(var1, var2.getName(), (float)(RealmsPlayerScreen.this.column1X + 3 + 12), (float)(var4 + 1), var7);
         if (var2.isOperator()) {
            RealmsPlayerScreen.this.drawOpped(var1, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, var4 + 1, var5, var6);
         } else {
            RealmsPlayerScreen.this.drawNormal(var1, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 10, var4 + 1, var5, var6);
         }

         RealmsPlayerScreen.this.drawRemoveIcon(var1, RealmsPlayerScreen.this.column1X + RealmsPlayerScreen.this.columnWidth - 22, var4 + 2, var5, var6);
         RealmsTextureManager.withBoundFace(var2.getUuid(), () -> {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            GuiComponent.blit(var1, RealmsPlayerScreen.this.column1X + 2 + 2, var4 + 1, 8, 8, 8.0F, 8.0F, 8, 8, 64, 64);
            GuiComponent.blit(var1, RealmsPlayerScreen.this.column1X + 2 + 2, var4 + 1, 8, 8, 40.0F, 8.0F, 8, 8, 64, 64);
         });
      }

      public Component getNarration() {
         return new TranslatableComponent("narrator.select", new Object[]{this.playerInfo.getName()});
      }
   }
}
