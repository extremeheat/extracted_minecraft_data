package net.minecraft.world.level;

import com.mojang.datafixers.util.Function4;
import java.util.Arrays;
import java.util.Set;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriFunction;

public interface UnlockCondition {
   default boolean playerDied(ServerLevel var1, ServerPlayer var2, DamageSource var3) {
      return false;
   }

   default boolean playerTookDamage(ServerLevel var1, ServerPlayer var2, DamageSource var3, float var4) {
      return false;
   }

   default boolean playerKilledEntity(ServerLevel var1, ServerPlayer var2, Entity var3) {
      return false;
   }

   default boolean playerAdvancement(ServerLevel var1, AdvancementHolder var2) {
      return false;
   }

   default boolean blockBreak(ServerLevel var1, BlockState var2, BlockPos var3) {
      return false;
   }

   default boolean blockDrops(ServerLevel var1, BlockState var2) {
      return false;
   }

   default boolean blockPlace(ServerLevel var1, BlockState var2, BlockPos var3) {
      return false;
   }

   default boolean craftedItem(ServerLevel var1, ServerPlayer var2, ItemStack var3) {
      return false;
   }

   default boolean obtainedItem(ServerLevel var1, ServerPlayer var2, ItemStack var3) {
      return false;
   }

   default boolean usedItem(ServerLevel var1, ServerPlayer var2, ItemStack var3) {
      return false;
   }

   default boolean fedAnimal(ServerLevel var1, ServerPlayer var2, Animal var3, ItemStack var4) {
      return false;
   }

   default boolean unlocked(ServerLevel var1, ServerPlayer var2, PlayerUnlock var3) {
      return false;
   }

   default boolean unlocked(ServerLevel var1, ServerPlayer var2, WorldEffect var3) {
      return false;
   }

   default boolean mapCompleted(ServerLevel var1, ServerPlayer var2, Set<WorldEffect> var3, boolean var4) {
      return false;
   }

   default boolean specialCompleted(ServerLevel var1, ServerPlayer var2, SpecialMine var3, boolean var4) {
      return false;
   }

   static UnlockCondition playerTookDamage(final Function4<ServerLevel, ServerPlayer, DamageSource, Float, Boolean> var0) {
      return new UnlockCondition() {
         public boolean playerTookDamage(ServerLevel var1, ServerPlayer var2, DamageSource var3, float var4) {
            return (Boolean)var0.apply(var1, var2, var3, var4);
         }
      };
   }

   static UnlockCondition playerDied(final TriFunction<ServerLevel, ServerPlayer, DamageSource, Boolean> var0) {
      return new UnlockCondition() {
         public boolean playerDied(ServerLevel var1, ServerPlayer var2, DamageSource var3) {
            return (Boolean)var0.apply(var1, var2, var3);
         }
      };
   }

   static UnlockCondition playerKilledEntity(EntityType<? extends Entity> var0) {
      return playerKilledEntity(1.0F, var0);
   }

   @SafeVarargs
   static UnlockCondition playerKilledEntity(final float var0, final EntityType<? extends Entity>... var1) {
      return new UnlockCondition() {
         public boolean playerKilledEntity(ServerLevel var1x, ServerPlayer var2, Entity var3) {
            return Arrays.stream(var1).anyMatch((var1xx) -> var1xx == var3.getType()) && var1x.random.nextDouble() < (double)var0;
         }
      };
   }

   static UnlockCondition playerKilledEntity(final TriFunction<ServerLevel, ServerPlayer, Entity, Boolean> var0) {
      return new UnlockCondition() {
         public boolean playerKilledEntity(ServerLevel var1, ServerPlayer var2, Entity var3) {
            return (Boolean)var0.apply(var1, var2, var3);
         }
      };
   }

   static UnlockCondition playerAdvancement(final BiFunction<ServerLevel, AdvancementHolder, Boolean> var0) {
      return new UnlockCondition() {
         public boolean playerAdvancement(ServerLevel var1, AdvancementHolder var2) {
            return (Boolean)var0.apply(var1, var2);
         }
      };
   }

   static UnlockCondition blockBreak(final BiFunction<ServerLevel, BlockState, Boolean> var0) {
      return new UnlockCondition() {
         public boolean blockBreak(ServerLevel var1, BlockState var2, BlockPos var3) {
            return (Boolean)var0.apply(var1, var2);
         }
      };
   }

   static UnlockCondition blockBreak(final TriFunction<ServerLevel, BlockState, BlockPos, Boolean> var0) {
      return new UnlockCondition() {
         public boolean blockBreak(ServerLevel var1, BlockState var2, BlockPos var3) {
            return (Boolean)var0.apply(var1, var2, var3);
         }
      };
   }

