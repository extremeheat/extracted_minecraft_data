package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WinScreen extends Screen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation EDITION_LOCATION = new ResourceLocation("textures/gui/title/edition.png");
   private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   private static final String OBFUSCATE_TOKEN;
   private final boolean poem;
   private final Runnable onFinished;
   private float time;
   private List<FormattedCharSequence> lines;
   private IntSet centeredLines;
   private int totalScrollLength;
   private float scrollSpeed = 0.5F;

   public WinScreen(boolean var1, Runnable var2) {
      super(NarratorChatListener.NO_TITLE);
      this.poem = var1;
      this.onFinished = var2;
      if (!var1) {
         this.scrollSpeed = 0.75F;
      }

   }

   public void tick() {
      this.minecraft.getMusicManager().tick();
      this.minecraft.getSoundManager().tick(false);
      float var1 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      if (this.time > var1) {
         this.respawn();
      }

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
         Resource var1 = null;

         try {
            boolean var2 = true;
            InputStream var3;
            BufferedReader var4;
            if (this.poem) {
               var1 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/end.txt"));
               var3 = var1.getInputStream();
               var4 = new BufferedReader(new InputStreamReader(var3, StandardCharsets.UTF_8));
               Random var5 = new Random(8124371L);

               label152:
               while(true) {
                  String var6;
                  int var7;
                  if ((var6 = var4.readLine()) == null) {
                     var3.close();
                     var7 = 0;

                     while(true) {
                        if (var7 >= 8) {
                           break label152;
                        }

                        this.lines.add(FormattedCharSequence.EMPTY);
                        ++var7;
                     }
                  }

                  String var8;
                  String var9;
                  for(var6 = var6.replaceAll("PLAYERNAME", this.minecraft.getUser().getName()); (var7 = var6.indexOf(OBFUSCATE_TOKEN)) != -1; var6 = var8 + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + "XXXXXXXX".substring(0, var5.nextInt(4) + 3) + var9) {
                     var8 = var6.substring(0, var7);
                     var9 = var6.substring(var7 + OBFUSCATE_TOKEN.length());
                  }

                  this.lines.addAll(this.minecraft.font.split(new TextComponent(var6), 274));
                  this.lines.add(FormattedCharSequence.EMPTY);
               }
            }

            var3 = this.minecraft.getResourceManager().getResource(new ResourceLocation("texts/credits.txt")).getInputStream();
            var4 = new BufferedReader(new InputStreamReader(var3, StandardCharsets.UTF_8));

            String var15;
            while((var15 = var4.readLine()) != null) {
               var15 = var15.replaceAll("PLAYERNAME", this.minecraft.getUser().getName());
               var15 = var15.replaceAll("\t", "    ");
               boolean var16;
               if (var15.startsWith("[C]")) {
                  var15 = var15.substring(3);
                  var16 = true;
               } else {
                  var16 = false;
               }

               List var17 = this.minecraft.font.split(new TextComponent(var15), 274);

               FormattedCharSequence var19;
               for(Iterator var18 = var17.iterator(); var18.hasNext(); this.lines.add(var19)) {
                  var19 = (FormattedCharSequence)var18.next();
                  if (var16) {
                     this.centeredLines.add(this.lines.size());
                  }
               }

               this.lines.add(FormattedCharSequence.EMPTY);
            }

            var3.close();
            this.totalScrollLength = this.lines.size() * 12;
         } catch (Exception var13) {
            LOGGER.error("Couldn't load credits", var13);
         } finally {
            IOUtils.closeQuietly(var1);
         }

      }
   }

   private void renderBg(int var1, int var2, float var3) {
      this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
      int var4 = this.width;
      float var5 = -this.time * 0.5F * this.scrollSpeed;
      float var6 = (float)this.height - this.time * 0.5F * this.scrollSpeed;
      float var7 = 0.015625F;
      float var8 = this.time * 0.02F;
      float var9 = (float)(this.totalScrollLength + this.height + this.height + 24) / this.scrollSpeed;
      float var10 = (var9 - 20.0F - this.time) * 0.005F;
      if (var10 < var8) {
         var8 = var10;
      }

      if (var8 > 1.0F) {
         var8 = 1.0F;
      }

      var8 *= var8;
      var8 = var8 * 96.0F / 255.0F;
      Tesselator var11 = Tesselator.getInstance();
      BufferBuilder var12 = var11.getBuilder();
      var12.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var12.vertex(0.0D, (double)this.height, (double)this.getBlitOffset()).uv(0.0F, var5 * 0.015625F).color(var8, var8, var8, 1.0F).endVertex();
      var12.vertex((double)var4, (double)this.height, (double)this.getBlitOffset()).uv((float)var4 * 0.015625F, var5 * 0.015625F).color(var8, var8, var8, 1.0F).endVertex();
      var12.vertex((double)var4, 0.0D, (double)this.getBlitOffset()).uv((float)var4 * 0.015625F, var6 * 0.015625F).color(var8, var8, var8, 1.0F).endVertex();
      var12.vertex(0.0D, 0.0D, (double)this.getBlitOffset()).uv(0.0F, var6 * 0.015625F).color(var8, var8, var8, 1.0F).endVertex();
      var11.end();
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBg(var2, var3, var4);
      boolean var5 = true;
      int var6 = this.width / 2 - 137;
      int var7 = this.height + 50;
      this.time += var4;
      float var8 = -this.time * this.scrollSpeed;
      RenderSystem.pushMatrix();
      RenderSystem.translatef(0.0F, var8, 0.0F);
      this.minecraft.getTextureManager().bind(LOGO_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableAlphaTest();
      RenderSystem.enableBlend();
      this.blitOutlineBlack(var6, var7, (var2x, var3x) -> {
         this.blit(var1, var2x + 0, var3x, 0, 0, 155, 44);
         this.blit(var1, var2x + 155, var3x, 0, 45, 155, 44);
      });
      RenderSystem.disableBlend();
      this.minecraft.getTextureManager().bind(EDITION_LOCATION);
      blit(var1, var6 + 88, var7 + 37, 0.0F, 0.0F, 98, 14, 128, 16);
      RenderSystem.disableAlphaTest();
      int var9 = var7 + 100;

      int var10;
      for(var10 = 0; var10 < this.lines.size(); ++var10) {
         if (var10 == this.lines.size() - 1) {
            float var11 = (float)var9 + var8 - (float)(this.height / 2 - 6);
            if (var11 < 0.0F) {
               RenderSystem.translatef(0.0F, -var11, 0.0F);
            }
         }

         if ((float)var9 + var8 + 12.0F + 8.0F > 0.0F && (float)var9 + var8 < (float)this.height) {
            FormattedCharSequence var14 = (FormattedCharSequence)this.lines.get(var10);
            if (this.centeredLines.contains(var10)) {
               this.font.drawShadow(var1, var14, (float)(var6 + (274 - this.font.width(var14)) / 2), (float)var9, 16777215);
            } else {
               this.font.random.setSeed((long)((float)((long)var10 * 4238972211L) + this.time / 4.0F));
               this.font.drawShadow(var1, var14, (float)var6, (float)var9, 16777215);
            }
         }

         var9 += 12;
      }

      RenderSystem.popMatrix();
      this.minecraft.getTextureManager().bind(VIGNETTE_LOCATION);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
      var10 = this.width;
      int var15 = this.height;
      Tesselator var12 = Tesselator.getInstance();
      BufferBuilder var13 = var12.getBuilder();
      var13.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var13.vertex(0.0D, (double)var15, (double)this.getBlitOffset()).uv(0.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var13.vertex((double)var10, (double)var15, (double)this.getBlitOffset()).uv(1.0F, 1.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var13.vertex((double)var10, 0.0D, (double)this.getBlitOffset()).uv(1.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var13.vertex(0.0D, 0.0D, (double)this.getBlitOffset()).uv(0.0F, 0.0F).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      var12.end();
      RenderSystem.disableBlend();
      super.render(var1, var2, var3, var4);
   }

   static {
      OBFUSCATE_TOKEN = "" + ChatFormatting.WHITE + ChatFormatting.OBFUSCATED + ChatFormatting.GREEN + ChatFormatting.AQUA;
   }
}
