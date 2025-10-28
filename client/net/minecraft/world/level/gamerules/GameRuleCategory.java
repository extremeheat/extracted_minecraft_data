package net.minecraft.world.level.gamerules;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record GameRuleCategory(ResourceLocation id) {
   private static final List<GameRuleCategory> SORT_ORDER = new ArrayList();
   public static final GameRuleCategory PLAYER = register("player");
   public static final GameRuleCategory MOBS = register("mobs");
   public static final GameRuleCategory SPAWNING = register("spawning");
   public static final GameRuleCategory DROPS = register("drops");
   public static final GameRuleCategory UPDATES = register("updates");
   public static final GameRuleCategory CHAT = register("chat");
   public static final GameRuleCategory MISC = register("misc");

   public GameRuleCategory(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation getDescriptionId() {
      return this.id;
   }

   private static GameRuleCategory register(String var0) {
      return register(ResourceLocation.withDefaultNamespace(var0));
   }

   public static GameRuleCategory register(ResourceLocation var0) {
      GameRuleCategory var1 = new GameRuleCategory(var0);
      if (SORT_ORDER.contains(var1)) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "Category '%s' is already registered.", var0));
      } else {
         SORT_ORDER.add(var1);
         return var1;
      }
   }

   public MutableComponent label() {
      return Component.translatable(this.id.toLanguageKey("gamerule.category"));
   }
}
