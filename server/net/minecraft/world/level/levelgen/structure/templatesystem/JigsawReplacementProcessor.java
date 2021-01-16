package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class JigsawReplacementProcessor extends StructureProcessor {
   public static final Codec<JigsawReplacementProcessor> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final JigsawReplacementProcessor INSTANCE = new JigsawReplacementProcessor();

   private JigsawReplacementProcessor() {
      super();
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      BlockState var7 = var5.state;
      if (var7.is(Blocks.JIGSAW)) {
         String var8 = var5.nbt.getString("final_state");
         BlockStateParser var9 = new BlockStateParser(new StringReader(var8), false);

         try {
            var9.parse(true);
         } catch (CommandSyntaxException var11) {
            throw new RuntimeException(var11);
         }

         return var9.getState().is(Blocks.STRUCTURE_VOID) ? null : new StructureTemplate.StructureBlockInfo(var5.pos, var9.getState(), (CompoundTag)null);
      } else {
         return var5;
      }
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.JIGSAW_REPLACEMENT;
   }
}
