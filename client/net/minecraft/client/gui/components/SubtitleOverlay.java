package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
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
      if (!this.isListening && this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Vec3 var2 = new Vec3(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
         Vec3 var3 = (new Vec3(0.0D, 0.0D, -1.0D)).xRot(-this.minecraft.player.getXRot() * 0.017453292F).yRot(-this.minecraft.player.getYRot() * 0.017453292F);
         Vec3 var4 = (new Vec3(0.0D, 1.0D, 0.0D)).xRot(-this.minecraft.player.getXRot() * 0.017453292F).yRot(-this.minecraft.player.getYRot() * 0.017453292F);
         Vec3 var5 = var3.cross(var4);
         int var6 = 0;
         int var7 = 0;
         Iterator var8 = this.subtitles.iterator();

         SubtitleOverlay.Subtitle var9;
         while(var8.hasNext()) {
            var9 = (SubtitleOverlay.Subtitle)var8.next();
            if (var9.getTime() + 3000L <= Util.getMillis()) {
               var8.remove();
            } else {
               var7 = Math.max(var7, this.minecraft.font.width((FormattedText)var9.getText()));
            }
         }

         var7 += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(var8 = this.subtitles.iterator(); var8.hasNext(); ++var6) {
            var9 = (SubtitleOverlay.Subtitle)var8.next();
            boolean var10 = true;
            Component var11 = var9.getText();
            Vec3 var12 = var9.getLocation().subtract(var2).normalize();
            double var13 = -var5.dot(var12);
            double var15 = -var3.dot(var12);
            boolean var17 = var15 > 0.5D;
            int var18 = var7 / 2;
            Objects.requireNonNull(this.minecraft.font);
            byte var19 = 9;
            int var20 = var19 / 2;
            float var21 = 1.0F;
            int var22 = this.minecraft.font.width((FormattedText)var11);
            int var23 = Mth.floor(Mth.clampedLerp(255.0F, 75.0F, (float)(Util.getMillis() - var9.getTime()) / 3000.0F));
            int var24 = var23 << 16 | var23 << 8 | var23;
            var1.pushPose();
            var1.translate((double)((float)this.minecraft.getWindow().getGuiScaledWidth() - (float)var18 * 1.0F - 2.0F), (double)((float)(this.minecraft.getWindow().getGuiScaledHeight() - 30) - (float)(var6 * (var19 + 1)) * 1.0F), 0.0D);
            var1.scale(1.0F, 1.0F, 1.0F);
            fill(var1, -var18 - 1, -var20 - 1, var18 + 1, var20 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            RenderSystem.enableBlend();
            if (!var17) {
               if (var13 > 0.0D) {
                  this.minecraft.font.draw(var1, ">", (float)(var18 - this.minecraft.font.width(">")), (float)(-var20), var24 + -16777216);
               } else if (var13 < 0.0D) {
                  this.minecraft.font.draw(var1, "<", (float)(-var18), (float)(-var20), var24 + -16777216);
               }
            }

            this.minecraft.font.draw(var1, var11, (float)(-var22 / 2), (float)(-var20), var24 + -16777216);
            var1.popPose();
         }

         RenderSystem.disableBlend();
      }
   }

   public void onPlaySound(SoundInstance var1, WeighedSoundEvents var2) {
      if (var2.getSubtitle() != null) {
         Component var3 = var2.getSubtitle();
         if (!this.subtitles.isEmpty()) {
            Iterator var4 = this.subtitles.iterator();

            while(var4.hasNext()) {
               SubtitleOverlay.Subtitle var5 = (SubtitleOverlay.Subtitle)var4.next();
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
