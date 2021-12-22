package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WinScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation EDITION_LOCATION = new ResourceLocation("textures/gui/title/edition.png");
   private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   private static final Component SECTION_HEADING;
   private static final String NAME_PREFIX = "           ";
   private static final String OBFUSCATE_TOKEN;
   private static final int LOGO_WIDTH = 274;
   private static final float SPEEDUP_FACTOR = 5.0F;
   private static final float SPEEDUP_FACTOR_FAST = 15.0F;
   private final boolean poem;
   private final Runnable onFinished;
   private float scroll;
   private List<FormattedCharSequence> lines;
   private IntSet centeredLines;
   private int totalScrollLength;
   private boolean speedupActive;
   private final IntSet speedupModifiers = new IntOpenHashSet();
   private float scrollSpeed;
   private final float unmodifiedScrollSpeed;

   public WinScreen(boolean var1, Runnable var2) {
      super(NarratorChatListener.NO_TITLE);
      this.poem = var1;
      this.onFinished = var2;
      if (!var1) {
         this.unmodifiedScrollSpeed = 0.75F;
      } else {
         this.unmodifiedScrollSpeed = 0.5F;
      }

      this.scrollSpeed = this.unmodifiedScrollSpeed;
   }

   private float calculateScrollSpeed() {
      return this.speedupActive ? this.unmodifiedScrollSpeed * (5.0F + (float)this.speedupModifiers.size() * 15.0F) : this.unmodifiedScrollSpeed;
   }

   public void tick() {
      this.minecraft.getMusicManager().tick();
      this.minecraft.getSoundManager().tick(false);
      float var1 = (float)(this.totalScrollLength + this.height + this.height + 24);
      if (this.scroll > var1) {
         this.respawn();
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 != 341 && var1 != 345) {
         if (var1 == 32) {
            this.speedupActive = true;
         }
      } else {
         this.speedupModifiers.add(var1);
      }

      this.scrollSpeed = this.calculateScrollSpeed();
      return super.keyPressed(var1, var2, var3);
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      if (var1 == 32) {
         this.speedupActive = false;
      } else if (var1 == 341 || var1 == 345) {
         this.speedupModifiers.remove(var1);
      }

      this.scrollSpeed = this.calculateScrollSpeed();
      return super.keyReleased(var1, var2, var3);
   }

   public void onClose() {
      this.respawn();
   }

   private void respawn() {
      this.onFinished.run();
      this.minecraft.setScreen((Screen)null);
   }

   protected void init() {
      if (this.lines == null) {
         this.lines = Lists.newArrayList();
         this.centeredLines = new IntOpenHashSet();
         if (this.poem) {
            this.wrapCreditsIO("texts/end.txt", this::addPoemFile);
         }

         this.wrapCreditsIO("texts/credits.json", this::addCreditsFile);
         if (this.poem) {
            this.wrapCreditsIO("texts/postcredits.txt", this::addPoemFile);
         }

         this.totalScrollLength = this.lines.size() * 12;
      }
   }

   private void wrapCreditsIO(String var1, WinScreen.CreditsReader var2) {
      Resource var3 = null;

      try {
         var3 = this.minecraft.getResourceManager().getResource(new ResourceLocation(var1));
         InputStreamReader var4 = new InputStreamReader(var3.getInputStream(), StandardCharsets.UTF_8);
         var2.read(var4);
      } catch (Exception var8) {
         LOGGER.error("Couldn't load credits", var8);
      } finally {
         IOUtils.closeQuietly(var3);
      }

   }

   private void addPoemFile(InputStreamReader var1) throws IOException {
      BufferedReader var2 = new BufferedReader(var1);
      Random var3 = new Random(8124371L);

      String var4;
      int var5;
      while((var4 = var2.readLine()) != null) {
         String var6;
         String var7;
         for(var4 = var4.replaceAll("PLAYERNAME", this.minecraft.getUser().getName()); (var5 = var4.indexOf(OBFUSCATE_TOKEN)) != -1; var4 = var6 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var3.nextInt(4) + 3) + var7) {
            var6 = var4.substring(0, var5);
            var7 = var4.substring(var5 + OBFUSCATE_TOKEN.length());
         }

         this.addPoemLines(var4);
         this.addEmptyLine();
      }

      for(var5 = 0; var5 < 8; ++var5) {
         this.addEmptyLine();
      }

   }

   private void addCreditsFile(InputStreamReader var1) {
      JsonArray var2 = GsonHelper.parseArray(var1);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         JsonElement var4 = (JsonElement)var3.next();
         JsonObject var5 = var4.getAsJsonObject();
         String var6 = var5.get("section").getAsString();
         this.addCreditsLine(SECTION_HEADING, true);
         this.addCreditsLine((new TextComponent(var6)).withStyle(ChatFormatting.YELLOW), true);
         this.addCreditsLine(SECTION_HEADING, true);
         this.addEmptyLine();
         this.addEmptyLine();
         JsonArray var7 = var5.getAsJsonArray("titles");
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            JsonElement var9 = (JsonElement)var8.next();
            JsonObject var10 = var9.getAsJsonObject();
            String var11 = var10.get("title").getAsString();
            JsonArray var12 = var10.getAsJsonArray("names");
            this.addCreditsLine((new TextComponent(var11)).withStyle(ChatFormatting.GRAY), false);
            Iterator var13 = var12.iterator();

            while(var13.hasNext()) {
               JsonElement var14 = (JsonElement)var13.next();
               String var15 = var14.getAsString();
               this.addCreditsLine((new TextComponent("           ")).append(var15).withStyle(ChatFormatting.WHITE), false);
            }

            this.addEmptyLine();
            this.addEmptyLine();
         }
      }

   }

   private void addEmptyLine() {
      this.lines.add(FormattedCharSequence.EMPTY);
   }

   private void addPoemLines(String var1) {
      this.lines.addAll(this.minecraft.font.split(new TextComponent(var1), 274));
   }

   private void addCreditsLine(Component var1, boolean var2) {
      if (var2) {
         this.centeredLines.add(this.lines.size());
      }

      this.lines.add(var1.getVisualOrderText());
   }

   private void renderBg() {
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
      int var1 = this.width;
      float var2 = -this.scroll * 0.5F;
      float var3 = (float)this.height - 0.5F * this.scroll;
      float var4 = 0.015625F;
      float var5 = this.scroll / this.unmodifiedScrollSpeed;
      float var6 = var5 * 0.02F;
      float var7 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.unmodifiedScrollSpeed;
      float var8 = (var7 - 20.0F - var5) * 0.005F;
      if (var8 < var6) {
         var6 = var8;
      }

      if (var6 > 1.0F) {
         var6 = 1.0F;
      }

      var6 *= var6;
      var6 = var6 * 96.0F / 255.0F;
      Tesselator var9 = Tesselator.getInstance();
      BufferBuilder var10 = var9.getBuilder();
      var10.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      var10.vertex(0.0D, (double)this.height, (double)this.getBlitOffset()).method_7(0.0F, var2 * 0.015625F).color(var6, var6, var6, 1.0F).endVertex();
      var10.vertex((double)var1, (double)this.height, (double)this.getBlitOffset()).method_7((float)var1 * 0.015625F, var2 * 0.015625F).color(var6, var6, var6, 1.0F).endVertex();
      var10.vertex((double)var1, 0.0D, (double)this.getBlitOffset()).method_7((float)var1 * 0.015625F, var3 * 0.015625F).color(var6, var6, var6, 1.0F).endVertex();
      var10.vertex(0.0D, 0.0D, (double)this.getBlitOffset()).method_7(0.0F, var3 * 0.015625F).color(var6, var6, var6, 1.0F).endVertex();
      var9.end();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.scroll += var4 * this.scrollSpeed;
      this.renderBg();
      int var5 = this.width / 2 - 137;
      int var6 = this.height + 50;
      float var7 = -this.scroll;
      var1.pushPose();
      var1.translate(0.0D, (double)var7, 0.0D);
      RenderSystem.setShaderTexture(0, LOGO_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      this.blitOutlineBlack(var5, var6, (var2x, var3x) -> {
         this.blit(var1, var2x + 0, var3x, 0, 0, 155, 44);
         this.blit(var1, var2x + 155, var3x, 0, 45, 155, 44);
      });
      RenderSystem.disableBlend();
      RenderSystem.setShaderTexture(0, EDITION_LOCATION);
      blit(var1, var5 + 88, var6 + 37, 0.0F, 0.0F, 98, 14, 128, 16);
      int var8 = var6 + 100;

      int var9;
      for(var9 = 0; var9 < this.lines.size(); ++var9) {
         if (var9 == this.lines.size() - 1) {
            float var10 = (float)var8 + var7 - (float)(this.height / 2 - 6);
            if (var10 < 0.0F) {
               var1.translate(0.0D, (double)(-var10), 0.0D);
            }
         }

         if ((float)var8 + var7 + 12.0F + 8.0F > 0.0F && (float)var8 + var7 < (float)this.height) {
            FormattedCharSequence var13 = (FormattedCharSequence)this.lines.get(var9);
            if (this.centeredLines.contains(var9)) {
               this.font.drawShadow(var1, var13, (float)(var5 + (274 - this.font.width(var13)) / 2), (float)var8, 16777215);
            } else {
               this.font.drawShadow(var1, var13, (float)var5, (float)var8, 16777215);
            }
         }

         var8 += 12;
      }

      var1.popPose();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
      var9 = this.width;
      int var14 = this.height;
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      var12.vertex(0.0D, (double)var14, (double)this.getBlitOffset()).method_7(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.vertex((double)var9, (double)var14, (double)this.getBlitOffset()).method_7(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.vertex((double)var9, 0.0D, (double)this.getBlitOffset()).method_7(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.vertex(0.0D, 0.0D, (double)this.getBlitOffset()).method_7(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var11.end();
      RenderSystem.disableBlend();
      super.render(var1, var2, var3, var4);
   }

   static {
      SECTION_HEADING = (new TextComponent("============")).withStyle(ChatFormatting.WHITE);
      OBFUSCATE_TOKEN = ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + ChatFormatting.GREEN + ChatFormatting.AQUA;
   }

   @FunctionalInterface
   private interface CreditsReader {
      void read(InputStreamReader var1) throws IOException;
   }
}
