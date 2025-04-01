package net.minecraft.world.level.mines;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;

public class Battle implements MineEvent {
   private static final int MAX_SPAWN_ATTEMPTS = 500;
   private final ResourceLocation id;
   private final List<Wave> waves;
   private final List<UUID> spawnedMobs;
   private int currentWave;
   private long waveCompletedTick;
   private BlockPos position;
   private MineEvent.Status status;
   public static final MapCodec<Battle> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("id").forGetter((var0x) -> var0x.id), Battle.Wave.CODEC.listOf().fieldOf("waves").forGetter((var0x) -> var0x.waves), UUIDUtil.CODEC.listOf().fieldOf("spawned_mobs").forGetter((var0x) -> var0x.spawnedMobs), Codec.INT.fieldOf("current_wave").forGetter((var0x) -> var0x.currentWave), Codec.LONG.fieldOf("wave_completed_tick").forGetter((var0x) -> var0x.waveCompletedTick), BlockPos.CODEC.fieldOf("position").forGetter((var0x) -> var0x.position), MineEvent.Status.CODEC.fieldOf("status").forGetter((var0x) -> var0x.status)).apply(var0, Battle::new));

   public Battle(ResourceLocation var1, List<Wave> var2, List<UUID> var3, int var4, long var5, BlockPos var7, MineEvent.Status var8) {
      super();
      this.id = var1;
      this.waves = var2;
      this.spawnedMobs = new ArrayList(var3);
      this.currentWave = var4;
      this.waveCompletedTick = var5;
      this.position = var7;
      this.status = var8;
   }

   public Battle(ResourceLocation var1, List<Wave> var2) {
      this(var1, var2, List.of(), -1, 0L, BlockPos.ZERO, MineEvent.Status.ACTIVE);
   }

   public void tick(ServerLevel var1) {
      if (this.getStatus() != MineEvent.Status.ACTIVE) {
         CustomBossEvents var9 = var1.theGame().getCustomBossEvents();
         CustomBossEvent var3 = var9.get(this.id);
         if (var3 != null) {
            var9.remove(var3);
         }

      } else {
         long var2 = var1.getGameTime();
         if (this.currentWave == -1) {
            this.currentWave = 0;
            this.waveCompletedTick = var2;
         }

         if (this.waveCompletedTick >= 0L) {
            if (this.currentWave < this.waves.size()) {
               Wave var4 = (Wave)this.waves.get(this.currentWave);
               CustomBossEvents var5 = var1.theGame().getCustomBossEvents();
               long var6 = this.waveCompletedTick + (long)var4.ticksDelay();
               if (var6 < var2) {
                  this.spawnWave(var4, var1);
                  this.waveCompletedTick = -1L;
                  CustomBossEvent var8 = var5.get(this.id);
                  if (var8 != null) {
                     var5.remove(var8);
                  }
               } else {
                  CustomBossEvent var13 = var5.get(this.id);
                  if (var13 == null) {
                     var13 = var5.create(this.id, var4.countdown);
                  }

                  var13.setMax(var4.ticksDelay);
                  var13.setName(var4.countdown);
                  var13.setValue((int)(var2 - this.waveCompletedTick));
                  var13.setPlayers(var1.players());
               }
            }
         } else {
            List var10 = this.spawnedMobs.stream().flatMap((var1x) -> Optional.ofNullable(var1.getEntity(var1x)).filter(Entity::isAlive).stream()).toList();
            if (this.currentWave < this.waves.size()) {
               Wave var11 = (Wave)this.waves.get(this.currentWave);
               if (var11.showBar) {
                  CustomBossEvents var12 = var1.theGame().getCustomBossEvents();
                  CustomBossEvent var7 = var12.get(this.id);
                  if (var7 == null) {
                     var7 = var12.create(this.id, Component.translatable("world.event.remaining"));
                  }

                  var7.setName(Component.translatable("world.event.remaining"));
                  var7.setMax(this.spawnedMobs.size());
                  var7.setValue(var10.size());
                  var7.setPlayers(var1.players());
               }
            }

            if (var10.isEmpty()) {
               ++this.currentWave;
               this.waveCompletedTick = var2;
            } else {
               this.position = ((Entity)var10.getFirst()).blockPosition();
            }
         }

         this.status = this.currentWave < this.waves.size() ? MineEvent.Status.ACTIVE : MineEvent.Status.WON;
      }
   }

   public void end(ServerLevel var1, boolean var2) {
      this.status = var2 ? MineEvent.Status.WON : MineEvent.Status.FAILED;
      this.currentWave = this.waves.size();
      this.waveCompletedTick = -1L;
      CustomBossEvents var3 = var1.theGame().getCustomBossEvents();
      CustomBossEvent var4 = var3.get(this.id);
      if (var4 != null) {
         var3.remove(var4);
      }

   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void spawnWave(Wave var1, ServerLevel var2) {
      this.spawnedMobs.clear();

      for(SpawnGroup var4 : var1.groups) {
         Optional var5 = var4.types.getRandomElement(var2.getRandom());
         if (!var5.isEmpty()) {
            Holder var6 = (Holder)var5.get();
            int var7 = var4.count.sample(var2.getRandom());

            for(int var8 = 0; var8 < var7; ++var8) {
               BlockPos var9 = var4.spawnStrategy.getSpawnPos(var2);
               if (var9 != null) {
                  Entity var10 = ((EntityType)var6.value()).spawn(var2, var9, EntitySpawnReason.EVENT);
                  if (var10 instanceof Mob) {
                     Mob var11 = (Mob)var10;
                     if (var4.useBabyMobs) {
                        var11.setBaby(true);
                        if (var11 instanceof Zombie) {
                           Zombie var12 = (Zombie)var11;
                           Item var13 = Items.CHAINMAIL_HELMET;
                           var11.equipItemIfPossible(var2, var13.getDefaultInstance());
                        }
                     }

                     var11.setPersistenceRequired();
                     var11.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1), var11);
                     this.spawnedMobs.add(var10.getUUID());
                  }
               }
            }
         }
      }

   }

   public MineEvent.Status getStatus() {
      return this.status;
   }

   public MapCodec<Battle> codec() {
      return CODEC;
   }

   public static Builder builder(ServerLevel var0, String var1) {
      return new Builder(var0.dimension().location().withSuffix("/" + var1));
   }

   public static enum SpawnType implements StringRepresentable {
      FIXED_POSITION("fixed_position"),
      ON_HEIGHTMAP("on_heightmap"),
      NEAR_POSITION("near_position"),
      NEAR_PLAYER("near_player"),
      WARDEN_ARENA("warden_arena");

      public static final Codec<SpawnType> CODEC = StringRepresentable.<SpawnType>fromEnum(SpawnType::values);
      private final String id;

      private SpawnType(final String var3) {
         this.id = var3;
      }

      public String getSerializedName() {
         return this.id;
      }

      // $FF: synthetic method
      private static SpawnType[] $values() {
         return new SpawnType[]{FIXED_POSITION, ON_HEIGHTMAP, NEAR_POSITION, NEAR_PLAYER, WARDEN_ARENA};
      }
   }

   public static record SpawnStrategy(SpawnType type, BlockPos offset, int range, boolean allowInAir) {
      public static final Codec<SpawnStrategy> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Battle.SpawnType.CODEC.fieldOf("type").forGetter(SpawnStrategy::type), BlockPos.CODEC.fieldOf("offset").forGetter(SpawnStrategy::offset), ExtraCodecs.POSITIVE_INT.fieldOf("range").forGetter(SpawnStrategy::range), Codec.BOOL.fieldOf("allow_in_air").forGetter(SpawnStrategy::allowInAir)).apply(var0, SpawnStrategy::new));

      public SpawnStrategy(SpawnType var1, BlockPos var2, int var3, boolean var4) {
         super();
         this.type = var1;
         this.offset = var2;
         this.range = var3;
         this.allowInAir = var4;
      }

      @Nullable
      public BlockPos getSpawnPos(ServerLevel var1) {
         BlockPos var10000;
         switch (this.type.ordinal()) {
            case 0 -> var10000 = this.offset;
            case 1 -> var10000 = this.offset.atY(var1.getHeight(Heightmap.Types.WORLD_SURFACE, this.offset) + 1);
            case 2 -> var10000 = this.positionNear(var1, this.offset);
            case 3 -> var10000 = this.positionNearPlayer(var1);
            case 4 -> var10000 = var1.WARDEN_ARENA_POS.offset(5, 2, -1);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      @Nullable
      private BlockPos positionNear(ServerLevel var1, BlockPos var2) {
         RandomSource var3 = var1.getRandom();

         for(int var4 = 0; var4 < 500; ++var4) {
            BlockPos var5 = var2.offset(var3.nextIntBetweenInclusive(-this.range, this.range), 0, var3.nextIntBetweenInclusive(-this.range, this.range));
            if (this.allowInAir) {
               if (isOkSpawnSpace(var1, var5)) {
                  return var5;
               }
            } else {
               BlockPos.MutableBlockPos var6 = var5.mutable();

               while(isEmpty(var1, var6) && Math.abs(var6.getY() - var2.getY()) < this.range) {
                  var6.move(Direction.DOWN);
               }

               var6.move(Direction.UP);

               while(!isEmpty(var1, var6) && Math.abs(var6.getY() - var2.getY()) < this.range) {
                  var6.move(Direction.UP);
               }

               if (!isEmpty(var1, var6.below()) && isOkSpawnSpace(var1, var6)) {
                  return var6.immutable();
               }
            }
         }

         return null;
      }

      private static boolean isEmpty(ServerLevel var0, BlockPos var1) {
         return var0.getBlockState(var1).getCollisionShape(var0, var1).isEmpty();
      }

      private static boolean isOkSpawnSpace(ServerLevel var0, BlockPos var1) {
         for(BlockPos var3 : BlockPos.betweenClosed(var1.offset(-1, 0, -1), var1.offset(1, 1, 1))) {
            if (!isEmpty(var0, var3)) {
               return false;
            }
         }

         return true;
      }

      @Nullable
      private BlockPos positionNearPlayer(ServerLevel var1) {
         List var2 = var1.getPlayers((var0) -> var0.isAlive() && !var0.isSpectator());
         return var2.isEmpty() ? this.positionNear(var1, this.offset) : this.positionNear(var1, ((ServerPlayer)Util.getRandom(var2, var1.getRandom())).blockPosition().offset(this.offset));
      }

      public static class Builder {
         private SpawnType type;
         private BlockPos offset;
         private int range;
         private boolean allowInAir;

         public Builder() {
            super();
            this.type = Battle.SpawnType.NEAR_PLAYER;
            this.offset = BlockPos.ZERO;
            this.range = 20;
            this.allowInAir = false;
         }

         public Builder type(SpawnType var1) {
            this.type = var1;
            return this;
         }

         public Builder offset(BlockPos var1) {
            this.offset = var1;
            return this;
         }

         public Builder range(int var1) {
            this.range = var1;
            return this;
         }

         public boolean allowedInAir() {
            this.allowInAir = true;
            return true;
         }

         public SpawnStrategy build() {
            return new SpawnStrategy(this.type, this.offset, this.range, this.allowInAir);
         }
      }
   }

   public static record SpawnGroup(HolderSet<EntityType<?>> types, boolean useBabyMobs, IntProvider count, SpawnStrategy spawnStrategy) {
      final HolderSet<EntityType<?>> types;
      final boolean useBabyMobs;
      final IntProvider count;
      final SpawnStrategy spawnStrategy;
      public static final Codec<SpawnGroup> CODEC = RecordCodecBuilder.create((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("types").forGetter(SpawnGroup::types), Codec.BOOL.fieldOf("useBabyMobs").forGetter(SpawnGroup::useBabyMobs), IntProvider.CODEC.fieldOf("count").forGetter(SpawnGroup::count), Battle.SpawnStrategy.CODEC.fieldOf("spawn_strategy").forGetter(SpawnGroup::spawnStrategy)).apply(var0, SpawnGroup::new));

      public SpawnGroup(HolderSet<EntityType<?>> var1, boolean var2, IntProvider var3, SpawnStrategy var4) {
         super();
         this.types = var1;
         this.useBabyMobs = var2;
         this.count = var3;
         this.spawnStrategy = var4;
      }

      public static class Builder {
         private HolderSet<EntityType<?>> types = HolderSet.<EntityType<?>>empty();
         private Boolean useBabyMobs = false;
         private IntProvider count = ConstantInt.of(1);
         private SpawnStrategy spawnStrategy;

         public Builder() {
            super();
            this.spawnStrategy = new SpawnStrategy(Battle.SpawnType.NEAR_PLAYER, BlockPos.ZERO, 40, false);
         }

         public Builder types(HolderSet<EntityType<?>> var1) {
            this.types = var1;
            return this;
         }

         public Builder type(EntityType<?> var1) {
            return this.types(HolderSet.direct(var1.builtInRegistryHolder()));
         }

         public Builder useBabyMobs(boolean var1) {
            this.useBabyMobs = var1;
            return this;
         }

         public Builder count(IntProvider var1) {
            this.count = var1;
            return this;
         }

         public Builder count(int var1) {
            return this.count(ConstantInt.of(var1));
         }

         public Builder withSpawnStrategy(Consumer<SpawnStrategy.Builder> var1) {
            SpawnStrategy.Builder var2 = new SpawnStrategy.Builder();
            var1.accept(var2);
            this.spawnStrategy = var2.build();
            return this;
         }

         public SpawnGroup build() {
            return new SpawnGroup(this.types, this.useBabyMobs, this.count, this.spawnStrategy);
         }
      }
   }

   public static record Wave(List<SpawnGroup> groups, boolean showBar, Component countdown, int ticksDelay) {
      final List<SpawnGroup> groups;
      final boolean showBar;
      final Component countdown;
      final int ticksDelay;
      public static final Codec<Wave> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Battle.SpawnGroup.CODEC.listOf().fieldOf("groups").forGetter(Wave::groups), Codec.BOOL.fieldOf("show_bar").forGetter(Wave::showBar), ComponentSerialization.CODEC.fieldOf("countdown").forGetter(Wave::countdown), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("ticks_delay").forGetter(Wave::ticksDelay)).apply(var0, Wave::new));

      public Wave(List<SpawnGroup> var1, boolean var2, Component var3, int var4) {
         super();
         this.groups = var1;
         this.showBar = var2;
         this.countdown = var3;
         this.ticksDelay = var4;
      }

      public static class Builder {
         private final List<SpawnGroup> groups = new ArrayList();
         private boolean showBar = true;
         private Component countdown = Component.translatable("world.event.next_wave");
         private int ticksDelay = 0;

         public Builder() {
            super();
         }

         public Builder spawns(Consumer<SpawnGroup.Builder> var1) {
            SpawnGroup.Builder var2 = new SpawnGroup.Builder();
            var1.accept(var2);
            this.groups.add(var2.build());
            return this;
         }

         public Builder noBossBar() {
            this.showBar = false;
            return this;
         }

         public Builder countdownLabel(Component var1) {
            this.countdown = var1;
            return this;
         }

         public Builder ticksDelay(int var1) {
            this.ticksDelay = var1;
            return this;
         }

         public Wave build() {
            return new Wave(this.groups, this.showBar, this.countdown, this.ticksDelay);
         }
      }
   }

   public static class Builder {
      private final ResourceLocation id;
      private final List<Wave> waves = new ArrayList();

      public Builder(ResourceLocation var1) {
         super();
         this.id = var1;
      }

      public Builder withWave(Consumer<Wave.Builder> var1) {
         Wave.Builder var2 = new Wave.Builder();
         var1.accept(var2);
         this.waves.add(var2.build());
         return this;
      }

      public Battle build() {
         return new Battle(this.id, this.waves);
      }
   }
}
