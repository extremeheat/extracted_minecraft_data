package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class JigsawReplacementProcessor extends StructureProcessor {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<JigsawReplacementProcessor> CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final JigsawReplacementProcessor INSTANCE = new JigsawReplacementProcessor();

   private JigsawReplacementProcessor() {
      super();
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo processBlock(LevelReader var1, BlockPos var2, BlockPos var3, StructureTemplate.StructureBlockInfo var4, StructureTemplate.StructureBlockInfo var5, StructurePlaceSettings var6) {
      BlockState var7 = var5.state();
      if (var7.is(Blocks.JIGSAW)) {
         if (var5.nbt() == null) {
            LOGGER.warn("Jigsaw block at {} is missing nbt, will not replace", var2);
            return var5;
         } else {
            String var8 = var5.nbt().getString("final_state");

            BlockState var9;
            try {
               BlockStateParser.BlockResult var10 = BlockStateParser.parseForBlock(var1.holderLookup(Registries.BLOCK), var8, true);
               var9 = var10.blockState();
            } catch (CommandSyntaxException var11) {
               LOGGER.error("Failed to parse jigsaw replacement state '{}' at {}: {}", new Object[]{var8, var2, var11.getMessage()});
               return null;
            }

            return var9.is(Blocks.STRUCTURE_VOID) ? null : new StructureTemplate.StructureBlockInfo(var5.pos(), var9, (CompoundTag)null);
         }
      } else {
         return var5;
      }
   }

   protected StructureProcessorType<?> getType() {
      return StructureProcessorType.JIGSAW_REPLACEMENT;
   }
}
