package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public class SystemToast implements Toast {
   private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/system");
   private static final int MAX_LINE_SIZE = 200;
   private static final int LINE_SPACING = 12;
   private static final int MARGIN = 10;
   private final SystemToastId id;
   private Component title;
   private List<FormattedCharSequence> messageLines;
   private long lastChanged;
   private boolean changed;
   private final int width;
   private boolean forceHide;
   private Toast.Visibility wantedVisibility;

   public SystemToast(final SystemToastId id, final Component title, final @Nullable Component message) {
      this(id, title, nullToEmpty(message), Math.max(160, 30 + Math.max(Minecraft.getInstance().font.width((FormattedText)title), message == null ? 0 : Minecraft.getInstance().font.width((FormattedText)message))));
   }

   public static SystemToast multiline(final Minecraft minecraft, final SystemToastId id, final Component title, final Component message) {
      Font font = minecraft.font;
      List<FormattedCharSequence> lines = font.split(message, 200);
      Stream var10001 = lines.stream();
      Objects.requireNonNull(font);
      int width = Math.max(200, var10001.mapToInt(font::width).max().orElse(200));
      return new SystemToast(id, title, lines, width + 30);
   }

   private SystemToast(final SystemToastId id, final Component title, final List<FormattedCharSequence> messageLines, final int width) {
      super();
      this.wantedVisibility = Toast.Visibility.HIDE;
      this.id = id;
      this.title = title;
      this.messageLines = messageLines;
      this.width = width;
   }

   private static ImmutableList<FormattedCharSequence> nullToEmpty(final @Nullable Component message) {
      return message == null ? ImmutableList.of() : ImmutableList.of(message.getVisualOrderText());
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return 20 + Math.max(this.messageLines.size(), 1) * 12;
   }

   public void forceHide() {
      this.forceHide = true;
   }

   public Toast.Visibility getWantedVisibility() {
      return this.wantedVisibility;
   }

   public void update(final ToastManager manager, final long fullyVisibleForMs) {
      if (this.changed) {
         this.lastChanged = fullyVisibleForMs;
         this.changed = false;
      }

      double timeToDisplayUpdate = (double)this.id.displayTime * manager.getNotificationDisplayTimeMultiplier();
      long timeSinceUpdate = fullyVisibleForMs - this.lastChanged;
      this.wantedVisibility = !this.forceHide && (double)timeSinceUpdate < timeToDisplayUpdate ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
   }

   public void render(final GuiGraphics graphics, final Font font, final long fullyVisibleForMs) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      if (this.messageLines.isEmpty()) {
         graphics.drawString(font, (Component)this.title, 18, 12, -256, false);
      } else {
         graphics.drawString(font, (Component)this.title, 18, 7, -256, false);

         for(int i = 0; i < this.messageLines.size(); ++i) {
            graphics.drawString(font, (FormattedCharSequence)((FormattedCharSequence)this.messageLines.get(i)), 18, 18 + i * 12, -1, false);
         }
      }

   }

   public void reset(final Component title, final @Nullable Component message) {
      this.title = title;
      this.messageLines = nullToEmpty(message);
      this.changed = true;
   }

   public SystemToastId getToken() {
      return this.id;
   }

   public static void add(final ToastManager toastManager, final SystemToastId id, final Component title, final @Nullable Component message) {
      toastManager.addToast(new SystemToast(id, title, message));
   }

   public static void addOrUpdate(final ToastManager toastManager, final SystemToastId id, final Component title, final @Nullable Component message) {
      SystemToast toast = (SystemToast)toastManager.getToast(SystemToast.class, id);
      if (toast == null) {
         add(toastManager, id, title, message);
      } else {
         toast.reset(title, message);
      }

   }

   public static void forceHide(final ToastManager toastManager, final SystemToastId id) {
      SystemToast toast = (SystemToast)toastManager.getToast(SystemToast.class, id);
      if (toast != null) {
         toast.forceHide();
      }

   }

   public static void onWorldAccessFailure(final Minecraft minecraft, final String levelId) {
      add(minecraft.getToastManager(), SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.access_failure"), Component.literal(levelId));
   }

   public static void onWorldDeleteFailure(final Minecraft minecraft, final String levelId) {
      add(minecraft.getToastManager(), SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.delete_failure"), Component.literal(levelId));
   }

   public static void onPackCopyFailure(final Minecraft minecraft, final String extraInfo) {
      add(minecraft.getToastManager(), SystemToast.SystemToastId.PACK_COPY_FAILURE, Component.translatable("pack.copyFailure"), Component.literal(extraInfo));
   }

   public static void onFileDropFailure(final Minecraft minecraft, final int count) {
      add(minecraft.getToastManager(), SystemToast.SystemToastId.FILE_DROP_FAILURE, Component.translatable("gui.fileDropFailure.title"), Component.translatable("gui.fileDropFailure.detail", count));
   }

   public static void onLowDiskSpace(final Minecraft minecraft) {
      addOrUpdate(minecraft.getToastManager(), SystemToast.SystemToastId.LOW_DISK_SPACE, Component.translatable("chunk.toast.lowDiskSpace"), Component.translatable("chunk.toast.lowDiskSpace.description"));
   }

   public static void onChunkLoadFailure(final Minecraft minecraft, final ChunkPos pos) {
      addOrUpdate(minecraft.getToastManager(), SystemToast.SystemToastId.CHUNK_LOAD_FAILURE, Component.translatable("chunk.toast.loadFailure", Component.translationArg(pos)).withStyle(ChatFormatting.RED), Component.translatable("chunk.toast.checkLog"));
   }

   public static void onChunkSaveFailure(final Minecraft minecraft, final ChunkPos pos) {
      addOrUpdate(minecraft.getToastManager(), SystemToast.SystemToastId.CHUNK_SAVE_FAILURE, Component.translatable("chunk.toast.saveFailure", Component.translationArg(pos)).withStyle(ChatFormatting.RED), Component.translatable("chunk.toast.checkLog"));
   }

   public static class SystemToastId {
      public static final SystemToastId NARRATOR_TOGGLE = new SystemToastId();
      public static final SystemToastId WORLD_BACKUP = new SystemToastId();
      public static final SystemToastId PACK_LOAD_FAILURE = new SystemToastId();
      public static final SystemToastId WORLD_ACCESS_FAILURE = new SystemToastId();
      public static final SystemToastId PACK_COPY_FAILURE = new SystemToastId();
      public static final SystemToastId FILE_DROP_FAILURE = new SystemToastId();
      public static final SystemToastId PERIODIC_NOTIFICATION = new SystemToastId();
      public static final SystemToastId LOW_DISK_SPACE = new SystemToastId(10000L);
      public static final SystemToastId CHUNK_LOAD_FAILURE = new SystemToastId();
      public static final SystemToastId CHUNK_SAVE_FAILURE = new SystemToastId();
      public static final SystemToastId UNSECURE_SERVER_WARNING = new SystemToastId(10000L);
      private final long displayTime;

      public SystemToastId(final long displayTime) {
         super();
         this.displayTime = displayTime;
      }

      public SystemToastId() {
         this(5000L);
      }
   }
}
