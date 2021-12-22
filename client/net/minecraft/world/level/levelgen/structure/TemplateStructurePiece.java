package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final String templateName;
   protected StructureTemplate template;
   protected StructurePlaceSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(StructurePieceType var1, int var2, StructureManager var3, ResourceLocation var4, String var5, StructurePlaceSettings var6, BlockPos var7) {
      super(var1, var2, var3.getOrCreate(var4).getBoundingBox(var6, var7));
      this.setOrientation(Direction.NORTH);
      this.templateName = var5;
      this.templatePosition = var7;
      this.template = var3.getOrCreate(var4);
      this.placeSettings = var6;
   }

   public TemplateStructurePiece(StructurePieceType var1, CompoundTag var2, StructureManager var3, Function<ResourceLocation, StructurePlaceSettings> var4) {
      super(var1, var2);
      this.setOrientation(Direction.NORTH);
      this.templateName = var2.getString("Template");
      this.templatePosition = new BlockPos(var2.getInt("TPX"), var2.getInt("TPY"), var2.getInt("TPZ"));
      ResourceLocation var5 = this.makeTemplateLocation();
      this.template = var3.getOrCreate(var5);
      this.placeSettings = (StructurePlaceSettings)var4.apply(var5);
      this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
   }

   protected ResourceLocation makeTemplateLocation() {
      return new ResourceLocation(this.templateName);
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      var2.putInt("TPX", this.templatePosition.getX());
      var2.putInt("TPY", this.templatePosition.getY());
      var2.putInt("TPZ", this.templatePosition.getZ());
      var2.putString("Template", this.templateName);
   }

   public void postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      this.placeSettings.setBoundingBox(var5);
      this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if (this.template.placeInWorld(var1, this.templatePosition, var7, this.placeSettings, var4, 2)) {
         List var8 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            StructureTemplate.StructureBlockInfo var10 = (StructureTemplate.StructureBlockInfo)var9.next();
            if (var10.nbt != null) {
               StructureMode var11 = StructureMode.valueOf(var10.nbt.getString("mode"));
               if (var11 == StructureMode.DATA) {
                  this.handleDataMarker(var10.nbt.getString("metadata"), var10.pos, var1, var4, var5);
               }
            }
         }

         List var17 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW);
         Iterator var18 = var17.iterator();

         while(var18.hasNext()) {
            StructureTemplate.StructureBlockInfo var19 = (StructureTemplate.StructureBlockInfo)var18.next();
            if (var19.nbt != null) {
               String var12 = var19.nbt.getString("final_state");
               BlockStateParser var13 = new BlockStateParser(new StringReader(var12), false);
               BlockState var14 = Blocks.AIR.defaultBlockState();

               try {
                  var13.parse(true);
                  BlockState var15 = var13.getState();
                  if (var15 != null) {
                     var14 = var15;
                  } else {
                     LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", var12, var19.pos);
                  }
               } catch (CommandSyntaxException var16) {
                  LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", var12, var19.pos);
               }

               var1.setBlock(var19.pos, var14, 3);
            }
         }
      }

   }

   protected abstract void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, Random var4, BoundingBox var5);

   /** @deprecated */
   @Deprecated
   public void move(int var1, int var2, int var3) {
      super.move(var1, var2, var3);
      this.templatePosition = this.templatePosition.offset(var1, var2, var3);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}
