package net.minecraft.client.resources.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record EquipmentClientInfo(Map<LayerType, List<Layer>> layers, Map<Identifier, Identifier> trimPaletteReplacements) {
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
      CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.nonEmptyMap(Codec.unboundedMap(EquipmentClientInfo.LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentClientInfo::layers), Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).optionalFieldOf("trim_palette_replacements", Map.of()).forGetter(EquipmentClientInfo::trimPaletteReplacements)).apply(i, EquipmentClientInfo::new));
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
      private final ImmutableMap.Builder<Identifier, Identifier> trimPaletteReplacements = ImmutableMap.builder();

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

      public Builder replaceTrimPalette(final Identifier fromPaletteId, final Identifier toPaletteId) {
         this.trimPaletteReplacements.put(fromPaletteId, toPaletteId);
         return this;
      }

      public EquipmentClientInfo build() {
         return new EquipmentClientInfo((Map)this.layersByType.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> List.copyOf((Collection)entry.getValue()))), this.trimPaletteReplacements.build());
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
}
