package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ClickEvent {
   private final ClickEvent.Action action;
   private final String value;

   public ClickEvent(ClickEvent.Action var1, String var2) {
      this.action = var1;
      this.value = var2;
   }

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         ClickEvent var2 = (ClickEvent)var1;
         if (this.action != var2.action) {
            return false;
         } else {
            if (this.value != null) {
               if (!this.value.equals(var2.value)) {
                  return false;
               }
            } else if (var2.value != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public String toString() {
      return "ClickEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
      return var1;
   }

   public static enum Action {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true),
      COPY_TO_CLIPBOARD("copy_to_clipboard", true);

      private static final Map LOOKUP = (Map)Arrays.stream(values()).collect(Collectors.toMap(ClickEvent.Action::getName, (var0) -> {
         return var0;
      }));
      private final boolean allowFromServer;
      private final String name;

      private Action(String var3, boolean var4) {
         this.name = var3;
         this.allowFromServer = var4;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getName() {
         return this.name;
      }

      public static ClickEvent.Action getByName(String var0) {
         return (ClickEvent.Action)LOOKUP.get(var0);
      }
   }
}
