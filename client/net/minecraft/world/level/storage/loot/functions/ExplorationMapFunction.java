package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.logging.LogUtils;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
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
import org.slf4j.Logger;

public class ExplorationMapFunction extends LootItemConditionalFunction {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final TagKey<Structure> DEFAULT_DESTINATION = StructureTags.ON_TREASURE_MAPS;
   public static final String DEFAULT_DECORATION_NAME = "mansion";
   public static final MapDecoration.Type DEFAULT_DECORATION = MapDecoration.Type.MANSION;
   public static final byte DEFAULT_ZOOM = 2;
   public static final int DEFAULT_SEARCH_RADIUS = 50;
   public static final boolean DEFAULT_SKIP_EXISTING = true;
   final TagKey<Structure> destination;
   final MapDecoration.Type mapDecoration;
   final byte zoom;
   final int searchRadius;
   final boolean skipKnownStructures;

   ExplorationMapFunction(LootItemCondition[] var1, TagKey<Structure> var2, MapDecoration.Type var3, byte var4, int var5, boolean var6) {
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
            BlockPos var5 = var4.findNearestMapStructure(this.destination, new BlockPos(var3), this.searchRadius, this.skipKnownStructures);
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

   public static class Serializer extends LootItemConditionalFunction.Serializer<ExplorationMapFunction> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ExplorationMapFunction var2, JsonSerializationContext var3) {
         super.serialize(var1, var2, var3);
         if (!var2.destination.equals(ExplorationMapFunction.DEFAULT_DESTINATION)) {
            var1.addProperty("destination", var2.destination.location().toString());
         }

         if (var2.mapDecoration != ExplorationMapFunction.DEFAULT_DECORATION) {
            var1.add("decoration", var3.serialize(var2.mapDecoration.toString().toLowerCase(Locale.ROOT)));
         }

         if (var2.zoom != 2) {
            var1.addProperty("zoom", var2.zoom);
         }

         if (var2.searchRadius != 50) {
            var1.addProperty("search_radius", var2.searchRadius);
         }

         if (!var2.skipKnownStructures) {
            var1.addProperty("skip_existing_chunks", var2.skipKnownStructures);
         }
      }

      public ExplorationMapFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         TagKey var4 = readStructure(var1);
         String var5 = var1.has("decoration") ? GsonHelper.getAsString(var1, "decoration") : "mansion";
         MapDecoration.Type var6 = ExplorationMapFunction.DEFAULT_DECORATION;

         try {
            var6 = MapDecoration.Type.valueOf(var5.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException var10) {
            ExplorationMapFunction.LOGGER
               .error("Error while parsing loot table decoration entry. Found {}. Defaulting to {}", var5, ExplorationMapFunction.DEFAULT_DECORATION);
         }

         byte var7 = GsonHelper.getAsByte(var1, "zoom", (byte)2);
         int var8 = GsonHelper.getAsInt(var1, "search_radius", 50);
         boolean var9 = GsonHelper.getAsBoolean(var1, "skip_existing_chunks", true);
         return new ExplorationMapFunction(var3, var4, var6, var7, var8, var9);
      }

      private static TagKey<Structure> readStructure(JsonObject var0) {
         if (var0.has("destination")) {
            String var1 = GsonHelper.getAsString(var0, "destination");
            return TagKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(var1));
         } else {
            return ExplorationMapFunction.DEFAULT_DESTINATION;
         }
      }
   }
}
