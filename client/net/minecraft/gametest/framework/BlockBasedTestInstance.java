package net.minecraft.gametest.framework;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TestBlock;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.TestBlockMode;

public class BlockBasedTestInstance extends GameTestInstance {
   public static final MapCodec<BlockBasedTestInstance> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(TestData.CODEC.forGetter(GameTestInstance::info)).apply(var0, BlockBasedTestInstance::new));

   public BlockBasedTestInstance(TestData<Holder<TestEnvironmentDefinition>> var1) {
      super(var1);
   }

   public void run(GameTestHelper var1) {
      BlockPos var2 = this.findStartBlock(var1);
      TestBlockEntity var3 = (TestBlockEntity)var1.getBlockEntity(var2, TestBlockEntity.class);
      var3.trigger();
      var1.onEachTick(() -> {
         List var2 = this.findTestBlocks(var1, TestBlockMode.ACCEPT);
         if (var2.isEmpty()) {
            var1.fail((Component)Component.translatable("test_block.error.missing", TestBlockMode.ACCEPT.getDisplayName()));
         }

         boolean var3 = var2.stream().map((var1x) -> (TestBlockEntity)var1.getBlockEntity(var1x, TestBlockEntity.class)).anyMatch(TestBlockEntity::hasTriggered);
         if (var3) {
            var1.succeed();
         } else {
            this.forAllTriggeredTestBlocks(var1, TestBlockMode.FAIL, (var1x) -> var1.fail((Component)Component.literal(var1x.getMessage())));
            this.forAllTriggeredTestBlocks(var1, TestBlockMode.LOG, TestBlockEntity::trigger);
         }

      });
   }

   private void forAllTriggeredTestBlocks(GameTestHelper var1, TestBlockMode var2, Consumer<TestBlockEntity> var3) {
      for(BlockPos var6 : this.findTestBlocks(var1, var2)) {
         TestBlockEntity var7 = (TestBlockEntity)var1.getBlockEntity(var6, TestBlockEntity.class);
         if (var7.hasTriggered()) {
            var3.accept(var7);
            var7.reset();
         }
      }

   }

   private BlockPos findStartBlock(GameTestHelper var1) {
      List var2 = this.findTestBlocks(var1, TestBlockMode.START);
      if (var2.isEmpty()) {
         var1.fail((Component)Component.translatable("test_block.error.missing", TestBlockMode.START.getDisplayName()));
      }

      if (var2.size() != 1) {
         var1.fail((Component)Component.translatable("test_block.error.too_many", TestBlockMode.START.getDisplayName()));
      }

      return (BlockPos)var2.getFirst();
   }

   private List<BlockPos> findTestBlocks(GameTestHelper var1, TestBlockMode var2) {
      ArrayList var3 = new ArrayList();
      var1.forEveryBlockInStructure((var3x) -> {
         BlockState var4 = var1.getBlockState(var3x);
         if (var4.is(Blocks.TEST_BLOCK) && var4.getValue(TestBlock.MODE) == var2) {
            var3.add(var3x.immutable());
         }

      });
      return var3;
   }

   public MapCodec<BlockBasedTestInstance> codec() {
      return CODEC;
   }

   protected MutableComponent typeDescription() {
      return Component.translatable("test_instance.type.block_based");
   }
}
