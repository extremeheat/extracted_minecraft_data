package net.minecraft.realms;

import com.google.common.util.concurrent.ListenableFuture;
import com.mojang.util.UUIDTypeAdapter;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class RealmsScreen extends RealmsGuiEventListener {
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
   private final GuiScreenRealmsProxy proxy = new GuiScreenRealmsProxy(this);

   public RealmsScreen() {
      super();
   }

   public GuiScreenRealmsProxy getProxy() {
      return this.proxy;
   }

   public void init() {
   }

   public void init(Minecraft var1, int var2, int var3) {
      this.minecraft = var1;
   }

   public void drawCenteredString(String var1, int var2, int var3, int var4) {
      this.proxy.func_154325_a(var1, var2, var3, var4);
   }

   public int draw(String var1, int var2, int var3, int var4, boolean var5) {
      return this.proxy.func_209208_b(var1, var2, var3, var4, var5);
   }

   public void drawString(String var1, int var2, int var3, int var4) {
      this.drawString(var1, var2, var3, var4, true);
   }

   public void drawString(String var1, int var2, int var3, int var4, boolean var5) {
      this.proxy.func_207734_a(var1, var2, var3, var4, false);
   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.proxy.func_73729_b(var1, var2, var3, var4, var5, var6);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, float var8, float var9) {
      Gui.func_152125_a(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, float var6, float var7) {
      Gui.func_146110_a(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.proxy.func_73733_a(var1, var2, var3, var4, var5, var6);
   }

   public void renderBackground() {
      this.proxy.func_146276_q_();
   }

   public boolean isPauseScreen() {
      return this.proxy.func_73868_f();
   }

   public void renderBackground(int var1) {
      this.proxy.func_146270_b(var1);
   }

   public void render(int var1, int var2, float var3) {
      for(int var4 = 0; var4 < this.proxy.func_154320_j().size(); ++var4) {
         ((RealmsButton)this.proxy.func_154320_j().get(var4)).render(var1, var2, var3);
      }

   }

   public void renderTooltip(ItemStack var1, int var2, int var3) {
      this.proxy.func_146285_a(var1, var2, var3);
   }

   public void renderTooltip(String var1, int var2, int var3) {
      this.proxy.func_146279_a(var1, var2, var3);
   }

   public void renderTooltip(List<String> var1, int var2, int var3) {
      this.proxy.func_146283_a(var1, var2, var3);
   }

   public static void bindFace(String var0, String var1) {
      ResourceLocation var2 = AbstractClientPlayer.func_110311_f(var1);
      if (var2 == null) {
         var2 = DefaultPlayerSkin.func_177334_a(UUIDTypeAdapter.fromString(var0));
      }

      AbstractClientPlayer.func_110304_a(var2, var1);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(var2);
   }

   public static void bind(String var0) {
      ResourceLocation var1 = new ResourceLocation(var0);
      Minecraft.func_71410_x().func_110434_K().func_110577_a(var1);
   }

   public void tick() {
   }

   public int width() {
      return this.proxy.field_146294_l;
   }

   public int height() {
      return this.proxy.field_146295_m;
   }

   public ListenableFuture<Object> threadSafeSetScreen(RealmsScreen var1) {
      return this.minecraft.func_152344_a(() -> {
         Realms.setScreen(var1);
      });
   }

   public int fontLineHeight() {
      return this.proxy.func_154329_h();
   }

   public int fontWidth(String var1) {
      return this.proxy.func_207731_c(var1);
   }

   public void fontDrawShadow(String var1, int var2, int var3, int var4) {
      this.proxy.func_207728_b(var1, var2, var3, var4);
   }

   public List<String> fontSplit(String var1, int var2) {
      return this.proxy.func_154323_a(var1, var2);
   }

   public void childrenClear() {
      this.proxy.func_207735_j();
   }

   public void addWidget(RealmsGuiEventListener var1) {
      this.proxy.func_207730_a(var1);
   }

   public void removeWidget(RealmsGuiEventListener var1) {
      this.proxy.func_207733_b(var1);
   }

   public boolean hasWidget(RealmsGuiEventListener var1) {
      return this.proxy.func_212332_c(var1);
   }

   public void buttonsAdd(RealmsButton var1) {
      this.proxy.func_154327_a(var1);
   }

   public List<RealmsButton> buttons() {
      return this.proxy.func_154320_j();
   }

   protected void buttonsClear() {
      this.proxy.func_207729_m();
   }

   protected void focusOn(RealmsGuiEventListener var1) {
      this.proxy.func_205725_b(var1.getProxy());
   }

   public void focusNext() {
      this.proxy.func_207714_t();
   }

   public RealmsEditBox newEditBox(int var1, int var2, int var3, int var4, int var5) {
      return new RealmsEditBox(var1, var2, var3, var4, var5);
   }

   public void confirmResult(boolean var1, int var2) {
   }

   public static String getLocalizedString(String var0) {
      return I18n.func_135052_a(var0);
   }

   public static String getLocalizedString(String var0, Object... var1) {
      return I18n.func_135052_a(var0, var1);
   }

   public List<String> getLocalizedStringWithLineWidth(String var1, int var2) {
      return this.minecraft.field_71466_p.func_78271_c(I18n.func_135052_a(var1), var2);
   }

   public RealmsAnvilLevelStorageSource getLevelStorageSource() {
      return new RealmsAnvilLevelStorageSource(Minecraft.func_71410_x().func_71359_d());
   }

   public void removed() {
   }

   protected void removeButton(RealmsButton var1) {
      this.proxy.func_207732_b(var1);
   }

   protected void setKeyboardHandlerSendRepeatsToGui(boolean var1) {
      this.minecraft.field_195559_v.func_197967_a(var1);
   }

   protected boolean isKeyDown(int var1) {
      return InputMappings.func_197956_a(var1);
   }
}
