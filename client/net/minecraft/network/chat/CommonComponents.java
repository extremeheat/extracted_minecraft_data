package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Collection;

public class CommonComponents {
   public static final Component EMPTY = Component.empty();
   public static final Component OPTION_ON = Component.translatable("options.on");
   public static final Component OPTION_OFF = Component.translatable("options.off");
   public static final Component GUI_DONE = Component.translatable("gui.done");
   public static final Component GUI_CANCEL = Component.translatable("gui.cancel");
   public static final Component GUI_YES = Component.translatable("gui.yes");
   public static final Component GUI_NO = Component.translatable("gui.no");
   public static final Component GUI_OK = Component.translatable("gui.ok");
   public static final Component GUI_PROCEED = Component.translatable("gui.proceed");
   public static final Component GUI_CONTINUE = Component.translatable("gui.continue");
   public static final Component GUI_BACK = Component.translatable("gui.back");
   public static final Component GUI_TO_TITLE = Component.translatable("gui.toTitle");
   public static final Component GUI_ACKNOWLEDGE = Component.translatable("gui.acknowledge");
   public static final Component GUI_OPEN_IN_BROWSER = Component.translatable("chat.link.open");
   public static final Component GUI_COPY_LINK_TO_CLIPBOARD = Component.translatable("gui.copy_link_to_clipboard");
   public static final Component GUI_DISCONNECT = Component.translatable("menu.disconnect");
   public static final Component TRANSFER_CONNECT_FAILED = Component.translatable("connect.failed.transfer");
   public static final Component CONNECT_FAILED = Component.translatable("connect.failed");
   public static final Component NEW_LINE = Component.literal("\n");
   public static final Component NARRATION_SEPARATOR = Component.literal(". ");
   public static final Component ELLIPSIS = Component.literal("...");
   public static final Component SPACE = space();

   public CommonComponents() {
      super();
   }

   public static MutableComponent space() {
      return Component.literal(" ");
   }

   public static MutableComponent days(long var0) {
      return Component.translatable("gui.days", var0);
   }

   public static MutableComponent hours(long var0) {
      return Component.translatable("gui.hours", var0);
   }

   public static MutableComponent minutes(long var0) {
      return Component.translatable("gui.minutes", var0);
   }

   public static Component optionStatus(boolean var0) {
      return var0 ? OPTION_ON : OPTION_OFF;
   }

   public static MutableComponent optionStatus(Component var0, boolean var1) {
      return Component.translatable(var1 ? "options.on.composed" : "options.off.composed", var0);
   }

   public static MutableComponent optionNameValue(Component var0, Component var1) {
      return Component.translatable("options.generic_value", var0, var1);
   }

   public static MutableComponent joinForNarration(Component... var0) {
      MutableComponent var1 = Component.empty();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1.append(var0[var2]);
         if (var2 != var0.length - 1) {
            var1.append(NARRATION_SEPARATOR);
         }
      }

      return var1;
   }

   public static Component joinLines(Component... var0) {
      return joinLines((Collection)Arrays.asList(var0));
   }

   public static Component joinLines(Collection<? extends Component> var0) {
      return ComponentUtils.formatList(var0, NEW_LINE);
   }
}
