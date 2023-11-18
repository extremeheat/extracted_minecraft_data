package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SubtitleOverlay implements SoundEventListener {
   private static final long DISPLAY_TIME = 3000L;
   private final Minecraft minecraft;
   private final List<SubtitleOverlay.Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;

   public SubtitleOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(GuiGraphics var1) {
      if (!this.isListening && this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         Vec3 var2 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
         Vec3 var3 = new Vec3(0.0, 0.0, -1.0).xRot(-this.minecraft.player.getXRot() * 0.017453292F).yRot(-this.minecraft.player.getYRot() * 0.017453292F);
         Vec3 var4 = new Vec3(0.0, 1.0, 0.0).xRot(-this.minecraft.player.getXRot() * 0.017453292F).yRot(-this.minecraft.player.getYRot() * 0.017453292F);
         Vec3 var5 = var3.cross(var4);
         int var6 = 0;
         int var7 = 0;
         double var8 = this.minecraft.options.notificationDisplayTime().get();
         Iterator var10 = this.subtitles.iterator();

         while(var10.hasNext()) {
            SubtitleOverlay.Subtitle var11 = (SubtitleOverlay.Subtitle)var10.next();
            if ((double)var11.getTime() + 3000.0 * var8 <= (double)Util.getMillis()) {
               var10.remove();
            } else {
               var7 = Math.max(var7, this.minecraft.font.width(var11.getText()));
            }
         }

         var7 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(SubtitleOverlay.Subtitle var30 : this.subtitles) {
            boolean var12 = true;
            Component var13 = var30.getText();
            Vec3 var14 = var30.getLocation().subtract(var2).normalize();
            double var15 = -var5.dot(var14);
            double var17 = -var3.dot(var14);
            boolean var19 = var17 > 0.5;
            int var20 = var7 / 2;
            byte var21 = 9;
            int var22 = var21 / 2;
            float var23 = 1.0F;
            int var24 = this.minecraft.font.width(var13);
            int var25 = Mth.floor(Mth.clampedLerp(255.0F, 75.0F, (float)(Util.getMillis() - var30.getTime()) / (float)(3000.0 * var8)));
            int var26 = var25 << 16 | var25 << 8 | var25;
            var1.pose().pushPose();
            var1.pose()
               .translate((float)var1.guiWidth() - (float)var20 * 1.0F - 2.0F, (float)(var1.guiHeight() - 35) - (float)(var6 * (var21 + 1)) * 1.0F, 0.0F);
            var1.pose().scale(1.0F, 1.0F, 1.0F);
            var1.fill(-var20 - 1, -var22 - 1, var20 + 1, var22 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            int var27 = var26 + -16777216;
            if (!var19) {
               if (var15 > 0.0) {
                  var1.drawString(this.minecraft.font, ">", var20 - this.minecraft.font.width(">"), -var22, var27);
               } else if (var15 < 0.0) {
                  var1.drawString(this.minecraft.font, "<", -var20, -var22, var27);
               }
            }

            var1.drawString(this.minecraft.font, var13, -var24 / 2, -var22, var27);
            var1.pose().popPose();
            ++var6;
         }
      }
   }

   @Override
   public void onPlaySound(SoundInstance var1, WeighedSoundEvents var2) {
      if (var2.getSubtitle() != null) {
         Component var3 = var2.getSubtitle();
         if (!this.subtitles.isEmpty()) {
            for(SubtitleOverlay.Subtitle var5 : this.subtitles) {
               if (var5.getText().equals(var3)) {
                  var5.refresh(new Vec3(var1.getX(), var1.getY(), var1.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new SubtitleOverlay.Subtitle(var3, new Vec3(var1.getX(), var1.getY(), var1.getZ())));
      }
   }

   public static class Subtitle {
      private final Component text;
      private long time;
      private Vec3 location;

      public Subtitle(Component var1, Vec3 var2) {
         super();
         this.text = var1;
         this.location = var2;
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
   }
}
