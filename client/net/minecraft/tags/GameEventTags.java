package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.world.level.gameevent.GameEvent;

public class GameEventTags {
   protected static final StaticTagHelper<GameEvent> HELPER;
   public static final Tag.Named<GameEvent> VIBRATIONS;
   public static final Tag.Named<GameEvent> IGNORE_VIBRATIONS_SNEAKING;

   public GameEventTags() {
      super();
   }

   private static Tag.Named<GameEvent> bind(String var0) {
      return HELPER.bind(var0);
   }

   public static TagCollection<GameEvent> getAllTags() {
      return HELPER.getAllTags();
   }

   static {
      HELPER = StaticTags.create(Registry.GAME_EVENT_REGISTRY, "tags/game_events");
      VIBRATIONS = bind("vibrations");
      IGNORE_VIBRATIONS_SNEAKING = bind("ignore_vibrations_sneaking");
   }
}
