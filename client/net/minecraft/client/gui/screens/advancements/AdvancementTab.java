package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class AdvancementTab extends GuiComponent {
   private final Minecraft minecraft;
   private final AdvancementsScreen screen;
   private final AdvancementTabType type;
   private final int index;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final ItemStack icon;
   private final Component title;
   private final AdvancementWidget root;
   private final Map<Advancement, AdvancementWidget> widgets = Maps.newLinkedHashMap();
   private double scrollX;
   private double scrollY;
   private int minX = 2147483647;
   private int minY = 2147483647;
   private int maxX = -2147483648;
   private int maxY = -2147483648;
   private float fade;
   private boolean centered;

   public AdvancementTab(Minecraft var1, AdvancementsScreen var2, AdvancementTabType var3, int var4, Advancement var5, DisplayInfo var6) {
      super();
      this.minecraft = var1;
      this.screen = var2;
      this.type = var3;
      this.index = var4;
      this.advancement = var5;
      this.display = var6;
      this.icon = var6.getIcon();
      this.title = var6.getTitle();
      this.root = new AdvancementWidget(this, var1, var5, var6);
      this.addWidget(this.root, var5);
   }

   public AdvancementTabType getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public Advancement getAdvancement() {
      return this.advancement;
   }

   public Component getTitle() {
      return this.title;
   }

   public DisplayInfo getDisplay() {
      return this.display;
   }

   public void drawTab(PoseStack var1, int var2, int var3, boolean var4) {
      this.type.draw(var1, this, var2, var3, var4, this.index);
   }

   public void drawIcon(int var1, int var2, ItemRenderer var3) {
      this.type.drawIcon(var1, var2, this.index, var3, this.icon);
   }

   public void drawContents(PoseStack var1) {
      if (!this.centered) {
         this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
         this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
         this.centered = true;
      }

      var1.pushPose();
      var1.translate(0.0F, 0.0F, 950.0F);
      RenderSystem.enableDepthTest();
      RenderSystem.colorMask(false, false, false, false);
      fill(var1, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      var1.translate(0.0F, 0.0F, -950.0F);
      RenderSystem.depthFunc(518);
      fill(var1, 234, 113, 0, 0, -16777216);
      RenderSystem.depthFunc(515);
      ResourceLocation var2 = this.display.getBackground();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      if (var2 != null) {
         RenderSystem.setShaderTexture(0, var2);
      } else {
         RenderSystem.setShaderTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);
      }

      int var3 = Mth.floor(this.scrollX);
      int var4 = Mth.floor(this.scrollY);
      int var5 = var3 % 16;
      int var6 = var4 % 16;

      for(int var7 = -1; var7 <= 15; ++var7) {
         for(int var8 = -1; var8 <= 8; ++var8) {
            blit(var1, var5 + 16 * var7, var6 + 16 * var8, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.root.drawConnectivity(var1, var3, var4, true);
      this.root.drawConnectivity(var1, var3, var4, false);
      this.root.draw(var1, var3, var4);
      RenderSystem.depthFunc(518);
      var1.translate(0.0F, 0.0F, -950.0F);
      RenderSystem.colorMask(false, false, false, false);
      fill(var1, 4680, 2260, -4680, -2260, -16777216);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.depthFunc(515);
      var1.popPose();
   }

   public void drawTooltips(PoseStack var1, int var2, int var3, int var4, int var5) {
      var1.pushPose();
      var1.translate(0.0F, 0.0F, -200.0F);
      fill(var1, 0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
      boolean var6 = false;
      int var7 = Mth.floor(this.scrollX);
      int var8 = Mth.floor(this.scrollY);
      if (var2 > 0 && var2 < 234 && var3 > 0 && var3 < 113) {
         for(AdvancementWidget var10 : this.widgets.values()) {
            if (var10.isMouseOver(var7, var8, var2, var3)) {
               var6 = true;
               var10.drawHover(var1, var7, var8, this.fade, var4, var5);
               break;
            }
         }
      }

      var1.popPose();
      if (var6) {
         this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
      }
   }

   public boolean isMouseOver(int var1, int var2, double var3, double var5) {
      return this.type.isMouseOver(var1, var2, this.index, var3, var5);
   }

   @Nullable
   public static AdvancementTab create(Minecraft var0, AdvancementsScreen var1, int var2, Advancement var3) {
      if (var3.getDisplay() == null) {
         return null;
      } else {
         for(AdvancementTabType var7 : AdvancementTabType.values()) {
            if (var2 < var7.getMax()) {
               return new AdvancementTab(var0, var1, var7, var2, var3, var3.getDisplay());
            }

            var2 -= var7.getMax();
         }

         return null;
      }
   }

   public void scroll(double var1, double var3) {
      if (this.maxX - this.minX > 234) {
         this.scrollX = Mth.clamp(this.scrollX + var1, (double)(-(this.maxX - 234)), 0.0);
      }

      if (this.maxY - this.minY > 113) {
         this.scrollY = Mth.clamp(this.scrollY + var3, (double)(-(this.maxY - 113)), 0.0);
      }
   }

   public void addAdvancement(Advancement var1) {
      if (var1.getDisplay() != null) {
         AdvancementWidget var2 = new AdvancementWidget(this, this.minecraft, var1, var1.getDisplay());
         this.addWidget(var2, var1);
      }
   }

   private void addWidget(AdvancementWidget var1, Advancement var2) {
      this.widgets.put(var2, var1);
      int var3 = var1.getX();
      int var4 = var3 + 28;
      int var5 = var1.getY();
      int var6 = var5 + 27;
      this.minX = Math.min(this.minX, var3);
      this.maxX = Math.max(this.maxX, var4);
      this.minY = Math.min(this.minY, var5);
      this.maxY = Math.max(this.maxY, var6);

      for(AdvancementWidget var8 : this.widgets.values()) {
         var8.attachToParent();
      }
   }

   @Nullable
   public AdvancementWidget getWidget(Advancement var1) {
      return this.widgets.get(var1);
   }

   public AdvancementsScreen getScreen() {
      return this.screen;
   }
}
