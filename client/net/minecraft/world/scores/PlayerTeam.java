package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

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

   public PlayerTeam(final Scoreboard scoreboard, final String name) {
      super();
      this.playerPrefix = CommonComponents.EMPTY;
      this.playerSuffix = CommonComponents.EMPTY;
      this.allowFriendlyFire = true;
      this.seeFriendlyInvisibles = true;
      this.nameTagVisibility = Team.Visibility.ALWAYS;
      this.deathMessageVisibility = Team.Visibility.ALWAYS;
      this.color = ChatFormatting.RESET;
      this.collisionRule = Team.CollisionRule.ALWAYS;
      this.scoreboard = scoreboard;
      this.name = name;
      this.displayName = Component.literal(name);
      this.displayNameStyle = Style.EMPTY.withInsertion(name).withHoverEvent(new HoverEvent.ShowText(Component.literal(name)));
   }

   public Packed pack() {
      return new Packed(this.name, Optional.of(this.displayName), this.color != ChatFormatting.RESET ? Optional.of(this.color) : Optional.empty(), this.allowFriendlyFire, this.seeFriendlyInvisibles, this.playerPrefix, this.playerSuffix, this.nameTagVisibility, this.deathMessageVisibility, this.collisionRule, List.copyOf(this.players));
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
      MutableComponent result = ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle(this.displayNameStyle));
      ChatFormatting color = this.getColor();
      if (color != ChatFormatting.RESET) {
         result.withStyle(color);
      }

      return result;
   }

   public void setDisplayName(final Component displayName) {
      if (displayName == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = displayName;
         this.scoreboard.onTeamChanged(this);
      }
   }

   public void setPlayerPrefix(final @Nullable Component playerPrefix) {
      this.playerPrefix = playerPrefix == null ? CommonComponents.EMPTY : playerPrefix;
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerPrefix() {
      return this.playerPrefix;
   }

   public void setPlayerSuffix(final @Nullable Component playerSuffix) {
      this.playerSuffix = playerSuffix == null ? CommonComponents.EMPTY : playerSuffix;
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerSuffix() {
      return this.playerSuffix;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public MutableComponent getFormattedName(final Component teamMemberName) {
      MutableComponent result = Component.empty().append(this.playerPrefix).append(teamMemberName).append(this.playerSuffix);
      ChatFormatting color = this.getColor();
      if (color != ChatFormatting.RESET) {
         result.withStyle(color);
      }

      return result;
   }

   public static MutableComponent formatNameForTeam(final @Nullable Team team, final Component name) {
      return team == null ? name.copy() : team.getFormattedName(name);
   }

   public boolean isAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(final boolean allowFriendlyFire) {
      this.allowFriendlyFire = allowFriendlyFire;
      this.scoreboard.onTeamChanged(this);
   }

   public boolean canSeeFriendlyInvisibles() {
      return this.seeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisibles(final boolean seeFriendlyInvisibles) {
      this.seeFriendlyInvisibles = seeFriendlyInvisibles;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.Visibility getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public Team.Visibility getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(final Team.Visibility visibility) {
      this.nameTagVisibility = visibility;
      this.scoreboard.onTeamChanged(this);
   }

   public void setDeathMessageVisibility(final Team.Visibility visibility) {
      this.deathMessageVisibility = visibility;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(final Team.CollisionRule collisionRule) {
      this.collisionRule = collisionRule;
      this.scoreboard.onTeamChanged(this);
   }

   public int packOptions() {
      int result = 0;
      if (this.isAllowFriendlyFire()) {
         result |= 1;
      }

      if (this.canSeeFriendlyInvisibles()) {
         result |= 2;
      }

      return result;
   }

   public void unpackOptions(final int options) {
      this.setAllowFriendlyFire((options & 1) > 0);
      this.setSeeFriendlyInvisibles((options & 2) > 0);
   }

   public void setColor(final ChatFormatting color) {
      this.color = color;
      this.scoreboard.onTeamChanged(this);
   }

   public ChatFormatting getColor() {
      return this.color;
   }

   public static record Packed(String name, Optional<Component> displayName, Optional<ChatFormatting> color, boolean allowFriendlyFire, boolean seeFriendlyInvisibles, Component memberNamePrefix, Component memberNameSuffix, Team.Visibility nameTagVisibility, Team.Visibility deathMessageVisibility, Team.CollisionRule collisionRule, List<String> players) {
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("Name").forGetter(Packed::name), ComponentSerialization.CODEC.optionalFieldOf("DisplayName").forGetter(Packed::displayName), ChatFormatting.COLOR_CODEC.optionalFieldOf("TeamColor").forGetter(Packed::color), Codec.BOOL.optionalFieldOf("AllowFriendlyFire", true).forGetter(Packed::allowFriendlyFire), Codec.BOOL.optionalFieldOf("SeeFriendlyInvisibles", true).forGetter(Packed::seeFriendlyInvisibles), ComponentSerialization.CODEC.optionalFieldOf("MemberNamePrefix", CommonComponents.EMPTY).forGetter(Packed::memberNamePrefix), ComponentSerialization.CODEC.optionalFieldOf("MemberNameSuffix", CommonComponents.EMPTY).forGetter(Packed::memberNameSuffix), Team.Visibility.CODEC.optionalFieldOf("NameTagVisibility", Team.Visibility.ALWAYS).forGetter(Packed::nameTagVisibility), Team.Visibility.CODEC.optionalFieldOf("DeathMessageVisibility", Team.Visibility.ALWAYS).forGetter(Packed::deathMessageVisibility), Team.CollisionRule.CODEC.optionalFieldOf("CollisionRule", Team.CollisionRule.ALWAYS).forGetter(Packed::collisionRule), Codec.STRING.listOf().optionalFieldOf("Players", List.of()).forGetter(Packed::players)).apply(i, Packed::new));

      public Packed {
         super();
      }
   }
}
