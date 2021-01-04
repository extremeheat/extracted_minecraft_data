package net.minecraft.world.level.levelgen.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeatureIO {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureFeature<?> MINESHAFT;
   public static final StructureFeature<?> PILLAGER_OUTPOST;
   public static final StructureFeature<?> NETHER_FORTRESS;
   public static final StructureFeature<?> STRONGHOLD;
   public static final StructureFeature<?> JUNGLE_PYRAMID;
   public static final StructureFeature<?> OCEAN_RUIN;
   public static final StructureFeature<?> DESERT_PYRAMID;
   public static final StructureFeature<?> IGLOO;
   public static final StructureFeature<?> SWAMP_HUT;
   public static final StructureFeature<?> OCEAN_MONUMENT;
   public static final StructureFeature<?> END_CITY;
   public static final StructureFeature<?> WOODLAND_MANSION;
   public static final StructureFeature<?> BURIED_TREASURE;
   public static final StructureFeature<?> SHIPWRECK;
   public static final StructureFeature<?> VILLAGE;

   private static StructureFeature<?> register(String var0, StructureFeature<?> var1) {
      return (StructureFeature)Registry.register(Registry.STRUCTURE_FEATURE, (String)var0.toLowerCase(Locale.ROOT), var1);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart loadStaticStart(ChunkGenerator<?> var0, StructureManager var1, BiomeSource var2, CompoundTag var3) {
      String var4 = var3.getString("id");
      if ("INVALID".equals(var4)) {
         return StructureStart.INVALID_START;
      } else {
         StructureFeature var5 = (StructureFeature)Registry.STRUCTURE_FEATURE.get(new ResourceLocation(var4.toLowerCase(Locale.ROOT)));
         if (var5 == null) {
            LOGGER.error("Unknown feature id: {}", var4);
            return null;
         } else {
            int var6 = var3.getInt("ChunkX");
            int var7 = var3.getInt("ChunkZ");
            Biome var8 = var3.contains("biome") ? (Biome)Registry.BIOME.get(new ResourceLocation(var3.getString("biome"))) : var2.getBiome(new BlockPos((var6 << 4) + 9, 0, (var7 << 4) + 9));
            BoundingBox var9 = var3.contains("BB") ? new BoundingBox(var3.getIntArray("BB")) : BoundingBox.getUnknownBox();
            ListTag var10 = var3.getList("Children", 10);

            try {
               StructureStart var11 = var5.getStartFactory().create(var5, var6, var7, var8, var9, 0, var0.getSeed());

               for(int var12 = 0; var12 < var10.size(); ++var12) {
                  CompoundTag var13 = var10.getCompound(var12);
                  String var14 = var13.getString("id");
                  StructurePieceType var15 = (StructurePieceType)Registry.STRUCTURE_PIECE.get(new ResourceLocation(var14.toLowerCase(Locale.ROOT)));
                  if (var15 == null) {
                     LOGGER.error("Unknown structure piece id: {}", var14);
                  } else {
                     try {
                        StructurePiece var16 = var15.load(var1, var13);
                        var11.pieces.add(var16);
                     } catch (Exception var17) {
                        LOGGER.error("Exception loading structure piece with id {}", var14, var17);
                     }
                  }
               }

               return var11;
            } catch (Exception var18) {
               LOGGER.error("Failed Start with id {}", var4, var18);
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
