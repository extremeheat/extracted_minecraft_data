package net.minecraft.world.level.block;

import com.google.common.collect.BiMap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class CopperChestBlock extends ChestBlock {
   public static final MapCodec<CopperChestBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperChestBlock::getState), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenChestSound), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseChestSound), propertiesCodec()).apply(var0, CopperChestBlock::new));
   private static final Map<Block, Supplier<Block>> COPPER_TO_COPPER_CHEST_MAPPING;
   private final WeatheringCopper.WeatherState weatherState;

   public MapCodec<? extends CopperChestBlock> codec() {
      return CODEC;
   }

   public CopperChestBlock(WeatheringCopper.WeatherState var1, SoundEvent var2, SoundEvent var3, BlockBehaviour.Properties var4) {
      super(() -> BlockEntityType.CHEST, var2, var3, var4);
      this.weatherState = var1;
   }

   public boolean chestCanConnectTo(BlockState var1) {
      return var1.is(BlockTags.COPPER_CHESTS) && var1.hasProperty(ChestBlock.TYPE);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = super.getStateForPlacement(var1);
      return getLeastOxidizedChestOfConnectedBlocks(var2, var1.getLevel(), var1.getClickedPos());
   }

   private static BlockState getLeastOxidizedChestOfConnectedBlocks(BlockState var0, Level var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2.relative(getConnectedDirection(var0)));
      if (!((ChestType)var0.getValue(ChestBlock.TYPE)).equals(ChestType.SINGLE)) {
         Block var6 = var0.getBlock();
         if (var6 instanceof CopperChestBlock) {
            CopperChestBlock var4 = (CopperChestBlock)var6;
            var6 = var3.getBlock();
            if (var6 instanceof CopperChestBlock) {
               CopperChestBlock var5 = (CopperChestBlock)var6;
               BlockState var10 = var0;
               BlockState var7 = var3;
               if (var4.isWaxed() != var5.isWaxed()) {
                  var10 = (BlockState)unwaxBlock(var4, var0).orElse(var0);
                  var7 = (BlockState)unwaxBlock(var5, var3).orElse(var3);
               }

               Block var8 = var4.weatherState.ordinal() <= var5.weatherState.ordinal() ? var10.getBlock() : var7.getBlock();
               return var8.withPropertiesOf(var10);
            }
         }
      }

      return var0;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      BlockState var9 = super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      if (this.chestCanConnectTo(var7)) {
         ChestType var10 = (ChestType)var9.getValue(ChestBlock.TYPE);
         if (!var10.equals(ChestType.SINGLE) && getConnectedDirection(var9) == var5) {
            return var7.getBlock().withPropertiesOf(var9);
         }
      }

      return var9;
   }

   private static Optional<BlockState> unwaxBlock(CopperChestBlock var0, BlockState var1) {
      return !var0.isWaxed() ? Optional.of(var1) : Optional.ofNullable((Block)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).get(var1.getBlock())).map((var1x) -> var1x.withPropertiesOf(var1));
   }

   public WeatheringCopper.WeatherState getState() {
      return this.weatherState;
   }

   public static BlockState getFromCopperBlock(Block var0, Direction var1, Level var2, BlockPos var3) {
      Map var10000 = COPPER_TO_COPPER_CHEST_MAPPING;
      Block var10002 = Blocks.COPPER_CHEST;
      Objects.requireNonNull(var10002);
      CopperChestBlock var4 = (CopperChestBlock)((Supplier)var10000.getOrDefault(var0, var10002::asBlock)).get();
      ChestType var5 = var4.getChestType(var2, var3, var1);
      BlockState var6 = (BlockState)((BlockState)var4.defaultBlockState().setValue(FACING, var1)).setValue(TYPE, var5);
      return getLeastOxidizedChestOfConnectedBlocks(var6, var2, var3);
   }

   public boolean isWaxed() {
      return true;
   }

   public boolean shouldChangedStateKeepBlockEntity(BlockState var1) {
      return var1.is(BlockTags.COPPER_CHESTS);
   }

   static {
      COPPER_TO_COPPER_CHEST_MAPPING = Map.of(Blocks.COPPER_BLOCK, (Supplier)() -> Blocks.COPPER_CHEST, Blocks.EXPOSED_COPPER, (Supplier)() -> Blocks.EXPOSED_COPPER_CHEST, Blocks.WEATHERED_COPPER, (Supplier)() -> Blocks.WEATHERED_COPPER_CHEST, Blocks.OXIDIZED_COPPER, (Supplier)() -> Blocks.OXIDIZED_COPPER_CHEST, Blocks.WAXED_COPPER_BLOCK, (Supplier)() -> Blocks.COPPER_CHEST, Blocks.WAXED_EXPOSED_COPPER, (Supplier)() -> Blocks.EXPOSED_COPPER_CHEST, Blocks.WAXED_WEATHERED_COPPER, (Supplier)() -> Blocks.WEATHERED_COPPER_CHEST, Blocks.WAXED_OXIDIZED_COPPER, (Supplier)() -> Blocks.OXIDIZED_COPPER_CHEST);
   }
}