   static UnlockCondition blockDrops(final BiFunction<ServerLevel, BlockState, Boolean> var0) {
      return new UnlockCondition() {
         public boolean blockDrops(ServerLevel var1, BlockState var2) {
            return (Boolean)var0.apply(var1, var2);
         }
      };
   }

   static UnlockCondition blockPlace(final BiFunction<ServerLevel, BlockState, Boolean> var0) {
      return new UnlockCondition() {
         public boolean blockPlace(ServerLevel var1, BlockState var2, BlockPos var3) {
            return (Boolean)var0.apply(var1, var2);
         }
      };
   }

   static UnlockCondition blockPlace(final TriFunction<ServerLevel, BlockState, BlockPos, Boolean> var0) {
      return new UnlockCondition() {
         public boolean blockPlace(ServerLevel var1, BlockState var2, BlockPos var3) {
            return (Boolean)var0.apply(var1, var2, var3);
         }
      };
   }

   static UnlockCondition craftedItem(final TriFunction<ServerLevel, ServerPlayer, ItemStack, Boolean> var0) {
      return new UnlockCondition() {
         public boolean craftedItem(ServerLevel var1, ServerPlayer var2, ItemStack var3) {
            return (Boolean)var0.apply(var1, var2, var3);
         }
      };
   }

   static UnlockCondition usedItem(final TriFunction<ServerLevel, ServerPlayer, ItemStack, Boolean> var0) {
      return new UnlockCondition() {
         public boolean usedItem(ServerLevel var1, ServerPlayer var2, ItemStack var3) {
            return (Boolean)var0.apply(var1, var2, var3);
         }
      };
   }

   static UnlockCondition obtainedItem(final TriFunction<ServerLevel, ServerPlayer, ItemStack, Boolean> var0) {
      return new UnlockCondition() {
         public boolean obtainedItem(ServerLevel var1, ServerPlayer var2, ItemStack var3) {
            return (Boolean)var0.apply(var1, var2, var3);
         }
      };
   }

   static UnlockCondition fedAnimal(final Function4<ServerLevel, ServerPlayer, Animal, ItemStack, Boolean> var0) {
      return new UnlockCondition() {
         public boolean fedAnimal(ServerLevel var1, ServerPlayer var2, Animal var3, ItemStack var4) {
            return (Boolean)var0.apply(var1, var2, var3, var4);
         }
      };
   }

   static UnlockCondition unlocked(final Holder<PlayerUnlock> var0) {
      return new UnlockCondition() {
         public boolean unlocked(ServerLevel var1, ServerPlayer var2, PlayerUnlock var3) {
            return var3.key().equals(((PlayerUnlock)var0.value()).key());
         }
      };
   }

   static UnlockCondition unlocked(final WorldEffect var0) {
      return new UnlockCondition() {
         public boolean unlocked(ServerLevel var1, ServerPlayer var2, WorldEffect var3) {
            return var3.key().equals(var0.key());
         }
      };
   }

   static UnlockCondition mineCompletedWith(final boolean var0, final WorldEffect... var1) {
      return new UnlockCondition() {
         public boolean mapCompleted(ServerLevel var1x, ServerPlayer var2, Set<WorldEffect> var3, boolean var4) {
            if (var0 && !var4) {
               return false;
            } else {
               for(WorldEffect var8 : var1) {
                  if (!var3.contains(var8)) {
                     return false;
                  }
               }

               return true;
            }
         }
      };
   }

   static UnlockCondition mineWon(final BiFunction<ServerLevel, ServerPlayer, Boolean> var0) {
      return new UnlockCondition() {
         public boolean mapCompleted(ServerLevel var1, ServerPlayer var2, Set<WorldEffect> var3, boolean var4) {
            return !var4 ? false : (Boolean)var0.apply(var1, var2);
         }
      };
   }

   static UnlockCondition specialCompleted(final boolean var0) {
      return new UnlockCondition() {
         public boolean specialCompleted(ServerLevel var1, ServerPlayer var2, SpecialMine var3, boolean var4) {
            return !var0 || var4;
         }
      };
   }

   static UnlockCondition specialCompleted(final boolean var0, final SpecialMine var1) {
      return new UnlockCondition() {
         public boolean specialCompleted(ServerLevel var1x, ServerPlayer var2, SpecialMine var3, boolean var4) {
            if (var0 && !var4) {
               return false;
            } else {
               return var1 == var3;
            }
         }
      };
   }

