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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public record EquipmentClientInfo(Map<LayerType, List<Layer>> layers) {
   private static final Codec<List<Layer>> LAYER_LIST_CODEC;
   public static final Codec<EquipmentClientInfo> CODEC;

   public EquipmentClientInfo(Map<LayerType, List<Layer>> var1) {
      super();
      this.layers = var1;
   }

   public static Builder builder() {
      return new Builder();
   }

   public List<Layer> getLayers(LayerType var1) {
      return (List)this.layers.getOrDefault(var1, List.of());
   }

   public Map<LayerType, List<Layer>> layers() {
      return this.layers;
   }

   static {
      LAYER_LIST_CODEC = ExtraCodecs.nonEmptyList(EquipmentClientInfo.Layer.CODEC.listOf());
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ExtraCodecs.nonEmptyMap(Codec.unboundedMap(EquipmentClientInfo.LayerType.CODEC, LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentClientInfo::layers)).apply(var0, EquipmentClientInfo::new);
      });
   }

   public static class Builder {
      private final Map<LayerType, List<Layer>> layersByType = new EnumMap(LayerType.class);

      Builder() {
         super();
      }

      public Builder addHumanoidLayers(ResourceLocation var1) {
         return this.addHumanoidLayers(var1, false);
      }

      public Builder addHumanoidLayers(ResourceLocation var1, boolean var2) {
         this.addLayers(EquipmentClientInfo.LayerType.HUMANOID_LEGGINGS, EquipmentClientInfo.Layer.leatherDyeable(var1, var2));
         this.addMainHumanoidLayer(var1, var2);
         return this;
      }

      public Builder addMainHumanoidLayer(ResourceLocation var1, boolean var2) {
         return this.addLayers(EquipmentClientInfo.LayerType.HUMANOID, EquipmentClientInfo.Layer.leatherDyeable(var1, var2));
      }

      public Builder addLayers(LayerType var1, Layer... var2) {
         Collections.addAll((Collection)this.layersByType.computeIfAbsent(var1, (var0) -> {
            return new ArrayList();
         }), var2);
         return this;
      }

      public EquipmentClientInfo build() {
         return new EquipmentClientInfo((Map)this.layersByType.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (var0) -> {
            return List.copyOf((Collection)var0.getValue());
         })));
      }
   }

   public static enum LayerType implements StringRepresentable {
      HUMANOID("humanoid"),
      HUMANOID_LEGGINGS("humanoid_leggings"),
      WINGS("wings"),
      WOLF_BODY("wolf_body"),
      HORSE_BODY("horse_body"),
      LLAMA_BODY("llama_body");

      public static final Codec<LayerType> CODEC = StringRepresentable.fromEnum(LayerType::values);
      private final String id;

      private LayerType(final String var3) {
         this.id = var3;
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static LayerType[] $values() {
         return new LayerType[]{HUMANOID, HUMANOID_LEGGINGS, WINGS, WOLF_BODY, HORSE_BODY, LLAMA_BODY};
      }
   }

   public static record Layer(ResourceLocation textureId, Optional<Dyeable> dyeable, boolean usePlayerTexture) {
      public static final Codec<Layer> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ResourceLocation.CODEC.fieldOf("texture").forGetter(Layer::textureId), EquipmentClientInfo.Dyeable.CODEC.optionalFieldOf("dyeable").forGetter(Layer::dyeable), Codec.BOOL.optionalFieldOf("use_player_texture", false).forGetter(Layer::usePlayerTexture)).apply(var0, Layer::new);
      });

      public Layer(ResourceLocation var1) {
         this(var1, Optional.empty(), false);
      }

      public Layer(ResourceLocation var1, Optional<Dyeable> var2, boolean var3) {
         super();
         this.textureId = var1;
         this.dyeable = var2;
         this.usePlayerTexture = var3;
      }

      public static Layer leatherDyeable(ResourceLocation var0, boolean var1) {
         return new Layer(var0, var1 ? Optional.of(new Dyeable(Optional.of(-6265536))) : Optional.empty(), false);
      }

      public static Layer onlyIfDyed(ResourceLocation var0, boolean var1) {
         return new Layer(var0, var1 ? Optional.of(new Dyeable(Optional.empty())) : Optional.empty(), false);
      }

      public ResourceLocation getTextureLocation(LayerType var1) {
         return this.textureId.withPath((var1x) -> {
            String var10000 = var1.getSerializedName();
            return "textures/entity/equipment/" + var10000 + "/" + var1x + ".png";
         });
      }

      public ResourceLocation textureId() {
         return this.textureId;
      }

      public Optional<Dyeable> dyeable() {
         return this.dyeable;
      }

      public boolean usePlayerTexture() {
         return this.usePlayerTexture;
      }
   }

   public static record Dyeable(Optional<Integer> colorWhenUndyed) {
      public static final Codec<Dyeable> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("color_when_undyed").forGetter(Dyeable::colorWhenUndyed)).apply(var0, Dyeable::new);
      });

      public Dyeable(Optional<Integer> var1) {
         super();
         this.colorWhenUndyed = var1;
      }

      public Optional<Integer> colorWhenUndyed() {
         return this.colorWhenUndyed;
      }
   }
}
