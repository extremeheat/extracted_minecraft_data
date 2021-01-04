package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class JigsawReplacementProcessor extends StructureProcessor {
   public static final JigsawReplacementProcessor INSTANCE = new JigsawReplacementProcessor();

   private JigsawReplacementProcessor() {
      super();
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, StructureTemplate.StructureBlockInfo var3, StructureTemplate.StructureBlockInfo var4, StructurePlaceSettings var5) {
      Block var6 = var4.state.getBlock();
      if (var6 != Blocks.JIGSAW_BLOCK) {
         return var4;
      } else {
         String var7 = var4.nbt.getString("final_state");
         BlockStateParser var8 = new BlockStateParser(new StringReader(var7), false);

         try {
            var8.parse(true);
         } catch (CommandSyntaxException var10) {
            throw new RuntimeException(var10);
         }

         return var8.getState().getBlock() == Blocks.STRUCTURE_VOID ? null : new StructureTemplate.StructureBlockInfo(var4.pos, var8.getState(), (CompoundTag)null);
      }
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.JIGSAW_REPLACEMENT;
   }

   protected <T> Dynamic<T> getDynamic(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.emptyMap());
   }
}
