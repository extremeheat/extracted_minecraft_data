package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SubtitleOverlay extends GuiComponent implements SoundEventListener {
   private final Minecraft minecraft;
   private final List<SubtitleOverlay.Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;

   public SubtitleOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render() {
      if (!this.isListening && this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         Vec3 var1 = new Vec3(this.minecraft.player.x, this.minecraft.player.y + (double)this.minecraft.player.getEyeHeight(), this.minecraft.player.z);
         Vec3 var2 = (new Vec3(0.0D, 0.0D, -1.0D)).xRot(-this.minecraft.player.xRot * 0.017453292F).yRot(-this.minecraft.player.yRot * 0.017453292F);
         Vec3 var3 = (new Vec3(0.0D, 1.0D, 0.0D)).xRot(-this.minecraft.player.xRot * 0.017453292F).yRot(-this.minecraft.player.yRot * 0.017453292F);
         Vec3 var4 = var2.cross(var3);
         int var5 = 0;
         int var6 = 0;
         Iterator var7 = this.subtitles.iterator();

         SubtitleOverlay.Subtitle var8;
         while(var7.hasNext()) {
            var8 = (SubtitleOverlay.Subtitle)var7.next();
            if (var8.getTime() + 3000L <= Util.getMillis()) {
               var7.remove();
            } else {
               var6 = Math.max(var6, this.minecraft.font.width(var8.getText()));
            }
         }

         var6 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(var7 = this.subtitles.iterator(); var7.hasNext(); ++var5) {
            var8 = (SubtitleOverlay.Subtitle)var7.next();
            boolean var9 = true;
            String var10 = var8.getText();
            Vec3 var11 = var8.getLocation().subtract(var1).normalize();
            double var12 = -var4.dot(var11);
            double var14 = -var2.dot(var11);
            boolean var16 = var14 > 0.5D;
            int var17 = var6 / 2;
            this.minecraft.font.getClass();
            byte var18 = 9;
            int var19 = var18 / 2;
            float var20 = 1.0F;
            int var21 = this.minecraft.font.width(var10);
            int var22 = Mth.floor(Mth.clampedLerp(255.0D, 75.0D, (double)((float)(Util.getMillis() - var8.getTime()) / 3000.0F)));
            int var23 = var22 << 16 | var22 << 8 | var22;
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)this.minecraft.window.getGuiScaledWidth() - (float)var17 * 1.0F - 2.0F, (float)(this.minecraft.window.getGuiScaledHeight() - 30) - (float)(var5 * (var18 + 1)) * 1.0F, 0.0F);
            GlStateManager.scalef(1.0F, 1.0F, 1.0F);
            fill(-var17 - 1, -var19 - 1, var17 + 1, var19 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            GlStateManager.enableBlend();
            if (!var16) {
               if (var12 > 0.0D) {
                  this.minecraft.font.draw(">", (float)(var17 - this.minecraft.font.width(">")), (float)(-var19), var23 + -16777216);
               } else if (var12 < 0.0D) {
                  this.minecraft.font.draw("<", (float)(-var17), (float)(-var19), var23 + -16777216);
               }
            }

            this.minecraft.font.draw(var10, (float)(-var21 / 2), (float)(-var19), var23 + -16777216);
            GlStateManager.popMatrix();
         }

         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }
   }

   public void onPlaySound(SoundInstance var1, WeighedSoundEvents var2) {
      if (var2.getSubtitle() != null) {
         String var3 = var2.getSubtitle().getColoredString();
         if (!this.subtitles.isEmpty()) {
            Iterator var4 = this.subtitles.iterator();

            while(var4.hasNext()) {
               SubtitleOverlay.Subtitle var5 = (SubtitleOverlay.Subtitle)var4.next();
               if (var5.getText().equals(var3)) {
                  var5.refresh(new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new SubtitleOverlay.Subtitle(var3, new Vec3((double)var1.getX(), (double)var1.getY(), (double)var1.getZ())));
      }
   }

   public class Subtitle {
      private final String text;
      private long time;
      private Vec3 location;

      public Subtitle(String var2, Vec3 var3) {
         super();
         this.text = var2;
         this.location = var3;
         this.time = Util.getMillis();
      }

      public String getText() {
         return this.text;
      }

      public long getTime() {
         return this.time;
      }

      public Vec3 getLocation() {
         return this.location;
      }

      public void refresh(Vec3 var1) {
         this.location = var1;
         this.time = Util.getMillis();
      }
   }
}
