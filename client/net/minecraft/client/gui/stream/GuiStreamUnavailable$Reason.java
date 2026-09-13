package net.minecraft.client.gui.stream;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public enum GuiStreamUnavailable$Reason {
   NO_FBO(new ChatComponentTranslation("stream.unavailable.no_fbo")),
   LIBRARY_ARCH_MISMATCH(new ChatComponentTranslation("stream.unavailable.library_arch_mismatch")),
   LIBRARY_FAILURE(new ChatComponentTranslation("stream.unavailable.library_failure"), new ChatComponentTranslation("stream.unavailable.report_to_mojang")),
   UNSUPPORTED_OS_WINDOWS(new ChatComponentTranslation("stream.unavailable.not_supported.windows")),
   UNSUPPORTED_OS_MAC(
      new ChatComponentTranslation("stream.unavailable.not_supported.mac"), new ChatComponentTranslation("stream.unavailable.not_supported.mac.okay")
   ),
   UNSUPPORTED_OS_OTHER(new ChatComponentTranslation("stream.unavailable.not_supported.other")),
   ACCOUNT_NOT_MIGRATED(
      new ChatComponentTranslation("stream.unavailable.account_not_migrated"), new ChatComponentTranslation("stream.unavailable.account_not_migrated.okay")
   ),
   ACCOUNT_NOT_BOUND(
      new ChatComponentTranslation("stream.unavailable.account_not_bound"), new ChatComponentTranslation("stream.unavailable.account_not_bound.okay")
   ),
   FAILED_TWITCH_AUTH(new ChatComponentTranslation("stream.unavailable.failed_auth"), new ChatComponentTranslation("stream.unavailable.failed_auth.okay")),
   FAILED_TWITCH_AUTH_ERROR(new ChatComponentTranslation("stream.unavailable.failed_auth_error")),
   INITIALIZATION_FAILURE(
      new ChatComponentTranslation("stream.unavailable.initialization_failure"), new ChatComponentTranslation("stream.unavailable.report_to_mojang")
   ),
   UNKNOWN(new ChatComponentTranslation("stream.unavailable.unknown"), new ChatComponentTranslation("stream.unavailable.report_to_mojang"));

   private final IChatComponent field_152574_m;
   private final IChatComponent field_152575_n;

   private GuiStreamUnavailable$Reason(IChatComponent var3) {
      this(var3, null);
   }

   private GuiStreamUnavailable$Reason(IChatComponent var3, IChatComponent var4) {
      this.field_152574_m = var3;
      this.field_152575_n = var4;
   }

   public IChatComponent func_152561_a() {
      return this.field_152574_m;
   }

   public IChatComponent func_152559_b() {
      return this.field_152575_n;
   }
}
