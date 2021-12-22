package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Collection;

public class CommonComponents {
   public static final Component OPTION_ON = new TranslatableComponent("options.on");
   public static final Component OPTION_OFF = new TranslatableComponent("options.off");
   public static final Component GUI_DONE = new TranslatableComponent("gui.done");
   public static final Component GUI_CANCEL = new TranslatableComponent("gui.cancel");
   public static final Component GUI_YES = new TranslatableComponent("gui.yes");
   public static final Component GUI_NO = new TranslatableComponent("gui.no");
   public static final Component GUI_PROCEED = new TranslatableComponent("gui.proceed");
   public static final Component GUI_BACK = new TranslatableComponent("gui.back");
   public static final Component CONNECT_FAILED = new TranslatableComponent("connect.failed");
   public static final Component NEW_LINE = new TextComponent("\n");
   public static final Component NARRATION_SEPARATOR = new TextComponent(". ");

   public CommonComponents() {
      super();
   }

   public static Component optionStatus(boolean var0) {
      return var0 ? OPTION_ON : OPTION_OFF;
   }

   public static MutableComponent optionStatus(Component var0, boolean var1) {
      return new TranslatableComponent(var1 ? "options.on.composed" : "options.off.composed", new Object[]{var0});
   }

   public static MutableComponent optionNameValue(Component var0, Component var1) {
      return new TranslatableComponent("options.generic_value", new Object[]{var0, var1});
   }

   public static MutableComponent joinForNarration(Component var0, Component var1) {
      return (new TextComponent("")).append(var0).append(NARRATION_SEPARATOR).append(var1);
   }

   public static Component joinLines(Component... var0) {
      return joinLines((Collection)Arrays.asList(var0));
   }

   public static Component joinLines(Collection<? extends Component> var0) {
      return ComponentUtils.formatList(var0, NEW_LINE);
   }
}
