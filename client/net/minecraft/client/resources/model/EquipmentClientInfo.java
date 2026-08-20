package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

public record EquipmentClientInfo(Map<LayerType, List<Layer>> layers, List<TrimOverride> trimOverrides) {
   private static final Codec<List<Layer>> LAYER_LIST_CODEC;
   public static final Codec<EquipmentClientInfo> CODEC;

   public EquipmentClientInfo {
      super();
   }

   public static Builder builder() {
      return new Builder();
   }

   public List<Layer> getLayers(final LayerType type) {
      return (List)this.layers.getOrDefault(type, List.of());
   }

   static {
      LAYER_LIST_CODEC = ExtraCodecs.nonEmptyList(EquipmentClientInfo.Layer.CODEC.listOf());
      CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.nonEmptyMap(Codec.unboundedMap(EquipmentClientInfo.LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentClientInfo::layers), EquipmentClientInfo.TrimOverride.CODEC.listOf().optionalFieldOf("trim_overrides", List.of()).forGetter(EquipmentClientInfo::trimOverrides)).apply(i, EquipmentClientInfo::new));
   }

   public static record Layer(Identifier textureId, Optional<Dyeable> dyeable, boolean usePlayerTexture) {
      public static final Codec<Layer> CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(Layer::textureId), EquipmentClientInfo.Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(Layer::dyeable), Codec.BOOL.optionalFieldOf("use_player_texture", false).forGetter(Layer::usePlayerTexture)).apply(i, Layer::new));

      public Layer(final Identifier textureId) {
         this(textureId, Optional.empty(), false);
      }

      public Layer {
         super();
      }

      public static Layer leatherDyeable(final Identifier textureId, final boolean dyeable) {
         return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
      }

      public static Layer onlyIfDyed(final Identifier textureId, final boolean dyeable) {
         return new Layer(textureId, dyeable ? Optional.of(new Dyeable(Optional.empty())) : Optional.empty(), false);
      }

      public Identifier getTextureLocation(final LayerType type) {
         return this.textureId.withPath((UnaryOperator)((path) -> {
            String var10000 = type.getSerializedName();
            return "textures/entity/equipment/" + var10000 + "/" + path + ".png";
         }));
      }
   }

   public static record Dyeable(Optional<Integer> colorWhenUndyed) {
      public static final Codec<Dyeable> CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color_when_undyed").forGetter(Dyeable::colorWhenUndyed)).apply(i, Dyeable::new));

      public Dyeable {
         super();
      }
   }

   public static class Builder {
      private final Map<LayerType, List<Layer>> layersByType = new EnumMap(LayerType.class);
      private final ImmutableList.Builder<TrimOverride> trimOverrides = ImmutableList.builder();

      private Builder() {
         super();
      }

      public Builder addHumanoidLayers(final Identifier textureId) {
         return this.addHumanoidLayers(textureId, false);
      }

      public Builder addHumanoidLayers(final Identifier textureId, final boolean dyeable) {
         this.addLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, EquipmentClientInfo.Layer.leatherDyeable(textureId, dyeable));
         this.addMainHumanoidLayer(textureId, dyeable);
         return this;
      }

      public Builder addMainHumanoidLayer(final Identifier textureId, final boolean dyeable) {
         this.addLayers(EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.Layer.leatherDyeable(textureId, dyeable));
         this.addLayers(EquipmentClientInfo.LayerType.HUMANOID_BABY, EquipmentClientInfo.Layer.leatherDyeable(textureId, dyeable));
         return this;
      }

      public Builder addLayers(final LayerType type, final Layer... layers) {
         Collections.addAll((Collection)this.layersByType.computeIfAbsent(type, (t) -> new ArrayList()), layers);
         return this;
      }

      public Builder replaceTrimPalette(final ResourceKey<TrimMaterial> fromMaterial, final Identifier toPaletteId) {
         TrimPredicate predicate = new TrimPredicate(Optional.of(fromMaterial), Optional.empty());
         this.trimOverrides.add(new TrimOverride(predicate, Optional.empty(), Optional.of(toPaletteId)));
         return this;
      }

      public EquipmentClientInfo build() {
         return new EquipmentClientInfo((Map)this.layersByType.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> List.copyOf((Collection)entry.getValue()))), this.trimOverrides.build());
      }
   }

   public static enum LayerType implements StringRepresentable {
      HUMANOID("humanoid"),
      HUMANOID_LEGGINGS("humanoid_leggings"),
      HUMANOID_BABY("humanoid_baby"),
      WINGS("wings"),
      WOLF_BODY("wolf_body"),
      HORSE_BODY("horse_body"),
      LLAMA_BODY("llama_body"),
      PIG_SADDLE("pig_saddle"),
      STRIDER_SADDLE("strider_saddle"),
      CAMEL_SADDLE("camel_saddle"),
      CAMEL_HUSK_SADDLE("camel_husk_saddle"),
      HORSE_SADDLE("horse_saddle"),
      DONKEY_SADDLE("donkey_saddle"),
      MULE_SADDLE("mule_saddle"),
      ZOMBIE_HORSE_SADDLE("zombie_horse_saddle"),
      SKELETON_HORSE_SADDLE("skeleton_horse_saddle"),
      HAPPY_GHAST_BODY("happy_ghast_body"),
      NAUTILUS_SADDLE("nautilus_saddle"),
      NAUTILUS_BODY("nautilus_body");

      public static final Codec<LayerType> CODEC = StringRepresentable.<LayerType>fromEnum(LayerType::values);
      private final String id;

      private LayerType(final String id) {
         this.id = id;
      }

      public String getSerializedName() {
         return this.id;
      }

      public String trimAssetPrefix() {
         return "trims/entity/" + this.id;
      }

      // $FF: synthetic method
      private static LayerType[] $values() {
         return new LayerType[]{HUMANOID, HUMANOID_LEGGINGS, HUMANOID_BABY, WINGS, WOLF_BODY, HORSE_BODY, LLAMA_BODY, PIG_SADDLE, STRIDER_SADDLE, CAMEL_SADDLE, CAMEL_HUSK_SADDLE, HORSE_SADDLE, DONKEY_SADDLE, MULE_SADDLE, ZOMBIE_HORSE_SADDLE, SKELETON_HORSE_SADDLE, HAPPY_GHAST_BODY, NAUTILUS_SADDLE, NAUTILUS_BODY};
      }
   }

   public static record TrimOverride(TrimPredicate predicate, Optional<Identifier> textureId, Optional<Identifier> paletteId) {
      public static final Codec<TrimOverride> CODEC = RecordCodecBuilder.create((i) -> i.group(EquipmentClientInfo.TrimPredicate.CODEC.fieldOf("when").forGetter(TrimOverride::predicate), Identifier.CODEC.optionalFieldOf("texture").forGetter(TrimOverride::textureId), Identifier.CODEC.optionalFieldOf("palette").forGetter(TrimOverride::paletteId)).apply(i, TrimOverride::new)).validate(TrimOverride::validate);

      public TrimOverride {
         super();
      }

      private static DataResult<TrimOverride> validate(final TrimOverride override) {
         return override.textureId.isEmpty() && override.paletteId.isEmpty() ? DataResult.error(() -> "One of texture or palette must be specified") : DataResult.success(override);
      }
   }

   public static record TrimPredicate(Optional<ResourceKey<TrimMaterial>> material, Optional<ResourceKey<TrimPattern>> pattern) {
      public static final Codec<TrimPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(ResourceKey.codec(Registries.TRIM_MATERIAL).optionalFieldOf("material").forGetter(TrimPredicate::material), ResourceKey.codec(Registries.TRIM_PATTERN).optionalFieldOf("pattern").forGetter(TrimPredicate::pattern)).apply(i, TrimPredicate::new)).validate(TrimPredicate::validate);

      public TrimPredicate {
         super();
      }

      private static DataResult<TrimPredicate> validate(final TrimPredicate override) {
         return override.material.isEmpty() && override.pattern.isEmpty() ? DataResult.error(() -> "One of material or pattern must be specified") : DataResult.success(override);
      }

      public boolean matches(final ArmorTrim trim) {
         return (this.material.isEmpty() || trim.material().is((ResourceKey)this.material.get())) && (this.pattern.isEmpty() || trim.pattern().is((ResourceKey)this.pattern.get()));
      }
   }
}
