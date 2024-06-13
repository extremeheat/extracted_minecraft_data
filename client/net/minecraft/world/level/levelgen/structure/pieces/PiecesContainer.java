package net.minecraft.world.level.levelgen.structure.pieces;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import org.slf4j.Logger;

public record PiecesContainer(List<StructurePiece> pieces) {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation JIGSAW_RENAME = new ResourceLocation("jigsaw");
   private static final Map<ResourceLocation, ResourceLocation> RENAMES = ImmutableMap.builder()
      .put(new ResourceLocation("nvi"), JIGSAW_RENAME)
      .put(new ResourceLocation("pcp"), JIGSAW_RENAME)
      .put(new ResourceLocation("bastionremnant"), JIGSAW_RENAME)
      .put(new ResourceLocation("runtime"), JIGSAW_RENAME)
      .build();

   public PiecesContainer(List<StructurePiece> pieces) {
      super();
      this.pieces = List.copyOf(pieces);
   }

   public boolean isEmpty() {
      return this.pieces.isEmpty();
   }

   public boolean isInsidePiece(BlockPos var1) {
      for (StructurePiece var3 : this.pieces) {
         if (var3.getBoundingBox().isInside(var1)) {
            return true;
         }
      }

      return false;
   }

   public Tag save(StructurePieceSerializationContext var1) {
      ListTag var2 = new ListTag();

      for (StructurePiece var4 : this.pieces) {
         var2.add(var4.createTag(var1));
      }

      return var2;
   }

   public static PiecesContainer load(ListTag var0, StructurePieceSerializationContext var1) {
      ArrayList var2 = Lists.newArrayList();

      for (int var3 = 0; var3 < var0.size(); var3++) {
         CompoundTag var4 = var0.getCompound(var3);
         String var5 = var4.getString("id").toLowerCase(Locale.ROOT);
         ResourceLocation var6 = new ResourceLocation(var5);
         ResourceLocation var7 = RENAMES.getOrDefault(var6, var6);
         StructurePieceType var8 = BuiltInRegistries.STRUCTURE_PIECE.get(var7);
         if (var8 == null) {
            LOGGER.error("Unknown structure piece id: {}", var7);
         } else {
            try {
               StructurePiece var9 = var8.load(var1, var4);
               var2.add(var9);
            } catch (Exception var10) {
               LOGGER.error("Exception loading structure piece with id {}", var7, var10);
            }
         }
      }

      return new PiecesContainer(var2);
   }

   public BoundingBox calculateBoundingBox() {
      return StructurePiece.createBoundingBox(this.pieces.stream());
   }
}
