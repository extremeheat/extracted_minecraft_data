package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class InfestedBlock extends Block {
   public static final MapCodec<InfestedBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("host").forGetter(InfestedBlock::getHostBlock), propertiesCodec())
            .apply(var0, InfestedBlock::new)
   );
   private final Block hostBlock;
   private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();
   private static final Map<BlockState, BlockState> HOST_TO_INFESTED_STATES = Maps.newIdentityHashMap();
   private static final Map<BlockState, BlockState> INFESTED_TO_HOST_STATES = Maps.newIdentityHashMap();

   @Override
   public MapCodec<? extends InfestedBlock> codec() {
      return CODEC;
   }

   public InfestedBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2.destroyTime(var1.defaultDestroyTime() / 2.0F).explosionResistance(0.75F));
      this.hostBlock = var1;
      BLOCK_BY_HOST_BLOCK.put(var1, this);
   }

   public Block getHostBlock() {
      return this.hostBlock;
   }

   public static boolean isCompatibleHostBlock(BlockState var0) {
      return BLOCK_BY_HOST_BLOCK.containsKey(var0.getBlock());
   }

   private void spawnInfestation(ServerLevel var1, BlockPos var2) {
      Silverfish var3 = EntityType.SILVERFISH.create(var1, EntitySpawnReason.TRIGGERED);
      if (var3 != null) {
         var3.moveTo((double)var2.getX() + 0.5, (double)var2.getY(), (double)var2.getZ() + 0.5, 0.0F, 0.0F);
         var1.addFreshEntity(var3);
         var3.spawnAnim();
      }
   }

   @Override
   protected void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var2.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !EnchantmentHelper.hasTag(var4, EnchantmentTags.PREVENTS_INFESTED_SPAWNS)) {
         this.spawnInfestation(var2, var3);
      }
   }

   public static BlockState infestedStateByHost(BlockState var0) {
      return getNewStateWithProperties(HOST_TO_INFESTED_STATES, var0, () -> BLOCK_BY_HOST_BLOCK.get(var0.getBlock()).defaultBlockState());
   }

   public BlockState hostStateByInfested(BlockState var1) {
      return getNewStateWithProperties(INFESTED_TO_HOST_STATES, var1, () -> this.getHostBlock().defaultBlockState());
   }

   private static BlockState getNewStateWithProperties(Map<BlockState, BlockState> var0, BlockState var1, Supplier<BlockState> var2) {
      return var0.computeIfAbsent(var1, var1x -> {
         BlockState var2x = (BlockState)var2.get();

         for (Property var4 : var1x.getProperties()) {
            var2x = var2x.hasProperty(var4) ? var2x.setValue(var4, var1x.getValue(var4)) : var2x;
         }

         return var2x;
      });
   }
}
