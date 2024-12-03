package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.component.CustomData;
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
   private static final Map<EntityType<? extends Mob>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();
   private final EntityType<?> defaultType;

   public SpawnEggItem(EntityType<? extends Mob> var1, Item.Properties var2) {
      super(var2);
      this.defaultType = var1;
      BY_ID.put(var1, this);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack var3 = var1.getItemInHand();
         BlockPos var4 = var1.getClickedPos();
         Direction var5 = var1.getClickedFace();
         BlockState var6 = var2.getBlockState(var4);
         BlockEntity var8 = var2.getBlockEntity(var4);
         if (var8 instanceof Spawner) {
            Spawner var9 = (Spawner)var8;
            EntityType var11 = this.getType(var2.registryAccess(), var3);
            var9.setEntityId(var11, var2.getRandom());
            var2.sendBlockUpdated(var4, var6, var6, 3);
            var2.gameEvent(var1.getPlayer(), GameEvent.BLOCK_CHANGE, var4);
            var3.shrink(1);
            return InteractionResult.SUCCESS;
         } else {
            BlockPos var7;
            if (var6.getCollisionShape(var2, var4).isEmpty()) {
               var7 = var4;
            } else {
               var7 = var4.relative(var5);
            }

            EntityType var10 = this.getType(var2.registryAccess(), var3);
            if (var10.spawn((ServerLevel)var2, var3, var1.getPlayer(), var7, EntitySpawnReason.SPAWN_ITEM_USE, true, !Objects.equals(var4, var7) && var5 == Direction.UP) != null) {
               var3.shrink(1);
               var2.gameEvent(var1.getPlayer(), GameEvent.ENTITY_PLACE, var4);
            }

            return InteractionResult.SUCCESS;
         }
      }
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.SOURCE_ONLY);
      if (var5.getType() != HitResult.Type.BLOCK) {
         return InteractionResult.PASS;
      } else if (var1 instanceof ServerLevel) {
         ServerLevel var6 = (ServerLevel)var1;
         BlockPos var8 = var5.getBlockPos();
         if (!(var1.getBlockState(var8).getBlock() instanceof LiquidBlock)) {
            return InteractionResult.PASS;
         } else if (var1.mayInteract(var2, var8) && var2.mayUseItemAt(var8, var5.getDirection(), var4)) {
            EntityType var9 = this.getType(var6.registryAccess(), var4);
            Entity var10 = var9.spawn(var6, var4, var2, var8, EntitySpawnReason.SPAWN_ITEM_USE, false, false);
            if (var10 == null) {
               return InteractionResult.PASS;
            } else {
               var4.consume(1, var2);
               var2.awardStat(Stats.ITEM_USED.get(this));
               var1.gameEvent(var2, GameEvent.ENTITY_PLACE, var10.position());
               return InteractionResult.SUCCESS;
            }
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public boolean spawnsEntity(HolderLookup.Provider var1, ItemStack var2, EntityType<?> var3) {
      return Objects.equals(this.getType(var1, var2), var3);
   }

   @Nullable
   public static SpawnEggItem byId(@Nullable EntityType<?> var0) {
      return (SpawnEggItem)BY_ID.get(var0);
   }

   public static Iterable<SpawnEggItem> eggs() {
      return Iterables.unmodifiableIterable(BY_ID.values());
   }

   public EntityType<?> getType(HolderLookup.Provider var1, ItemStack var2) {
      CustomData var3 = (CustomData)var2.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
      if (!var3.isEmpty()) {
         EntityType var4 = (EntityType)var3.parseEntityType(var1, Registries.ENTITY_TYPE);
         if (var4 != null) {
            return var4;
         }
      }

      return this.defaultType;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.defaultType.requiredFeatures();
   }

   public Optional<Mob> spawnOffspringFromSpawnEgg(Player var1, Mob var2, EntityType<? extends Mob> var3, ServerLevel var4, Vec3 var5, ItemStack var6) {
      if (!this.spawnsEntity(var4.registryAccess(), var6, var3)) {
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
               ((Mob)var7).moveTo(var5.x(), var5.y(), var5.z(), 0.0F, 0.0F);
               var4.addFreshEntityWithPassengers((Entity)var7);
               ((Mob)var7).setCustomName((Component)var6.get(DataComponents.CUSTOM_NAME));
               var6.consume(1, var1);
               return Optional.of(var7);
            }
         }
      }
   }

   public boolean shouldPrintOpWarning(ItemStack var1, @Nullable Player var2) {
      if (var2 != null && var2.getPermissionLevel() >= 2) {
         CustomData var3 = (CustomData)var1.get(DataComponents.ENTITY_DATA);
         if (var3 != null) {
            EntityType var4 = (EntityType)var3.parseEntityType(var2.level().registryAccess(), Registries.ENTITY_TYPE);
            return var4 != null && var4.onlyOpCanSetNbt();
         }
      }

      return false;
   }
}
