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

   public abstract Visibility getNameTagVisibility();

   public abstract ChatFormatting getColor();

   public abstract Collection<String> getPlayers();

   public abstract Visibility getDeathMessageVisibility();

   public abstract CollisionRule getCollisionRule();

   public static enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      private static final Map<String, CollisionRule> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
         return var0.name;
      }, (var0) -> {
         return var0;
      }));
      public final String name;
      public final int id;

      @Nullable
      public static CollisionRule byName(String var0) {
         return (CollisionRule)BY_NAME.get(var0);
      }

      private CollisionRule(final String var3, final int var4) {
         this.name = var3;
         this.id = var4;
      }

      public Component getDisplayName() {
         return Component.translatable("team.collision." + this.name);
      }

      // $FF: synthetic method
      private static CollisionRule[] $values() {
         return new CollisionRule[]{ALWAYS, NEVER, PUSH_OTHER_TEAMS, PUSH_OWN_TEAM};
      }
   }

   public static enum Visibility {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Visibility> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((var0) -> {
         return var0.name;
      }, (var0) -> {
         return var0;
      }));
      public final String name;
      public final int id;

      public static String[] getAllNames() {
         return (String[])BY_NAME.keySet().toArray(new String[0]);
      }

      @Nullable
      public static Visibility byName(String var0) {
         return (Visibility)BY_NAME.get(var0);
      }

      private Visibility(final String var3, final int var4) {
         this.name = var3;
         this.id = var4;
      }

      public Component getDisplayName() {
         return Component.translatable("team.visibility." + this.name);
      }

      // $FF: synthetic method
      private static Visibility[] $values() {
         return new Visibility[]{ALWAYS, NEVER, HIDE_FOR_OTHER_TEAMS, HIDE_FOR_OWN_TEAM};
      }
   }
}
