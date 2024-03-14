package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;

public class ExplorationMapFunction extends LootItemConditionalFunction {
   public static final TagKey<Structure> DEFAULT_DESTINATION = StructureTags.ON_TREASURE_MAPS;
   public static final MapDecoration.Type DEFAULT_DECORATION = MapDecoration.Type.MANSION;
   public static final byte DEFAULT_ZOOM = 2;
   public static final int DEFAULT_SEARCH_RADIUS = 50;
   public static final boolean DEFAULT_SKIP_EXISTING = true;
   public static final Codec<ExplorationMapFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  ExtraCodecs.strictOptionalField(TagKey.codec(Registries.STRUCTURE), "destination", DEFAULT_DESTINATION).forGetter(var0x -> var0x.destination),
                  MapDecoration.Type.CODEC.optionalFieldOf("decoration", DEFAULT_DECORATION).forGetter(var0x -> var0x.mapDecoration),
                  ExtraCodecs.strictOptionalField(Codec.BYTE, "zoom", (byte)2).forGetter(var0x -> var0x.zoom),
                  ExtraCodecs.strictOptionalField(Codec.INT, "search_radius", 50).forGetter(var0x -> var0x.searchRadius),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "skip_existing_chunks", true).forGetter(var0x -> var0x.skipKnownStructures)
               )
            )
            .apply(var0, ExplorationMapFunction::new)
   );
   private final TagKey<Structure> destination;
   private final MapDecoration.Type mapDecoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipKnownStructures;

   ExplorationMapFunction(List<LootItemCondition> var1, TagKey<Structure> var2, MapDecoration.Type var3, byte var4, int var5, boolean var6) {
      super(var1);
      this.destination = var2;
      this.mapDecoration = var3;
      this.zoom = var4;
      this.searchRadius = var5;
      this.skipKnownStructures = var6;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.EXPLORATION_MAP;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ORIGIN);
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      if (!var1.is(Items.MAP)) {
         return var1;
      } else {
         Vec3 var3 = var2.getParamOrNull(LootContextParams.ORIGIN);
         if (var3 != null) {
            ServerLevel var4 = var2.getLevel();
            BlockPos var5 = var4.findNearestMapStructure(this.destination, BlockPos.containing(var3), this.searchRadius, this.skipKnownStructures);
            if (var5 != null) {
               ItemStack var6 = MapItem.create(var4, var5.getX(), var5.getZ(), this.zoom, true, true);
               MapItem.renderBiomePreviewMap(var4, var6);
               MapItemSavedData.addTargetDecoration(var6, var5, "+", this.mapDecoration);
               return var6;
            }
         }

         return var1;
      }
   }

   public static ExplorationMapFunction.Builder makeExplorationMap() {
      return new ExplorationMapFunction.Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<ExplorationMapFunction.Builder> {
      private TagKey<Structure> destination = ExplorationMapFunction.DEFAULT_DESTINATION;
      private MapDecoration.Type mapDecoration = ExplorationMapFunction.DEFAULT_DECORATION;
      private byte zoom = 2;
      private int searchRadius = 50;
      private boolean skipKnownStructures = true;

      public Builder() {
         super();
      }

      protected ExplorationMapFunction.Builder getThis() {
         return this;
      }

      public ExplorationMapFunction.Builder setDestination(TagKey<Structure> var1) {
         this.destination = var1;
         return this;
      }

      public ExplorationMapFunction.Builder setMapDecoration(MapDecoration.Type var1) {
         this.mapDecoration = var1;
         return this;
      }

      public ExplorationMapFunction.Builder setZoom(byte var1) {
         this.zoom = var1;
         return this;
      }

      public ExplorationMapFunction.Builder setSearchRadius(int var1) {
         this.searchRadius = var1;
         return this;
      }

      public ExplorationMapFunction.Builder setSkipKnownStructures(boolean var1) {
         this.skipKnownStructures = var1;
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new ExplorationMapFunction(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
      }
   }
}
