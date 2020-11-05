package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SerializableUUID;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
   private static final Splitter COMMA_SPLITTER = Splitter.on(",");
   private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
   private static final Logger LOGGER = LogManager.getLogger();

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
            Iterator var5 = var4.getAllKeys().iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
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
         Iterator var3 = var1.getProperties().keySet().iterator();

         while(var3.hasNext()) {
            String var4 = (String)var3.next();
            ListTag var5 = new ListTag();

            CompoundTag var8;
            for(Iterator var6 = var1.getProperties().get(var4).iterator(); var6.hasNext(); var5.add(var8)) {
               Property var7 = (Property)var6.next();
               var8 = new CompoundTag();
               var8.putString("Value", var7.getValue());
               if (var7.hasSignature()) {
                  var8.putString("Signature", var7.getSignature());
               }
            }

            var2.put(var4, var5);
         }

         var0.put("Properties", var2);
      }

      return var0;
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
         CompoundTag var9 = (CompoundTag)var0;
         CompoundTag var10 = (CompoundTag)var1;
         Iterator var11 = var9.getAllKeys().iterator();

         String var12;
         Tag var13;
         do {
            if (!var11.hasNext()) {
               return true;
            }

            var12 = (String)var11.next();
            var13 = var9.get(var12);
         } while(compareNbt(var13, var10.get(var12), var2));

         return false;
      } else if (var0 instanceof ListTag && var2) {
         ListTag var3 = (ListTag)var0;
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
      return new IntArrayTag(SerializableUUID.uuidToIntArray(var0));
   }

   public static UUID loadUUID(Tag var0) {
      if (var0.getType() != IntArrayTag.TYPE) {
         throw new IllegalArgumentException("Expected UUID-Tag to be of type " + IntArrayTag.TYPE.getName() + ", but found " + var0.getType().getName() + ".");
      } else {
         int[] var1 = ((IntArrayTag)var0).getAsIntArray();
         if (var1.length != 4) {
            throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + var1.length + ".");
         } else {
            return SerializableUUID.uuidFromIntArray(var1);
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
         Block var1 = (Block)Registry.BLOCK.get(new ResourceLocation(var0.getString("Name")));
         BlockState var2 = var1.defaultBlockState();
         if (var0.contains("Properties", 10)) {
            CompoundTag var3 = var0.getCompound("Properties");
            StateDefinition var4 = var1.getStateDefinition();
            Iterator var5 = var3.getAllKeys().iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               net.minecraft.world.level.block.state.properties.Property var7 = var4.getProperty(var6);
               if (var7 != null) {
                  var2 = (BlockState)setValueHelper(var2, var7, var6, var3, var0);
               }
            }
         }

         return var2;
      }
   }

   private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(S var0, net.minecraft.world.level.block.state.properties.Property<T> var1, String var2, CompoundTag var3, CompoundTag var4) {
      Optional var5 = var1.getValue(var3.getString(var2));
      if (var5.isPresent()) {
         return (StateHolder)var0.setValue(var1, (Comparable)var5.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", var2, var3.getString(var2), var4.toString());
         return var0;
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
            var3.putString(var6.getName(), getName(var6, (Comparable)var5.getValue()));
         }

         var1.put("Properties", var3);
      }

      return var1;
   }

   private static <T extends Comparable<T>> String getName(net.minecraft.world.level.block.state.properties.Property<T> var0, Comparable<?> var1) {
      return var0.getName(var1);
   }

   public static CompoundTag update(DataFixer var0, DataFixTypes var1, CompoundTag var2, int var3) {
      return update(var0, var1, var2, var3, SharedConstants.getCurrentVersion().getWorldVersion());
   }

   public static CompoundTag update(DataFixer var0, DataFixTypes var1, CompoundTag var2, int var3, int var4) {
      return (CompoundTag)var0.update(var1.getType(), new Dynamic(NbtOps.INSTANCE, var2), var3, var4).getValue();
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
      CompoundTag.class.getClass();
      ListTag var3 = (ListTag)var10000.map(CompoundTag.class::cast).map(NbtUtils::packBlockState).map(StringTag::valueOf).collect(Collectors.toCollection(ListTag::new));
      var0.put("palette", var3);
      ListTag var4;
      ListTag var5;
      if (var2) {
         var4 = new ListTag();
         var5 = var0.getList("palettes", 9);
         var10000 = var5.stream();
         ListTag.class.getClass();
         var10000.map(ListTag.class::cast).forEach((var2x) -> {
            CompoundTag var3x = new CompoundTag();

            for(int var4x = 0; var4x < var2x.size(); ++var4x) {
               var3x.putString(var3.getString(var4x), packBlockState(var2x.getCompound(var4x)));
            }

            var4.add(var3x);
         });
         var0.put("palettes", var4);
      }

      if (var0.contains("entities", 10)) {
         var4 = var0.getList("entities", 10);
         var10000 = var4.stream();
         CompoundTag.class.getClass();
         var5 = (ListTag)var10000.map(CompoundTag.class::cast).sorted(Comparator.comparing((var0x) -> {
            return var0x.getList("pos", 6);
         }, YXZ_LISTTAG_DOUBLE_COMPARATOR)).collect(Collectors.toCollection(ListTag::new));
         var0.put("entities", var5);
      }

      var10000 = var0.getList("blocks", 10).stream();
      CompoundTag.class.getClass();
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
      StringTag.class.getClass();
      Map var2 = (Map)var10000.map(StringTag.class::cast).map(StringTag::getAsString).collect(ImmutableMap.toImmutableMap(Function.identity(), NbtUtils::unpackBlockState));
      if (var0.contains("palettes", 9)) {
         Stream var10002 = var0.getList("palettes", 10).stream();
         CompoundTag.class.getClass();
         var0.put("palettes", (Tag)var10002.map(CompoundTag.class::cast).map((var1x) -> {
            Stream var10000 = var2.keySet().stream();
            var1x.getClass();
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
            return var1x + ':' + var2.get(var1x).getAsString();
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
}
