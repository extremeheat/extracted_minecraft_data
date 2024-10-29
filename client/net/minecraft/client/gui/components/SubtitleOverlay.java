package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.audio.ListenerTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;
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
               var12.purgeOldInstances(3000.0 * var9);
               if (!var12.isStillActive()) {
                  var11.remove();
               } else {
                  var30 = Math.max(var30, this.minecraft.font.width((FormattedText)var12.getText()));
               }
            }

            var30 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");
            var11 = this.audibleSubtitles.iterator();

            while(var11.hasNext()) {
               var12 = (Subtitle)var11.next();
               boolean var13 = true;
               Component var14 = var12.getText();
               SoundPlayedAt var15 = var12.getClosest(var4);
               if (var15 != null) {
                  Vec3 var16 = var15.location.subtract(var4).normalize();
                  double var17 = var6.dot(var16);
                  double var19 = var5.dot(var16);
                  boolean var21 = var19 > 0.5;
                  int var22 = var30 / 2;
                  Objects.requireNonNull(this.minecraft.font);
                  byte var23 = 9;
                  int var24 = var23 / 2;
                  float var25 = 1.0F;
                  int var26 = this.minecraft.font.width((FormattedText)var14);
                  int var27 = Mth.floor(Mth.clampedLerp(255.0F, 75.0F, (float)(Util.getMillis() - var15.time) / (float)(3000.0 * var9)));
                  var1.pose().pushPose();
                  var1.pose().translate((float)var1.guiWidth() - (float)var22 * 1.0F - 2.0F, (float)(var1.guiHeight() - 35) - (float)(var29 * (var23 + 1)) * 1.0F, 0.0F);
                  var1.pose().scale(1.0F, 1.0F, 1.0F);
                  var1.fill(-var22 - 1, -var24 - 1, var22 + 1, var24 + 1, this.minecraft.options.getBackgroundColor(0.8F));
                  int var28 = ARGB.color(255, var27, var27, var27);
                  if (!var21) {
                     if (var17 > 0.0) {
                        var1.drawString(this.minecraft.font, ">", var22 - this.minecraft.font.width(">"), -var24, var28);
                     } else if (var17 < 0.0) {
                        var1.drawString(this.minecraft.font, "<", -var22, -var24, var28);
                     }
                  }

                  var1.drawString(this.minecraft.font, var14, -var26 / 2, -var24, var28);
                  var1.pose().popPose();
                  ++var29;
               }
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

   static class Subtitle {
      private final Component text;
      private final float range;
      private final List<SoundPlayedAt> playedAt = new ArrayList();

      public Subtitle(Component var1, float var2, Vec3 var3) {
         super();
         this.text = var1;
         this.range = var2;
         this.playedAt.add(new SoundPlayedAt(var3, Util.getMillis()));
      }

      public Component getText() {
         return this.text;
      }

      @Nullable
      public SoundPlayedAt getClosest(Vec3 var1) {
         if (this.playedAt.isEmpty()) {
            return null;
         } else {
            return this.playedAt.size() == 1 ? (SoundPlayedAt)this.playedAt.getFirst() : (SoundPlayedAt)this.playedAt.stream().min(Comparator.comparingDouble((var1x) -> {
               return var1x.location().distanceTo(var1);
            })).orElse((Object)null);
         }
      }

      public void refresh(Vec3 var1) {
         this.playedAt.removeIf((var1x) -> {
            return var1.equals(var1x.location());
         });
         this.playedAt.add(new SoundPlayedAt(var1, Util.getMillis()));
      }

      public boolean isAudibleFrom(Vec3 var1) {
         if (Float.isInfinite(this.range)) {
            return true;
         } else if (this.playedAt.isEmpty()) {
            return false;
         } else {
            SoundPlayedAt var2 = this.getClosest(var1);
            return var2 == null ? false : var1.closerThan(var2.location, (double)this.range);
         }
      }

      public void purgeOldInstances(double var1) {
         long var3 = Util.getMillis();
         this.playedAt.removeIf((var4) -> {
            return (double)(var3 - var4.time()) > var1;
         });
      }

      public boolean isStillActive() {
         return !this.playedAt.isEmpty();
      }
   }

   static record SoundPlayedAt(Vec3 location, long time) {
      final Vec3 location;
      final long time;

      SoundPlayedAt(Vec3 var1, long var2) {
         super();
         this.location = var1;
         this.time = var2;
      }

      public Vec3 location() {
         return this.location;
      }

      public long time() {
         return this.time;
      }
   }
}
