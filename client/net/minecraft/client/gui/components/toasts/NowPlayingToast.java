package net.minecraft.client.gui.components.toasts;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.jspecify.annotations.Nullable;

public class NowPlayingToast implements Toast {
   private static final Identifier NOW_PLAYING_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/now_playing");
   private static final Identifier MUSIC_NOTES_SPRITE = Identifier.parse("icon/music_notes");
   private static final int PADDING = 7;
   private static final int MUSIC_NOTES_SIZE = 16;
   private static final int HEIGHT = 30;
   private static final int MUSIC_NOTES_SPACE = 30;
   private static final int VISIBILITY_DURATION = 5000;
   private static final int TEXT_COLOR;
   private static final long MUSIC_COLOR_CHANGE_FREQUENCY_MS = 25L;
   private static int musicNoteColorTick;
   private static long lastMusicNoteColorChange;
   private static int musicNoteColor;
   private boolean updateToast;
   private double notificationDisplayTimeMultiplier;
   private static @Nullable String currentSong;
   private final Minecraft minecraft;
   private Toast.Visibility wantedVisibility;

   public NowPlayingToast() {
      super();
      this.wantedVisibility = Toast.Visibility.HIDE;
      this.minecraft = Minecraft.getInstance();
   }

   public static void renderToast(Minecraft var0, GuiGraphics var1, Font var2) {
      currentSong = var0.getMusicManager().getCurrentMusicTranslationKey();
      if (currentSong != null) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)NOW_PLAYING_BACKGROUND_SPRITE, 0, 0, getWidth(currentSong, var2), 30);
         boolean var3 = true;
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)MUSIC_NOTES_SPRITE, 7, 7, 16, 16, musicNoteColor);
         Component var10002 = getNowPlayingString(currentSong);
         Objects.requireNonNull(var2);
         var1.drawString(var2, (Component)var10002, 30, 15 - 9 / 2, TEXT_COLOR);
      }

   }

   public static void tickMusicNotes() {
      if (currentSong != null) {
         long var0 = System.currentTimeMillis();
         if (var0 > lastMusicNoteColorChange + 25L) {
            ++musicNoteColorTick;
            lastMusicNoteColorChange = var0;
            musicNoteColor = ColorLerper.getLerpedColor(ColorLerper.Type.MUSIC_NOTE, (float)musicNoteColorTick);
         }
      }

   }

   private static Component getNowPlayingString(@Nullable String var0) {
      return var0 == null ? Component.empty() : Component.translatable(var0.replace("/", "."));
   }

   public void showToast(Options var1) {
      this.updateToast = true;
      this.notificationDisplayTimeMultiplier = (Double)var1.notificationDisplayTime().get();
      this.setWantedVisibility(Toast.Visibility.SHOW);
   }

   public void update(ToastManager var1, long var2) {
      if (this.updateToast) {
         this.wantedVisibility = (double)var2 < 5000.0 * this.notificationDisplayTimeMultiplier ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
         tickMusicNotes();
      }

   }

   public void render(GuiGraphics var1, Font var2, long var3) {
      renderToast(this.minecraft, var1, var2);
   }

   public void onFinishedRendering() {
      this.updateToast = false;
   }

   public int width() {
      return getWidth(currentSong, this.minecraft.font);
   }

   private static int getWidth(@Nullable String var0, Font var1) {
      return 30 + var1.width((FormattedText)getNowPlayingString(var0)) + 7;
   }

   public int height() {
      return 30;
   }

   public float xPos(int var1, float var2) {
      return (float)this.width() * var2 - (float)this.width();
   }

   public float yPos(int var1) {
      return 0.0F;
   }

   public Toast.Visibility getWantedVisibility() {
      return this.wantedVisibility;
   }

   public void setWantedVisibility(Toast.Visibility var1) {
      this.wantedVisibility = var1;
   }

   static {
      TEXT_COLOR = DyeColor.LIGHT_GRAY.getTextColor();
      musicNoteColor = -1;
   }
}
