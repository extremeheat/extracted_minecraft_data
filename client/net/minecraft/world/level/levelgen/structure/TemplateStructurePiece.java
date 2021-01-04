package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   protected StructureTemplate template;
   protected StructurePlaceSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(StructurePieceType var1, int var2) {
      super(var1, var2);
   }

   public TemplateStructurePiece(StructurePieceType var1, CompoundTag var2) {
      super(var1, var2);
      this.templatePosition = new BlockPos(var2.getInt("TPX"), var2.getInt("TPY"), var2.getInt("TPZ"));
   }

   protected void setup(StructureTemplate var1, BlockPos var2, StructurePlaceSettings var3) {
      this.template = var1;
      this.setOrientation(Direction.NORTH);
      this.templatePosition = var2;
      this.placeSettings = var3;
      this.boundingBox = var1.getBoundingBox(var3, var2);
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      var1.putInt("TPX", this.templatePosition.getX());
      var1.putInt("TPY", this.templatePosition.getY());
      var1.putInt("TPZ", this.templatePosition.getZ());
   }

   public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
      this.placeSettings.setBoundingBox(var3);
      this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if (this.template.placeInWorld(var1, this.templatePosition, this.placeSettings, 2)) {
         List var5 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK);
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            StructureTemplate.StructureBlockInfo var7 = (StructureTemplate.StructureBlockInfo)var6.next();
            if (var7.nbt != null) {
               StructureMode var8 = StructureMode.valueOf(var7.nbt.getString("mode"));
               if (var8 == StructureMode.DATA) {
                  this.handleDataMarker(var7.nbt.getString("metadata"), var7.pos, var1, var2, var3);
               }
            }
         }

         List var14 = this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW_BLOCK);
         Iterator var15 = var14.iterator();

         while(var15.hasNext()) {
            StructureTemplate.StructureBlockInfo var16 = (StructureTemplate.StructureBlockInfo)var15.next();
            if (var16.nbt != null) {
               String var9 = var16.nbt.getString("final_state");
               BlockStateParser var10 = new BlockStateParser(new StringReader(var9), false);
               BlockState var11 = Blocks.AIR.defaultBlockState();

               try {
                  var10.parse(true);
                  BlockState var12 = var10.getState();
                  if (var12 != null) {
                     var11 = var12;
                  } else {
                     LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", var9, var16.pos);
                  }
               } catch (CommandSyntaxException var13) {
                  LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", var9, var16.pos);
               }

               var1.setBlock(var16.pos, var11, 3);
            }
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String var1, BlockPos var2, LevelAccessor var3, Random var4, BoundingBox var5);

   public void move(int var1, int var2, int var3) {
      super.move(var1, var2, var3);
      this.templatePosition = this.templatePosition.offset(var1, var2, var3);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}
