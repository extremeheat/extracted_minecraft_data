package net.minecraft.client.gui.stream;

import net.minecraft.client.stream.IStream$AuthFailureReason;
import net.minecraft.util.Util$EnumOS;

// $VF: synthetic class
class GuiStreamUnavailable$SwitchReason {
   static {
      try {
         field_152579_c[IStream$AuthFailureReason.INVALID_TOKEN.ordinal()] = 1;
      } catch (NoSuchFieldError var11) {
      }

      try {
         field_152579_c[IStream$AuthFailureReason.ERROR.ordinal()] = 2;
      } catch (NoSuchFieldError var10) {
      }

      field_152578_b = new int[Util$EnumOS.values().length];

      try {
         field_152578_b[Util$EnumOS.WINDOWS.ordinal()] = 1;
      } catch (NoSuchFieldError var9) {
      }

      try {
         field_152578_b[Util$EnumOS.OSX.ordinal()] = 2;
      } catch (NoSuchFieldError var8) {
      }

      field_152577_a = new int[GuiStreamUnavailable$Reason.values().length];

      try {
         field_152577_a[GuiStreamUnavailable$Reason.ACCOUNT_NOT_BOUND.ordinal()] = 1;
      } catch (NoSuchFieldError var7) {
      }

      try {
         field_152577_a[GuiStreamUnavailable$Reason.FAILED_TWITCH_AUTH.ordinal()] = 2;
      } catch (NoSuchFieldError var6) {
      }

      try {
         field_152577_a[GuiStreamUnavailable$Reason.ACCOUNT_NOT_MIGRATED.ordinal()] = 3;
      } catch (NoSuchFieldError var5) {
      }

      try {
         field_152577_a[GuiStreamUnavailable$Reason.UNSUPPORTED_OS_MAC.ordinal()] = 4;
      } catch (NoSuchFieldError var4) {
      }

      try {
         field_152577_a[GuiStreamUnavailable$Reason.UNKNOWN.ordinal()] = 5;
      } catch (NoSuchFieldError var3) {
      }

      try {
         field_152577_a[GuiStreamUnavailable$Reason.LIBRARY_FAILURE.ordinal()] = 6;
      } catch (NoSuchFieldError var2) {
      }

      try {
         field_152577_a[GuiStreamUnavailable$Reason.INITIALIZATION_FAILURE.ordinal()] = 7;
      } catch (NoSuchFieldError var1) {
      }
   }
}
