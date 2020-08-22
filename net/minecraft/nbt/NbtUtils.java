package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
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
   private static final Logger LOGGER = LogManager.getLogger();

   @Nullable
   public static GameProfile readGameProfile(CompoundTag var0) {
      String var1 = null;
      String var2 = null;
      if (var0.contains("Name", 8)) {
         var1 = var0.getString("Name");
      }

      if (var0.contains("Id", 8)) {
         var2 = var0.getString("Id");
      }

      try {
         UUID var3;
         try {
            var3 = UUID.fromString(var2);
         } catch (Throwable var12) {
            var3 = null;
         }

         GameProfile var4 = new GameProfile(var3, var1);
         if (var0.contains("Properties", 10)) {
            CompoundTag var5 = var0.getCompound("Properties");
            Iterator var6 = var5.getAllKeys().iterator();

            while(var6.hasNext()) {
               String var7 = (String)var6.next();
               ListTag var8 = var5.getList(var7, 10);

               for(int var9 = 0; var9 < var8.size(); ++var9) {
                  CompoundTag var10 = var8.getCompound(var9);
                  String var11 = var10.getString("Value");
                  if (var10.contains("Signature", 8)) {
                     var4.getProperties().put(var7, new Property(var7, var11, var10.getString("Signature")));
                  } else {
                     var4.getProperties().put(var7, new Property(var7, var11));
                  }
               }
            }
         }

         return var4;
      } catch (Throwable var13) {
         return null;
      }
   }

   public static CompoundTag writeGameProfile(CompoundTag var0, GameProfile var1) {
      if (!StringUtil.isNullOrEmpty(var1.getName())) {
         var0.putString("Name", var1.getName());
      }

      if (var1.getId() != null) {
         var0.putString("Id", var1.getId().toString());
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

   public static CompoundTag createUUIDTag(UUID var0) {
      CompoundTag var1 = new CompoundTag();
      var1.putLong("M", var0.getMostSignificantBits());
      var1.putLong("L", var0.getLeastSignificantBits());
      return var1;
   }

   public static UUID loadUUIDTag(CompoundTag var0) {
      return new UUID(var0.getLong("M"), var0.getLong("L"));
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

   private static StateHolder setValueHelper(StateHolder var0, net.minecraft.world.level.block.state.properties.Property var1, String var2, CompoundTag var3, CompoundTag var4) {
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

   private static String getName(net.minecraft.world.level.block.state.properties.Property var0, Comparable var1) {
      return var0.getName(var1);
   }

   public static CompoundTag update(DataFixer var0, DataFixTypes var1, CompoundTag var2, int var3) {
      return update(var0, var1, var2, var3, SharedConstants.getCurrentVersion().getWorldVersion());
   }

   public static CompoundTag update(DataFixer var0, DataFixTypes var1, CompoundTag var2, int var3, int var4) {
      return (CompoundTag)var0.update(var1.getType(), new Dynamic(NbtOps.INSTANCE, var2), var3, var4).getValue();
   }
}
