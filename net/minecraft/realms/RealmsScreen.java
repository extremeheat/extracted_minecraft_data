package net.minecraft.realms;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;

public abstract class RealmsScreen extends RealmsGuiEventListener implements RealmsConfirmResultListener {
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;
   private Minecraft minecraft;
   public int width;
   public int height;
   private final RealmsScreenProxy proxy = new RealmsScreenProxy(this);

   public RealmsScreenProxy getProxy() {
      return this.proxy;
   }

   public void init() {
   }

   public void init(Minecraft var1, int var2, int var3) {
      this.minecraft = var1;
   }

   public void drawCenteredString(String var1, int var2, int var3, int var4) {
      this.proxy.drawCenteredString(var1, var2, var3, var4);
   }

   public int draw(String var1, int var2, int var3, int var4, boolean var5) {
      return this.proxy.draw(var1, var2, var3, var4, var5);
   }

   public void drawString(String var1, int var2, int var3, int var4) {
      this.drawString(var1, var2, var3, var4, true);
   }

   public void drawString(String var1, int var2, int var3, int var4, boolean var5) {
      this.proxy.drawString(var1, var2, var3, var4, false);
   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.proxy.blit(var1, var2, var3, var4, var5, var6);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      GuiComponent.blit(var0, var1, var6, var7, var2, var3, var4, var5, var8, var9);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7) {
      GuiComponent.blit(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.proxy.fillGradient(var1, var2, var3, var4, var5, var6);
   }

   public void renderBackground() {
      this.proxy.renderBackground();
   }

   public boolean isPauseScreen() {
      return this.proxy.isPauseScreen();
   }

   public void renderBackground(int var1) {
      this.proxy.renderBackground(var1);
   }

   public void render(int var1, int var2, float var3) {
      for(int var4 = 0; var4 < this.proxy.buttons().size(); ++var4) {
         ((AbstractRealmsButton)this.proxy.buttons().get(var4)).render(var1, var2, var3);
      }

   }

   public void renderTooltip(ItemStack var1, int var2, int var3) {
      this.proxy.renderTooltip(var1, var2, var3);
   }

   public void renderTooltip(String var1, int var2, int var3) {
      this.proxy.renderTooltip(var1, var2, var3);
   }

   public void renderTooltip(List var1, int var2, int var3) {
      this.proxy.renderTooltip(var1, var2, var3);
   }

   public static void bind(String var0) {
      Realms.bind(var0);
   }

   public void tick() {
      this.tickButtons();
   }

   protected void tickButtons() {
      Iterator var1 = this.buttons().iterator();

      while(var1.hasNext()) {
         AbstractRealmsButton var2 = (AbstractRealmsButton)var1.next();
         var2.tick();
      }

   }

   public int width() {
      return this.proxy.width;
   }

   public int height() {
      return this.proxy.height;
   }

   public int fontLineHeight() {
      return this.proxy.fontLineHeight();
   }

   public int fontWidth(String var1) {
      return this.proxy.fontWidth(var1);
   }

   public void fontDrawShadow(String var1, int var2, int var3, int var4) {
      this.proxy.fontDrawShadow(var1, var2, var3, var4);
   }

   public List fontSplit(String var1, int var2) {
      return this.proxy.fontSplit(var1, var2);
   }

   public void childrenClear() {
      this.proxy.childrenClear();
   }

   public void addWidget(RealmsGuiEventListener var1) {
      this.proxy.addWidget(var1);
   }

   public void removeWidget(RealmsGuiEventListener var1) {
      this.proxy.removeWidget(var1);
   }

   public boolean hasWidget(RealmsGuiEventListener var1) {
      return this.proxy.hasWidget(var1);
   }

   public void buttonsAdd(AbstractRealmsButton var1) {
      this.proxy.buttonsAdd(var1);
   }

   public List buttons() {
      return this.proxy.buttons();
   }

   protected void buttonsClear() {
      this.proxy.buttonsClear();
   }

   protected void focusOn(RealmsGuiEventListener var1) {
      this.proxy.magicalSpecialHackyFocus(var1.getProxy());
   }

   public RealmsEditBox newEditBox(int var1, int var2, int var3, int var4, int var5) {
      return this.newEditBox(var1, var2, var3, var4, var5, "");
   }

   public RealmsEditBox newEditBox(int var1, int var2, int var3, int var4, int var5, String var6) {
      return new RealmsEditBox(var1, var2, var3, var4, var5, var6);
   }

   public void confirmResult(boolean var1, int var2) {
   }

   public static String getLocalizedString(String var0) {
      return Realms.getLocalizedString(var0);
   }

   public static String getLocalizedString(String var0, Object... var1) {
      return Realms.getLocalizedString(var0, var1);
   }

   public List getLocalizedStringWithLineWidth(String var1, int var2) {
      return this.minecraft.font.split(I18n.get(var1), var2);
   }

   public RealmsAnvilLevelStorageSource getLevelStorageSource() {
      return new RealmsAnvilLevelStorageSource(Minecraft.getInstance().getLevelSource());
   }

   public void removed() {
   }

   protected void removeButton(RealmsButton var1) {
      this.proxy.removeButton(var1);
   }

   protected void setKeyboardHandlerSendRepeatsToGui(boolean var1) {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(var1);
   }

   protected boolean isKeyDown(int var1) {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), var1);
   }

   protected void narrateLabels() {
      this.getProxy().narrateLabels();
   }

   public boolean isFocused(RealmsGuiEventListener var1) {
      return this.getProxy().getFocused() == var1.getProxy();
   }
}
