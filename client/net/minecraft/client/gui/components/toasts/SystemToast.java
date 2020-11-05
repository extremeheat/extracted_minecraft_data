package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

public class SystemToast implements Toast {
   private final SystemToast.SystemToastIds id;
   private Component title;
   private List<FormattedCharSequence> messageLines;
   private long lastChanged;
   private boolean changed;
   private final int width;

   public SystemToast(SystemToast.SystemToastIds var1, Component var2, @Nullable Component var3) {
      this(var1, var2, nullToEmpty(var3), 160);
   }

   public static SystemToast multiline(Minecraft var0, SystemToast.SystemToastIds var1, Component var2, Component var3) {
      Font var4 = var0.font;
      List var5 = var4.split(var3, 200);
      Stream var10001 = var5.stream();
      var4.getClass();
      int var6 = Math.max(200, var10001.mapToInt(var4::width).max().orElse(200));
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

   public int width() {
      return this.width;
   }

   public Toast.Visibility render(PoseStack var1, ToastComponent var2, long var3) {
      if (this.changed) {
         this.lastChanged = var3;
         this.changed = false;
      }

      var2.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      int var5 = this.width();
      boolean var6 = true;
      int var7;
      if (var5 == 160 && this.messageLines.size() <= 1) {
         var2.blit(var1, 0, 0, 0, 64, var5, this.height());
      } else {
         var7 = this.height() + Math.max(0, this.messageLines.size() - 1) * 12;
         boolean var8 = true;
         int var9 = Math.min(4, var7 - 28);
         this.renderBackgroundRow(var1, var2, var5, 0, 0, 28);

         for(int var10 = 28; var10 < var7 - var9; var10 += 10) {
            this.renderBackgroundRow(var1, var2, var5, 16, var10, Math.min(16, var7 - var10 - var9));
         }

         this.renderBackgroundRow(var1, var2, var5, 32 - var9, var7 - var9, var9);
      }

      if (this.messageLines == null) {
         var2.getMinecraft().font.draw(var1, (Component)this.title, 18.0F, 12.0F, -256);
      } else {
         var2.getMinecraft().font.draw(var1, (Component)this.title, 18.0F, 7.0F, -256);

         for(var7 = 0; var7 < this.messageLines.size(); ++var7) {
            var2.getMinecraft().font.draw(var1, (FormattedCharSequence)((FormattedCharSequence)this.messageLines.get(var7)), 18.0F, (float)(18 + var7 * 12), -1);
         }
      }

      return var3 - this.lastChanged < 5000L ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
   }

   private void renderBackgroundRow(PoseStack var1, ToastComponent var2, int var3, int var4, int var5, int var6) {
      int var7 = var4 == 0 ? 20 : 5;
      int var8 = Math.min(60, var3 - var7);
      var2.blit(var1, 0, var5, 0, 64 + var4, var7, var6);

      for(int var9 = var7; var9 < var3 - var8; var9 += 64) {
         var2.blit(var1, var9, var5, 32, 64 + var4, Math.min(64, var3 - var9 - var8), var6);
      }

      var2.blit(var1, var3 - var8, var5, 160 - var8, 64 + var4, var8, var6);
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
      SystemToast var4 = (SystemToast)var0.getToast(SystemToast.class, var1);
      if (var4 == null) {
         add(var0, var1, var2, var3);
      } else {
         var4.reset(var2, var3);
      }

   }

   public static void onWorldAccessFailure(Minecraft var0, String var1) {
      add(var0.getToasts(), SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE, new TranslatableComponent("selectWorld.access_failure"), new TextComponent(var1));
   }

   public static void onWorldDeleteFailure(Minecraft var0, String var1) {
      add(var0.getToasts(), SystemToast.SystemToastIds.WORLD_ACCESS_FAILURE, new TranslatableComponent("selectWorld.delete_failure"), new TextComponent(var1));
   }

   public static void onPackCopyFailure(Minecraft var0, String var1) {
      add(var0.getToasts(), SystemToast.SystemToastIds.PACK_COPY_FAILURE, new TranslatableComponent("pack.copyFailure"), new TextComponent(var1));
   }

   // $FF: synthetic method
   public Object getToken() {
      return this.getToken();
   }

   public static enum SystemToastIds {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP,
      WORLD_GEN_SETTINGS_TRANSFER,
      PACK_LOAD_FAILURE,
      WORLD_ACCESS_FAILURE,
      PACK_COPY_FAILURE;

      private SystemToastIds() {
      }
   }
}