   static void onPlayerTookDamage(Level var0, ServerPlayer var1, DamageSource var2, float var3) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var3x, var4) -> var4.playerTookDamage(var3x, var1, var2, var3));
   }

   static void onPlayerDied(Level var0, ServerPlayer var1, DamageSource var2) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var2x, var3) -> var3.playerDied(var2x, var1, var2));
   }

   static void onPlayerKilledEntity(Level var0, ServerPlayer var1, Entity var2) {
      checkAndUnlockDrop(var0, var1, var2.position(), (var2x, var3) -> var3.playerKilledEntity(var2x, var1, var2));
   }

   static void onPlayerAdvancement(Level var0, ServerPlayer var1, AdvancementHolder var2) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var1x, var2x) -> var2x.playerAdvancement(var1x, var2));
   }

   static void onBlockBreak(Level var0, ServerPlayer var1, BlockPos var2, BlockState var3) {
      checkAndUnlockDrop(var0, var1, var2.getCenter(), (var2x, var3x) -> var3x.blockBreak(var2x, var3, var2));
   }

   static void onBlockDrops(ServerLevel var0, BlockPos var1, BlockState var2) {
      checkAndUnlockDrop(var0, (ServerPlayer)null, var1.getCenter(), (var1x, var2x) -> var2x.blockDrops(var1x, var2));
   }

   static void onBlockPlace(Level var0, ServerPlayer var1, BlockPos var2, BlockState var3) {
      checkAndUnlockDrop(var0, var1, var2.getCenter(), (var2x, var3x) -> var3x.blockPlace(var2x, var3, var2));
   }

   static void onCraftedItem(Level var0, ServerPlayer var1, ItemStack var2) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var2x, var3) -> var3.craftedItem(var2x, var1, var2));
   }

   static void onUsedItem(Level var0, ServerPlayer var1, ItemStack var2) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var2x, var3) -> var3.usedItem(var2x, var1, var2));
   }

   static void onObtainedItem(Level var0, ServerPlayer var1, ItemStack var2) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var2x, var3) -> var3.obtainedItem(var2x, var1, var2));
   }

   static void onFedAnimal(Level var0, ServerPlayer var1, Animal var2, ItemStack var3) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var3x, var4) -> var4.fedAnimal(var3x, var1, var2, var3));
   }

   static void onUnlockedPlayerUnlock(Level var0, ServerPlayer var1, Holder<PlayerUnlock> var2) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var2x, var3) -> var3.unlocked(var2x, var1, (PlayerUnlock)var2.value()));
   }

   static void onUnlockedMapEffect(Level var0, ServerPlayer var1, WorldEffect var2) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var2x, var3) -> var3.unlocked(var2x, var1, var2));
   }

   static void onMapCompleted(Level var0, ServerPlayer var1, Set<WorldEffect> var2, boolean var3) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var3x, var4) -> var4.mapCompleted(var3x, var1, var2, var3));
   }

   static void onSpecialMineCompleted(Level var0, ServerPlayer var1, SpecialMine var2, boolean var3) {
      checkAndUnlockDrop(var0, var1, var1.position(), (var3x, var4) -> var4.specialCompleted(var3x, var1, var2, var3));
   }

   private static void checkAndUnlockDrop(Level var0, @Nullable ServerPlayer var1, Vec3 var2, BiFunction<ServerLevel, UnlockCondition, Boolean> var3) {
      if (var0 instanceof ServerLevel var4) {
         if (var1 == null || !var1.isRevisiting()) {
            for(WorldEffect var6 : BuiltInRegistries.WORLD_EFFECT) {
               for(UnlockCondition var8 : var6.unlockedBy()) {
                  if (!var4.isEffectUnlocked(var6)) {
                     boolean var9 = true;

                     for(WorldEffect var11 : var6.unlockedAfter()) {
                        if (!var4.isEffectUnlocked(var11)) {
                           var9 = false;
                           break;
                        }
                     }

                     if (var9 && (Boolean)var3.apply(var4, var8)) {
                        var4.dropUnlockEffect(var2, var6, var1);
                     }
                  }
               }
            }

            for(SpecialMine var14 : BuiltInRegistries.SPECIAL_MINE) {
               for(UnlockCondition var18 : var14.unlockedBy()) {
                  if (!var4.isSpecialMineUnlocked(var14)) {
                     boolean var20 = true;

                     for(SpecialMine var22 : var14.unlockedAfter()) {
                        if (!var4.isSpecialMineUnlocked(var22)) {
                           var20 = false;
                           break;
                        }
                     }

                     if (var20 && (Boolean)var3.apply(var4, var18)) {
                        var4.unlockSpecialMine(var14);
                     }
                  }
               }
            }

            if (var1 != null) {
               for(Holder var15 : BuiltInRegistries.PLAYER_UNLOCK.listElements().toList()) {
                  for(UnlockCondition var19 : ((PlayerUnlock)var15.value()).madeVisibleBy()) {
                     if ((Boolean)var3.apply(var4, var19)) {
                        var1.makeVisibile(var15);
                        CriteriaTriggers.PLAYER_UNLOCK_UNLOCKED.trigger(var1);
                     }
                  }
               }
            }

            return;
         }
      }

   }
}
