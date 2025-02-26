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
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
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
import javax.annotation.Nullable;
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
import org.slf4j.Logger;

public final class NbtUtils {
   private static final Comparator<ListTag> YXZ_LISTTAG_INT_COMPARATOR = Comparator.comparingInt((var0) -> var0.getIntOr(1, 0)).thenComparingInt((var0) -> var0.getIntOr(0, 0)).thenComparingInt((var0) -> var0.getIntOr(2, 0));
   private static final Comparator<ListTag> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.comparingDouble((var0) -> var0.getDoubleOr(1, 0.0)).thenComparingDouble((var0) -> var0.getDoubleOr(0, 0.0)).thenComparingDouble((var0) -> var0.getDoubleOr(2, 0.0));
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
   public static boolean compareNbt(@Nullable Tag var0, @Nullable Tag var1, boolean var2) {
      if (var0 == var1) {
         return true;
      } else if (var0 == null) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!var0.getClass().equals(var1.getClass())) {
         return false;
      } else if (var0 instanceof CompoundTag) {
         CompoundTag var3 = (CompoundTag)var0;
         CompoundTag var11 = (CompoundTag)var1;
         if (var11.size() < var3.size()) {
            return false;
         } else {
            for(Map.Entry var13 : var3.entrySet()) {
               Tag var14 = (Tag)var13.getValue();
               if (!compareNbt(var14, var11.get((String)var13.getKey()), var2)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         if (var0 instanceof ListTag) {
            ListTag var4 = (ListTag)var0;
            if (var2) {
               ListTag var5 = (ListTag)var1;
               if (var4.isEmpty()) {
                  return var5.isEmpty();
               }

               if (var5.size() < var4.size()) {
                  return false;
               }

               for(Tag var7 : var4) {
                  boolean var8 = false;

                  for(Tag var10 : var5) {
                     if (compareNbt(var7, var10, var2)) {
                        var8 = true;
                        break;
                     }
                  }

                  if (!var8) {
                     return false;
                  }
               }

               return true;
            }
         }

         return var0.equals(var1);
      }
   }

   public static BlockState readBlockState(HolderGetter<Block> var0, CompoundTag var1) {
      Optional var10000 = var1.read("Name", BLOCK_NAME_CODEC);
      Objects.requireNonNull(var0);
      Optional var2 = var10000.flatMap(var0::get);
      if (var2.isEmpty()) {
         return Blocks.AIR.defaultBlockState();
      } else {
         Block var3 = (Block)((Holder)var2.get()).value();
         BlockState var4 = var3.defaultBlockState();
         Optional var5 = var1.getCompound("Properties");
         if (var5.isPresent()) {
            StateDefinition var6 = var3.getStateDefinition();

            for(String var8 : ((CompoundTag)var5.get()).keySet()) {
               Property var9 = var6.getProperty(var8);
               if (var9 != null) {
                  var4 = (BlockState)setValueHelper(var4, var9, var8, (CompoundTag)var5.get(), var1);
               }
            }
         }

         return var4;
      }
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S var0, Property<T> var1, String var2, CompoundTag var3, CompoundTag var4) {
      Optional var10000 = var3.getString(var2);
      Objects.requireNonNull(var1);
      Optional var5 = var10000.flatMap(var1::getValue);
      if (var5.isPresent()) {
         return (S)(var0.setValue(var1, (Comparable)var5.get()));
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{var2, var3.get(var2), var4});
         return (S)var0;
      }
   }

   public static CompoundTag writeBlockState(BlockState var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", BuiltInRegistries.BLOCK.getKey(var0.getBlock()).toString());
      Map var2 = var0.getValues();
      if (!var2.isEmpty()) {
         CompoundTag var3 = new CompoundTag();

         for(Map.Entry var5 : var2.entrySet()) {
            Property var6 = (Property)var5.getKey();
            var3.putString(var6.getName(), getName(var6, (Comparable)var5.getValue()));
         }

         var1.put("Properties", var3);
      }

      return var1;
   }

   public static CompoundTag writeFluidState(FluidState var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", BuiltInRegistries.FLUID.getKey(var0.getType()).toString());
      Map var2 = var0.getValues();
      if (!var2.isEmpty()) {
         CompoundTag var3 = new CompoundTag();

         for(Map.Entry var5 : var2.entrySet()) {
            Property var6 = (Property)var5.getKey();
            var3.putString(var6.getName(), getName(var6, (Comparable)var5.getValue()));
         }

         var1.put("Properties", var3);
      }

      return var1;
   }

   private static <T extends Comparable<T>> String getName(Property<T> var0, Comparable<?> var1) {
      return var0.getName(var1);
   }

   public static String prettyPrint(Tag var0) {
      return prettyPrint(var0, false);
   }

   public static String prettyPrint(Tag var0, boolean var1) {
      return prettyPrint(new StringBuilder(), var0, 0, var1).toString();
   }

   public static StringBuilder prettyPrint(StringBuilder var0, Tag var1, int var2, boolean var3) {
      Objects.requireNonNull(var1);
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
      switch (var1.typeSwitch<invokedynamic>(var1, var5)) {
         case 0:
            PrimitiveTag var6 = (PrimitiveTag)var1;
            var10000 = var0.append(var6);
            break;
         case 1:
            EndTag var7 = (EndTag)var1;
            var10000 = var0;
            break;
         case 2:
            ByteArrayTag var8 = (ByteArrayTag)var1;
            byte[] var21 = var8.getAsByteArray();
            int var23 = var21.length;
            indent(var2, var0).append("byte[").append(var23).append("] {\n");
            if (!var3) {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(var2 + 1, var0);

               for(int var26 = 0; var26 < var21.length; ++var26) {
                  if (var26 != 0) {
                     var0.append(',');
                  }

                  if (var26 % 16 == 0 && var26 / 16 > 0) {
                     var0.append('\n');
                     if (var26 < var21.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var26 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format(Locale.ROOT, "0x%02X", var21[var26] & 255));
               }
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            var10000 = var0;
            break;
         case 3:
            ListTag var9 = (ListTag)var1;
            int var22 = var9.size();
            indent(var2, var0).append("list").append("[").append(var22).append("] [");
            if (var22 != 0) {
               var0.append('\n');
            }

            for(int var25 = 0; var25 < var22; ++var25) {
               if (var25 != 0) {
                  var0.append(",\n");
               }

               indent(var2 + 1, var0);
               prettyPrint(var0, var9.get(var25), var2 + 1, var3);
            }

            if (var22 != 0) {
               var0.append('\n');
            }

            indent(var2, var0).append(']');
            var10000 = var0;
            break;
         case 4:
            IntArrayTag var10 = (IntArrayTag)var1;
            int[] var24 = var10.getAsIntArray();
            int var28 = 0;

            for(int var38 : var24) {
               var28 = Math.max(var28, String.format(Locale.ROOT, "%X", var38).length());
            }

            int var31 = var24.length;
            indent(var2, var0).append("int[").append(var31).append("] {\n");
            if (!var3) {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(var2 + 1, var0);

               for(int var34 = 0; var34 < var24.length; ++var34) {
                  if (var34 != 0) {
                     var0.append(',');
                  }

                  if (var34 % 16 == 0 && var34 / 16 > 0) {
                     var0.append('\n');
                     if (var34 < var24.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var34 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format(Locale.ROOT, "0x%0" + var28 + "X", var24[var34]));
               }
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            var10000 = var0;
            break;
         case 5:
            CompoundTag var11 = (CompoundTag)var1;
            ArrayList var27 = Lists.newArrayList(var11.keySet());
            Collections.sort(var27);
            indent(var2, var0).append('{');
            if (var0.length() - var0.lastIndexOf("\n") > 2 * (var2 + 1)) {
               var0.append('\n');
               indent(var2 + 1, var0);
            }

            int var29 = var27.stream().mapToInt(String::length).max().orElse(0);
            String var32 = Strings.repeat(" ", var29);

            for(int var15 = 0; var15 < var27.size(); ++var15) {
               if (var15 != 0) {
                  var0.append(",\n");
               }

               String var37 = (String)var27.get(var15);
               indent(var2 + 1, var0).append('"').append(var37).append('"').append(var32, 0, var32.length() - var37.length()).append(": ");
               prettyPrint(var0, var11.get(var37), var2 + 1, var3);
            }

            if (!var27.isEmpty()) {
               var0.append('\n');
            }

            indent(var2, var0).append('}');
            var10000 = var0;
            break;
         case 6:
            LongArrayTag var12 = (LongArrayTag)var1;
            long[] var13 = var12.getAsLongArray();
            long var14 = 0L;

            for(long var19 : var13) {
               var14 = Math.max(var14, (long)String.format(Locale.ROOT, "%X", var19).length());
            }

            long var36 = (long)var13.length;
            indent(var2, var0).append("long[").append(var36).append("] {\n");
            if (!var3) {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(var2 + 1, var0);

               for(int var39 = 0; var39 < var13.length; ++var39) {
                  if (var39 != 0) {
                     var0.append(',');
                  }

                  if (var39 % 16 == 0 && var39 / 16 > 0) {
                     var0.append('\n');
                     if (var39 < var13.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var39 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format(Locale.ROOT, "0x%0" + var14 + "X", var13[var39]));
               }
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            var10000 = var0;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static StringBuilder indent(int var0, StringBuilder var1) {
      int var2 = var1.lastIndexOf("\n") + 1;
      int var3 = var1.length() - var2;

      for(int var4 = 0; var4 < 2 * var0 - var3; ++var4) {
         var1.append(' ');
      }

      return var1;
   }

   public static Component toPrettyComponent(Tag var0) {
      return (new TextComponentTagVisitor("")).visit(var0);
   }

   public static String structureToSnbt(CompoundTag var0) {
      return (new SnbtPrinterTagVisitor()).visit(packStructureTemplate(var0));
   }

   public static CompoundTag snbtToStructure(String var0) throws CommandSyntaxException {
      return unpackStructureTemplate(TagParser.parseCompoundFully(var0));
   }

   @VisibleForTesting
   static CompoundTag packStructureTemplate(CompoundTag var0) {
      Optional var2 = var0.getList("palettes");
      ListTag var1;
      if (var2.isPresent()) {
         var1 = ((ListTag)var2.get()).getListOrEmpty(0);
      } else {
         var1 = var0.getListOrEmpty("palette");
      }

      ListTag var3 = (ListTag)var1.compoundStream().map(NbtUtils::packBlockState).map(StringTag::valueOf).collect(Collectors.toCollection(ListTag::new));
      var0.put("palette", var3);
      if (var2.isPresent()) {
         ListTag var4 = new ListTag();
         ((ListTag)var2.get()).stream().flatMap((var0x) -> var0x.asList().stream()).forEach((var2x) -> {
            CompoundTag var3x = new CompoundTag();

            for(int var4x = 0; var4x < var2x.size(); ++var4x) {
               var3x.putString((String)var3.getString(var4x).orElseThrow(), packBlockState((CompoundTag)var2x.getCompound(var4x).orElseThrow()));
            }

            var4.add(var3x);
         });
         var0.put("palettes", var4);
      }

      Optional var6 = var0.getList("entities");
      if (var6.isPresent()) {
         ListTag var5 = (ListTag)((ListTag)var6.get()).compoundStream().sorted(Comparator.comparing((var0x) -> var0x.getList("pos"), Comparators.emptiesLast(YXZ_LISTTAG_DOUBLE_COMPARATOR))).collect(Collectors.toCollection(ListTag::new));
         var0.put("entities", var5);
      }

      ListTag var7 = (ListTag)var0.getList("blocks").stream().flatMap(ListTag::compoundStream).sorted(Comparator.comparing((var0x) -> var0x.getList("pos"), Comparators.emptiesLast(YXZ_LISTTAG_INT_COMPARATOR))).peek((var1x) -> var1x.putString("state", (String)var3.getString(var1x.getIntOr("state", 0)).orElseThrow())).collect(Collectors.toCollection(ListTag::new));
      var0.put("data", var7);
      var0.remove("blocks");
      return var0;
   }

   @VisibleForTesting
   static CompoundTag unpackStructureTemplate(CompoundTag var0) {
      ListTag var1 = var0.getListOrEmpty("palette");
      Map var2 = (Map)var1.stream().flatMap((var0x) -> var0x.asString().stream()).collect(ImmutableMap.toImmutableMap(Function.identity(), NbtUtils::unpackBlockState));
      Optional var3 = var0.getList("palettes");
      if (var3.isPresent()) {
         var0.put("palettes", (Tag)((ListTag)var3.get()).compoundStream().map((var1x) -> (ListTag)var2.keySet().stream().map((var1) -> (String)var1x.getString(var1).orElseThrow()).map(NbtUtils::unpackBlockState).collect(Collectors.toCollection(ListTag::new))).collect(Collectors.toCollection(ListTag::new)));
         var0.remove("palette");
      } else {
         var0.put("palette", (Tag)var2.values().stream().collect(Collectors.toCollection(ListTag::new)));
      }

      Optional var4 = var0.getList("data");
      if (var4.isPresent()) {
         Object2IntOpenHashMap var5 = new Object2IntOpenHashMap();
         var5.defaultReturnValue(-1);

         for(int var6 = 0; var6 < var1.size(); ++var6) {
            var5.put((String)var1.getString(var6).orElseThrow(), var6);
         }

         ListTag var11 = (ListTag)var4.get();

         for(int var7 = 0; var7 < var11.size(); ++var7) {
            CompoundTag var8 = (CompoundTag)var11.getCompound(var7).orElseThrow();
            String var9 = (String)var8.getString("state").orElseThrow();
            int var10 = var5.getInt(var9);
            if (var10 == -1) {
               throw new IllegalStateException("Entry " + var9 + " missing from palette");
            }

            var8.putInt("state", var10);
         }

         var0.put("blocks", var11);
         var0.remove("data");
      }

      return var0;
   }

   @VisibleForTesting
   static String packBlockState(CompoundTag var0) {
      StringBuilder var1 = new StringBuilder((String)var0.getString("Name").orElseThrow());
      var0.getCompound("Properties").ifPresent((var1x) -> {
         String var2 = (String)var1x.entrySet().stream().sorted(Entry.comparingByKey()).map((var0) -> {
            String var10000 = (String)var0.getKey();
            return var10000 + ":" + (String)((Tag)var0.getValue()).asString().orElseThrow();
         }).collect(Collectors.joining(","));
         var1.append('{').append(var2).append('}');
      });
      return var1.toString();
   }

   @VisibleForTesting
   static CompoundTag unpackBlockState(String var0) {
      CompoundTag var1 = new CompoundTag();
      int var2 = var0.indexOf(123);
      String var3;
      if (var2 >= 0) {
         var3 = var0.substring(0, var2);
         CompoundTag var4 = new CompoundTag();
         if (var2 + 2 <= var0.length()) {
            String var5 = var0.substring(var2 + 1, var0.indexOf(125, var2));
            COMMA_SPLITTER.split(var5).forEach((var2x) -> {
               List var3 = COLON_SPLITTER.splitToList(var2x);
               if (var3.size() == 2) {
                  var4.putString((String)var3.get(0), (String)var3.get(1));
               } else {
                  LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", var0);
               }

            });
            var1.put("Properties", var4);
         }
      } else {
         var3 = var0;
      }

      var1.putString("Name", var3);
      return var1;
   }

   public static CompoundTag addCurrentDataVersion(CompoundTag var0) {
      int var1 = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
      return addDataVersion(var0, var1);
   }

   public static CompoundTag addDataVersion(CompoundTag var0, int var1) {
      var0.putInt("DataVersion", var1);
      return var0;
   }

   public static int getDataVersion(CompoundTag var0, int var1) {
      return var0.getIntOr("DataVersion", var1);
   }

   public static int getDataVersion(Dynamic<?> var0, int var1) {
      return var0.get("DataVersion").asInt(var1);
   }

   static {
      BLOCK_NAME_CODEC = ResourceKey.codec(Registries.BLOCK);
      COMMA_SPLITTER = Splitter.on(",");
      COLON_SPLITTER = Splitter.on(':').limit(2);
      LOGGER = LogUtils.getLogger();
   }
}
