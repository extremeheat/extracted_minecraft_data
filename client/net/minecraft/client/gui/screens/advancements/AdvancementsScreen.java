package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;

public class AdvancementsScreen extends Screen implements ClientAdvancements.Listener {
   private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
   private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
   private static final Component VERY_SAD_LABEL = new TranslatableComponent("advancements.sad_label");
   private static final Component NO_ADVANCEMENTS_LABEL = new TranslatableComponent("advancements.empty");
   private static final Component TITLE = new TranslatableComponent("gui.advancements");
   private final ClientAdvancements advancements;
   private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
   private AdvancementTab selectedTab;
   private boolean isScrolling;

   public AdvancementsScreen(ClientAdvancements var1) {
      super(NarratorChatListener.NO_TITLE);
      this.advancements = var1;
   }

   protected void init() {
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         this.advancements.setSelectedTab(((AdvancementTab)this.tabs.values().iterator().next()).getAdvancement(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
      }

   }

   public void removed() {
      this.advancements.setListener((ClientAdvancements.Listener)null);
      ClientPacketListener var1 = this.minecraft.getConnection();
      if (var1 != null) {
         var1.send((Packet)ServerboundSeenAdvancementsPacket.closedScreen());
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         int var6 = (this.width - 252) / 2;
         int var7 = (this.height - 140) / 2;
         Iterator var8 = this.tabs.values().iterator();

         while(var8.hasNext()) {
            AdvancementTab var9 = (AdvancementTab)var8.next();
            if (var9.isMouseOver(var6, var7, var1, var3)) {
               this.advancements.setSelectedTab(var9.getAdvancement(), true);
               break;
            }
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.minecraft.options.keyAdvancements.matches(var1, var2)) {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      int var5 = (this.width - 252) / 2;
      int var6 = (this.height - 140) / 2;
      this.renderBackground(var1);
      this.renderInside(var1, var2, var3, var5, var6);
      this.renderWindow(var1, var5, var6);
      this.renderTooltips(var1, var2, var3, var5, var6);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (var5 != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(var6, var8);
         }

         return true;
      }
   }

   private void renderInside(PoseStack var1, int var2, int var3, int var4, int var5) {
      AdvancementTab var6 = this.selectedTab;
      if (var6 == null) {
         fill(var1, var4 + 9, var5 + 18, var4 + 9 + 234, var5 + 18 + 113, -16777216);
         int var7 = var4 + 9 + 117;
         Font var10001 = this.font;
         Component var10002 = NO_ADVANCEMENTS_LABEL;
         int var10004 = var5 + 18 + 56;
         this.font.getClass();
         drawCenteredString(var1, var10001, var10002, var7, var10004 - 9 / 2, -1);
         var10001 = this.font;
         var10002 = VERY_SAD_LABEL;
         var10004 = var5 + 18 + 113;
         this.font.getClass();
         drawCenteredString(var1, var10001, var10002, var7, var10004 - 9, -1);
      } else {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(var4 + 9), (float)(var5 + 18), 0.0F);
         var6.drawContents(var1);
         RenderSystem.popMatrix();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
      }
   }

   public void renderWindow(PoseStack var1, int var2, int var3) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bind(WINDOW_LOCATION);
      this.blit(var1, var2, var3, 0, 0, 252, 140);
      if (this.tabs.size() > 1) {
         this.minecraft.getTextureManager().bind(TABS_LOCATION);
         Iterator var4 = this.tabs.values().iterator();

         AdvancementTab var5;
         while(var4.hasNext()) {
            var5 = (AdvancementTab)var4.next();
            var5.drawTab(var1, var2, var3, var5 == this.selectedTab);
         }

         RenderSystem.enableRescaleNormal();
         RenderSystem.defaultBlendFunc();
         var4 = this.tabs.values().iterator();

         while(var4.hasNext()) {
            var5 = (AdvancementTab)var4.next();
            var5.drawIcon(var2, var3, this.itemRenderer);
         }

         RenderSystem.disableBlend();
      }

      this.font.draw(var1, TITLE, (float)(var2 + 8), (float)(var3 + 6), 4210752);
   }

   private void renderTooltips(PoseStack var1, int var2, int var3, int var4, int var5) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.selectedTab != null) {
         RenderSystem.pushMatrix();
         RenderSystem.enableDepthTest();
         RenderSystem.translatef((float)(var4 + 9), (float)(var5 + 18), 400.0F);
         this.selectedTab.drawTooltips(var1, var2 - var4 - 9, var3 - var5 - 18, var4, var5);
         RenderSystem.disableDepthTest();
         RenderSystem.popMatrix();
      }

      if (this.tabs.size() > 1) {
         Iterator var6 = this.tabs.values().iterator();

         while(var6.hasNext()) {
            AdvancementTab var7 = (AdvancementTab)var6.next();
            if (var7.isMouseOver(var4, var5, (double)var2, (double)var3)) {
               this.renderTooltip(var1, var7.getTitle(), var2, var3);
            }
         }
      }

   }

   public void onAddAdvancementRoot(Advancement var1) {
      AdvancementTab var2 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), var1);
      if (var2 != null) {
         this.tabs.put(var1, var2);
      }
   }

   public void onRemoveAdvancementRoot(Advancement var1) {
   }

   public void onAddAdvancementTask(Advancement var1) {
      AdvancementTab var2 = this.getTab(var1);
      if (var2 != null) {
         var2.addAdvancement(var1);
      }

   }

   public void onRemoveAdvancementTask(Advancement var1) {
   }

   public void onUpdateAdvancementProgress(Advancement var1, AdvancementProgress var2) {
      AdvancementWidget var3 = this.getAdvancementWidget(var1);
      if (var3 != null) {
         var3.setProgress(var2);
      }

   }

   public void onSelectedTabChanged(@Nullable Advancement var1) {
      this.selectedTab = (AdvancementTab)this.tabs.get(var1);
   }

   public void onAdvancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public AdvancementWidget getAdvancementWidget(Advancement var1) {
      AdvancementTab var2 = this.getTab(var1);
      return var2 == null ? null : var2.getWidget(var1);
   }

   @Nullable
   private AdvancementTab getTab(Advancement var1) {
      while(var1.getParent() != null) {
         var1 = var1.getParent();
      }

      return (AdvancementTab)this.tabs.get(var1);
   }
}
