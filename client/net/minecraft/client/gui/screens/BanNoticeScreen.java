package net.minecraft.client.gui.screens;

import com.mojang.authlib.minecraft.BanDetails;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.time.Duration;
import java.time.Instant;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.chat.report.ReportReason;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.StringUtils;

public class BanNoticeScreen {
   public static final String URL_MODERATION = "https://aka.ms/mcjavamoderation";
   private static final Component TEMPORARY_BAN_TITLE;
   private static final Component PERMANENT_BAN_TITLE;

   public BanNoticeScreen() {
      super();
   }

   public static ConfirmLinkScreen create(BooleanConsumer var0, BanDetails var1) {
      return new ConfirmLinkScreen(var0, getBannedTitle(var1), getBannedScreenText(var1), "https://aka.ms/mcjavamoderation", CommonComponents.GUI_ACKNOWLEDGE, true);
   }

   private static Component getBannedTitle(BanDetails var0) {
      return isTemporaryBan(var0) ? TEMPORARY_BAN_TITLE : PERMANENT_BAN_TITLE;
   }

   private static Component getBannedScreenText(BanDetails var0) {
      return Component.translatable("gui.banned.description", getBanReasonText(var0), getBanStatusText(var0), Component.literal("https://aka.ms/mcjavamoderation"));
   }

   private static Component getBanReasonText(BanDetails var0) {
      String var1 = var0.reason();
      String var2 = var0.reasonMessage();
      if (StringUtils.isNumeric(var1)) {
         int var3 = Integer.parseInt(var1);
         Component var4 = ReportReason.getTranslationById(var3);
         MutableComponent var5;
         if (var4 != null) {
            var5 = ComponentUtils.mergeStyles(var4.copy(), Style.EMPTY.withBold(true));
         } else if (var2 != null) {
            var5 = Component.translatable("gui.banned.description.reason_id_message", var3, var2).withStyle(ChatFormatting.BOLD);
         } else {
            var5 = Component.translatable("gui.banned.description.reason_id", var3).withStyle(ChatFormatting.BOLD);
         }

         return Component.translatable("gui.banned.description.reason", var5);
      } else {
         return Component.translatable("gui.banned.description.unknownreason");
      }
   }

   private static Component getBanStatusText(BanDetails var0) {
      if (isTemporaryBan(var0)) {
         Component var1 = getBanDurationText(var0);
         return Component.translatable("gui.banned.description.temporary", Component.translatable("gui.banned.description.temporary.duration", var1).withStyle(ChatFormatting.BOLD));
      } else {
         return Component.translatable("gui.banned.description.permanent").withStyle(ChatFormatting.BOLD);
      }
   }

   private static Component getBanDurationText(BanDetails var0) {
      Duration var1 = Duration.between(Instant.now(), var0.expires());
      long var2 = var1.toHours();
      if (var2 > 72L) {
         return CommonComponents.days(var1.toDays());
      } else {
         return var2 < 1L ? CommonComponents.minutes(var1.toMinutes()) : CommonComponents.hours(var1.toHours());
      }
   }

   private static boolean isTemporaryBan(BanDetails var0) {
      return var0.expires() != null;
   }

   static {
      TEMPORARY_BAN_TITLE = Component.translatable("gui.banned.title.temporary").withStyle(ChatFormatting.BOLD);
      PERMANENT_BAN_TITLE = Component.translatable("gui.banned.title.permanent").withStyle(ChatFormatting.BOLD);
   }
}
