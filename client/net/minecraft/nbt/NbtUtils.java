package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class NbtUtils {
   private static final Comparator<ListTag> YXZ_LISTTAG_INT_COMPARATOR = Comparator.comparingInt((list) -> list.getIntOr(1, 0)).thenComparingInt((list) -> list.getIntOr(0, 0)).thenComparingInt((list) -> list.getIntOr(2, 0));
   private static final Comparator<ListTag> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.comparingDouble((list) -> list.getDoubleOr(1, 0.0)).thenComparingDouble((list) -> list.getDoubleOr(0, 0.0)).thenComparingDouble((list) -> list.getDoubleOr(2, 0.0));
   private static final Codec<ResourceKey<Block>> BLOCK_NAME_CODEC;
   public static final String SNBT_DATA_TAG = "data";
   private static final char PROPERTIES_START = '{';
   private static final char PROPERTIES_END = '}';
   private static final String ELEMENT_SEPARATOR = ",";
   private static final char KEY_VALUE_SEPARATOR = ':';
   private static final Splitter COMMA_SPLITTER;
   private static final Splitter COLON_SPLITTER;
   private static final Logger LOGGER;
   private static final int NOT_FOUND = -1;
   private static final int BLOCK_STATE_ID_PROPERTIES_RENAME_VERSION = 5006;
   public static final String LEGACY_BLOCK_STATE_ID_TAG = "Name";
   public static final String LEGACY_BLOCKSTATE_PROPERTY_TAG = "Properties";

   private NbtUtils() {
      super();
   }

   @VisibleForTesting
   public static boolean compareNbt(final @Nullable Tag expected, final @Nullable Tag actual, final boolean partialListMatches) {
      if (expected == actual) {
         return true;
      } else if (expected == null) {
         return true;
      } else if (actual == null) {
         return false;
      } else if (!expected.getClass().equals(actual.getClass())) {
         return false;
      } else if (expected instanceof CompoundTag) {
         CompoundTag expectedCompound = (CompoundTag)expected;
         CompoundTag actualCompound = (CompoundTag)actual;
         if (actualCompound.size() < expectedCompound.size()) {
            return false;
         } else {
            for(Map.Entry<String, Tag> entry : expectedCompound.entrySet()) {
               Tag tag = (Tag)entry.getValue();
               if (!compareNbt(tag, actualCompound.get((String)entry.getKey()), partialListMatches)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         if (expected instanceof ListTag) {
            ListTag expectedList = (ListTag)expected;
            if (partialListMatches) {
               ListTag actualList = (ListTag)actual;
               if (expectedList.isEmpty()) {
                  return actualList.isEmpty();
               }

               if (actualList.size() < expectedList.size()) {
                  return false;
               }

               for(Tag tag : expectedList) {
                  boolean found = false;

                  for(Tag value : actualList) {
                     if (compareNbt(tag, value, partialListMatches)) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     return false;
                  }
               }

               return true;
            }
         }

         return expected.equals(actual);
      }
   }

   public static BlockState readBlockState(final HolderGetter<Block> blocks, final CompoundTag tag) {
      Optional var10000 = tag.read("id", BLOCK_NAME_CODEC);
      Objects.requireNonNull(blocks);
      Optional<? extends Holder<Block>> blockHolder = var10000.flatMap(blocks::get);
      if (blockHolder.isEmpty()) {
         return Blocks.AIR.defaultBlockState();
      } else {
         Block block = (Block)((Holder)blockHolder.get()).value();
         BlockState result = block.defaultBlockState();
         Optional<CompoundTag> properties = tag.getCompound("properties");
         if (properties.isPresent()) {
            StateDefinition<Block, BlockState> definition = block.getStateDefinition();

            for(String key : ((CompoundTag)properties.get()).keySet()) {
               Property<?> property = definition.getProperty(key);
               if (property != null) {
                  result = (BlockState)setValueHelper(result, property, key, (CompoundTag)properties.get(), tag);
               }
            }
         }

         return result;
      }
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(final S result, final Property<T> property, final String key, final CompoundTag properties, final CompoundTag tag) {
      Optional var10000 = properties.getString(key);
      Objects.requireNonNull(property);
      Optional<T> value = var10000.flatMap(property::getValue);
      if (value.isPresent()) {
         return (S)(((StateHolder)result).setValue(property, (Comparable)value.get()));
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{key, properties.get(key), tag});
         return result;
      }
   }

   private static void writeStateProperties(final StateHolder<?, ?> state, final CompoundTag tag) {
      if (!state.isSingletonState()) {
         CompoundTag properties = new CompoundTag();
         state.getValues().forEach((value) -> properties.putString(value.property().getName(), value.valueName()));
         tag.put("properties", properties);
      }

   }

   public static CompoundTag writeBlockState(final BlockState state) {
      CompoundTag tag = new CompoundTag();
      tag.putString("id", BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
      writeStateProperties(state, tag);
      return tag;
   }

   public static Component toPrettyComponent(final Tag tag) {
      return (new TextComponentTagVisitor("")).visit(tag);
   }

   public static String structureToSnbt(final CompoundTag structure) {
      return (new SnbtPrinterTagVisitor()).visit(packStructureTemplate(structure));
   }

   public static CompoundTag snbtToStructure(final String snbt) throws CommandSyntaxException {
      return unpackStructureTemplate(TagParser.parseCompoundFully(snbt));
   }

   @VisibleForTesting
   static CompoundTag packStructureTemplate(final CompoundTag snbt) {
      int templateVersion = getDataVersion(snbt);
      Optional<ListTag> palettes = snbt.getList("palettes");
      ListTag palette;
      if (palettes.isPresent()) {
         palette = ((ListTag)palettes.get()).getListOrEmpty(0);
      } else {
         palette = snbt.getListOrEmpty("palette");
      }

      ListTag deflatedPalette = (ListTag)palette.compoundStream().map((compound) -> packBlockState(compound, templateVersion)).map(StringTag::valueOf).collect(Collectors.toCollection(ListTag::new));
      snbt.put("palette", deflatedPalette);
      if (palettes.isPresent()) {
         ListTag newPalettes = new ListTag();
         ((ListTag)palettes.get()).stream().flatMap((tag) -> tag.asList().stream()).forEach((oldPalette) -> {
            CompoundTag newPalette = new CompoundTag();

            for(int i = 0; i < oldPalette.size(); ++i) {
               newPalette.putString((String)deflatedPalette.getString(i).orElseThrow(), packBlockState((CompoundTag)oldPalette.getCompound(i).orElseThrow(), templateVersion));
            }

            newPalettes.add(newPalette);
         });
         snbt.put("palettes", newPalettes);
      }

      Optional<ListTag> oldEntities = snbt.getList("entities");
      if (oldEntities.isPresent()) {
         ListTag newEntities = (ListTag)((ListTag)oldEntities.get()).compoundStream().sorted(Comparator.comparing((tag) -> tag.getList("pos"), Comparators.emptiesLast(YXZ_LISTTAG_DOUBLE_COMPARATOR))).collect(Collectors.toCollection(ListTag::new));
         snbt.put("entities", newEntities);
      }

      ListTag blockData = (ListTag)snbt.getList("blocks").stream().flatMap(ListTag::compoundStream).sorted(Comparator.comparing((tag) -> tag.getList("pos"), Comparators.emptiesLast(YXZ_LISTTAG_INT_COMPARATOR))).peek((block) -> block.putString("state", (String)deflatedPalette.getString(block.getIntOr("state", 0)).orElseThrow())).collect(Collectors.toCollection(ListTag::new));
      snbt.put("data", blockData);
      snbt.remove("blocks");
      return snbt;
   }

   @VisibleForTesting
   static CompoundTag unpackStructureTemplate(final CompoundTag template) {
      int templateVersion = getDataVersion(template);
      ListTag packedPalette = template.getListOrEmpty("palette");
      Map<String, Tag> palette = (Map)packedPalette.stream().flatMap((tag) -> tag.asString().stream()).collect(ImmutableMap.toImmutableMap(Function.identity(), (compound) -> unpackBlockState(compound, templateVersion)));
      Optional<ListTag> oldPalettes = template.getList("palettes");
      if (oldPalettes.isPresent()) {
         template.put("palettes", (Tag)((ListTag)oldPalettes.get()).compoundStream().map((oldPalette) -> (ListTag)palette.keySet().stream().map((key) -> (String)oldPalette.getString(key).orElseThrow()).map((compound) -> unpackBlockState(compound, templateVersion)).collect(Collectors.toCollection(ListTag::new))).collect(Collectors.toCollection(ListTag::new)));
         template.remove("palette");
      } else {
         template.put("palette", (Tag)palette.values().stream().collect(Collectors.toCollection(ListTag::new)));
      }

      Optional<ListTag> maybeBlocks = template.getList("data");
      if (maybeBlocks.isPresent()) {
         Object2IntMap<String> paletteToId = new Object2IntOpenHashMap();
         paletteToId.defaultReturnValue(-1);

         for(int i = 0; i < packedPalette.size(); ++i) {
            paletteToId.put((String)packedPalette.getString(i).orElseThrow(), i);
         }

         ListTag blocks = (ListTag)maybeBlocks.get();

         for(int i = 0; i < blocks.size(); ++i) {
            CompoundTag block = (CompoundTag)blocks.getCompound(i).orElseThrow();
            String stateName = (String)block.getString("state").orElseThrow();
            int stateId = paletteToId.getInt(stateName);
            if (stateId == -1) {
               throw new IllegalStateException("Entry " + stateName + " missing from palette");
            }

            block.putInt("state", stateId);
         }

         template.put("blocks", blocks);
         template.remove("data");
      }

      return template;
   }

   @VisibleForTesting
   static String packBlockState(final CompoundTag compound, final int version) {
      String idTag;
      String propertiesTag;
      if (version >= 5006) {
         idTag = "id";
         propertiesTag = "properties";
      } else {
         idTag = "Name";
         propertiesTag = "Properties";
      }

      StringBuilder builder = new StringBuilder((String)compound.getString(idTag).orElseThrow());
      compound.getCompound(propertiesTag).ifPresent((properties) -> {
         String keyValues = (String)properties.entrySet().stream().sorted(Entry.comparingByKey()).map((entry) -> {
            String var10000 = (String)entry.getKey();
            return var10000 + ":" + (String)((Tag)entry.getValue()).asString().orElseThrow();
         }).collect(Collectors.joining(","));
         builder.append('{').append(keyValues).append('}');
      });
      return builder.toString();
   }

   @VisibleForTesting
   static CompoundTag unpackBlockState(final String compound, final int version) {
      CompoundTag tag = new CompoundTag();
      int openIndex = compound.indexOf(123);
      CompoundTag properties = new CompoundTag();
      String name;
      if (openIndex >= 0) {
         name = compound.substring(0, openIndex);
         if (openIndex + 2 <= compound.length()) {
            String values = compound.substring(openIndex + 1, compound.indexOf(125, openIndex));
            COMMA_SPLITTER.split(values).forEach((keyValue) -> {
               List<String> parts = COLON_SPLITTER.splitToList(keyValue);
               if (parts.size() == 2) {
                  properties.putString((String)parts.get(0), (String)parts.get(1));
               } else {
                  LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", compound);
               }

            });
         }
      } else {
         name = compound;
      }

      if (version >= 5006) {
         tag.putString("id", name);
         if (!properties.isEmpty()) {
            tag.put("properties", properties);
         }
      } else {
         tag.putString("Name", name);
         if (!properties.isEmpty()) {
            tag.put("Properties", properties);
         }
      }

      return tag;
   }

   public static CompoundTag addCurrentDataVersion(final CompoundTag tag) {
      int version = SharedConstants.getCurrentVersion().dataVersion().version();
      return addDataVersion(tag, version);
   }

   public static CompoundTag addDataVersion(final CompoundTag tag, final int version) {
      tag.putInt("DataVersion", version);
      return tag;
   }

   public static <T> Dynamic<T> addDataVersion(final Dynamic<T> tag, final int version) {
      return tag.set("DataVersion", tag.createInt(version));
   }

   public static void addCurrentDataVersion(final ValueOutput output) {
      int version = SharedConstants.getCurrentVersion().dataVersion().version();
      addDataVersion(output, version);
   }

   public static void addDataVersion(final ValueOutput output, final int version) {
      output.putInt("DataVersion", version);
   }

   public static int getDataVersion(final CompoundTag tag) {
      return getDataVersion(tag, -1);
   }

   public static int getDataVersion(final CompoundTag tag, final int _default) {
      return tag.getIntOr("DataVersion", _default);
   }

   public static int getDataVersion(final Dynamic<?> dynamic) {
      return getDataVersion((Dynamic)dynamic, -1);
   }

   public static int getDataVersion(final Dynamic<?> dynamic, final int _default) {
      return dynamic.get("DataVersion").asInt(_default);
   }

   static {
      BLOCK_NAME_CODEC = ResourceKey.codec(Registries.BLOCK);
      COMMA_SPLITTER = Splitter.on(",");
      COLON_SPLITTER = Splitter.on(':').limit(2);
      LOGGER = LogUtils.getLogger();
   }
}
