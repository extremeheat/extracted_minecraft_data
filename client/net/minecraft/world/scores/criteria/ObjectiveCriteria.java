package net.minecraft.world.scores.criteria;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatType;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.scores.TeamColor;

public class ObjectiveCriteria {
   private static final Map<String, ObjectiveCriteria> CUSTOM_CRITERIA = Maps.newHashMap();
   private static final Map<String, ObjectiveCriteria> CRITERIA_CACHE = Maps.newHashMap();
   public static final Codec<ObjectiveCriteria> CODEC;
   public static final ObjectiveCriteria DUMMY;
   public static final ObjectiveCriteria TRIGGER;
   public static final ObjectiveCriteria DEATH_COUNT;
   public static final ObjectiveCriteria KILL_COUNT_PLAYERS;
   public static final ObjectiveCriteria KILL_COUNT_ALL;
   public static final ObjectiveCriteria HEALTH;
   public static final ObjectiveCriteria FOOD;
   public static final ObjectiveCriteria AIR;
   public static final ObjectiveCriteria ARMOR;
   public static final ObjectiveCriteria EXPERIENCE;
   public static final ObjectiveCriteria LEVEL;
   public static final Map<TeamColor, ObjectiveCriteria> TEAM_KILL;
   public static final Map<TeamColor, ObjectiveCriteria> KILLED_BY_TEAM;
   private final String name;
   private final boolean readOnly;
   private final RenderType renderType;

   private static Map<TeamColor, ObjectiveCriteria> registerForEveryTeamColor(final Function<TeamColor, String> idFactory) {
      Map<TeamColor, ObjectiveCriteria> result = new EnumMap(TeamColor.class);

      for(TeamColor value : TeamColor.values()) {
         String id = (String)idFactory.apply(value);
         result.put(value, registerCustom(id));
      }

      return result;
   }

   private static ObjectiveCriteria registerCustom(final String name, final boolean readOnly, final RenderType renderType) {
      ObjectiveCriteria result = new ObjectiveCriteria(name, readOnly, renderType);
      CUSTOM_CRITERIA.put(name, result);
      return result;
   }

   private static ObjectiveCriteria registerCustom(final String name) {
      return registerCustom(name, false, ObjectiveCriteria.RenderType.INTEGER);
   }

   protected ObjectiveCriteria(final String name) {
      this(name, false, ObjectiveCriteria.RenderType.INTEGER);
   }

   protected ObjectiveCriteria(final String name, final boolean readOnly, final RenderType renderType) {
      super();
      this.name = name;
      this.readOnly = readOnly;
      this.renderType = renderType;
      CRITERIA_CACHE.put(name, this);
   }

   public static Set<String> getCustomCriteriaNames() {
      return ImmutableSet.copyOf(CUSTOM_CRITERIA.keySet());
   }

   public static Optional<ObjectiveCriteria> byName(final String name) {
      ObjectiveCriteria value = (ObjectiveCriteria)CRITERIA_CACHE.get(name);
      if (value != null) {
         return Optional.of(value);
      } else {
         int colonPos = name.indexOf(58);
         return colonPos < 0 ? Optional.empty() : BuiltInRegistries.STAT_TYPE.getOptional(Identifier.bySeparator(name.substring(0, colonPos), '.')).flatMap((statType) -> getStat(statType, Identifier.bySeparator(name.substring(colonPos + 1), '.')));
      }
   }

   private static <T> Optional<ObjectiveCriteria> getStat(final StatType<T> statType, final Identifier key) {
      Optional var10000 = statType.getRegistry().getOptional(key);
      Objects.requireNonNull(statType);
      return var10000.map(statType::get);
   }

   public String getName() {
      return this.name;
   }

   public boolean isReadOnly() {
      return this.readOnly;
   }

   public RenderType getDefaultRenderType() {
      return this.renderType;
   }

   static {
      CODEC = Codec.STRING.comapFlatMap((name) -> (DataResult)byName(name).map(DataResult::success).orElse(DataResult.error(() -> "No scoreboard criteria with name: " + name)), ObjectiveCriteria::getName);
      DUMMY = registerCustom("dummy");
      TRIGGER = registerCustom("trigger");
      DEATH_COUNT = registerCustom("deathCount");
      KILL_COUNT_PLAYERS = registerCustom("playerKillCount");
      KILL_COUNT_ALL = registerCustom("totalKillCount");
      HEALTH = registerCustom("health", true, ObjectiveCriteria.RenderType.HEARTS);
      FOOD = registerCustom("food", true, ObjectiveCriteria.RenderType.INTEGER);
      AIR = registerCustom("air", true, ObjectiveCriteria.RenderType.INTEGER);
      ARMOR = registerCustom("armor", true, ObjectiveCriteria.RenderType.INTEGER);
      EXPERIENCE = registerCustom("xp", true, ObjectiveCriteria.RenderType.INTEGER);
      LEVEL = registerCustom("level", true, ObjectiveCriteria.RenderType.INTEGER);
      TEAM_KILL = registerForEveryTeamColor((format) -> "teamkill." + format.getSerializedName());
      KILLED_BY_TEAM = registerForEveryTeamColor((format) -> "killedByTeam." + format.getSerializedName());
   }

   public static enum RenderType implements StringRepresentable {
      INTEGER("integer"),
      HEARTS("hearts");

      private final String id;
      public static final StringRepresentable.EnumCodec<RenderType> CODEC = StringRepresentable.<RenderType>fromEnum(RenderType::values);

      private RenderType(final String id) {
         this.id = id;
      }

      public String getId() {
         return this.id;
      }

      public String getSerializedName() {
         return this.id;
      }

      public static RenderType byId(final String key) {
         return (RenderType)CODEC.byName(key, INTEGER);
      }

      // $FF: synthetic method
      private static RenderType[] $values() {
         return new RenderType[]{INTEGER, HEARTS};
      }
   }
}
