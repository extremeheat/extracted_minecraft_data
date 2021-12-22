package net.minecraft.world.scores;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class Team {
   public Team() {
      super();
   }

   public boolean isAlliedTo(@Nullable Team var1) {
      if (var1 == null) {
         return false;
      } else {
         return this == var1;
      }
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

      private static final Map<String, Team.CollisionRule> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
         return var0.name;
      }, (var0) -> {
         return var0;
      }));
      public final String name;
      // $FF: renamed from: id int
      public final int field_467;

      @Nullable
      public static Team.CollisionRule byName(String var0) {
         return (Team.CollisionRule)BY_NAME.get(var0);
      }

      private CollisionRule(String var3, int var4) {
         this.name = var3;
         this.field_467 = var4;
      }

      public Component getDisplayName() {
         return new TranslatableComponent("team.collision." + this.name);
      }

      // $FF: synthetic method
      private static Team.CollisionRule[] $values() {
         return new Team.CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
      }
   }

   public static enum Visibility {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Team.Visibility> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
         return var0.name;
      }, (var0) -> {
         return var0;
      }));
      public final String name;
      // $FF: renamed from: id int
      public final int field_180;

      public static String[] getAllNames() {
         return (String[])BY_NAME.keySet().toArray(new String[0]);
      }

      @Nullable
      public static Team.Visibility byName(String var0) {
         return (Team.Visibility)BY_NAME.get(var0);
      }

      private Visibility(String var3, int var4) {
         this.name = var3;
         this.field_180 = var4;
      }

      public Component getDisplayName() {
         return new TranslatableComponent("team.visibility." + this.name);
      }

      // $FF: synthetic method
      private static Team.Visibility[] $values() {
         return new Team.Visibility[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
      }
   }
}
