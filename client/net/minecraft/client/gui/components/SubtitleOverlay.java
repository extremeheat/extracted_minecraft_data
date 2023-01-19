package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SubtitleOverlay extends GuiComponent implements SoundEventListener {
   private static final long DISPLAY_TIME = 3000L;
   private final Minecraft minecraft;
   private final List<SubtitleOverlay.Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;

   public SubtitleOverlay(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1) {
      if (!this.isListening && this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !this.minecraft.options.showSubtitles().get()) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Vec3 var2 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
         Vec3 var3 = new Vec3(0.0, 0.0, -1.0).xRot(-this.minecraft.player.getXRot() * 0.017453292F).yRot(-this.minecraft.player.getYRot() * 0.017453292F);
         Vec3 var4 = new Vec3(0.0, 1.0, 0.0).xRot(-this.minecraft.player.getXRot() * 0.017453292F).yRot(-this.minecraft.player.getYRot() * 0.017453292F);
         Vec3 var5 = var3.cross(var4);
         int var6 = 0;
         int var7 = 0;
         Iterator var8 = this.subtitles.iterator();

         while(var8.hasNext()) {
            SubtitleOverlay.Subtitle var9 = (SubtitleOverlay.Subtitle)var8.next();
            if (var9.getTime() + 3000L <= Util.getMillis()) {
               var8.remove();
            } else {
               var7 = Math.max(var7, this.minecraft.font.width(var9.getText()));
            }
         }

         var7 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(SubtitleOverlay.Subtitle var28 : this.subtitles) {
            boolean var10 = true;
            Component var11 = var28.getText();
            Vec3 var12 = var28.getLocation().subtract(var2).normalize();
            double var13 = -var5.dot(var12);
            double var15 = -var3.dot(var12);
            boolean var17 = var15 > 0.5;
            int var18 = var7 / 2;
            byte var19 = 9;
            int var20 = var19 / 2;
            float var21 = 1.0F;
            int var22 = this.minecraft.font.width(var11);
            int var23 = Mth.floor(Mth.clampedLerp(255.0F, 75.0F, (float)(Util.getMillis() - var28.getTime()) / 3000.0F));
            int var24 = var23 << 16 | var23 << 8 | var23;
            var1.pushPose();
            var1.translate(
               (float)this.minecraft.getWindow().getGuiScaledWidth() - (float)var18 * 1.0F - 2.0F,
               (float)(this.minecraft.getWindow().getGuiScaledHeight() - 35) - (float)(var6 * (var19 + 1)) * 1.0F,
               0.0F
            );
            var1.scale(1.0F, 1.0F, 1.0F);
            fill(var1, -var18 - 1, -var20 - 1, var18 + 1, var20 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            RenderSystem.enableBlend();
            int var25 = var24 + -16777216;
            if (!var17) {
               if (var13 > 0.0) {
                  drawString(var1, this.minecraft.font, ">", var18 - this.minecraft.font.width(">"), -var20, var25);
               } else if (var13 < 0.0) {
                  drawString(var1, this.minecraft.font, "<", -var18, -var20, var25);
               }
            }

            drawString(var1, this.minecraft.font, var11, -var22 / 2, -var20, var25);
            var1.popPose();
            ++var6;
         }

         RenderSystem.disableBlend();
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
