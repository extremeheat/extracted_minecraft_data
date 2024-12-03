package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.util.context.ContextKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class LootContextParams {
   public static final ContextKey<Entity> THIS_ENTITY = ContextKey.<Entity>vanilla("this_entity");
   public static final ContextKey<Player> LAST_DAMAGE_PLAYER = ContextKey.<Player>vanilla("last_damage_player");
   public static final ContextKey<DamageSource> DAMAGE_SOURCE = ContextKey.<DamageSource>vanilla("damage_source");
   public static final ContextKey<Entity> ATTACKING_ENTITY = ContextKey.<Entity>vanilla("attacking_entity");
   public static final ContextKey<Entity> DIRECT_ATTACKING_ENTITY = ContextKey.<Entity>vanilla("direct_attacking_entity");
   public static final ContextKey<Vec3> ORIGIN = ContextKey.<Vec3>vanilla("origin");
   public static final ContextKey<BlockState> BLOCK_STATE = ContextKey.<BlockState>vanilla("block_state");
   public static final ContextKey<BlockEntity> BLOCK_ENTITY = ContextKey.<BlockEntity>vanilla("block_entity");
   public static final ContextKey<ItemStack> TOOL = ContextKey.<ItemStack>vanilla("tool");
   public static final ContextKey<Float> EXPLOSION_RADIUS = ContextKey.<Float>vanilla("explosion_radius");
   public static final ContextKey<Integer> ENCHANTMENT_LEVEL = ContextKey.<Integer>vanilla("enchantment_level");
   public static final ContextKey<Boolean> ENCHANTMENT_ACTIVE = ContextKey.<Boolean>vanilla("enchantment_active");

   public LootContextParams() {
      super();
   }
}
