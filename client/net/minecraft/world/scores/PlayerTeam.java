package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jspecify.annotations.Nullable;

public class PlayerTeam extends Team {
   private static final byte BIT_FRIENDLY_FIRE = 1;
   private static final byte BIT_SEE_INVISIBLES = 2;
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
   private Optional<TeamColor> color;
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
      this.color = Optional.empty();
      this.collisionRule = Team.CollisionRule.ALWAYS;
      this.scoreboard = scoreboard;
      this.name = name;
      this.displayName = Component.literal(name);
      this.displayNameStyle = Style.EMPTY.withInsertion(name).withHoverEvent(new HoverEvent.ShowText(Component.literal(name)));
   }

   public Packed pack() {
      return new Packed(this.name, Optional.of(this.displayName), this.color, this.allowFriendlyFire, this.seeFriendlyInvisibles, this.playerPrefix, this.playerSuffix, this.nameTagVisibility, this.deathMessageVisibility, this.collisionRule, List.copyOf(this.players));
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

   private MutableComponent applyColor(final MutableComponent result) {
      this.color.ifPresent((teamColor) -> result.withColor(teamColor.textColor()));
      return result;
   }

   public MutableComponent getFormattedDisplayName() {
      return this.applyColor(ComponentUtils.wrapInSquareBrackets(this.displayName.copy().withStyle(this.displayNameStyle)));
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
      return this.applyColor(Component.empty().append(this.playerPrefix).append(teamMemberName).append(this.playerSuffix));
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

   public @PlayerTeam.OptionFlags byte packOptions() {
      byte result = 0;
      if (this.isAllowFriendlyFire()) {
         result = (byte)(result | 1);
      }

      if (this.canSeeFriendlyInvisibles()) {
         result = (byte)(result | 2);
      }

      return result;
   }

   public void unpackOptions(final @PlayerTeam.OptionFlags byte options) {
      this.setAllowFriendlyFire((options & 1) != 0);
      this.setSeeFriendlyInvisibles((options & 2) != 0);
   }

   public void setColor(final Optional<TeamColor> color) {
      this.color = color;
      this.scoreboard.onTeamChanged(this);
   }

   public Optional<TeamColor> getColor() {
      return this.color;
   }

   public static record Packed(String name, Optional<Component> displayName, Optional<TeamColor> color, boolean allowFriendlyFire, boolean seeFriendlyInvisibles, Component memberNamePrefix, Component memberNameSuffix, Team.Visibility nameTagVisibility, Team.Visibility deathMessageVisibility, Team.CollisionRule collisionRule, List<String> players) {
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("Name").forGetter(Packed::name), ComponentSerialization.CODEC.optionalFieldOf("DisplayName").forGetter(Packed::displayName), TeamColor.CODEC.optionalFieldOf("TeamColor").forGetter(Packed::color), Codec.BOOL.optionalFieldOf("AllowFriendlyFire", true).forGetter(Packed::allowFriendlyFire), Codec.BOOL.optionalFieldOf("SeeFriendlyInvisibles", true).forGetter(Packed::seeFriendlyInvisibles), ComponentSerialization.CODEC.optionalFieldOf("MemberNamePrefix", CommonComponents.EMPTY).forGetter(Packed::memberNamePrefix), ComponentSerialization.CODEC.optionalFieldOf("MemberNameSuffix", CommonComponents.EMPTY).forGetter(Packed::memberNameSuffix), Team.Visibility.CODEC.optionalFieldOf("NameTagVisibility", Team.Visibility.ALWAYS).forGetter(Packed::nameTagVisibility), Team.Visibility.CODEC.optionalFieldOf("DeathMessageVisibility", Team.Visibility.ALWAYS).forGetter(Packed::deathMessageVisibility), Team.CollisionRule.CODEC.optionalFieldOf("CollisionRule", Team.CollisionRule.ALWAYS).forGetter(Packed::collisionRule), Codec.STRING.listOf().optionalFieldOf("Players", List.of()).forGetter(Packed::players)).apply(i, Packed::new));

      public Packed {
         super();
      }
   }

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.TYPE_USE})
   public @interface OptionFlags {
   }
}
