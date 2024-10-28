package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

public class ClickEvent {
   public static final Codec<ClickEvent> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ClickEvent.Action.CODEC.forGetter((var0x) -> {
         return var0x.action;
      }), Codec.STRING.fieldOf("value").forGetter((var0x) -> {
         return var0x.value;
      })).apply(var0, ClickEvent::new);
   });
   private final Action action;
   private final String value;

   public ClickEvent(Action var1, String var2) {
      super();
      this.action = var1;
      this.value = var2;
   }

   public Action getAction() {
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
         return this.action == var2.action && this.value.equals(var2.value);
      } else {
         return false;
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.action);
      return "ClickEvent{action=" + var10000 + ", value='" + this.value + "'}";
   }

   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = 31 * var1 + this.value.hashCode();
      return var1;
   }

   public static enum Action implements StringRepresentable {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true),
      COPY_TO_CLIPBOARD("copy_to_clipboard", true);

      public static final MapCodec<Action> UNSAFE_CODEC = StringRepresentable.fromEnum(Action::values).fieldOf("action");
      public static final MapCodec<Action> CODEC = UNSAFE_CODEC.validate(Action::filterForSerialization);
      private final boolean allowFromServer;
      private final String name;

      private Action(final String var3, final boolean var4) {
         this.name = var3;
         this.allowFromServer = var4;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      public String getSerializedName() {
         return this.name;
      }

      public static DataResult<Action> filterForSerialization(Action var0) {
         return !var0.isAllowedFromServer() ? DataResult.error(() -> {
            return "Action not allowed: " + String.valueOf(var0);
         }) : DataResult.success(var0, Lifecycle.stable());
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD};
      }
   }
}
