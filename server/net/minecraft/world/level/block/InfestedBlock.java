package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class InfestedBlock extends Block {
   private final Block hostBlock;
   private static final Map<Block, Block> BLOCK_BY_HOST_BLOCK = Maps.newIdentityHashMap();

   public InfestedBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
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
      Silverfish var3 = (Silverfish)EntityType.SILVERFISH.create(var1);
      var3.moveTo((double)var2.getX() + 0.5D, (double)var2.getY(), (double)var2.getZ() + 0.5D, 0.0F, 0.0F);
      var1.addFreshEntity(var3);
      var3.spawnAnim();
   }

   public void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4) {
      super.spawnAfterBreak(var1, var2, var3, var4);
      if (var2.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, var4) == 0) {
         this.spawnInfestation(var2, var3);
      }

   }

   public void wasExploded(Level var1, BlockPos var2, Explosion var3) {
      if (var1 instanceof ServerLevel) {
         this.spawnInfestation((ServerLevel)var1, var2);
      }

   }

   public static BlockState stateByHostBlock(Block var0) {
      return ((Block)BLOCK_BY_HOST_BLOCK.get(var0)).defaultBlockState();
   }
}
