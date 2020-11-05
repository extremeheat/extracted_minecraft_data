package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SpawnEggItem extends Item {
   private static final Map<EntityType<? extends Mob>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();
   private final int color1;
   private final int color2;
   private final EntityType<?> defaultType;

   public SpawnEggItem(EntityType<? extends Mob> var1, int var2, int var3, Item.Properties var4) {
      super(var4);
      this.defaultType = var1;
      this.color1 = var2;
      this.color2 = var3;
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
         if (var6.is(Blocks.SPAWNER)) {
            BlockEntity var7 = var2.getBlockEntity(var4);
            if (var7 instanceof SpawnerBlockEntity) {
               BaseSpawner var11 = ((SpawnerBlockEntity)var7).getSpawner();
               EntityType var9 = this.getType(var3.getTag());
               var11.setEntityId(var9);
               var7.setChanged();
               var2.sendBlockUpdated(var4, var6, var6, 3);
               var3.shrink(1);
               return InteractionResult.CONSUME;
            }
         }

         BlockPos var10;
         if (var6.getCollisionShape(var2, var4).isEmpty()) {
            var10 = var4;
         } else {
            var10 = var4.relative(var5);
         }

         EntityType var8 = this.getType(var3.getTag());
         if (var8.spawn((ServerLevel)var2, var3, var1.getPlayer(), var10, MobSpawnType.SPAWN_EGG, true, !Objects.equals(var4, var10) && var5 == Direction.UP) != null) {
            var3.shrink(1);
         }

         return InteractionResult.CONSUME;
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
         BlockHitResult var6 = (BlockHitResult)var5;
         BlockPos var7 = var6.getBlockPos();
         if (!(var1.getBlockState(var7).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(var4);
         } else if (var1.mayInteract(var2, var7) && var2.mayUseItemAt(var7, var6.getDirection(), var4)) {
            EntityType var8 = this.getType(var4.getTag());
            if (var8.spawn((ServerLevel)var1, var4, var2, var7, MobSpawnType.SPAWN_EGG, false, false) == null) {
               return InteractionResultHolder.pass(var4);
            } else {
               if (!var2.getAbilities().instabuild) {
                  var4.shrink(1);
               }

               var2.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.consume(var4);
            }
         } else {
            return InteractionResultHolder.fail(var4);
         }
      }
   }

   public boolean spawnsEntity(@Nullable CompoundTag var1, EntityType<?> var2) {
      return Objects.equals(this.getType(var1), var2);
   }

   public int getColor(int var1) {
      return var1 == 0 ? this.color1 : this.color2;
   }

   @Nullable
   public static SpawnEggItem byId(@Nullable EntityType<?> var0) {
      return (SpawnEggItem)BY_ID.get(var0);
   }

   public static Iterable<SpawnEggItem> eggs() {
      return Iterables.unmodifiableIterable(BY_ID.values());
   }

   public EntityType<?> getType(@Nullable CompoundTag var1) {
      if (var1 != null && var1.contains("EntityTag", 10)) {
         CompoundTag var2 = var1.getCompound("EntityTag");
         if (var2.contains("id", 8)) {
            return (EntityType)EntityType.byString(var2.getString("id")).orElse(this.defaultType);
         }
      }

      return this.defaultType;
   }

   public Optional<Mob> spawnOffspringFromSpawnEgg(Player var1, Mob var2, EntityType<? extends Mob> var3, ServerLevel var4, Vec3 var5, ItemStack var6) {
      if (!this.spawnsEntity(var6.getTag(), var3)) {
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
               if (var6.hasCustomHoverName()) {
                  ((Mob)var7).setCustomName(var6.getHoverName());
               }

               if (!var1.getAbilities().instabuild) {
                  var6.shrink(1);
               }

               return Optional.of(var7);
            }
         }
      }
   }
}
