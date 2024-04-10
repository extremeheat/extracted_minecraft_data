package net.minecraft.world.scores;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class Team {
   public Team() {
      super();
   }

   public boolean isAlliedTo(@Nullable Team var1) {
      return var1 == null ? false : this == var1;
   }

   public abstract String getName();

   public abstract MutableComponent getFormattedName(Component var1);

   public abstract boolean canSeeFriendlyInvisibles();

   public abstract boolean isAllowFriendlyFire();

   public abstract Team.Visibility getNameTagVisibility();

   public abstract ChatFormatting getColor();

   public abstract Collection<String> getPlayers();

   public abstract Team.Visibility getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public static enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      private static final Map<String, Team.CollisionRule> BY_NAME = Arrays.stream(values())
         .collect(Collectors.toMap(var0 -> var0.name, var0 -> (Team.CollisionRule)var0));
      public final String name;
      public final int id;

      @Nullable
      public static Team.CollisionRule byName(String var0) {
         return BY_NAME.get(var0);
      }

      private CollisionRule(final String param3, final int param4) {
         this.name = nullxx;
         this.id = nullxxx;
      }

      public Component getDisplayName() {
         return Component.translatable("team.collision." + this.name);
      }
   }

   public static enum Visibility {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Team.Visibility> BY_NAME = Arrays.stream(values())
         .collect(Collectors.toMap(var0 -> var0.name, var0 -> (Team.Visibility)var0));
      public final String name;
      public final int id;

      public static String[] getAllNames() {
         return BY_NAME.keySet().toArray(new String[0]);
      }

      @Nullable
      public static Team.Visibility byName(String var0) {
         return BY_NAME.get(var0);
      }

      private Visibility(final String param3, final int param4) {
         this.name = nullxx;
         this.id = nullxxx;
      }

      public Component getDisplayName() {
         return Component.translatable("team.visibility." + this.name);
      }
   }
}
