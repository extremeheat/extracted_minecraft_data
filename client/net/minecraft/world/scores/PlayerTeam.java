package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;

public class PlayerTeam extends Team {
   private final Scoreboard scoreboard;
   private final String name;
   private final Set<String> players = Sets.newHashSet();
   private Component displayName;
   private Component playerPrefix = new TextComponent("");
   private Component playerSuffix = new TextComponent("");
   private boolean allowFriendlyFire = true;
   private boolean seeFriendlyInvisibles = true;
   private Team.Visibility nameTagVisibility;
   private Team.Visibility deathMessageVisibility;
   private ChatFormatting color;
   private Team.CollisionRule collisionRule;

   public PlayerTeam(Scoreboard var1, String var2) {
      super();
      this.nameTagVisibility = Team.Visibility.ALWAYS;
      this.deathMessageVisibility = Team.Visibility.ALWAYS;
      this.color = ChatFormatting.RESET;
      this.collisionRule = Team.CollisionRule.ALWAYS;
      this.scoreboard = var1;
      this.name = var2;
      this.displayName = new TextComponent(var2);
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public Component getFormattedDisplayName() {
      Component var1 = ComponentUtils.wrapInSquareBrackets(this.displayName.deepCopy().withStyle((var1x) -> {
         var1x.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.name)));
      }));
      ChatFormatting var2 = this.getColor();
      if (var2 != ChatFormatting.RESET) {
         var1.withStyle(var2);
      }

      return var1;
   }

   public void setDisplayName(Component var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = var1;
         this.scoreboard.onTeamChanged(this);
      }
   }

   public void setPlayerPrefix(@Nullable Component var1) {
      this.playerPrefix = (Component)(var1 == null ? new TextComponent("") : var1.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerPrefix() {
      return this.playerPrefix;
   }

   public void setPlayerSuffix(@Nullable Component var1) {
      this.playerSuffix = (Component)(var1 == null ? new TextComponent("") : var1.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerSuffix() {
      return this.playerSuffix;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public Component getFormattedName(Component var1) {
      Component var2 = (new TextComponent("")).append(this.playerPrefix).append(var1).append(this.playerSuffix);
      ChatFormatting var3 = this.getColor();
      if (var3 != ChatFormatting.RESET) {
         var2.withStyle(var3);
      }

      return var2;
   }

   public static Component formatNameForTeam(@Nullable Team var0, Component var1) {
      return var0 == null ? var1.deepCopy() : var0.getFormattedName(var1);
   }

   public boolean isAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean var1) {
      this.allowFriendlyFire = var1;
      this.scoreboard.onTeamChanged(this);
   }

   public boolean canSeeFriendlyInvisibles() {
      return this.seeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisibles(boolean var1) {
      this.seeFriendlyInvisibles = var1;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.Visibility getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public Team.Visibility getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.Visibility var1) {
      this.nameTagVisibility = var1;
      this.scoreboard.onTeamChanged(this);
   }

   public void setDeathMessageVisibility(Team.Visibility var1) {
      this.deathMessageVisibility = var1;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(Team.CollisionRule var1) {
      this.collisionRule = var1;
      this.scoreboard.onTeamChanged(this);
   }

   public int packOptions() {
      int var1 = 0;
      if (this.isAllowFriendlyFire()) {
         var1 |= 1;
      }

      if (this.canSeeFriendlyInvisibles()) {
         var1 |= 2;
      }

      return var1;
   }

   public void unpackOptions(int var1) {
      this.setAllowFriendlyFire((var1 & 1) > 0);
      this.setSeeFriendlyInvisibles((var1 & 2) > 0);
   }

   public void setColor(ChatFormatting var1) {
      this.color = var1;
      this.scoreboard.onTeamChanged(this);
   }

   public ChatFormatting getColor() {
      return this.color;
   }
}
