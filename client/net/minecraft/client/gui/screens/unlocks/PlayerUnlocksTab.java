package net.minecraft.client.gui.screens.unlocks;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientPlayerUnlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class PlayerUnlocksTab {
   private final Minecraft minecraft;
   private final PlayerUnlocksScreen screen;
   private final UnlockTabType type;
   private final int index;
   private final Holder<PlayerUnlock> rootNode;
   private final DisplayInfo display;
   private final ItemStack icon;
   private final Component title;
   private final PlayerUnlockWidget root;
   private final Map<Holder<PlayerUnlock>, PlayerUnlockWidget> widgets = Maps.newLinkedHashMap();
   private double scrollX;
   private double scrollY;
   private int minX = 2147483647;
   private int minY = 2147483647;
   private int maxX = -2147483648;
   private int maxY = -2147483648;
   private float fade;
   private boolean centered;

   public PlayerUnlocksTab(Minecraft var1, PlayerUnlocksScreen var2, UnlockTabType var3, int var4, Holder<PlayerUnlock> var5, DisplayInfo var6) {
      super();
      this.minecraft = var1;
      this.screen = var2;
      this.type = var3;
      this.index = var4;
      this.rootNode = var5;
      this.display = var6;
      this.icon = var6.getIcon();
      this.title = var6.getTitle();
      this.root = new PlayerUnlockWidget(this, var1, var5, var6, var2.getVisibility(var5), var2.isUnlocked(var5), ((PlayerUnlock)var5.value()).unlockPrice(), !((PlayerUnlock)var5.value()).exclusiveKey().isEmpty(), var2.isActiveExclusive(var5));
      this.addWidget(this.root, var5);
   }

   public UnlockTabType getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public Holder<PlayerUnlock> getRoot() {
      return this.rootNode;
   }

   public Component getTitle() {
      return this.title;
   }

   public DisplayInfo getDisplay() {
      return this.display;
   }

   public void drawTab(GuiGraphics var1, int var2, int var3, boolean var4) {
      this.type.draw(var1, var2, var3, var4, this.index, this.screen.getWindowWidth(), this.screen.getWindowHeight());
   }

   public void drawIcon(GuiGraphics var1, int var2, int var3) {
      this.type.drawIcon(var1, var2, var3, this.index, this.icon, this.screen.getWindowWidth(), this.screen.getWindowHeight());
   }

   public void drawContents(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      if (!this.centered) {
         this.scrollX = (double)var4 / 2.0 - (double)(this.maxX + this.minX) / 2.0;
         this.scrollY = (double)var5 / 2.0 - (double)(this.maxY + this.minY) / 2.0;
         this.centered = true;
      }

      var1.enableScissor(var2, var3, var2 + var4, var3 + var5);
      var1.pose().pushPose();
      var1.pose().translate((float)var2, (float)var3, 0.0F);
      ResourceLocation var6 = (ResourceLocation)this.display.getBackground().map(ClientAsset::id).orElse(TextureManager.INTENTIONAL_MISSING_TEXTURE);
      int var7 = Mth.floor(this.scrollX);
      int var8 = Mth.floor(this.scrollY);
      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var6, 0, 0, var4, var5);
      this.root.drawConnectivity(var1, var7, var8, true);
      this.root.drawConnectivity(var1, var7, var8, false);
      this.root.draw(var1, var7, var8);
      var1.pose().popPose();
      var1.disableScissor();
   }

   public void drawTooltips(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      var1.pose().pushPose();
      var1.pose().translate(0.0F, 0.0F, -200.0F);
      var1.fill(0, 0, var6, var7, Mth.floor(this.fade * 255.0F) << 24);
      boolean var8 = false;
      int var9 = Mth.floor(this.scrollX);
      int var10 = Mth.floor(this.scrollY);
      if (var2 > 0 && var2 < var6 && var3 > 0 && var3 < var7) {
         for(PlayerUnlockWidget var12 : this.widgets.values()) {
            if (var12.isMouseOver(var9, var10, var2, var3)) {
               var8 = true;
               var12.drawHover(var1, var9, var10, this.fade, var4, var5, var6, var7);
               break;
            }
         }
      }

      var1.pose().popPose();
      if (var8) {
         this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }

   }

   public boolean isMouseOver(int var1, int var2, double var3, double var5) {
      return this.type.isMouseOver(var1, var2, this.index, var3, var5, this.screen.getWindowWidth(), this.screen.getWindowHeight());
   }

   @Nullable
   public static PlayerUnlocksTab create(Minecraft var0, PlayerUnlocksScreen var1, ClientPlayerUnlocks var2, int var3, Holder<PlayerUnlock> var4) {
      PlayerUnlockTreeNodePosition.run(var4, var2);
      DisplayInfo var5 = ((PlayerUnlock)var4.value()).display();

      for(UnlockTabType var9 : UnlockTabType.values()) {
         if (var3 < var9.getMax()) {
            return new PlayerUnlocksTab(var0, var1, var9, var3, var4, var5);
         }

         var3 -= var9.getMax();
      }

      return null;
   }

   public void updateLayout() {
      ClientPlayerUnlocks var1 = this.minecraft.getConnection().getUnlocks();
      PlayerUnlocksTree var2 = var1.getTree();

      for(Holder var4 : var2.getChildren(this.rootNode)) {
         PlayerUnlockWidget var5 = this.getWidget(var4);
         if (var5 == null) {
            this.addWidget(this.root, this.rootNode);
         }
      }

      PlayerUnlockTreeNodePosition.run(this.rootNode, var1);
      this.screen.repositionElements();
   }

   public void scroll(double var1, double var3, int var5, int var6) {
      if (this.maxX - this.minX > var5) {
         this.scrollX = Mth.clamp(this.scrollX + var1, (double)(-(this.maxX - var5)), 0.0);
      }

      if (this.maxY - this.minY > var6) {
         this.scrollY = Mth.clamp(this.scrollY + var3, (double)(-(this.maxY - var6)), 0.0);
      }

   }

   public void addUnlock(Holder<PlayerUnlock> var1) {
      DisplayInfo var2 = ((PlayerUnlock)var1.value()).display();
      PlayerUnlockWidget var3 = new PlayerUnlockWidget(this, this.minecraft, var1, var2, this.screen.getVisibility(var1), this.screen.isUnlocked(var1), ((PlayerUnlock)var1.value()).unlockPrice(), !((PlayerUnlock)var1.value()).exclusiveKey().isEmpty(), this.screen.isActiveExclusive(var1));
      this.addWidget(var3, var1);
   }

   private void addWidget(PlayerUnlockWidget var1, Holder<PlayerUnlock> var2) {
      this.widgets.put(var2, var1);
      int var3 = var1.getX();
      int var4 = var3 + 28;
      int var5 = var1.getY();
      int var6 = var5 + 27;
      this.minX = Math.min(this.minX, var3);
      this.maxX = Math.max(this.maxX, var4);
      this.minY = Math.min(this.minY, var5);
      this.maxY = Math.max(this.maxY, var6);

      for(PlayerUnlockWidget var8 : this.widgets.values()) {
         var8.attachToParent();
      }

      var1.attachToParent();
   }

   @Nullable
   public PlayerUnlockWidget getWidget(Holder<PlayerUnlock> var1) {
      return (PlayerUnlockWidget)this.widgets.get(var1);
   }

   public PlayerUnlocksScreen getScreen() {
      return this.screen;
   }

   public void mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         int var6 = Mth.floor(this.scrollX);
         int var7 = Mth.floor(this.scrollY);

         for(PlayerUnlockWidget var9 : this.widgets.values()) {
            if (var9.isMouseOver(var6, var7, (int)var1, (int)var3)) {
               var9.onClicked();
               break;
            }
         }

      }
   }
}
