package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.world.level.material.FluidState;
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
   private static final int INDENT = 2;
   private static final int NOT_FOUND = -1;

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

   public static CompoundTag writeFluidState(final FluidState state) {
      CompoundTag tag = new CompoundTag();
      tag.putString("id", BuiltInRegistries.FLUID.getKey(state.getType()).toString());
      writeStateProperties(state, tag);
      return tag;
   }

   public static String prettyPrint(final Tag tag, final boolean withBinaryBlobs) {
      return prettyPrint(new StringBuilder(), tag, 0, withBinaryBlobs).toString();
   }

   public static StringBuilder prettyPrint(final StringBuilder builder, final Tag input, final int indent, final boolean withBinaryBlobs) {
      Objects.requireNonNull(input);
      byte var5 = 0;
      StringBuilder var10000;
      //$FF: var5->value
      //0->net/minecraft/nbt/PrimitiveTag
      //1->net/minecraft/nbt/EndTag
      //2->net/minecraft/nbt/ByteArrayTag
      //3->net/minecraft/nbt/ListTag
      //4->net/minecraft/nbt/IntArrayTag
      //5->net/minecraft/nbt/CompoundTag
      //6->net/minecraft/nbt/LongArrayTag
      switch (input.typeSwitch<invokedynamic>(input, var5)) {
         case 0:
            PrimitiveTag primitive = (PrimitiveTag)input;
            var10000 = builder.append(primitive);
            break;
         case 1:
            EndTag ignored = (EndTag)input;
            var10000 = builder;
            break;
         case 2:
            ByteArrayTag tag = (ByteArrayTag)input;
            byte[] array = tag.getAsByteArray();
            int length = array.length;
            indent(indent, builder).append("byte[").append(length).append("] {\n");
            if (!withBinaryBlobs) {
               indent(indent + 1, builder).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(indent + 1, builder);

               for(int i = 0; i < array.length; ++i) {
                  if (i != 0) {
                     builder.append(',');
                  }

                  if (i % 16 == 0 && i / 16 > 0) {
                     builder.append('\n');
                     if (i < array.length) {
                        indent(indent + 1, builder);
                     }
                  } else if (i != 0) {
                     builder.append(' ');
                  }

                  builder.append(String.format(Locale.ROOT, "0x%02X", array[i] & 255));
               }
            }

            builder.append('\n');
            indent(indent, builder).append('}');
            var10000 = builder;
            break;
         case 3:
            ListTag tag = (ListTag)input;
            int size = tag.size();
            indent(indent, builder).append("list").append("[").append(size).append("] [");
            if (size != 0) {
               builder.append('\n');
            }

            for(int i = 0; i < size; ++i) {
               if (i != 0) {
                  builder.append(",\n");
               }

               indent(indent + 1, builder);
               prettyPrint(builder, tag.get(i), indent + 1, withBinaryBlobs);
            }

            if (size != 0) {
               builder.append('\n');
            }

            indent(indent, builder).append(']');
            var10000 = builder;
            break;
         case 4:
            IntArrayTag tag = (IntArrayTag)input;
            int[] array = tag.getAsIntArray();
            int size = 0;

            for(int i : array) {
               size = Math.max(size, String.format(Locale.ROOT, "%X", i).length());
            }

            int length = array.length;
            indent(indent, builder).append("int[").append(length).append("] {\n");
            if (!withBinaryBlobs) {
               indent(indent + 1, builder).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(indent + 1, builder);

               for(int i = 0; i < array.length; ++i) {
                  if (i != 0) {
                     builder.append(',');
                  }

                  if (i % 16 == 0 && i / 16 > 0) {
                     builder.append('\n');
                     if (i < array.length) {
                        indent(indent + 1, builder);
                     }
                  } else if (i != 0) {
                     builder.append(' ');
                  }

                  builder.append(String.format(Locale.ROOT, "0x%0" + size + "X", array[i]));
               }
            }

            builder.append('\n');
            indent(indent, builder).append('}');
            var10000 = builder;
            break;
         case 5:
            CompoundTag tag = (CompoundTag)input;
            List<String> keys = Lists.newArrayList(tag.keySet());
            Collections.sort(keys);
            indent(indent, builder).append('{');
            if (builder.length() - builder.lastIndexOf("\n") > 2 * (indent + 1)) {
               builder.append('\n');
               indent(indent + 1, builder);
            }

            int paddingLength = keys.stream().mapToInt(String::length).max().orElse(0);
            String padding = Strings.repeat(" ", paddingLength);

            for(int i = 0; i < keys.size(); ++i) {
               if (i != 0) {
                  builder.append(",\n");
               }

               String key = (String)keys.get(i);
               indent(indent + 1, builder).append('"').append(key).append('"').append(padding, 0, padding.length() - key.length()).append(": ");
               prettyPrint(builder, tag.get(key), indent + 1, withBinaryBlobs);
            }

            if (!keys.isEmpty()) {
               builder.append('\n');
            }

            indent(indent, builder).append('}');
            var10000 = builder;
            break;
         case 6:
            LongArrayTag tag = (LongArrayTag)input;
            long[] array = tag.getAsLongArray();
            long size = 0L;

            for(long i : array) {
               size = Math.max(size, (long)String.format(Locale.ROOT, "%X", i).length());
            }

            long length = (long)array.length;
            indent(indent, builder).append("long[").append(length).append("] {\n");
            if (!withBinaryBlobs) {
               indent(indent + 1, builder).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(indent + 1, builder);

               for(int i = 0; i < array.length; ++i) {
                  if (i != 0) {
                     builder.append(',');
                  }

                  if (i % 16 == 0 && i / 16 > 0) {
                     builder.append('\n');
                     if (i < array.length) {
                        indent(indent + 1, builder);
                     }
                  } else if (i != 0) {
                     builder.append(' ');
                  }

                  builder.append(String.format(Locale.ROOT, "0x%0" + size + "X", array[i]));
               }
            }

            builder.append('\n');
            indent(indent, builder).append('}');
            var10000 = builder;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static StringBuilder indent(final int indent, final StringBuilder builder) {
      int index = builder.lastIndexOf("\n") + 1;
      int len = builder.length() - index;

      for(int i = 0; i < 2 * indent - len; ++i) {
         builder.append(' ');
      }

      return builder;
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
      Optional<ListTag> palettes = snbt.getList("palettes");
      ListTag palette;
      if (palettes.isPresent()) {
         palette = ((ListTag)palettes.get()).getListOrEmpty(0);
      } else {
         palette = snbt.getListOrEmpty("palette");
      }

      ListTag deflatedPalette = (ListTag)palette.compoundStream().map(NbtUtils::packBlockState).map(StringTag::valueOf).collect(Collectors.toCollection(ListTag::new));
      snbt.put("palette", deflatedPalette);
      if (palettes.isPresent()) {
         ListTag newPalettes = new ListTag();
         ((ListTag)palettes.get()).stream().flatMap((tag) -> tag.asList().stream()).forEach((oldPalette) -> {
            CompoundTag newPalette = new CompoundTag();

            for(int i = 0; i < oldPalette.size(); ++i) {
               newPalette.putString((String)deflatedPalette.getString(i).orElseThrow(), packBlockState((CompoundTag)oldPalette.getCompound(i).orElseThrow()));
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
      ListTag packedPalette = template.getListOrEmpty("palette");
      Map<String, Tag> palette = (Map)packedPalette.stream().flatMap((tag) -> tag.asString().stream()).collect(ImmutableMap.toImmutableMap(Function.identity(), NbtUtils::unpackBlockState));
      Optional<ListTag> oldPalettes = template.getList("palettes");
      if (oldPalettes.isPresent()) {
         template.put("palettes", (Tag)((ListTag)oldPalettes.get()).compoundStream().map((oldPalette) -> (ListTag)palette.keySet().stream().map((key) -> (String)oldPalette.getString(key).orElseThrow()).map(NbtUtils::unpackBlockState).collect(Collectors.toCollection(ListTag::new))).collect(Collectors.toCollection(ListTag::new)));
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
   static String packBlockState(final CompoundTag compound) {
      StringBuilder builder = new StringBuilder((String)compound.getString("id").orElseThrow());
      compound.getCompound("properties").ifPresent((properties) -> {
         String keyValues = (String)properties.entrySet().stream().sorted(Entry.comparingByKey()).map((entry) -> {
            String var10000 = (String)entry.getKey();
            return var10000 + ":" + (String)((Tag)entry.getValue()).asString().orElseThrow();
         }).collect(Collectors.joining(","));
         builder.append('{').append(keyValues).append('}');
      });
      return builder.toString();
   }

   @VisibleForTesting
   static CompoundTag unpackBlockState(final String compound) {
      CompoundTag tag = new CompoundTag();
      int openIndex = compound.indexOf(123);
      String name;
      if (openIndex >= 0) {
         name = compound.substring(0, openIndex);
         CompoundTag properties = new CompoundTag();
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
            tag.put("properties", properties);
         }
      } else {
         name = compound;
      }

      tag.putString("id", name);
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
