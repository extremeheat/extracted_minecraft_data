package net.minecraft.world.entity.animal.golem;

import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.WeatheringCopper;

public class CopperGolemOxidationLevels {
   private static final CopperGolemOxidationLevel UNAFFECTED;
   private static final CopperGolemOxidationLevel EXPOSED;
   private static final CopperGolemOxidationLevel WEATHERED;
   private static final CopperGolemOxidationLevel OXIDIZED;
   private static final Map<WeatheringCopper.WeatherState, CopperGolemOxidationLevel> WEATHERED_STATES;

   public CopperGolemOxidationLevels() {
      super();
   }

   public static CopperGolemOxidationLevel getOxidationLevel(WeatheringCopper.WeatherState var0) {
      return (CopperGolemOxidationLevel)WEATHERED_STATES.get(var0);
   }

   static {
      UNAFFECTED = new CopperGolemOxidationLevel(SoundEvents.COPPER_GOLEM_SPIN, SoundEvents.COPPER_GOLEM_HURT, SoundEvents.COPPER_GOLEM_DEATH, SoundEvents.COPPER_GOLEM_STEP, Identifier.withDefaultNamespace("textures/entity/copper_golem/copper_golem.png"), Identifier.withDefaultNamespace("textures/entity/copper_golem/copper_golem_eyes.png"));
      EXPOSED = new CopperGolemOxidationLevel(SoundEvents.COPPER_GOLEM_SPIN, SoundEvents.COPPER_GOLEM_HURT, SoundEvents.COPPER_GOLEM_DEATH, SoundEvents.COPPER_GOLEM_STEP, Identifier.withDefaultNamespace("textures/entity/copper_golem/exposed_copper_golem.png"), Identifier.withDefaultNamespace("textures/entity/copper_golem/exposed_copper_golem_eyes.png"));
      WEATHERED = new CopperGolemOxidationLevel(SoundEvents.COPPER_GOLEM_WEATHERED_SPIN, SoundEvents.COPPER_GOLEM_WEATHERED_HURT, SoundEvents.COPPER_GOLEM_WEATHERED_DEATH, SoundEvents.COPPER_GOLEM_WEATHERED_STEP, Identifier.withDefaultNamespace("textures/entity/copper_golem/weathered_copper_golem.png"), Identifier.withDefaultNamespace("textures/entity/copper_golem/weathered_copper_golem_eyes.png"));
      OXIDIZED = new CopperGolemOxidationLevel(SoundEvents.COPPER_GOLEM_OXIDIZED_SPIN, SoundEvents.COPPER_GOLEM_OXIDIZED_HURT, SoundEvents.COPPER_GOLEM_OXIDIZED_DEATH, SoundEvents.COPPER_GOLEM_OXIDIZED_STEP, Identifier.withDefaultNamespace("textures/entity/copper_golem/oxidized_copper_golem.png"), Identifier.withDefaultNamespace("textures/entity/copper_golem/oxidized_copper_golem_eyes.png"));
      WEATHERED_STATES = Map.of(WeatheringCopper.WeatherState.UNAFFECTED, UNAFFECTED, WeatheringCopper.WeatherState.EXPOSED, EXPOSED, WeatheringCopper.WeatherState.WEATHERED, WEATHERED, WeatheringCopper.WeatherState.OXIDIZED, OXIDIZED);
   }
}
