package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record GameEvent(int notificationRadius) {
   public static final Holder.Reference<GameEvent> BLOCK_ACTIVATE = register("block_activate");
   public static final Holder.Reference<GameEvent> BLOCK_ATTACH = register("block_attach");
   public static final Holder.Reference<GameEvent> BLOCK_CHANGE = register("block_change");
   public static final Holder.Reference<GameEvent> BLOCK_CLOSE = register("block_close");
   public static final Holder.Reference<GameEvent> BLOCK_DEACTIVATE = register("block_deactivate");
   public static final Holder.Reference<GameEvent> BLOCK_DESTROY = register("block_destroy");
   public static final Holder.Reference<GameEvent> BLOCK_DETACH = register("block_detach");
   public static final Holder.Reference<GameEvent> BLOCK_OPEN = register("block_open");
   public static final Holder.Reference<GameEvent> BLOCK_PLACE = register("block_place");
   public static final Holder.Reference<GameEvent> CONTAINER_CLOSE = register("container_close");
   public static final Holder.Reference<GameEvent> CONTAINER_OPEN = register("container_open");
   public static final Holder.Reference<GameEvent> DRINK = register("drink");
   public static final Holder.Reference<GameEvent> EAT = register("eat");
   public static final Holder.Reference<GameEvent> ELYTRA_GLIDE = register("elytra_glide");
   public static final Holder.Reference<GameEvent> ENTITY_DAMAGE = register("entity_damage");
   public static final Holder.Reference<GameEvent> ENTITY_DIE = register("entity_die");
   public static final Holder.Reference<GameEvent> ENTITY_DISMOUNT = register("entity_dismount");
   public static final Holder.Reference<GameEvent> ENTITY_INTERACT = register("entity_interact");
   public static final Holder.Reference<GameEvent> ENTITY_MOUNT = register("entity_mount");
   public static final Holder.Reference<GameEvent> ENTITY_PLACE = register("entity_place");
   public static final Holder.Reference<GameEvent> ENTITY_ACTION = register("entity_action");
   public static final Holder.Reference<GameEvent> EQUIP = register("equip");
   public static final Holder.Reference<GameEvent> EXPLODE = register("explode");
   public static final Holder.Reference<GameEvent> FLAP = register("flap");
   public static final Holder.Reference<GameEvent> FLUID_PICKUP = register("fluid_pickup");
   public static final Holder.Reference<GameEvent> FLUID_PLACE = register("fluid_place");
   public static final Holder.Reference<GameEvent> HIT_GROUND = register("hit_ground");
   public static final Holder.Reference<GameEvent> INSTRUMENT_PLAY = register("instrument_play");
   public static final Holder.Reference<GameEvent> ITEM_INTERACT_FINISH = register("item_interact_finish");
   public static final Holder.Reference<GameEvent> ITEM_INTERACT_START = register("item_interact_start");
   public static final Holder.Reference<GameEvent> JUKEBOX_PLAY = register("jukebox_play", 10);
   public static final Holder.Reference<GameEvent> JUKEBOX_STOP_PLAY = register("jukebox_stop_play", 10);
   public static final Holder.Reference<GameEvent> LIGHTNING_STRIKE = register("lightning_strike");
   public static final Holder.Reference<GameEvent> NOTE_BLOCK_PLAY = register("note_block_play");
   public static final Holder.Reference<GameEvent> PRIME_FUSE = register("prime_fuse");
   public static final Holder.Reference<GameEvent> PROJECTILE_LAND = register("projectile_land");
   public static final Holder.Reference<GameEvent> PROJECTILE_SHOOT = register("projectile_shoot");
   public static final Holder.Reference<GameEvent> SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
   public static final Holder.Reference<GameEvent> SHEAR = register("shear");
   public static final Holder.Reference<GameEvent> SHRIEK = register("shriek", 32);
   public static final Holder.Reference<GameEvent> SPLASH = register("splash");
   public static final Holder.Reference<GameEvent> STEP = register("step");
   public static final Holder.Reference<GameEvent> SWIM = register("swim");
   public static final Holder.Reference<GameEvent> TELEPORT = register("teleport");
   public static final Holder.Reference<GameEvent> UNEQUIP = register("unequip");
   public static final Holder.Reference<GameEvent> RESONATE_1 = register("resonate_1");
   public static final Holder.Reference<GameEvent> RESONATE_2 = register("resonate_2");
   public static final Holder.Reference<GameEvent> RESONATE_3 = register("resonate_3");
   public static final Holder.Reference<GameEvent> RESONATE_4 = register("resonate_4");
   public static final Holder.Reference<GameEvent> RESONATE_5 = register("resonate_5");
   public static final Holder.Reference<GameEvent> RESONATE_6 = register("resonate_6");
   public static final Holder.Reference<GameEvent> RESONATE_7 = register("resonate_7");
   public static final Holder.Reference<GameEvent> RESONATE_8 = register("resonate_8");
   public static final Holder.Reference<GameEvent> RESONATE_9 = register("resonate_9");
   public static final Holder.Reference<GameEvent> RESONATE_10 = register("resonate_10");
   public static final Holder.Reference<GameEvent> RESONATE_11 = register("resonate_11");
   public static final Holder.Reference<GameEvent> RESONATE_12 = register("resonate_12");
   public static final Holder.Reference<GameEvent> RESONATE_13 = register("resonate_13");
   public static final Holder.Reference<GameEvent> RESONATE_14 = register("resonate_14");
   public static final Holder.Reference<GameEvent> RESONATE_15 = register("resonate_15");
   public static final int DEFAULT_NOTIFICATION_RADIUS = 16;
   public static final Codec<Holder<GameEvent>> CODEC;

   public GameEvent(int var1) {
      super();
      this.notificationRadius = var1;
   }

   public static Holder<GameEvent> bootstrap(Registry<GameEvent> var0) {
      return BLOCK_ACTIVATE;
   }

   public int notificationRadius() {
      return this.notificationRadius;
   }

   private static Holder.Reference<GameEvent> register(String var0) {
      return register(var0, 16);
   }

   private static Holder.Reference<GameEvent> register(String var0, int var1) {
      return Registry.registerForHolder(BuiltInRegistries.GAME_EVENT, (ResourceLocation)ResourceLocation.withDefaultNamespace(var0), new GameEvent(var1));
   }

   static {
      CODEC = RegistryFixedCodec.create(Registries.GAME_EVENT);
   }

   public static final class ListenerInfo implements Comparable<ListenerInfo> {
      private final Holder<GameEvent> gameEvent;
      private final Vec3 source;
      private final Context context;
      private final GameEventListener recipient;
      private final double distanceToRecipient;

      public ListenerInfo(Holder<GameEvent> var1, Vec3 var2, Context var3, GameEventListener var4, Vec3 var5) {
         super();
         this.gameEvent = var1;
         this.source = var2;
         this.context = var3;
         this.recipient = var4;
         this.distanceToRecipient = var2.distanceToSqr(var5);
      }

      public int compareTo(ListenerInfo var1) {
         return Double.compare(this.distanceToRecipient, var1.distanceToRecipient);
      }

      public Holder<GameEvent> gameEvent() {
         return this.gameEvent;
      }

      public Vec3 source() {
         return this.source;
      }

      public Context context() {
         return this.context;
      }

      public GameEventListener recipient() {
         return this.recipient;
      }

      // $FF: synthetic method
      public int compareTo(final Object var1) {
         return this.compareTo((ListenerInfo)var1);
      }
   }

   public static record Context(@Nullable Entity sourceEntity, @Nullable BlockState affectedState) {
      public Context(@Nullable Entity var1, @Nullable BlockState var2) {
         super();
         this.sourceEntity = var1;
         this.affectedState = var2;
      }

      public static Context of(@Nullable Entity var0) {
         return new Context(var0, (BlockState)null);
      }

      public static Context of(@Nullable BlockState var0) {
         return new Context((Entity)null, var0);
      }

      public static Context of(@Nullable Entity var0, @Nullable BlockState var1) {
         return new Context(var0, var1);
      }

      @Nullable
      public Entity sourceEntity() {
         return this.sourceEntity;
      }

      @Nullable
      public BlockState affectedState() {
         return this.affectedState;
      }
   }
}
