package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class SystemToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("toast/system");
   private static final int MAX_LINE_SIZE = 200;
   private static final int LINE_SPACING = 12;
   private static final int MARGIN = 10;
   private final SystemToast.SystemToastIds id;
   private Component title;
   private List<FormattedCharSequence> messageLines;
   private long lastChanged;
   private boolean changed;
   private final int width;

   public SystemToast(SystemToast.SystemToastIds var1, Component var2, @Nullable Component var3) {
      this(
         var1,
         var2,
         nullToEmpty(var3),
         Math.max(160, 30 + Math.max(Minecraft.getInstance().font.width(var2), var3 == null ? 0 : Minecraft.getInstance().font.width(var3)))
      );
   }

   public static SystemToast multiline(Minecraft var0, SystemToast.SystemToastIds var1, Component var2, Component var3) {
      Font var4 = var0.font;
      List var5 = var4.split(var3, 200);
      int var6 = Math.max(200, var5.stream().mapToInt(var4::width).max().orElse(200));
      return new SystemToast(var1, var2, var5, var6 + 30);
   }

   private SystemToast(SystemToast.SystemToastIds var1, Component var2, List<FormattedCharSequence> var3, int var4) {
      super();
      this.id = var1;
      this.title = var2;
      this.messageLines = var3;
      this.width = var4;
   }

   private static ImmutableList<FormattedCharSequence> nullToEmpty(@Nullable Component var0) {
      return var0 == null ? ImmutableList.of() : ImmutableList.of(var0.getVisualOrderText());
   }

   @Override
   public int width() {
      return this.width;
   }

   @Override
   public int height() {
      return 20 + Math.max(this.messageLines.size(), 1) * 12;
   }

   @Override
   public Toast.Visibility render(GuiGraphics var1, ToastComponent var2, long var3) {
      if (this.changed) {
         this.lastChanged = var3;
         this.changed = false;
      }

      int var5 = this.width();
      if (var5 == 160 && this.messageLines.size() <= 1) {
         var1.blitSprite(BACKGROUND_SPRITE, 0, 0, var5, this.height());
      } else {
         int var6 = this.height();
         boolean var7 = true;
         int var8 = Math.min(4, var6 - 28);
         this.renderBackgroundRow(var1, var5, 0, 0, 28);

         for(int var9 = 28; var9 < var6 - var8; var9 += 10) {
            this.renderBackgroundRow(var1, var5, 16, var9, Math.min(16, var6 - var9 - var8));
         }

         this.renderBackgroundRow(var1, var5, 32 - var8, var6 - var8, var8);
      }

      if (this.messageLines == null) {
         var1.drawString(var2.getMinecraft().font, this.title, 18, 12, -256, false);
      } else {
         var1.drawString(var2.getMinecraft().font, this.title, 18, 7, -256, false);

         for(int var10 = 0; var10 < this.messageLines.size(); ++var10) {
            var1.drawString(var2.getMinecraft().font, this.messageLines.get(var10), 18, 18 + var10 * 12, -1, false);
         }
      }

      return (double)(var3 - this.lastChanged) < (double)this.id.displayTime * var2.getNotificationDisplayTimeMultiplier()
         ? Toast.Visibility.SHOW
         : Toast.Visibility.HIDE;
   }

   private void renderBackgroundRow(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 == 0 ? 20 : 5;
      int var7 = Math.min(60, var2 - var6);
      ResourceLocation var8 = BACKGROUND_SPRITE;
      var1.blitSprite(var8, 160, 32, 0, var3, 0, var4, var6, var5);

      for(int var9 = var6; var9 < var2 - var7; var9 += 64) {
         var1.blitSprite(var8, 160, 32, 32, var3, var9, var4, Math.min(64, var2 - var9 - var7), var5);
      }

      var1.blitSprite(var8, 160, 32, 160 - var7, var3, var2 - var7, var4, var7, var5);
   }

   public void reset(Component var1, @Nullable Component var2) {
      this.title = var1;
      this.messageLines = nullToEmpty(var2);
      this.changed = true;
   }

   public SystemToast.SystemToastIds getToken() {
      return this.id;
   }

   public static void add(ToastComponent var0, SystemToast.SystemToastIds var1, Component var2, @Nullable Component var3) {
      var0.addToast(new SystemToast(var1, var2, var3));
   }

   public static void addOrUpdate(ToastComponent var0, SystemToast.SystemToastIds var1, Component var2, @Nullable Component var3) {
      SystemToast var4 = var0.getToast(SystemToast.class, var1);
      if (var4 == null) {
         add(var0, var1, var2, var3);
      } else {
         var4.reset(var2, var3);
      }
   }

   public static void onWorldAccessFailure(Minecraft var0, String var1) {
      add(var0.getToasts(), SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.access_failure"), Component.literal(var1));
   }

   public static void onWorldDeleteFailure(Minecraft var0, String var1) {
      add(var0.getToasts(), SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE, Component.translatable("selectWorld.delete_failure"), Component.literal(var1));
   }

   public static void onPackCopyFailure(Minecraft var0, String var1) {
      add(var0.getToasts(), SystemToast.SystemToastIds.PACK_COPY_FAILURE, Component.translatable("pack.copyFailure"), Component.literal(var1));
   }

   public static enum SystemToastIds {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP,
      PACK_LOAD_FAILURE,
      WORLD_ACCESS_FAILURE,
      PACK_COPY_FAILURE,
      PERIODIC_NOTIFICATION,
      UNSECURE_SERVER_WARNING(10000L);

      final long displayTime;

      private SystemToastIds(long var3) {
         this.displayTime = var3;
      }

      private SystemToastIds() {
         this(5000L);
      }
   }
}
