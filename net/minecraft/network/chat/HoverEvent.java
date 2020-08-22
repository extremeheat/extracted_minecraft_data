package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HoverEvent {
   private final HoverEvent.Action action;
   private final Component value;

   public HoverEvent(HoverEvent.Action var1, Component var2) {
      this.action = var1;
      this.value = var2;
   }

   public HoverEvent.Action getAction() {
      return this.action;
   }

   public Component getValue() {
      return this.value;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         HoverEvent var2 = (HoverEvent)var1;
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
      return "HoverEvent{action=" + this.action + ", value='" + this.value + '\'' + '}';
   }

   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = 31 * var1 + (this.value != null ? this.value.hashCode() : 0);
      return var1;
   }

   public static enum Action {
      SHOW_TEXT("show_text", true),
      SHOW_ITEM("show_item", true),
      SHOW_ENTITY("show_entity", true);

      private static final Map LOOKUP = (Map)Arrays.stream(values()).collect(Collectors.toMap(HoverEvent.Action::getName, (var0) -> {
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

      public static HoverEvent.Action getByName(String var0) {
         return (HoverEvent.Action)LOOKUP.get(var0);
      }
   }
}
