package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
   private static final MapCodec<EntityType<?>> ENTITY_TYPE_FIELD_CODEC;
   private final int backgroundColor;
   private final int highlightColor;
   private final EntityType<?> defaultType;

   public SpawnEggItem(EntityType<? extends Mob> var1, int var2, int var3, Item.Properties var4) {
      super(var4);
      this.defaultType = var1;
      this.backgroundColor = var2;
      this.highlightColor = var3;
      BY_ID.put(var1, this);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      if (!(var2 instanceof ServerLevel)) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack var3 = var1.getItemInHand();
         BlockPos var4 = var1.getClickedPos();
         Direction var5 = var1.getClickedFace();
         BlockState var6 = var2.getBlockState(var4);
         BlockEntity var8 = var2.getBlockEntity(var4);
         EntityType var10;
         if (var8 instanceof Spawner) {
            Spawner var9 = (Spawner)var8;
            var10 = this.getType(var3);
            var9.setEntityId(var10, var2.getRandom());
            var2.sendBlockUpdated(var4, var6, var6, 3);
            var2.gameEvent(var1.getPlayer(), GameEvent.BLOCK_CHANGE, var4);
            var3.shrink(1);
            return InteractionResult.CONSUME;
         } else {
            BlockPos var7;
            if (var6.getCollisionShape(var2, var4).isEmpty()) {
               var7 = var4;
            } else {
               var7 = var4.relative(var5);
            }

            var10 = this.getType(var3);
            if (var10.spawn((ServerLevel)var2, var3, var1.getPlayer(), var7, MobSpawnType.SPAWN_EGG, true, !Objects.equals(var4, var7) && var5 == Direction.UP) != null) {
               var3.shrink(1);
               var2.gameEvent(var1.getPlayer(), GameEvent.ENTITY_PLACE, var4);
            }

            return InteractionResult.CONSUME;
         }
      }
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      BlockHitResult var5 = getPlayerPOVHitResult(var1, var2, ClipContext.Fluid.SOURCE_ONLY);
      if (var5.getType() != HitResult.Type.BLOCK) {
         return InteractionResultHolder.pass(var4);
      } else if (!(var1 instanceof ServerLevel)) {
         return InteractionResultHolder.success(var4);
      } else {
         BlockPos var7 = var5.getBlockPos();
         if (!(var1.getBlockState(var7).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(var4);
         } else if (var1.mayInteract(var2, var7) && var2.mayUseItemAt(var7, var5.getDirection(), var4)) {
            EntityType var8 = this.getType(var4);
            Entity var9 = var8.spawn((ServerLevel)var1, var4, var2, var7, MobSpawnType.SPAWN_EGG, false, false);
            if (var9 == null) {
               return InteractionResultHolder.pass(var4);
            } else {
               var4.consume(1, var2);
               var2.awardStat(Stats.ITEM_USED.get(this));
               var1.gameEvent(var2, GameEvent.ENTITY_PLACE, var9.position());
               return InteractionResultHolder.consume(var4);
            }
         } else {
            return InteractionResultHolder.fail(var4);
         }
      }
   }

   public boolean spawnsEntity(ItemStack var1, EntityType<?> var2) {
      return Objects.equals(this.getType(var1), var2);
   }

   public int getColor(int var1) {
      return var1 == 0 ? this.backgroundColor : this.highlightColor;
   }

   @Nullable
   public static SpawnEggItem byId(@Nullable EntityType<?> var0) {
      return (SpawnEggItem)BY_ID.get(var0);
   }

   public static Iterable<SpawnEggItem> eggs() {
      return Iterables.unmodifiableIterable(BY_ID.values());
   }

   public EntityType<?> getType(ItemStack var1) {
      CustomData var2 = (CustomData)var1.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
      return !var2.isEmpty() ? (EntityType)var2.read(ENTITY_TYPE_FIELD_CODEC).result().orElse(this.defaultType) : this.defaultType;
   }

   public FeatureFlagSet requiredFeatures() {
      return this.defaultType.requiredFeatures();
   }

   public Optional<Mob> spawnOffspringFromSpawnEgg(Player var1, Mob var2, EntityType<? extends Mob> var3, ServerLevel var4, Vec3 var5, ItemStack var6) {
      if (!this.spawnsEntity(var6, var3)) {
         return Optional.empty();
      } else {
         Object var7;
         if (var2 instanceof AgeableMob) {
            var7 = ((AgeableMob)var2).getBreedOffspring(var4, (AgeableMob)var2);
         } else {
            var7 = (Mob)var3.create(var4);
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

   static {
      ENTITY_TYPE_FIELD_CODEC = BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("id");
   }
}
