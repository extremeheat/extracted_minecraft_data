package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.ChunkPos;

public class SystemToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/system");
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

   public SystemToast(SystemToastId var1, Component var2, @Nullable Component var3) {
      this(var1, var2, nullToEmpty(var3), Math.max(160, 30 + Math.max(Minecraft.getInstance().font.width((FormattedText)var2), var3 == null ? 0 : Minecraft.getInstance().font.width((FormattedText)var3))));
   }

   public static SystemToast multiline(Minecraft var0, SystemToastId var1, Component var2, Component var3) {
      Font var4 = var0.font;
      List var5 = var4.split(var3, 200);
      Stream var10001 = var5.stream();
      Objects.requireNonNull(var4);
      int var6 = Math.max(200, var10001.mapToInt(var4::width).max().orElse(200));
      return new SystemToast(var1, var2, var5, var6 + 30);
   }

   private SystemToast(SystemToastId var1, Component var2, List<FormattedCharSequence> var3, int var4) {
      super();
      this.wantedVisibility = Toast.Visibility.HIDE;
      this.id = var1;
      this.title = var2;
      this.messageLines = var3;
      this.width = var4;
   }

   private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component var0) {
      return var0 == null ? ImmutableList.of() : ImmutableList.of(var0.getVisualOrderText());
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

   public void update(ToastManager var1, long var2) {
      if (this.changed) {
         this.lastChanged = var2;
         this.changed = false;
      }

      double var4 = (double)this.id.displayTime * var1.getNotificationDisplayTimeMultiplier();
      long var6 = var2 - this.lastChanged;
      this.wantedVisibility = !this.forceHide && (double)var6 < var4 ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
   }

   public void render(GuiGraphics var1, Font var2, long var3) {
      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      if (this.messageLines.isEmpty()) {
         var1.drawString(var2, (Component)this.title, 18, 12, -256, false);
      } else {
         var1.drawString(var2, (Component)this.title, 18, 7, -256, false);

         for(int var5 = 0; var5 < this.messageLines.size(); ++var5) {
            var1.drawString(var2, (FormattedCharSequence)((FormattedCharSequence)this.messageLines.get(var5)), 18, 18 + var5 * 12, -1, false);
         }
      }

   }

   public void reset(Component var1, @Nullable Component var2) {
      this.title = var1;
      this.messageLines = nullToEmpty(var2);
      this.changed = true;
   }

   public SystemToastId getToken() {
      return this.id;
   }

   public static void add(ToastManager var0, SystemToastId var1, Component var2, @Nullable Component var3) {
      var0.addToast(new SystemToast(var1, var2, var3));
   }

   public static void addOrUpdate(ToastManager var0, SystemToastId var1, Component var2, @Nullable Component var3) {
      SystemToast var4 = (SystemToast)var0.getToast(SystemToast.class, var1);
      if (var4 == null) {
         add(var0, var1, var2, var3);
      } else {
         var4.reset(var2, var3);
      }

   }

   public static void forceHide(ToastManager var0, SystemToastId var1) {
      SystemToast var2 = (SystemToast)var0.getToast(SystemToast.class, var1);
      if (var2 != null) {
         var2.forceHide();
      }

   }

   public static void onWorldAccessFailure(Minecraft var0, String var1) {
      add(var0.getToastManager(), SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.access_failure"), Component.literal(var1));
   }

   public static void onWorldDeleteFailure(Minecraft var0, String var1) {
      add(var0.getToastManager(), SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.delete_failure"), Component.literal(var1));
   }

   public static void onPackCopyFailure(Minecraft var0, String var1) {
      add(var0.getToastManager(), SystemToast.SystemToastId.PACK_COPY_FAILURE, Component.translatable("pack.copyFailure"), Component.literal(var1));
   }

   public static void onFileDropFailure(Minecraft var0, int var1) {
      add(var0.getToastManager(), SystemToast.SystemToastId.FILE_DROP_FAILURE, Component.translatable("gui.fileDropFailure.title"), Component.translatable("gui.fileDropFailure.detail", var1));
   }

   public static void onLowDiskSpace(Minecraft var0) {
      addOrUpdate(var0.getToastManager(), SystemToast.SystemToastId.LOW_DISK_SPACE, Component.translatable("chunk.toast.lowDiskSpace"), Component.translatable("chunk.toast.lowDiskSpace.description"));
   }

   public static void onChunkLoadFailure(Minecraft var0, ChunkPos var1) {
      addOrUpdate(var0.getToastManager(), SystemToast.SystemToastId.CHUNK_LOAD_FAILURE, Component.translatable("chunk.toast.loadFailure", Component.translationArg(var1)).withStyle(ChatFormatting.RED), Component.translatable("chunk.toast.checkLog"));
   }

   public static void onChunkSaveFailure(Minecraft var0, ChunkPos var1) {
      addOrUpdate(var0.getToastManager(), SystemToast.SystemToastId.CHUNK_SAVE_FAILURE, Component.translatable("chunk.toast.saveFailure", Component.translationArg(var1)).withStyle(ChatFormatting.RED), Component.translatable("chunk.toast.checkLog"));
   }

   // $FF: synthetic method
   public Object getToken() {
      return this.getToken();
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
      final long displayTime;

      public SystemToastId(long var1) {
         super();
         this.displayTime = var1;
      }

      public SystemToastId() {
         this(5000L);
      }
   }
}
