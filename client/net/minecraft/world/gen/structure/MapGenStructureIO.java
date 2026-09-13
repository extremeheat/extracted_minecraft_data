package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO {
   private static final Logger field_151687_a = LogManager.getLogger();
   private static Map field_143040_a = new HashMap();
   private static Map field_143038_b = new HashMap();
   private static Map field_143039_c = new HashMap();
   private static Map field_143037_d = new HashMap();

   private static void func_143034_b(Class var0, String var1) {
      field_143040_a.put(var1, var0);
      field_143038_b.put(var0, var1);
   }

   static void func_143031_a(Class var0, String var1) {
      field_143039_c.put(var1, var0);
      field_143037_d.put(var0, var1);
   }

   public static String func_143033_a(StructureStart var0) {
      return (String)field_143038_b.get(var0.getClass());
   }

   public static String func_143036_a(StructureComponent var0) {
      return (String)field_143037_d.get(var0.getClass());
   }

   public static StructureStart func_143035_a(NBTTagCompound var0, World var1) {
      StructureStart var2 = null;

      try {
         Class var3 = (Class)field_143040_a.get(var0.func_74779_i("id"));
         if (var3 != null) {
            var2 = (StructureStart)var3.newInstance();
         }
      } catch (Exception var4) {
         field_151687_a.warn("Failed Start with id " + var0.func_74779_i("id"));
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.func_143020_a(var1, var0);
      } else {
         field_151687_a.warn("Skipping Structure with id " + var0.func_74779_i("id"));
      }

      return var2;
   }

   public static StructureComponent func_143032_b(NBTTagCompound var0, World var1) {
      StructureComponent var2 = null;

      try {
         Class var3 = (Class)field_143039_c.get(var0.func_74779_i("id"));
         if (var3 != null) {
            var2 = (StructureComponent)var3.newInstance();
         }
      } catch (Exception var4) {
         field_151687_a.warn("Failed Piece with id " + var0.func_74779_i("id"));
         var4.printStackTrace();
      }

      if (var2 != null) {
         var2.func_143009_a(var1, var0);
      } else {
         field_151687_a.warn("Skipping Piece with id " + var0.func_74779_i("id"));
      }

      return var2;
   }

   static {
      func_143034_b(StructureMineshaftStart.class, "Mineshaft");
      func_143034_b(MapGenVillage$Start.class, "Village");
      func_143034_b(MapGenNetherBridge$Start.class, "Fortress");
      func_143034_b(MapGenStronghold$Start.class, "Stronghold");
      func_143034_b(MapGenScatteredFeature$Start.class, "Temple");
      StructureMineshaftPieces.func_143048_a();
      StructureVillagePieces.func_143016_a();
      StructureNetherBridgePieces.func_143049_a();
      StructureStrongholdPieces.func_143046_a();
      ComponentScatteredFeaturePieces.func_143045_a();
   }
}
