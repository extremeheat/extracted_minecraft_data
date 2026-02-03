package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record BedRule(Rule canSleep, Rule canSetSpawn, boolean explodes, Optional<Component> errorMessage) {
   public static final BedRule CAN_SLEEP_WHEN_DARK;
   public static final BedRule EXPLODES;
   public static final Codec<BedRule> CODEC;

   public BedRule {
      super();
   }

   public boolean canSleep(final Level level) {
      return this.canSleep.test(level);
   }

   public boolean canSetSpawn(final Level level) {
      return this.canSetSpawn.test(level);
   }

   public Player.BedSleepingProblem asProblem() {
      return new Player.BedSleepingProblem((Component)this.errorMessage.orElse((Object)null));
   }

   static {
      CAN_SLEEP_WHEN_DARK = new BedRule(BedRule.Rule.WHEN_DARK, BedRule.Rule.ALWAYS, false, Optional.of(Component.translatable("block.minecraft.bed.no_sleep")));
      EXPLODES = new BedRule(BedRule.Rule.NEVER, BedRule.Rule.NEVER, true, Optional.empty());
      CODEC = RecordCodecBuilder.create((i) -> i.group(BedRule.Rule.CODEC.fieldOf("can_sleep").forGetter(BedRule::canSleep), BedRule.Rule.CODEC.fieldOf("can_set_spawn").forGetter(BedRule::canSetSpawn), Codec.BOOL.optionalFieldOf("explodes", false).forGetter(BedRule::explodes), ComponentSerialization.CODEC.optionalFieldOf("error_message").forGetter(BedRule::errorMessage)).apply(i, BedRule::new));
   }

   public static enum Rule implements StringRepresentable {
      ALWAYS("always"),
      WHEN_DARK("when_dark"),
      NEVER("never");

      public static final Codec<Rule> CODEC = StringRepresentable.<Rule>fromEnum(Rule::values);
      private final String name;

      private Rule(final String name) {
         this.name = name;
      }

      public boolean test(final Level level) {
         boolean var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = true;
            case 1 -> var10000 = level.isDarkOutside();
            case 2 -> var10000 = false;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Rule[] $values() {
         return new Rule[]{ALWAYS, WHEN_DARK, NEVER};
      }
   }
}
