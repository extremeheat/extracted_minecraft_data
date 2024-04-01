package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.util.RandomSource;

public class PotatoPoemScreen extends Screen {
   private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   private static final String OBFUSCATE_TOKEN = "" + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + ChatFormatting.GREEN + ChatFormatting.AQUA;
   private static final float SPEEDUP_FACTOR = 5.0F;
   private static final float SPEEDUP_FACTOR_FAST = 15.0F;
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

   public PotatoPoemScreen(Runnable var1) {
      super(GameNarrator.NO_TITLE);
      this.onFinished = var1;
      this.unmodifiedScrollSpeed = 0.5F;
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
      float var1 = (float)(this.totalScrollLength + this.height + 50);
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
         this.wrapCreditsIO("texts/potato.txt", this::addPoemFile);
         this.totalScrollLength = this.lines.size() * 12;
      }
   }

   private void wrapCreditsIO(String var1, PotatoPoemScreen.CreditsReader var2) {
      try (BufferedReader var3 = this.minecraft.getResourceManager().openAsReader(new ResourceLocation(var1))) {
         var2.read(var3);
      } catch (Exception var8) {
      }
   }

   private void addPoemFile(Reader var1) throws IOException {
      BufferedReader var2 = new BufferedReader(var1);
      RandomSource var3 = RandomSource.create(8124371L);

      String var4;
      while((var4 = var2.readLine()) != null) {
         int var5;
         String var6;
         String var7;
         for(var4 = var4.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
            (var5 = var4.indexOf(OBFUSCATE_TOKEN)) != -1;
            var4 = var6 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var3.nextInt(4) + 3) + var7
         ) {
            var6 = var4.substring(0, var5);
            var7 = var4.substring(var5 + OBFUSCATE_TOKEN.length());
         }

         this.addPoemLines(var4);
         this.addEmptyLine();
      }

      for(int var9 = 0; var9 < 8; ++var9) {
         this.addEmptyLine();
      }
   }

   private void addEmptyLine() {
      this.lines.add(FormattedCharSequence.EMPTY);
   }

   private void addPoemLines(String var1) {
      this.lines.addAll(this.minecraft.font.split(Component.literal(var1), 256));
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

      for(int var9 = 0; var9 < this.lines.size(); ++var9) {
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
      var1.fillRenderType(RenderType.poisonousPotato(), 0, 0, this.width, this.height, 0);
   }

   @Override
   public boolean isPauseScreen() {
      return false;
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
