package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class WinScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/credits_vignette.png");
   private static final Component SECTION_HEADING = Component.literal("============").withStyle(ChatFormatting.WHITE);
   private static final String NAME_PREFIX = "           ";
   private static final String OBFUSCATE_TOKEN = "" + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + ChatFormatting.GREEN + ChatFormatting.AQUA;
   private static final float SPEEDUP_FACTOR = 5.0F;
   private static final float SPEEDUP_FACTOR_FAST = 15.0F;
   private static final ResourceLocation END_POEM_LOCATION = ResourceLocation.withDefaultNamespace("texts/end.txt");
   private static final ResourceLocation CREDITS_LOCATION = ResourceLocation.withDefaultNamespace("texts/credits.json");
   private static final ResourceLocation POSTCREDITS_LOCATION = ResourceLocation.withDefaultNamespace("texts/postcredits.txt");
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
   private int direction;
   private final LogoRenderer logoRenderer = new LogoRenderer(false);

   public WinScreen(boolean var1, Runnable var2) {
      super(GameNarrator.NO_TITLE);
      this.poem = var1;
      this.onFinished = var2;
      if (!var1) {
         this.unmodifiedScrollSpeed = 0.75F;
      } else {
         this.unmodifiedScrollSpeed = 0.5F;
      }

      this.direction = 1;
      this.scrollSpeed = this.unmodifiedScrollSpeed;
   }

   private float calculateScrollSpeed() {
      return this.speedupActive
         ? this.unmodifiedScrollSpeed * (5.0F + (float)this.speedupModifiers.size() * 15.0F) * (float)this.direction
         : this.unmodifiedScrollSpeed * (float)this.direction;
   }

   @Override
   public void tick() {
      this.minecraft.getMusicManager().tick();
      this.minecraft.getSoundManager().tick(false);
      float var1 = (float)(this.totalScrollLength + this.height + this.height + 24);
      if (this.scroll > var1) {
         this.respawn();
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 265) {
         this.direction = -1;
      } else if (var1 == 341 || var1 == 345) {
         this.speedupModifiers.add(var1);
      } else if (var1 == 32) {
         this.speedupActive = true;
      }

      this.scrollSpeed = this.calculateScrollSpeed();
      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean keyReleased(int var1, int var2, int var3) {
      if (var1 == 265) {
         this.direction = 1;
      }

      if (var1 == 32) {
         this.speedupActive = false;
      } else if (var1 == 341 || var1 == 345) {
         this.speedupModifiers.remove(var1);
      }

      this.scrollSpeed = this.calculateScrollSpeed();
      return super.keyReleased(var1, var2, var3);
   }

   @Override
   public void onClose() {
      this.respawn();
   }

   private void respawn() {
      this.onFinished.run();
   }

   @Override
   protected void init() {
      if (this.lines == null) {
         this.lines = Lists.newArrayList();
         this.centeredLines = new IntOpenHashSet();
         if (this.poem) {
            this.wrapCreditsIO(END_POEM_LOCATION, this::addPoemFile);
         }

         this.wrapCreditsIO(CREDITS_LOCATION, this::addCreditsFile);
         if (this.poem) {
            this.wrapCreditsIO(POSTCREDITS_LOCATION, this::addPoemFile);
         }

         this.totalScrollLength = this.lines.size() * 12;
      }
   }

   private void wrapCreditsIO(ResourceLocation var1, WinScreen.CreditsReader var2) {
      try (BufferedReader var3 = this.minecraft.getResourceManager().openAsReader(var1)) {
         var2.read(var3);
      } catch (Exception var8) {
         LOGGER.error("Couldn't load credits from file {}", var1, var8);
      }
   }

   private void addPoemFile(Reader var1) throws IOException {
      BufferedReader var2 = new BufferedReader(var1);
      RandomSource var3 = RandomSource.create(8124371L);

      String var4;
      while ((var4 = var2.readLine()) != null) {
         var4 = var4.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());

         int var5;
         while ((var5 = var4.indexOf(OBFUSCATE_TOKEN)) != -1) {
            Object var6 = var4.substring(0, var5);
            String var7 = var4.substring(var5 + OBFUSCATE_TOKEN.length());
            var4 = var6 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var3.nextInt(4) + 3) + var7;
         }

         this.addPoemLines(var4);
         this.addEmptyLine();
      }

      for (int var9 = 0; var9 < 8; var9++) {
         this.addEmptyLine();
      }
   }

   private void addCreditsFile(Reader var1) {
      for (JsonElement var4 : GsonHelper.parseArray(var1)) {
         JsonObject var5 = var4.getAsJsonObject();
         String var6 = var5.get("section").getAsString();
         this.addCreditsLine(SECTION_HEADING, true);
         this.addCreditsLine(Component.literal(var6).withStyle(ChatFormatting.YELLOW), true);
         this.addCreditsLine(SECTION_HEADING, true);
         this.addEmptyLine();
         this.addEmptyLine();

         for (JsonElement var9 : var5.getAsJsonArray("disciplines")) {
            JsonObject var10 = var9.getAsJsonObject();
            String var11 = var10.get("discipline").getAsString();
            if (StringUtils.isNotEmpty(var11)) {
               this.addCreditsLine(Component.literal(var11).withStyle(ChatFormatting.YELLOW), true);
               this.addEmptyLine();
               this.addEmptyLine();
            }

            for (JsonElement var14 : var10.getAsJsonArray("titles")) {
               JsonObject var15 = var14.getAsJsonObject();
               String var16 = var15.get("title").getAsString();
               JsonArray var17 = var15.getAsJsonArray("names");
               this.addCreditsLine(Component.literal(var16).withStyle(ChatFormatting.GRAY), false);

               for (JsonElement var19 : var17) {
                  String var20 = var19.getAsString();
                  this.addCreditsLine(Component.literal("           ").append(var20).withStyle(ChatFormatting.WHITE), false);
               }

               this.addEmptyLine();
               this.addEmptyLine();
            }
         }
      }
   }

   private void addEmptyLine() {
      this.lines.add(FormattedCharSequence.EMPTY);
   }

   private void addPoemLines(String var1) {
      this.lines.addAll(this.minecraft.font.split(Component.literal(var1), 256));
   }

   private void addCreditsLine(Component var1, boolean var2) {
      if (var2) {
         this.centeredLines.add(this.lines.size());
      }

      this.lines.add(var1.getVisualOrderText());
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderVignette(var1);
      this.scroll = Math.max(0.0F, this.scroll + var4 * this.scrollSpeed);
      int var5 = this.width / 2 - 128;
      int var6 = this.height + 50;
      float var7 = -this.scroll;
      var1.pose().pushPose();
      var1.pose().translate(0.0F, var7, 0.0F);
      this.logoRenderer.renderLogo(var1, this.width, 1.0F, var6);
      int var8 = var6 + 100;

      for (int var9 = 0; var9 < this.lines.size(); var9++) {
         if (var9 == this.lines.size() - 1) {
            float var10 = (float)var8 + var7 - (float)(this.height / 2 - 6);
            if (var10 < 0.0F) {
               var1.pose().translate(0.0F, -var10, 0.0F);
            }
         }

         if ((float)var8 + var7 + 12.0F + 8.0F > 0.0F && (float)var8 + var7 < (float)this.height) {
            FormattedCharSequence var11 = this.lines.get(var9);
            if (this.centeredLines.contains(var9)) {
               var1.drawCenteredString(this.font, var11, var5 + 128, var8, -1);
            } else {
               var1.drawString(this.font, var11, var5, var8, -1);
            }
         }

         var8 += 12;
      }

      var1.pose().popPose();
   }

   private void renderVignette(GuiGraphics var1) {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
      var1.blit(VIGNETTE_LOCATION, 0, 0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.poem) {
         var1.fillRenderType(RenderType.endPortal(), 0, 0, this.width, this.height, 0);
      } else {
         super.renderBackground(var1, var2, var3, var4);
      }
   }

   @Override
   protected void renderMenuBackground(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      float var6 = this.scroll * 0.5F;
      Screen.renderMenuBackgroundTexture(var1, Screen.MENU_BACKGROUND, 0, 0, 0.0F, var6, var4, var5);
   }

   @Override
   public boolean isPauseScreen() {
      return !this.poem;
   }

   @Override
   public void removed() {
      this.minecraft.getMusicManager().stopPlaying(Musics.CREDITS);
   }

   @Override
   public Music getBackgroundMusic() {
      return Musics.CREDITS;
   }

   @FunctionalInterface
   interface CreditsReader {
      void read(Reader var1) throws IOException;
   }
}
