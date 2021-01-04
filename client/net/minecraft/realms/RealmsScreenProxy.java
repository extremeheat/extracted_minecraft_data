package net.minecraft.realms;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsScreenProxy extends Screen {
   private final RealmsScreen screen;
   private static final Logger LOGGER = LogManager.getLogger();

   public RealmsScreenProxy(RealmsScreen var1) {
      super(NarratorChatListener.NO_TITLE);
      this.screen = var1;
   }

   public RealmsScreen getScreen() {
      return this.screen;
   }

   public void init(Minecraft var1, int var2, int var3) {
      this.screen.init(var1, var2, var3);
      super.init(var1, var2, var3);
   }

   public void init() {
      this.screen.init();
      super.init();
   }

   public void drawCenteredString(String var1, int var2, int var3, int var4) {
      super.drawCenteredString(this.font, var1, var2, var3, var4);
   }

   public void drawString(String var1, int var2, int var3, int var4, boolean var5) {
      if (var5) {
         super.drawString(this.font, var1, var2, var3, var4);
      } else {
         this.font.draw(var1, (float)var2, (float)var3, var4);
      }

   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.screen.blit(var1, var2, var3, var4, var5, var6);
      super.blit(var1, var2, var3, var4, var5, var6);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      GuiComponent.blit(var0, var1, var6, var7, var2, var3, var4, var5, var8, var9);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7) {
      GuiComponent.blit(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      super.fillGradient(var1, var2, var3, var4, var5, var6);
   }

   public void renderBackground() {
      super.renderBackground();
   }

   public boolean isPauseScreen() {
      return super.isPauseScreen();
   }

   public void renderBackground(int var1) {
      super.renderBackground(var1);
   }

   public void render(int var1, int var2, float var3) {
      this.screen.render(var1, var2, var3);
   }

   public void renderTooltip(ItemStack var1, int var2, int var3) {
      super.renderTooltip(var1, var2, var3);
   }

   public void renderTooltip(String var1, int var2, int var3) {
      super.renderTooltip(var1, var2, var3);
   }

   public void renderTooltip(List<String> var1, int var2, int var3) {
      super.renderTooltip(var1, var2, var3);
   }

   public void tick() {
      this.screen.tick();
      super.tick();
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public int fontLineHeight() {
      this.font.getClass();
      return 9;
   }

   public int fontWidth(String var1) {
      return this.font.width(var1);
   }

   public void fontDrawShadow(String var1, int var2, int var3, int var4) {
      this.font.drawShadow(var1, (float)var2, (float)var3, var4);
   }

   public List<String> fontSplit(String var1, int var2) {
      return this.font.split(var1, var2);
   }

   public void childrenClear() {
      this.children.clear();
   }

   public void addWidget(RealmsGuiEventListener var1) {
      if (this.hasWidget(var1) || !this.children.add(var1.getProxy())) {
         LOGGER.error("Tried to add the same widget multiple times: " + var1);
      }

   }

   public void narrateLabels() {
      List var1 = (List)this.children.stream().filter((var0) -> {
         return var0 instanceof RealmsLabelProxy;
      }).map((var0) -> {
         return ((RealmsLabelProxy)var0).getLabel().getText();
      }).collect(Collectors.toList());
      Realms.narrateNow((Iterable)var1);
   }

   public void removeWidget(RealmsGuiEventListener var1) {
      if (!this.hasWidget(var1) || !this.children.remove(var1.getProxy())) {
         LOGGER.error("Tried to add the same widget multiple times: " + var1);
      }

   }

   public boolean hasWidget(RealmsGuiEventListener var1) {
      return this.children.contains(var1.getProxy());
   }

   public void buttonsAdd(AbstractRealmsButton<?> var1) {
      this.addButton(var1.getProxy());
   }

   public List<AbstractRealmsButton<?>> buttons() {
      ArrayList var1 = Lists.newArrayListWithExpectedSize(this.buttons.size());
      Iterator var2 = this.buttons.iterator();

      while(var2.hasNext()) {
         AbstractWidget var3 = (AbstractWidget)var2.next();
         var1.add(((RealmsAbstractButtonProxy)var3).getButton());
      }

      return var1;
   }

   public void buttonsClear() {
      HashSet var1 = Sets.newHashSet(this.buttons);
      this.children.removeIf(var1::contains);
      this.buttons.clear();
   }

   public void removeButton(RealmsButton var1) {
      this.children.remove(var1.getProxy());
      this.buttons.remove(var1.getProxy());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.screen.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.screen.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.screen.mouseDragged(var1, var3, var5, var6, var8) ? true : super.mouseDragged(var1, var3, var5, var6, var8);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.screen.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return this.screen.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   public void removed() {
      this.screen.removed();
      super.removed();
   }

   public int draw(String var1, int var2, int var3, int var4, boolean var5) {
      return var5 ? this.font.drawShadow(var1, (float)var2, (float)var3, var4) : this.font.draw(var1, (float)var2, (float)var3, var4);
   }

   public Font getFont() {
      return this.font;
   }
}
