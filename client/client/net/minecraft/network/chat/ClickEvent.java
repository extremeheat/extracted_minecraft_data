package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

public class ClickEvent {
   public static final Codec<ClickEvent> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ClickEvent.Action.CODEC.forGetter(var0x -> var0x.action), Codec.STRING.fieldOf("value").forGetter(var0x -> var0x.value))
            .apply(var0, ClickEvent::new)
   );
   private final ClickEvent.Action action;
   private final String value;

   public ClickEvent(ClickEvent.Action var1, String var2) {
      super();
      this.action = var1;
      this.value = var2;
   }

   public ClickEvent.Action getAction() {
      return this.action;
   }

   public String getValue() {
      return this.value;
   }

   @Override
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

   @Override
   public String toString() {
      return "ClickEvent{action=" + this.action + ", value='" + this.value + "'}";
   }

   @Override
   public int hashCode() {
      int var1 = this.action.hashCode();
      return 31 * var1 + this.value.hashCode();
   }

   public static enum Action implements StringRepresentable {
      OPEN_URL("open_url", true),
      OPEN_FILE("open_file", false),
      RUN_COMMAND("run_command", true),
      SUGGEST_COMMAND("suggest_command", true),
      CHANGE_PAGE("change_page", true),
      COPY_TO_CLIPBOARD("copy_to_clipboard", true);

      public static final MapCodec<ClickEvent.Action> UNSAFE_CODEC = StringRepresentable.fromEnum(ClickEvent.Action::values).fieldOf("action");
      public static final MapCodec<ClickEvent.Action> CODEC = UNSAFE_CODEC.validate(ClickEvent.Action::filterForSerialization);
      private final boolean allowFromServer;
      private final String name;

      private Action(final String param3, final boolean param4) {
         this.name = nullxx;
         this.allowFromServer = nullxxx;
      }

      public boolean isAllowedFromServer() {
         return this.allowFromServer;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public static DataResult<ClickEvent.Action> filterForSerialization(ClickEvent.Action var0) {
         return !var0.isAllowedFromServer() ? DataResult.error(() -> "Action not allowed: " + var0) : DataResult.success(var0, Lifecycle.stable());
      }
   }
}
