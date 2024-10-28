package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.audio.ListenerTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SubtitleOverlay implements SoundEventListener {
   private static final long DISPLAY_TIME = 3000L;
   private final Minecraft minecraft;
   private final List<Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;
   private final List<Subtitle> audibleSubtitles = new ArrayList();

   public SubtitleOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(GuiGraphics var1) {
      SoundManager var2 = this.minecraft.getSoundManager();
      if (!this.isListening && (Boolean)this.minecraft.options.showSubtitles().get()) {
         var2.addListener(this);
         this.isListening = true;
      } else if (this.isListening && !(Boolean)this.minecraft.options.showSubtitles().get()) {
         var2.removeListener(this);
         this.isListening = false;
      }

      if (this.isListening) {
         ListenerTransform var3 = var2.getListenerTransform();
         Vec3 var4 = var3.position();
         Vec3 var5 = var3.forward();
         Vec3 var6 = var3.right();
         this.audibleSubtitles.clear();
         Iterator var7 = this.subtitles.iterator();

         while(var7.hasNext()) {
            Subtitle var8 = (Subtitle)var7.next();
            if (var8.isAudibleFrom(var4)) {
               this.audibleSubtitles.add(var8);
            }
         }

         if (!this.audibleSubtitles.isEmpty()) {
            int var29 = 0;
            int var30 = 0;
            double var9 = (Double)this.minecraft.options.notificationDisplayTime().get();
            Iterator var11 = this.audibleSubtitles.iterator();

            Subtitle var12;
            while(var11.hasNext()) {
               var12 = (Subtitle)var11.next();
               if ((double)var12.getTime() + 3000.0 * var9 <= (double)Util.getMillis()) {
                  var11.remove();
               } else {
                  var30 = Math.max(var30, this.minecraft.font.width((FormattedText)var12.getText()));
               }
            }

            var30 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

            for(var11 = this.audibleSubtitles.iterator(); var11.hasNext(); ++var29) {
               var12 = (Subtitle)var11.next();
               boolean var13 = true;
               Component var14 = var12.getText();
               Vec3 var15 = var12.getLocation().subtract(var4).normalize();
               double var16 = var6.dot(var15);
               double var18 = var5.dot(var15);
               boolean var20 = var18 > 0.5;
               int var21 = var30 / 2;
               Objects.requireNonNull(this.minecraft.font);
               byte var22 = 9;
               int var23 = var22 / 2;
               float var24 = 1.0F;
               int var25 = this.minecraft.font.width((FormattedText)var14);
               int var26 = Mth.floor(Mth.clampedLerp(255.0F, 75.0F, (float)(Util.getMillis() - var12.getTime()) / (float)(3000.0 * var9)));
               int var27 = var26 << 16 | var26 << 8 | var26;
               var1.pose().pushPose();
               var1.pose().translate((float)var1.guiWidth() - (float)var21 * 1.0F - 2.0F, (float)(var1.guiHeight() - 35) - (float)(var29 * (var22 + 1)) * 1.0F, 0.0F);
               var1.pose().scale(1.0F, 1.0F, 1.0F);
               var1.fill(-var21 - 1, -var23 - 1, var21 + 1, var23 + 1, this.minecraft.options.getBackgroundColor(0.8F));
               int var28 = var27 + -16777216;
               if (!var20) {
                  if (var16 > 0.0) {
                     var1.drawString(this.minecraft.font, ">", var21 - this.minecraft.font.width(">"), -var23, var28);
                  } else if (var16 < 0.0) {
                     var1.drawString(this.minecraft.font, "<", -var21, -var23, var28);
                  }
               }

               var1.drawString(this.minecraft.font, var14, -var25 / 2, -var23, var28);
               var1.pose().popPose();
            }

         }
      }
   }

   public void onPlaySound(SoundInstance var1, WeighedSoundEvents var2, float var3) {
      if (var2.getSubtitle() != null) {
         Component var4 = var2.getSubtitle();
         if (!this.subtitles.isEmpty()) {
            Iterator var5 = this.subtitles.iterator();

            while(var5.hasNext()) {
               Subtitle var6 = (Subtitle)var5.next();
               if (var6.getText().equals(var4)) {
                  var6.refresh(new Vec3(var1.getX(), var1.getY(), var1.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new Subtitle(var4, var3, new Vec3(var1.getX(), var1.getY(), var1.getZ())));
      }
   }

   public static class Subtitle {
      private final Component text;
      private final float range;
      private long time;
      private Vec3 location;

      public Subtitle(Component var1, float var2, Vec3 var3) {
         super();
         this.text = var1;
         this.range = var2;
         this.location = var3;
         this.time = Util.getMillis();
      }

      public Component getText() {
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

      public boolean isAudibleFrom(Vec3 var1) {
         return Float.isInfinite(this.range) || var1.closerThan(this.location, (double)this.range);
      }
   }
}
