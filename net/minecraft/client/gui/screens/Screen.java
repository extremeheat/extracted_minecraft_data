package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Matrix4f;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Screen extends AbstractContainerEventHandler implements Widget {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set ALLOWED_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   protected final Component title;
   protected final List children = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   protected final List buttons = Lists.newArrayList();
   public boolean passEvents;
   protected Font font;
   private URI clickedLink;

   protected Screen(Component var1) {
      this.title = var1;
   }

   public Component getTitle() {
      return this.title;
   }

   public String getNarrationMessage() {
      return this.getTitle().getString();
   }

   public void render(int var1, int var2, float var3) {
      for(int var4 = 0; var4 < this.buttons.size(); ++var4) {
         ((AbstractWidget)this.buttons.get(var4)).render(var1, var2, var3);
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

         return true;
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

   protected AbstractWidget addButton(AbstractWidget var1) {
      this.buttons.add(var1);
      this.children.add(var1);
      return var1;
   }

   protected void renderTooltip(ItemStack var1, int var2, int var3) {
      this.renderTooltip(this.getTooltipFromItem(var1), var2, var3);
   }

   public List getTooltipFromItem(ItemStack var1) {
      List var2 = var1.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         Component var5 = (Component)var4.next();
         var3.add(var5.getColoredString());
      }

      return var3;
   }

   public void renderTooltip(String var1, int var2, int var3) {
      this.renderTooltip(Arrays.asList(var1), var2, var3);
   }

   public void renderTooltip(List var1, int var2, int var3) {
      if (!var1.isEmpty()) {
         RenderSystem.disableRescaleNormal();
         RenderSystem.disableDepthTest();
         int var4 = 0;
         Iterator var5 = var1.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            int var7 = this.font.width(var6);
            if (var7 > var4) {
               var4 = var7;
            }
         }

         int var17 = var2 + 12;
         int var18 = var3 - 12;
         int var8 = 8;
         if (var1.size() > 1) {
            var8 += 2 + (var1.size() - 1) * 10;
         }

         if (var17 + var4 > this.width) {
            var17 -= 28 + var4;
         }

         if (var18 + var8 + 6 > this.height) {
            var18 = this.height - var8 - 6;
         }

         this.setBlitOffset(300);
         this.itemRenderer.blitOffset = 300.0F;
         int var9 = -267386864;
         this.fillGradient(var17 - 3, var18 - 4, var17 + var4 + 3, var18 - 3, -267386864, -267386864);
         this.fillGradient(var17 - 3, var18 + var8 + 3, var17 + var4 + 3, var18 + var8 + 4, -267386864, -267386864);
         this.fillGradient(var17 - 3, var18 - 3, var17 + var4 + 3, var18 + var8 + 3, -267386864, -267386864);
         this.fillGradient(var17 - 4, var18 - 3, var17 - 3, var18 + var8 + 3, -267386864, -267386864);
         this.fillGradient(var17 + var4 + 3, var18 - 3, var17 + var4 + 4, var18 + var8 + 3, -267386864, -267386864);
         int var10 = 1347420415;
         int var11 = 1344798847;
         this.fillGradient(var17 - 3, var18 - 3 + 1, var17 - 3 + 1, var18 + var8 + 3 - 1, 1347420415, 1344798847);
         this.fillGradient(var17 + var4 + 2, var18 - 3 + 1, var17 + var4 + 3, var18 + var8 + 3 - 1, 1347420415, 1344798847);
         this.fillGradient(var17 - 3, var18 - 3, var17 + var4 + 3, var18 - 3 + 1, 1347420415, 1347420415);
         this.fillGradient(var17 - 3, var18 + var8 + 2, var17 + var4 + 3, var18 + var8 + 3, 1344798847, 1344798847);
         PoseStack var12 = new PoseStack();
         MultiBufferSource.BufferSource var13 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         var12.translate(0.0D, 0.0D, (double)this.itemRenderer.blitOffset);
         Matrix4f var14 = var12.last().pose();

         for(int var15 = 0; var15 < var1.size(); ++var15) {
            String var16 = (String)var1.get(var15);
            if (var16 != null) {
               this.font.drawInBatch(var16, (float)var17, (float)var18, -1, true, var14, var13, false, 0, 15728880);
            }

            if (var15 == 0) {
               var18 += 2;
            }

            var18 += 10;
         }

         var13.endBatch();
         this.setBlitOffset(0);
         this.itemRenderer.blitOffset = 0.0F;
         RenderSystem.enableDepthTest();
         RenderSystem.enableRescaleNormal();
      }
   }

   protected void renderComponentHoverEffect(Component var1, int var2, int var3) {
      if (var1 != null && var1.getStyle().getHoverEvent() != null) {
         HoverEvent var4 = var1.getStyle().getHoverEvent();
         if (var4.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack var5 = ItemStack.EMPTY;

            try {
               CompoundTag var6 = TagParser.parseTag(var4.getValue().getString());
               if (var6 instanceof CompoundTag) {
                  var5 = ItemStack.of((CompoundTag)var6);
               }
            } catch (CommandSyntaxException var10) {
            }

            if (var5.isEmpty()) {
               this.renderTooltip(ChatFormatting.RED + "Invalid Item!", var2, var3);
            } else {
               this.renderTooltip(var5, var2, var3);
            }
         } else if (var4.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.minecraft.options.advancedItemTooltips) {
               try {
                  CompoundTag var11 = TagParser.parseTag(var4.getValue().getString());
                  ArrayList var12 = Lists.newArrayList();
                  Component var7 = Component.Serializer.fromJson(var11.getString("name"));
                  if (var7 != null) {
                     var12.add(var7.getColoredString());
                  }

                  if (var11.contains("type", 8)) {
                     String var8 = var11.getString("type");
                     var12.add("Type: " + var8);
                  }

                  var12.add(var11.getString("id"));
                  this.renderTooltip((List)var12, var2, var3);
               } catch (CommandSyntaxException | JsonSyntaxException var9) {
                  this.renderTooltip(ChatFormatting.RED + "Invalid Entity!", var2, var3);
               }
            }
         } else if (var4.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.font.split(var4.getValue().getColoredString(), Math.max(this.width / 2, 200)), var2, var3);
         }

      }
   }

   protected void insertText(String var1, boolean var2) {
   }

   public boolean handleComponentClicked(Component var1) {
      if (var1 == null) {
         return false;
      } else {
         ClickEvent var2 = var1.getStyle().getClickEvent();
         if (hasShiftDown()) {
            if (var1.getStyle().getInsertion() != null) {
               this.insertText(var1.getStyle().getInsertion(), false);
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

   public void setSize(int var1, int var2) {
      this.width = var1;
      this.height = var2;
   }

   public List children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground() {
      this.renderBackground(0);
   }

   public void renderBackground(int var1) {
      if (this.minecraft.level != null) {
         this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.renderDirtBackground(var1);
      }

   }

   public void renderDirtBackground(int var1) {
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var4 = 32.0F;
      var3.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
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
}
