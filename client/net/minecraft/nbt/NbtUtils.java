package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public final class NbtUtils {
   private static final Comparator<ListTag> YXZ_LISTTAG_INT_COMPARATOR = Comparator.comparingInt((var0) -> {
      return var0.getInt(1);
   }).thenComparingInt((var0) -> {
      return var0.getInt(0);
   }).thenComparingInt((var0) -> {
      return var0.getInt(2);
   });
   private static final Comparator<ListTag> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.comparingDouble((var0) -> {
      return var0.getDouble(1);
   }).thenComparingDouble((var0) -> {
      return var0.getDouble(0);
   }).thenComparingDouble((var0) -> {
      return var0.getDouble(2);
   });
   public static final String SNBT_DATA_TAG = "data";
   private static final char PROPERTIES_START = '{';
   private static final char PROPERTIES_END = '}';
   private static final String ELEMENT_SEPARATOR = ",";
   private static final char KEY_VALUE_SEPARATOR = ':';
   private static final Splitter COMMA_SPLITTER = Splitter.on(",");
   private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
   private static final Logger LOGGER = LogUtils.getLogger();
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
      } else {
         Iterator var6;
         if (var0 instanceof CompoundTag) {
            CompoundTag var3 = (CompoundTag)var0;
            CompoundTag var11 = (CompoundTag)var1;
            if (var11.size() < var3.size()) {
               return false;
            } else {
               var6 = var3.getAllKeys().iterator();

               String var12;
               Tag var13;
               do {
                  if (!var6.hasNext()) {
                     return true;
                  }

                  var12 = (String)var6.next();
                  var13 = var3.get(var12);
               } while(compareNbt(var13, var11.get(var12), var2));

               return false;
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

                  var6 = var4.iterator();

                  boolean var8;
                  do {
                     if (!var6.hasNext()) {
                        return true;
                     }

                     Tag var7 = (Tag)var6.next();
                     var8 = false;
                     Iterator var9 = var5.iterator();

                     while(var9.hasNext()) {
                        Tag var10 = (Tag)var9.next();
                        if (compareNbt(var7, var10, var2)) {
                           var8 = true;
                           break;
                        }
                     }
                  } while(var8);

                  return false;
               }
            }

            return var0.equals(var1);
         }
      }
   }

   public static IntArrayTag createUUID(UUID var0) {
      return new IntArrayTag(UUIDUtil.uuidToIntArray(var0));
   }

   public static UUID loadUUID(Tag var0) {
      if (var0.getType() != IntArrayTag.TYPE) {
         String var10002 = IntArrayTag.TYPE.getName();
         throw new IllegalArgumentException("Expected UUID-Tag to be of type " + var10002 + ", but found " + var0.getType().getName() + ".");
      } else {
         int[] var1 = ((IntArrayTag)var0).getAsIntArray();
         if (var1.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + var1.length + ".");
         } else {
            return UUIDUtil.uuidFromIntArray(var1);
         }
      }
   }

   public static Optional<BlockPos> readBlockPos(CompoundTag var0, String var1) {
      int[] var2 = var0.getIntArray(var1);
      return var2.length == 3 ? Optional.of(new BlockPos(var2[0], var2[1], var2[2])) : Optional.empty();
   }

   public static Tag writeBlockPos(BlockPos var0) {
      return new IntArrayTag(new int[]{var0.getX(), var0.getY(), var0.getZ()});
   }

   public static BlockState readBlockState(HolderGetter<Block> var0, CompoundTag var1) {
      if (!var1.contains("Name", 8)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         ResourceLocation var2 = new ResourceLocation(var1.getString("Name"));
         Optional var3 = var0.get(ResourceKey.create(Registries.BLOCK, var2));
         if (var3.isEmpty()) {
            return Blocks.AIR.defaultBlockState();
         } else {
            Block var4 = (Block)((Holder)var3.get()).value();
            BlockState var5 = var4.defaultBlockState();
            if (var1.contains("Properties", 10)) {
               CompoundTag var6 = var1.getCompound("Properties");
               StateDefinition var7 = var4.getStateDefinition();
               Iterator var8 = var6.getAllKeys().iterator();

               while(var8.hasNext()) {
                  String var9 = (String)var8.next();
                  Property var10 = var7.getProperty(var9);
                  if (var10 != null) {
                     var5 = (BlockState)setValueHelper(var5, var10, var9, var6, var1);
                  }
               }
            }

            return var5;
         }
      }
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S var0, Property<T> var1, String var2, CompoundTag var3, CompoundTag var4) {
      Optional var5 = var1.getValue(var3.getString(var2));
      if (var5.isPresent()) {
         return (StateHolder)var0.setValue(var1, (Comparable)var5.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{var2, var3.getString(var2), var4});
         return var0;
      }
   }

   public static CompoundTag writeBlockState(BlockState var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", BuiltInRegistries.BLOCK.getKey(var0.getBlock()).toString());
      Map var2 = var0.getValues();
      if (!var2.isEmpty()) {
         CompoundTag var3 = new CompoundTag();
         Iterator var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
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
         Iterator var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
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
      int var9;
      int var10;
      int var21;
      int var22;
      String var24;
      int var26;
      switch (var1.getId()) {
         case 0:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 8:
            var0.append(var1);
            break;
         case 7:
            ByteArrayTag var16 = (ByteArrayTag)var1;
            byte[] var20 = var16.getAsByteArray();
            var21 = var20.length;
            indent(var2, var0).append("byte[").append(var21).append("] {\n");
            if (!var3) {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(var2 + 1, var0);

               for(var22 = 0; var22 < var20.length; ++var22) {
                  if (var22 != 0) {
                     var0.append(',');
                  }

                  if (var22 % 16 == 0 && var22 / 16 > 0) {
                     var0.append('\n');
                     if (var22 < var20.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var22 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format(Locale.ROOT, "0x%02X", var20[var22] & 255));
               }
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            break;
         case 9:
            ListTag var15 = (ListTag)var1;
            int var19 = var15.size();
            byte var23 = var15.getElementType();
            var24 = var23 == 0 ? "undefined" : TagTypes.getType(var23).getPrettyName();
            indent(var2, var0).append("list<").append(var24).append(">[").append(var19).append("] [");
            if (var19 != 0) {
               var0.append('\n');
            }

            for(var26 = 0; var26 < var19; ++var26) {
               if (var26 != 0) {
                  var0.append(",\n");
               }

               indent(var2 + 1, var0);
               prettyPrint(var0, var15.get(var26), var2 + 1, var3);
            }

            if (var19 != 0) {
               var0.append('\n');
            }

            indent(var2, var0).append(']');
            break;
         case 10:
            CompoundTag var14 = (CompoundTag)var1;
            ArrayList var18 = Lists.newArrayList(var14.getAllKeys());
            Collections.sort(var18);
            indent(var2, var0).append('{');
            if (var0.length() - var0.lastIndexOf("\n") > 2 * (var2 + 1)) {
               var0.append('\n');
               indent(var2 + 1, var0);
            }

            var21 = var18.stream().mapToInt(String::length).max().orElse(0);
            var24 = Strings.repeat(" ", var21);

            for(var26 = 0; var26 < var18.size(); ++var26) {
               if (var26 != 0) {
                  var0.append(",\n");
               }

               String var27 = (String)var18.get(var26);
               indent(var2 + 1, var0).append('"').append(var27).append('"').append(var24, 0, var24.length() - var27.length()).append(": ");
               prettyPrint(var0, var14.get(var27), var2 + 1, var3);
            }

            if (!var18.isEmpty()) {
               var0.append('\n');
            }

            indent(var2, var0).append('}');
            break;
         case 11:
            IntArrayTag var13 = (IntArrayTag)var1;
            int[] var17 = var13.getAsIntArray();
            var21 = 0;
            int[] var7 = var17;
            var26 = var17.length;

            for(var9 = 0; var9 < var26; ++var9) {
               var10 = var7[var9];
               var21 = Math.max(var21, String.format(Locale.ROOT, "%X", var10).length());
            }

            var22 = var17.length;
            indent(var2, var0).append("int[").append(var22).append("] {\n");
            if (!var3) {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(var2 + 1, var0);

               for(var26 = 0; var26 < var17.length; ++var26) {
                  if (var26 != 0) {
                     var0.append(',');
                  }

                  if (var26 % 16 == 0 && var26 / 16 > 0) {
                     var0.append('\n');
                     if (var26 < var17.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var26 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format(Locale.ROOT, "0x%0" + var21 + "X", var17[var26]));
               }
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            break;
         case 12:
            LongArrayTag var4 = (LongArrayTag)var1;
            long[] var5 = var4.getAsLongArray();
            long var6 = 0L;
            long[] var8 = var5;
            var9 = var5.length;

            for(var10 = 0; var10 < var9; ++var10) {
               long var11 = var8[var10];
               var6 = Math.max(var6, (long)String.format(Locale.ROOT, "%X", var11).length());
            }

            long var25 = (long)var5.length;
            indent(var2, var0).append("long[").append(var25).append("] {\n");
            if (!var3) {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               indent(var2 + 1, var0);

               for(var10 = 0; var10 < var5.length; ++var10) {
                  if (var10 != 0) {
                     var0.append(',');
                  }

                  if (var10 % 16 == 0 && var10 / 16 > 0) {
                     var0.append('\n');
                     if (var10 < var5.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var10 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format(Locale.ROOT, "0x%0" + var6 + "X", var5[var10]));
               }
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            break;
         default:
            var0.append("<UNKNOWN :(>");
      }

      return var0;
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
      return (new TextComponentTagVisitor("", 0)).visit(var0);
   }

   public static String structureToSnbt(CompoundTag var0) {
      return (new SnbtPrinterTagVisitor()).visit(packStructureTemplate(var0));
   }

   public static CompoundTag snbtToStructure(String var0) throws CommandSyntaxException {
      return unpackStructureTemplate(TagParser.parseTag(var0));
   }

   @VisibleForTesting
   static CompoundTag packStructureTemplate(CompoundTag var0) {
      boolean var2 = var0.contains("palettes", 9);
      ListTag var1;
      if (var2) {
         var1 = var0.getList("palettes", 9).getList(0);
      } else {
         var1 = var0.getList("palette", 10);
      }

      Stream var10000 = var1.stream();
      Objects.requireNonNull(CompoundTag.class);
      ListTag var3 = (ListTag)var10000.map(CompoundTag.class::cast).map(NbtUtils::packBlockState).map(StringTag::valueOf).collect(Collectors.toCollection(ListTag::new));
      var0.put("palette", var3);
      ListTag var4;
      ListTag var5;
      if (var2) {
         var4 = new ListTag();
         var5 = var0.getList("palettes", 9);
         var10000 = var5.stream();
         Objects.requireNonNull(ListTag.class);
         var10000.map(ListTag.class::cast).forEach((var2x) -> {
            CompoundTag var3x = new CompoundTag();

            for(int var4x = 0; var4x < var2x.size(); ++var4x) {
               var3x.putString(var3.getString(var4x), packBlockState(var2x.getCompound(var4x)));
            }

            var4.add(var3x);
         });
         var0.put("palettes", var4);
      }

      if (var0.contains("entities", 9)) {
         var4 = var0.getList("entities", 10);
         var10000 = var4.stream();
         Objects.requireNonNull(CompoundTag.class);
         var5 = (ListTag)var10000.map(CompoundTag.class::cast).sorted(Comparator.comparing((var0x) -> {
            return var0x.getList("pos", 6);
         }, YXZ_LISTTAG_DOUBLE_COMPARATOR)).collect(Collectors.toCollection(ListTag::new));
         var0.put("entities", var5);
      }

      var10000 = var0.getList("blocks", 10).stream();
      Objects.requireNonNull(CompoundTag.class);
      var4 = (ListTag)var10000.map(CompoundTag.class::cast).sorted(Comparator.comparing((var0x) -> {
         return var0x.getList("pos", 3);
      }, YXZ_LISTTAG_INT_COMPARATOR)).peek((var1x) -> {
         var1x.putString("state", var3.getString(var1x.getInt("state")));
      }).collect(Collectors.toCollection(ListTag::new));
      var0.put("data", var4);
      var0.remove("blocks");
      return var0;
   }

   @VisibleForTesting
   static CompoundTag unpackStructureTemplate(CompoundTag var0) {
      ListTag var1 = var0.getList("palette", 8);
      Stream var10000 = var1.stream();
      Objects.requireNonNull(StringTag.class);
      Map var2 = (Map)var10000.map(StringTag.class::cast).map(StringTag::getAsString).collect(ImmutableMap.toImmutableMap(Function.identity(), NbtUtils::unpackBlockState));
      if (var0.contains("palettes", 9)) {
         Stream var10002 = var0.getList("palettes", 10).stream();
         Objects.requireNonNull(CompoundTag.class);
         var0.put("palettes", (Tag)var10002.map(CompoundTag.class::cast).map((var1x) -> {
            Stream var10000 = var2.keySet().stream();
            Objects.requireNonNull(var1x);
            return (ListTag)var10000.map(var1x::getString).map(NbtUtils::unpackBlockState).collect(Collectors.toCollection(ListTag::new));
         }).collect(Collectors.toCollection(ListTag::new)));
         var0.remove("palette");
      } else {
         var0.put("palette", (Tag)var2.values().stream().collect(Collectors.toCollection(ListTag::new)));
      }

      if (var0.contains("data", 9)) {
         Object2IntOpenHashMap var3 = new Object2IntOpenHashMap();
         var3.defaultReturnValue(-1);

         for(int var4 = 0; var4 < var1.size(); ++var4) {
            var3.put(var1.getString(var4), var4);
         }

         ListTag var9 = var0.getList("data", 10);

         for(int var5 = 0; var5 < var9.size(); ++var5) {
            CompoundTag var6 = var9.getCompound(var5);
            String var7 = var6.getString("state");
            int var8 = var3.getInt(var7);
            if (var8 == -1) {
               throw new IllegalStateException("Entry " + var7 + " missing from palette");
            }

            var6.putInt("state", var8);
         }

         var0.put("blocks", var9);
         var0.remove("data");
      }

      return var0;
   }

   @VisibleForTesting
   static String packBlockState(CompoundTag var0) {
      StringBuilder var1 = new StringBuilder(var0.getString("Name"));
      if (var0.contains("Properties", 10)) {
         CompoundTag var2 = var0.getCompound("Properties");
         String var3 = (String)var2.getAllKeys().stream().sorted().map((var1x) -> {
            return var1x + ":" + var2.get(var1x).getAsString();
         }).collect(Collectors.joining(","));
         var1.append('{').append(var3).append('}');
      }

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
      return var0.contains("DataVersion", 99) ? var0.getInt("DataVersion") : var1;
   }
}
