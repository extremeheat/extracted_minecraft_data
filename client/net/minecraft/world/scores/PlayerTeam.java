package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class PlayerTeam extends Team {
   private static final int BIT_FRIENDLY_FIRE = 0;
   private static final int BIT_SEE_INVISIBLES = 1;
   private final Scoreboard scoreboard;
   private final String name;
   private final Set<String> players = Sets.newHashSet();
   private Component displayName;
   private Component playerPrefix;
   private Component playerSuffix;
   private boolean allowFriendlyFire;
   private boolean seeFriendlyInvisibles;
   private Team.Visibility nameTagVisibility;
   private Team.Visibility deathMessageVisibility;
   private ChatFormatting color;
   private Team.CollisionRule collisionRule;
   private final Style displayNameStyle;

   public PlayerTeam(Scoreboard var1, String var2) {
      super();
      this.playerPrefix = CommonComponents.EMPTY;
      this.playerSuffix = CommonComponents.EMPTY;
      this.allowFriendlyFire = true;
      this.seeFriendlyInvisibles = true;
      this.nameTagVisibility = Team.Visibility.ALWAYS;
      this.deathMessageVisibility = Team.Visibility.ALWAYS;
      this.color = ChatFormatting.RESET;
      this.collisionRule = Team.CollisionRule.ALWAYS;
      this.scoreboard = var1;
      this.name = var2;
      this.displayName = Component.literal(var2);
      this.displayNameStyle = Style.EMPTY.withInsertion(var2).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(var2)));
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public MutableComponent getFormattedDisplayName() {
      MutableComponent var1 = ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle(this.displayNameStyle));
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
      this.playerPrefix = var1 == null ? CommonComponents.EMPTY : var1;
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerPrefix() {
      return this.playerPrefix;
   }

   public void setPlayerSuffix(@Nullable Component var1) {
      this.playerSuffix = var1 == null ? CommonComponents.EMPTY : var1;
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerSuffix() {
      return this.playerSuffix;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public MutableComponent getFormattedName(Component var1) {
      MutableComponent var2 = Component.empty().append(this.playerPrefix).append(var1).append(this.playerSuffix);
      ChatFormatting var3 = this.getColor();
      if (var3 != ChatFormatting.RESET) {
         var2.withStyle(var3);
      }

      return var2;
   }

   public static MutableComponent formatNameForTeam(@Nullable Team var0, Component var1) {
      return var0 == null ? var1.copy() : var0.getFormattedName(var1);
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
