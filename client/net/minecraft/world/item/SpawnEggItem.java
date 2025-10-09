package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.stats.Stats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SpawnEggItem extends Item {
   private static final Map<EntityType<?>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();

   public SpawnEggItem(Item.Properties var1) {
      super(var1);
      TypedEntityData var2 = (TypedEntityData)this.components().get(DataComponents.ENTITY_DATA);
      if (var2 != null) {
         BY_ID.put((EntityType)var2.type(), this);
      }

   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      if (!(var2 instanceof ServerLevel var3)) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack var4 = var1.getItemInHand();
         BlockPos var5 = var1.getClickedPos();
         Direction var6 = var1.getClickedFace();
         BlockState var7 = var2.getBlockState(var5);
         BlockEntity var9 = var2.getBlockEntity(var5);
         if (var9 instanceof Spawner var12) {
            EntityType var13 = this.getType(var4);
            if (var13 == null) {
               return InteractionResult.FAIL;
            } else if (!var3.isSpawnerBlockEnabled()) {
               Player var11 = var1.getPlayer();
               if (var11 instanceof ServerPlayer) {
                  ServerPlayer var10 = (ServerPlayer)var11;
                  var10.sendSystemMessage(Component.translatable("advMode.notEnabled.spawner"));
               }

               return InteractionResult.FAIL;
            } else {
               var12.setEntityId(var13, var2.getRandom());
               var2.sendBlockUpdated(var5, var7, var7, 3);
               var2.gameEvent(var1.getPlayer(), GameEvent.BLOCK_CHANGE, var5);
               var4.shrink(1);
               return InteractionResult.SUCCESS;
            }
         } else {
            BlockPos var8;
            if (var7.getCollisionShape(var2, var5).isEmpty()) {
               var8 = var5;
            } else {
               var8 = var5.relative(var6);
            }

            return this.spawnMob(var1.getPlayer(), var4, var2, var8, true, !Objects.equals(var5, var8) && var6 == Direction.UP);
         }
      }
   }

   private InteractionResult spawnMob(@Nullable LivingEntity var1, ItemStack var2, Level var3, BlockPos var4, boolean var5, boolean var6) {
      EntityType var7 = this.getType(var2);
      if (var7 == null) {
         return InteractionResult.FAIL;
      } else if (!var7.isAllowedInPeaceful() && var3.getDifficulty() == Difficulty.PEACEFUL) {
         return InteractionResult.FAIL;
      } else {
         if (var7.spawn((ServerLevel)var3, var2, var1, var4, EntitySpawnReason.SPAWN_ITEM_USE, var5, var6) != null) {
            var2.consume(1, var1);
            var3.gameEvent(var1, GameEvent.ENTITY_PLACE, var4);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.SOURCE_ONLY);
      if (var5.getType() != HitResult.Type.BLOCK) {
         return InteractionResult.PASS;
      } else if (var1 instanceof ServerLevel) {
         ServerLevel var6 = (ServerLevel)var1;
         BlockPos var7 = var5.getBlockPos();
         if (!(var1.getBlockState(var7).getBlock() instanceof LiquidBlock)) {
            return InteractionResult.PASS;
         } else if (var1.mayInteract(var2, var7) && var2.mayUseItemAt(var7, var5.getDirection(), var4)) {
            InteractionResult var8 = this.spawnMob(var2, var4, var1, var7, false, false);
            if (var8 == InteractionResult.SUCCESS) {
               var2.awardStat(Stats.ITEM_USED.get(this));
            }

            return var8;
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public boolean spawnsEntity(ItemStack var1, EntityType<?> var2) {
      return Objects.equals(this.getType(var1), var2);
   }

   @Nullable
   public static SpawnEggItem byId(@Nullable EntityType<?> var0) {
      return (SpawnEggItem)BY_ID.get(var0);
   }

   public static Iterable<SpawnEggItem> eggs() {
      return Iterables.unmodifiableIterable(BY_ID.values());
   }

   @Nullable
   public EntityType<?> getType(ItemStack var1) {
      TypedEntityData var2 = (TypedEntityData)var1.get(DataComponents.ENTITY_DATA);
      return var2 != null ? (EntityType)var2.type() : null;
   }

   public FeatureFlagSet requiredFeatures() {
      return (FeatureFlagSet)Optional.ofNullable((TypedEntityData)this.components().get(DataComponents.ENTITY_DATA)).map(TypedEntityData::type).map(EntityType::requiredFeatures).orElseGet(FeatureFlagSet::of);
   }

   public Optional<Mob> spawnOffspringFromSpawnEgg(Player var1, Mob var2, EntityType<? extends Mob> var3, ServerLevel var4, Vec3 var5, ItemStack var6) {
      if (!this.spawnsEntity(var6, var3)) {
         return Optional.empty();
      } else {
         Object var7;
         if (var2 instanceof AgeableMob) {
            var7 = ((AgeableMob)var2).getBreedOffspring(var4, (AgeableMob)var2);
         } else {
            var7 = (Mob)var3.create(var4, EntitySpawnReason.SPAWN_ITEM_USE);
         }

         if (var7 == null) {
            return Optional.empty();
         } else {
            ((Mob)var7).setBaby(true);
            if (!((Mob)var7).isBaby()) {
               return Optional.empty();
            } else {
               ((Mob)var7).snapTo(var5.x(), var5.y(), var5.z(), 0.0F, 0.0F);
               ((Mob)var7).applyComponentsFromItemStack(var6);
               var4.addFreshEntityWithPassengers((Entity)var7);
               var6.consume(1, var1);
               return Optional.of(var7);
            }
         }
      }
   }

   public boolean shouldPrintOpWarning(ItemStack var1, @Nullable Player var2) {
      if (var2 != null && var2.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)) {
         TypedEntityData var3 = (TypedEntityData)var1.get(DataComponents.ENTITY_DATA);
         if (var3 != null) {
            return ((EntityType)var3.type()).onlyOpCanSetNbt();
         }
      }

      return false;
   }
}
