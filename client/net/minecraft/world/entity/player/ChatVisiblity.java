package net.minecraft.world.entity.player;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum ChatVisiblity implements OptionEnum {
   FULL(0, "options.chat.visibility.full"),
   SYSTEM(1, "options.chat.visibility.system"),
   HIDDEN(2, "options.chat.visibility.hidden");

   private static final IntFunction<ChatVisiblity> BY_ID = ByIdMap.<ChatVisiblity>continuous(ChatVisiblity::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String key;

   private ChatVisiblity(final int var3, final String var4) {
      this.id = var3;
      this.key = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static ChatVisiblity byId(int var0) {
      return (ChatVisiblity)BY_ID.apply(var0);
   }

   // $FF: synthetic method
   private static ChatVisiblity[] $values() {
      return new ChatVisiblity[]{FULL, SYSTEM, HIDDEN};
   }
}
