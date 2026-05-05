package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.audio.ListenerTransform;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SubtitleOverlay implements SoundEventListener {
   private static final long DISPLAY_TIME = 3000L;
   private final Minecraft minecraft;
   private final List<Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;
   private final List<Subtitle> audibleSubtitles = new ArrayList();

   public SubtitleOverlay(final Minecraft minecraft) {
      super();
      this.minecraft = minecraft;
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics) {
      SoundManager soundManager = this.minecraft.getSoundManager();
      if (!this.isListening && (Boolean)this.minecraft.options.showSubtitles().get()) {
         soundManager.addListener(this);
         this.isListening = true;
      } else if (this.isListening && !(Boolean)this.minecraft.options.showSubtitles().get()) {
         soundManager.removeListener(this);
         this.isListening = false;
      }

      if (this.isListening) {
         ListenerTransform listener = soundManager.getListenerTransform();
         Vec3 position = listener.position();
         Vec3 forwards = listener.forward();
         Vec3 right = listener.right();
         this.audibleSubtitles.clear();

         for(Subtitle subtitle : this.subtitles) {
            if (subtitle.isAudibleFrom(position)) {
               this.audibleSubtitles.add(subtitle);
            }
         }

         if (!this.audibleSubtitles.isEmpty()) {
            int row = 0;
            int width = 0;
            double displayTimeMultiplier = (Double)this.minecraft.options.notificationDisplayTime().get();
            Iterator<Subtitle> iterator = this.audibleSubtitles.iterator();

            while(iterator.hasNext()) {
               Subtitle subtitle = (Subtitle)iterator.next();
               subtitle.purgeOldInstances(3000.0 * displayTimeMultiplier);
               if (!subtitle.isStillActive()) {
                  iterator.remove();
               } else {
                  width = Math.max(width, this.minecraft.font.width((FormattedText)subtitle.getText()));
               }
            }

            width += this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");
            if (!this.audibleSubtitles.isEmpty()) {
               graphics.nextStratum();
            }

            for(Subtitle subtitle : this.audibleSubtitles) {
               int alpha = 255;
               Component text = subtitle.getText();
               SoundPlayedAt closestRecentLocation = subtitle.getClosest(position);
               if (closestRecentLocation != null) {
                  Vec3 delta = closestRecentLocation.location.subtract(position).normalize();
                  double rightness = right.dot(delta);
                  double forwardness = forwards.dot(delta);
                  boolean inView = forwardness > 0.5;
                  int halfWidth = width / 2;
                  Objects.requireNonNull(this.minecraft.font);
                  int height = 9;
                  int halfHeight = height / 2;
                  float scale = 1.0F;
                  int textWidth = this.minecraft.font.width((FormattedText)text);
                  int brightness = Mth.floor(Mth.clampedLerp((float)(Util.getMillis() - closestRecentLocation.time) / (float)(3000.0 * displayTimeMultiplier), 255.0F, 75.0F));
                  graphics.pose().pushMatrix();
                  graphics.pose().translate((float)graphics.guiWidth() - (float)halfWidth * 1.0F - 2.0F, (float)(graphics.guiHeight() - 35) - (float)(row * (height + 1)) * 1.0F);
                  graphics.pose().scale(1.0F, 1.0F);
                  graphics.fill(-halfWidth - 1, -halfHeight - 1, halfWidth + 1, halfHeight + 1, this.minecraft.options.getBackgroundColor(0.8F));
                  int textColor = ARGB.color(255, brightness, brightness, brightness);
                  if (!inView) {
                     if (rightness > 0.0) {
                        graphics.text(this.minecraft.font, ">", halfWidth - this.minecraft.font.width(">"), -halfHeight, textColor);
                     } else if (rightness < 0.0) {
                        graphics.text(this.minecraft.font, "<", -halfWidth, -halfHeight, textColor);
                     }
                  }

                  graphics.text(this.minecraft.font, text, -textWidth / 2, -halfHeight, textColor);
                  graphics.pose().popMatrix();
                  ++row;
               }
            }

         }
      }
   }

   public void onPlaySound(final SoundInstance sound, final WeighedSoundEvents soundEvent, final float range) {
      if (soundEvent.getSubtitle() != null) {
         Component text = soundEvent.getSubtitle();
         if (!this.subtitles.isEmpty()) {
            for(Subtitle subtitle : this.subtitles) {
               if (subtitle.getText().equals(text)) {
                  subtitle.refresh(new Vec3(sound.getX(), sound.getY(), sound.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new Subtitle(text, range, new Vec3(sound.getX(), sound.getY(), sound.getZ())));
      }
   }

   private static record SoundPlayedAt(Vec3 location, long time) {
      private SoundPlayedAt {
         super();
      }
   }

   private static class Subtitle {
      private final Component text;
      private final float range;
      private final List<SoundPlayedAt> playedAt = new ArrayList();

      public Subtitle(final Component text, final float range, final Vec3 location) {
         super();
         this.text = text;
         this.range = range;
         this.playedAt.add(new SoundPlayedAt(location, Util.getMillis()));
      }

      public Component getText() {
         return this.text;
      }

      public @Nullable SoundPlayedAt getClosest(final Vec3 position) {
         if (this.playedAt.isEmpty()) {
            return null;
         } else {
            return this.playedAt.size() == 1 ? (SoundPlayedAt)this.playedAt.getFirst() : (SoundPlayedAt)this.playedAt.stream().min(Comparator.comparingDouble((soundPlayedAt) -> soundPlayedAt.location().distanceTo(position))).orElse((Object)null);
         }
      }

      public void refresh(final Vec3 location) {
         this.playedAt.removeIf((soundPlayedAt) -> location.equals(soundPlayedAt.location()));
         this.playedAt.add(new SoundPlayedAt(location, Util.getMillis()));
      }

      public boolean isAudibleFrom(final Vec3 camera) {
         if (Float.isInfinite(this.range)) {
            return true;
         } else if (this.playedAt.isEmpty()) {
            return false;
         } else {
            SoundPlayedAt closest = this.getClosest(camera);
            return closest == null ? false : camera.closerThan(closest.location, (double)this.range);
         }
      }

      public void purgeOldInstances(final double maxAge) {
         long currentTime = Util.getMillis();
         this.playedAt.removeIf((soundPlayedAt) -> (double)(currentTime - soundPlayedAt.time()) > maxAge);
      }

      public boolean isStillActive() {
         return !this.playedAt.isEmpty();
      }
   }
}
