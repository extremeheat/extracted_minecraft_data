package net.minecraft.world.level.levelgen.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeatureIO {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureFeature MINESHAFT;
   public static final StructureFeature PILLAGER_OUTPOST;
   public static final StructureFeature NETHER_FORTRESS;
   public static final StructureFeature STRONGHOLD;
   public static final StructureFeature JUNGLE_PYRAMID;
   public static final StructureFeature OCEAN_RUIN;
   public static final StructureFeature DESERT_PYRAMID;
   public static final StructureFeature IGLOO;
   public static final StructureFeature SWAMP_HUT;
   public static final StructureFeature OCEAN_MONUMENT;
   public static final StructureFeature END_CITY;
   public static final StructureFeature WOODLAND_MANSION;
   public static final StructureFeature BURIED_TREASURE;
   public static final StructureFeature SHIPWRECK;
   public static final StructureFeature VILLAGE;

   private static StructureFeature register(String var0, StructureFeature var1) {
      return (StructureFeature)Registry.register(Registry.STRUCTURE_FEATURE, (String)var0.toLowerCase(Locale.ROOT), var1);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart loadStaticStart(ChunkGenerator var0, StructureManager var1, CompoundTag var2) {
      String var3 = var2.getString("id");
      if ("INVALID".equals(var3)) {
         return StructureStart.INVALID_START;
      } else {
         StructureFeature var4 = (StructureFeature)Registry.STRUCTURE_FEATURE.get(new ResourceLocation(var3.toLowerCase(Locale.ROOT)));
         if (var4 == null) {
            LOGGER.error("Unknown feature id: {}", var3);
            return null;
         } else {
            int var5 = var2.getInt("ChunkX");
            int var6 = var2.getInt("ChunkZ");
            int var7 = var2.getInt("references");
            BoundingBox var8 = var2.contains("BB") ? new BoundingBox(var2.getIntArray("BB")) : BoundingBox.getUnknownBox();
            ListTag var9 = var2.getList("Children", 10);

            try {
               StructureStart var10 = var4.getStartFactory().create(var4, var5, var6, var8, var7, var0.getSeed());

               for(int var11 = 0; var11 < var9.size(); ++var11) {
                  CompoundTag var12 = var9.getCompound(var11);
                  String var13 = var12.getString("id");
                  StructurePieceType var14 = (StructurePieceType)Registry.STRUCTURE_PIECE.get(new ResourceLocation(var13.toLowerCase(Locale.ROOT)));
                  if (var14 == null) {
                     LOGGER.error("Unknown structure piece id: {}", var13);
                  } else {
                     try {
                        StructurePiece var15 = var14.load(var1, var12);
                        var10.pieces.add(var15);
                     } catch (Exception var16) {
                        LOGGER.error("Exception loading structure piece with id {}", var13, var16);
                     }
                  }
               }

               return var10;
            } catch (Exception var17) {
               LOGGER.error("Failed Start with id {}", var3, var17);
               return null;
            }
         }
      }
   }

   static {
      MINESHAFT = register("Mineshaft", Feature.MINESHAFT);
      PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
      NETHER_FORTRESS = register("Fortress", Feature.NETHER_BRIDGE);
      STRONGHOLD = register("Stronghold", Feature.STRONGHOLD);
      JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
      OCEAN_RUIN = register("Ocean_Ruin", Feature.OCEAN_RUIN);
      DESERT_PYRAMID = register("Desert_Pyramid", Feature.DESERT_PYRAMID);
      IGLOO = register("Igloo", Feature.IGLOO);
      SWAMP_HUT = register("Swamp_Hut", Feature.SWAMP_HUT);
      OCEAN_MONUMENT = register("Monument", Feature.OCEAN_MONUMENT);
      END_CITY = register("EndCity", Feature.END_CITY);
      WOODLAND_MANSION = register("Mansion", Feature.WOODLAND_MANSION);
      BURIED_TREASURE = register("Buried_Treasure", Feature.BURIED_TREASURE);
      SHIPWRECK = register("Shipwreck", Feature.SHIPWRECK);
      VILLAGE = register("Village", Feature.VILLAGE);
   }
}
