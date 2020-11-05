package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TickableWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Screen extends AbstractContainerEventHandler implements TickableWidget, Widget {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<String> ALLOWED_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   protected final Component title;
   protected final List<GuiEventListener> children = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   protected final List<AbstractWidget> buttons = Lists.newArrayList();
   public boolean passEvents;
   protected Font font;
   private URI clickedLink;

   protected Screen(Component var1) {
      super();
      this.title = var1;
   }

   public Component getTitle() {
      return this.title;
   }

   public String getNarrationMessage() {
      return this.getTitle().getString();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      for(int var5 = 0; var5 < this.buttons.size(); ++var5) {
         ((AbstractWidget)this.buttons.get(var5)).render(var1, var2, var3, var4);
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if (var1 == 258) {
         boolean var4 = !hasShiftDown();
         if (!this.changeFocus(var4)) {
            this.changeFocus(var4);
         }

         return false;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.setScreen((Screen)null);
   }

   protected <T extends AbstractWidget> T addButton(T var1) {
      this.buttons.add(var1);
      return (AbstractWidget)this.addWidget(var1);
   }

   protected <T extends GuiEventListener> T addWidget(T var1) {
      this.children.add(var1);
      return var1;
   }

   protected void renderTooltip(PoseStack var1, ItemStack var2, int var3, int var4) {
      this.renderComponentTooltip(var1, this.getTooltipFromItem(var2), var3, var4);
   }

   public List<Component> getTooltipFromItem(ItemStack var1) {
      return var1.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
   }

   public void renderTooltip(PoseStack var1, Component var2, int var3, int var4) {
      this.renderTooltip(var1, Arrays.asList(var2.getVisualOrderText()), var3, var4);
   }

   public void renderComponentTooltip(PoseStack var1, List<Component> var2, int var3, int var4) {
      this.renderTooltip(var1, Lists.transform(var2, Component::getVisualOrderText), var3, var4);
   }

   public void renderTooltip(PoseStack var1, List<? extends FormattedCharSequence> var2, int var3, int var4) {
      if (!var2.isEmpty()) {
         int var5 = 0;
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            FormattedCharSequence var7 = (FormattedCharSequence)var6.next();
            int var8 = this.font.width(var7);
            if (var8 > var5) {
               var5 = var8;
            }
         }

         int var20 = var3 + 12;
         int var21 = var4 - 12;
         int var9 = 8;
         if (var2.size() > 1) {
            var9 += 2 + (var2.size() - 1) * 10;
         }

         if (var20 + var5 > this.width) {
            var20 -= 28 + var5;
         }

         if (var21 + var9 + 6 > this.height) {
            var21 = this.height - var9 - 6;
         }

         var1.pushPose();
         int var10 = -267386864;
         int var11 = 1347420415;
         int var12 = 1344798847;
         boolean var13 = true;
         Tesselator var14 = Tesselator.getInstance();
         BufferBuilder var15 = var14.getBuilder();
         var15.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         Matrix4f var16 = var1.last().pose();
         fillGradient(var16, var15, var20 - 3, var21 - 4, var20 + var5 + 3, var21 - 3, 400, -267386864, -267386864);
         fillGradient(var16, var15, var20 - 3, var21 + var9 + 3, var20 + var5 + 3, var21 + var9 + 4, 400, -267386864, -267386864);
         fillGradient(var16, var15, var20 - 3, var21 - 3, var20 + var5 + 3, var21 + var9 + 3, 400, -267386864, -267386864);
         fillGradient(var16, var15, var20 - 4, var21 - 3, var20 - 3, var21 + var9 + 3, 400, -267386864, -267386864);
         fillGradient(var16, var15, var20 + var5 + 3, var21 - 3, var20 + var5 + 4, var21 + var9 + 3, 400, -267386864, -267386864);
         fillGradient(var16, var15, var20 - 3, var21 - 3 + 1, var20 - 3 + 1, var21 + var9 + 3 - 1, 400, 1347420415, 1344798847);
         fillGradient(var16, var15, var20 + var5 + 2, var21 - 3 + 1, var20 + var5 + 3, var21 + var9 + 3 - 1, 400, 1347420415, 1344798847);
         fillGradient(var16, var15, var20 - 3, var21 - 3, var20 + var5 + 3, var21 - 3 + 1, 400, 1347420415, 1347420415);
         fillGradient(var16, var15, var20 - 3, var21 + var9 + 2, var20 + var5 + 3, var21 + var9 + 3, 400, 1344798847, 1344798847);
         RenderSystem.enableDepthTest();
         RenderSystem.disableTexture();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.shadeModel(7425);
         var15.end();
         BufferUploader.end(var15);
         RenderSystem.shadeModel(7424);
         RenderSystem.disableBlend();
         RenderSystem.enableTexture();
         MultiBufferSource.BufferSource var17 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         var1.translate(0.0D, 0.0D, 400.0D);

         for(int var18 = 0; var18 < var2.size(); ++var18) {
            FormattedCharSequence var19 = (FormattedCharSequence)var2.get(var18);
            if (var19 != null) {
               this.font.drawInBatch((FormattedCharSequence)var19, (float)var20, (float)var21, -1, true, var16, var17, false, 0, 15728880);
            }

            if (var18 == 0) {
               var21 += 2;
            }

            var21 += 10;
         }

         var17.endBatch();
         var1.popPose();
      }
   }

   protected void renderComponentHoverEffect(PoseStack var1, @Nullable Style var2, int var3, int var4) {
      if (var2 != null && var2.getHoverEvent() != null) {
         HoverEvent var5 = var2.getHoverEvent();
         HoverEvent.ItemStackInfo var6 = (HoverEvent.ItemStackInfo)var5.getValue(HoverEvent.Action.SHOW_ITEM);
         if (var6 != null) {
            this.renderTooltip(var1, var6.getItemStack(), var3, var4);
         } else {
            HoverEvent.EntityTooltipInfo var7 = (HoverEvent.EntityTooltipInfo)var5.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (var7 != null) {
               if (this.minecraft.options.advancedItemTooltips) {
                  this.renderComponentTooltip(var1, var7.getTooltipLines(), var3, var4);
               }
            } else {
               Component var8 = (Component)var5.getValue(HoverEvent.Action.SHOW_TEXT);
               if (var8 != null) {
                  this.renderTooltip(var1, this.minecraft.font.split(var8, Math.max(this.width / 2, 200)), var3, var4);
               }
            }
         }

      }
   }

   protected void insertText(String var1, boolean var2) {
   }

   public boolean handleComponentClicked(@Nullable Style var1) {
      if (var1 == null) {
         return false;
      } else {
         ClickEvent var2 = var1.getClickEvent();
         if (hasShiftDown()) {
            if (var1.getInsertion() != null) {
               this.insertText(var1.getInsertion(), false);
            }
         } else if (var2 != null) {
            URI var3;
            if (var2.getAction() == ClickEvent.Action.OPEN_URL) {
               if (!this.minecraft.options.chatLinks) {
                  return false;
               }

               try {
                  var3 = new URI(var2.getValue());
                  String var4 = var3.getScheme();
                  if (var4 == null) {
                     throw new URISyntaxException(var2.getValue(), "Missing protocol");
                  }

                  if (!ALLOWED_PROTOCOLS.contains(var4.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(var2.getValue(), "Unsupported protocol: " + var4.toLowerCase(Locale.ROOT));
                  }

                  if (this.minecraft.options.chatLinksPrompt) {
                     this.clickedLink = var3;
                     this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, var2.getValue(), false));
                  } else {
                     this.openLink(var3);
                  }
               } catch (URISyntaxException var5) {
                  LOGGER.error("Can't open url for {}", var2, var5);
               }
            } else if (var2.getAction() == ClickEvent.Action.OPEN_FILE) {
               var3 = (new File(var2.getValue())).toURI();
               this.openLink(var3);
            } else if (var2.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(var2.getValue(), true);
            } else if (var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendMessage(var2.getValue(), false);
            } else if (var2.getAction() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
               this.minecraft.keyboardHandler.setClipboard(var2.getValue());
            } else {
               LOGGER.error("Don't know how to handle {}", var2);
            }

            return true;
         }

         return false;
      }
   }

   public void sendMessage(String var1) {
      this.sendMessage(var1, true);
   }

   public void sendMessage(String var1, boolean var2) {
      if (var2) {
         this.minecraft.gui.getChat().addRecentChat(var1);
      }

      this.minecraft.player.chat(var1);
   }

   public void init(Minecraft var1, int var2, int var3) {
      this.minecraft = var1;
      this.itemRenderer = var1.getItemRenderer();
      this.font = var1.font;
      this.width = var2;
      this.height = var3;
      this.buttons.clear();
      this.children.clear();
      this.setFocused((GuiEventListener)null);
      this.init();
   }

   public List<? extends GuiEventListener> children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground(PoseStack var1) {
      this.renderBackground(var1, 0);
   }

   public void renderBackground(PoseStack var1, int var2) {
      if (this.minecraft.level != null) {
         this.fillGradient(var1, 0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.renderDirtBackground(var2);
      }

   }

   public void renderDirtBackground(int var1) {
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var4 = 32.0F;
      var3.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      var3.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)this.height / 32.0F + (float)var1).color(64, 64, 64, 255).endVertex();
      var3.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)var1).color(64, 64, 64, 255).endVertex();
      var3.vertex((double)this.width, 0.0D, 0.0D).uv((float)this.width / 32.0F, (float)var1).color(64, 64, 64, 255).endVertex();
      var3.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float)var1).color(64, 64, 64, 255).endVertex();
      var2.end();
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean var1) {
      if (var1) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.setScreen(this);
   }

   private void openLink(URI var1) {
      Util.getPlatform().openUri(var1);
   }

   public static boolean hasControlDown() {
      if (Minecraft.ON_OSX) {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347);
      } else {
         return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
      }
   }

   public static boolean hasShiftDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344);
   }

   public static boolean hasAltDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
   }

   public static boolean isCut(int var0) {
      return var0 == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int var0) {
      return var0 == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int var0) {
      return var0 == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int var0) {
      return var0 == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public void resize(Minecraft var1, int var2, int var3) {
      this.init(var1, var2, var3);
   }

   public static void wrapScreenError(Runnable var0, String var1, String var2) {
      try {
         var0.run();
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, var1);
         CrashReportCategory var5 = var4.addCategory("Affected screen");
         var5.setDetail("Screen name", () -> {
            return var2;
         });
         throw new ReportedException(var4);
      }
   }

   protected boolean isValidCharacterForName(String var1, char var2, int var3) {
      int var4 = var1.indexOf(58);
      int var5 = var1.indexOf(47);
      if (var2 == ':') {
         return (var5 == -1 || var3 <= var5) && var4 == -1;
      } else if (var2 == '/') {
         return var3 > var4;
      } else {
         return var2 == '_' || var2 == '-' || var2 >= 'a' && var2 <= 'z' || var2 >= '0' && var2 <= '9' || var2 == '.';
      }
   }

   public boolean isMouseOver(double var1, double var3) {
      return true;
   }

   public void onFilesDrop(List<Path> var1) {
   }
}
