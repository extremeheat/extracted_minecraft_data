package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public final class NbtUtils {
   private static final Comparator<ListTag> YXZ_LISTTAG_INT_COMPARATOR = Comparator.<ListTag>comparingInt(var0 -> var0.getInt(1))
      .thenComparingInt(var0 -> var0.getInt(0))
      .thenComparingInt(var0 -> var0.getInt(2));
   private static final Comparator<ListTag> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.<ListTag>comparingDouble(var0 -> var0.getDouble(1))
      .thenComparingDouble(var0 -> var0.getDouble(0))
      .thenComparingDouble(var0 -> var0.getDouble(2));
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

   @Nullable
   public static GameProfile readGameProfile(CompoundTag var0) {
      String var1 = null;
      UUID var2 = null;
      if (var0.contains("Name", 8)) {
         var1 = var0.getString("Name");
      }

      if (var0.hasUUID("Id")) {
         var2 = var0.getUUID("Id");
      }

      try {
         GameProfile var3 = new GameProfile(var2, var1);
         if (var0.contains("Properties", 10)) {
            CompoundTag var4 = var0.getCompound("Properties");

            for(String var6 : var4.getAllKeys()) {
               ListTag var7 = var4.getList(var6, 10);

               for(int var8 = 0; var8 < var7.size(); ++var8) {
                  CompoundTag var9 = var7.getCompound(var8);
                  String var10 = var9.getString("Value");
                  if (var9.contains("Signature", 8)) {
                     var3.getProperties().put(var6, new Property(var6, var10, var9.getString("Signature")));
                  } else {
                     var3.getProperties().put(var6, new Property(var6, var10));
                  }
               }
            }
         }

         return var3;
      } catch (Throwable var11) {
         return null;
      }
   }

   public static CompoundTag writeGameProfile(CompoundTag var0, GameProfile var1) {
      if (!StringUtil.isNullOrEmpty(var1.getName())) {
         var0.putString("Name", var1.getName());
      }

      if (var1.getId() != null) {
         var0.putUUID("Id", var1.getId());
      }

      if (!var1.getProperties().isEmpty()) {
         CompoundTag var2 = new CompoundTag();

         for(String var4 : var1.getProperties().keySet()) {
            ListTag var5 = new ListTag();

            for(Property var7 : var1.getProperties().get(var4)) {
               CompoundTag var8 = new CompoundTag();
               var8.putString("Value", var7.getValue());
               if (var7.hasSignature()) {
                  var8.putString("Signature", var7.getSignature());
               }

               var5.add(var8);
            }

            var2.put(var4, var5);
         }

         var0.put("Properties", var2);
      }

      return var0;
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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
      } else if (var0 instanceof CompoundTag var9) {
         CompoundTag var10 = (CompoundTag)var1;

         for(String var12 : var9.getAllKeys()) {
            Tag var13 = var9.get(var12);
            if (!compareNbt(var13, var10.get(var12), var2)) {
               return false;
            }
         }

         return true;
      } else if (var0 instanceof ListTag var3 && var2) {
         ListTag var4 = (ListTag)var1;
         if (var3.isEmpty()) {
            return var4.isEmpty();
         } else {
            for(int var5 = 0; var5 < var3.size(); ++var5) {
               Tag var6 = var3.get(var5);
               boolean var7 = false;

               for(int var8 = 0; var8 < var4.size(); ++var8) {
                  if (compareNbt(var6, var4.get(var8), var2)) {
                     var7 = true;
                     break;
                  }
               }

               if (!var7) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return var0.equals(var1);
      }
   }

   public static IntArrayTag createUUID(UUID var0) {
      return new IntArrayTag(UUIDUtil.uuidToIntArray(var0));
   }

   public static UUID loadUUID(Tag var0) {
      if (var0.getType() != IntArrayTag.TYPE) {
         throw new IllegalArgumentException("Expected UUID-Tag to be of type " + IntArrayTag.TYPE.getName() + ", but found " + var0.getType().getName() + ".");
      } else {
         int[] var1 = ((IntArrayTag)var0).getAsIntArray();
         if (var1.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + var1.length + ".");
         } else {
            return UUIDUtil.uuidFromIntArray(var1);
         }
      }
   }

   public static BlockPos readBlockPos(CompoundTag var0) {
      return new BlockPos(var0.getInt("X"), var0.getInt("Y"), var0.getInt("Z"));
   }

   public static CompoundTag writeBlockPos(BlockPos var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putInt("X", var0.getX());
      var1.putInt("Y", var0.getY());
      var1.putInt("Z", var0.getZ());
      return var1;
   }

   public static BlockState readBlockState(CompoundTag var0) {
      if (!var0.contains("Name", 8)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         Block var1 = Registry.BLOCK.get(new ResourceLocation(var0.getString("Name")));
         BlockState var2 = var1.defaultBlockState();
         if (var0.contains("Properties", 10)) {
            CompoundTag var3 = var0.getCompound("Properties");
            StateDefinition var4 = var1.getStateDefinition();

            for(String var6 : var3.getAllKeys()) {
               net.minecraft.world.level.block.state.properties.Property var7 = var4.getProperty(var6);
               if (var7 != null) {
                  var2 = setValueHelper(var2, var7, var6, var3, var0);
               }
            }
         }

         return var2;
      }
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(
      S var0, net.minecraft.world.level.block.state.properties.Property<T> var1, String var2, CompoundTag var3, CompoundTag var4
   ) {
      Optional var5 = var1.getValue(var3.getString(var2));
      if (var5.isPresent()) {
         return (S)var0.setValue(var1, (Comparable)var5.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{var2, var3.getString(var2), var4.toString()});
         return (S)var0;
      }
   }

   public static CompoundTag writeBlockState(BlockState var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", Registry.BLOCK.getKey(var0.getBlock()).toString());
      ImmutableMap var2 = var0.getValues();
      if (!var2.isEmpty()) {
         CompoundTag var3 = new CompoundTag();
         UnmodifiableIterator var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            net.minecraft.world.level.block.state.properties.Property var6 = (net.minecraft.world.level.block.state.properties.Property)var5.getKey();
            var3.putString(var6.getName(), getName(var6, (Comparable<?>)var5.getValue()));
         }

         var1.put("Properties", var3);
      }

      return var1;
   }

   public static CompoundTag writeFluidState(FluidState var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putString("Name", Registry.FLUID.getKey(var0.getType()).toString());
      ImmutableMap var2 = var0.getValues();
      if (!var2.isEmpty()) {
         CompoundTag var3 = new CompoundTag();
         UnmodifiableIterator var4 = var2.entrySet().iterator();

         while(var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            net.minecraft.world.level.block.state.properties.Property var6 = (net.minecraft.world.level.block.state.properties.Property)var5.getKey();
            var3.putString(var6.getName(), getName(var6, (Comparable<?>)var5.getValue()));
         }

         var1.put("Properties", var3);
      }

      return var1;
   }

   private static <T extends Comparable<T>> String getName(net.minecraft.world.level.block.state.properties.Property<T> var0, Comparable<?> var1) {
      return var0.getName((T)var1);
   }

   public static String prettyPrint(Tag var0) {
      return prettyPrint(var0, false);
   }

   public static String prettyPrint(Tag var0, boolean var1) {
      return prettyPrint(new StringBuilder(), var0, 0, var1).toString();
   }

   public static StringBuilder prettyPrint(StringBuilder var0, Tag var1, int var2, boolean var3) {
      switch(var1.getId()) {
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
            int var24 = var20.length;
            indent(var2, var0).append("byte[").append(var24).append("] {\n");
            if (var3) {
               indent(var2 + 1, var0);

               for(int var28 = 0; var28 < var20.length; ++var28) {
                  if (var28 != 0) {
                     var0.append(',');
                  }

                  if (var28 % 16 == 0 && var28 / 16 > 0) {
                     var0.append('\n');
                     if (var28 < var20.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var28 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format("0x%02X", var20[var28] & 255));
               }
            } else {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            break;
         case 9:
            ListTag var15 = (ListTag)var1;
            int var19 = var15.size();
            byte var23 = var15.getElementType();
            String var27 = var23 == 0 ? "undefined" : TagTypes.getType(var23).getPrettyName();
            indent(var2, var0).append("list<").append(var27).append(">[").append(var19).append("] [");
            if (var19 != 0) {
               var0.append('\n');
            }

            for(int var33 = 0; var33 < var19; ++var33) {
               if (var33 != 0) {
                  var0.append(",\n");
               }

               indent(var2 + 1, var0);
               prettyPrint(var0, var15.get(var33), var2 + 1, var3);
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

            int var22 = var18.stream().mapToInt(String::length).max().orElse(0);
            String var26 = Strings.repeat(" ", var22);

            for(int var32 = 0; var32 < var18.size(); ++var32) {
               if (var32 != 0) {
                  var0.append(",\n");
               }

               String var35 = (String)var18.get(var32);
               indent(var2 + 1, var0).append('"').append(var35).append('"').append(var26, 0, var26.length() - var35.length()).append(": ");
               prettyPrint(var0, var14.get(var35), var2 + 1, var3);
            }

            if (!var18.isEmpty()) {
               var0.append('\n');
            }

            indent(var2, var0).append('}');
            break;
         case 11:
            IntArrayTag var13 = (IntArrayTag)var1;
            int[] var17 = var13.getAsIntArray();
            int var21 = 0;

            for(int var37 : var17) {
               var21 = Math.max(var21, String.format("%X", var37).length());
            }

            int var25 = var17.length;
            indent(var2, var0).append("int[").append(var25).append("] {\n");
            if (var3) {
               indent(var2 + 1, var0);

               for(int var31 = 0; var31 < var17.length; ++var31) {
                  if (var31 != 0) {
                     var0.append(',');
                  }

                  if (var31 % 16 == 0 && var31 / 16 > 0) {
                     var0.append('\n');
                     if (var31 < var17.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var31 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format("0x%0" + var21 + "X", var17[var31]));
               }
            } else {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
            }

            var0.append('\n');
            indent(var2, var0).append('}');
            break;
         case 12:
            LongArrayTag var4 = (LongArrayTag)var1;
            long[] var5 = var4.getAsLongArray();
            long var6 = 0L;

            for(long var11 : var5) {
               var6 = Math.max(var6, (long)String.format("%X", var11).length());
            }

            long var29 = (long)var5.length;
            indent(var2, var0).append("long[").append(var29).append("] {\n");
            if (var3) {
               indent(var2 + 1, var0);

               for(int var36 = 0; var36 < var5.length; ++var36) {
                  if (var36 != 0) {
                     var0.append(',');
                  }

                  if (var36 % 16 == 0 && var36 / 16 > 0) {
                     var0.append('\n');
                     if (var36 < var5.length) {
                        indent(var2 + 1, var0);
                     }
                  } else if (var36 != 0) {
                     var0.append(' ');
                  }

                  var0.append(String.format("0x%0" + var6 + "X", var5[var36]));
               }
            } else {
               indent(var2 + 1, var0).append(" // Skipped, supply withBinaryBlobs true");
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

   public static CompoundTag update(DataFixer var0, DataFixTypes var1, CompoundTag var2, int var3) {
      return update(var0, var1, var2, var3, SharedConstants.getCurrentVersion().getWorldVersion());
   }

   public static CompoundTag update(DataFixer var0, DataFixTypes var1, CompoundTag var2, int var3, int var4) {
      return (CompoundTag)var0.update(var1.getType(), new Dynamic(NbtOps.INSTANCE, var2), var3, var4).getValue();
   }

   public static Component toPrettyComponent(Tag var0) {
      return new TextComponentTagVisitor("", 0).visit(var0);
   }

   public static String structureToSnbt(CompoundTag var0) {
      return new SnbtPrinterTagVisitor().visit(packStructureTemplate(var0));
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

      ListTag var3 = var1.stream()
         .map(CompoundTag.class::cast)
         .map(NbtUtils::packBlockState)
         .map(StringTag::valueOf)
         .collect(Collectors.toCollection(ListTag::new));
      var0.put("palette", var3);
      if (var2) {
         ListTag var4 = new ListTag();
         ListTag var5 = var0.getList("palettes", 9);
         var5.stream().map(ListTag.class::cast).forEach(var2x -> {
            CompoundTag var3x = new CompoundTag();

            for(int var4x = 0; var4x < var2x.size(); ++var4x) {
               var3x.putString(var3.getString(var4x), packBlockState(var2x.getCompound(var4x)));
            }

            var4.add(var3x);
         });
         var0.put("palettes", var4);
      }

      if (var0.contains("entities", 10)) {
         ListTag var6 = var0.getList("entities", 10);
         ListTag var8 = var6.stream()
            .map(CompoundTag.class::cast)
            .sorted(Comparator.comparing(var0x -> var0x.getList("pos", 6), YXZ_LISTTAG_DOUBLE_COMPARATOR))
            .collect(Collectors.toCollection(ListTag::new));
         var0.put("entities", var8);
      }

      ListTag var7 = var0.getList("blocks", 10)
         .stream()
         .map(CompoundTag.class::cast)
         .sorted(Comparator.comparing(var0x -> var0x.getList("pos", 3), YXZ_LISTTAG_INT_COMPARATOR))
         .peek(var1x -> var1x.putString("state", var3.getString(var1x.getInt("state"))))
         .collect(Collectors.toCollection(ListTag::new));
      var0.put("data", var7);
      var0.remove("blocks");
      return var0;
   }

   @VisibleForTesting
   static CompoundTag unpackStructureTemplate(CompoundTag var0) {
      ListTag var1 = var0.getList("palette", 8);
      Map var2 = var1.stream()
         .map(StringTag.class::cast)
         .map(StringTag::getAsString)
         .collect(ImmutableMap.toImmutableMap(Function.identity(), NbtUtils::unpackBlockState));
      if (var0.contains("palettes", 9)) {
         var0.put(
            "palettes",
            var0.getList("palettes", 10)
               .stream()
               .map(CompoundTag.class::cast)
               .map(var1x -> var2.keySet().stream().map(var1x::getString).map(NbtUtils::unpackBlockState).collect(Collectors.toCollection(ListTag::new)))
               .collect(Collectors.toCollection(ListTag::new))
         );
         var0.remove("palette");
      } else {
         var0.put("palette", var2.values().stream().collect(Collectors.toCollection(ListTag::new)));
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
         String var3 = var2.getAllKeys().stream().sorted().map(var1x -> var1x + ":" + var2.get(var1x).getAsString()).collect(Collectors.joining(","));
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
            COMMA_SPLITTER.split(var5).forEach(var2x -> {
               List var3x = COLON_SPLITTER.splitToList(var2x);
               if (var3x.size() == 2) {
                  var4.putString((String)var3x.get(0), (String)var3x.get(1));
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
}
