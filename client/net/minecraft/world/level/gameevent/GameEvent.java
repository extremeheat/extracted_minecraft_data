package net.minecraft.world.level.gameevent;

import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class GameEvent {
   public static final GameEvent BLOCK_ACTIVATE = register("block_activate");
   public static final GameEvent BLOCK_ATTACH = register("block_attach");
   public static final GameEvent BLOCK_CHANGE = register("block_change");
   public static final GameEvent BLOCK_CLOSE = register("block_close");
   public static final GameEvent BLOCK_DEACTIVATE = register("block_deactivate");
   public static final GameEvent BLOCK_DESTROY = register("block_destroy");
   public static final GameEvent BLOCK_DETACH = register("block_detach");
   public static final GameEvent BLOCK_OPEN = register("block_open");
   public static final GameEvent BLOCK_PLACE = register("block_place");
   public static final GameEvent CONTAINER_CLOSE = register("container_close");
   public static final GameEvent CONTAINER_OPEN = register("container_open");
   public static final GameEvent DISPENSE_FAIL = register("dispense_fail");
   public static final GameEvent DRINK = register("drink");
   public static final GameEvent EAT = register("eat");
   public static final GameEvent ELYTRA_GLIDE = register("elytra_glide");
   public static final GameEvent ENTITY_DAMAGE = register("entity_damage");
   public static final GameEvent ENTITY_DIE = register("entity_die");
   public static final GameEvent ENTITY_INTERACT = register("entity_interact");
   public static final GameEvent ENTITY_PLACE = register("entity_place");
   public static final GameEvent ENTITY_ROAR = register("entity_roar");
   public static final GameEvent ENTITY_SHAKE = register("entity_shake");
   public static final GameEvent EQUIP = register("equip");
   public static final GameEvent EXPLODE = register("explode");
   public static final GameEvent FLAP = register("flap");
   public static final GameEvent FLUID_PICKUP = register("fluid_pickup");
   public static final GameEvent FLUID_PLACE = register("fluid_place");
   public static final GameEvent HIT_GROUND = register("hit_ground");
   public static final GameEvent INSTRUMENT_PLAY = register("instrument_play");
   public static final GameEvent ITEM_INTERACT_FINISH = register("item_interact_finish");
   public static final GameEvent ITEM_INTERACT_START = register("item_interact_start");
   public static final GameEvent LIGHTNING_STRIKE = register("lightning_strike");
   public static final GameEvent NOTE_BLOCK_PLAY = register("note_block_play");
   public static final GameEvent PISTON_CONTRACT = register("piston_contract");
   public static final GameEvent PISTON_EXTEND = register("piston_extend");
   public static final GameEvent PRIME_FUSE = register("prime_fuse");
   public static final GameEvent PROJECTILE_LAND = register("projectile_land");
   public static final GameEvent PROJECTILE_SHOOT = register("projectile_shoot");
   public static final GameEvent SCULK_SENSOR_TENDRILS_CLICKING = register("sculk_sensor_tendrils_clicking");
   public static final GameEvent SHEAR = register("shear");
   public static final GameEvent SHRIEK = register("shriek", 32);
   public static final GameEvent SPLASH = register("splash");
   public static final GameEvent STEP = register("step");
   public static final GameEvent SWIM = register("swim");
   public static final GameEvent TELEPORT = register("teleport");
   public static final int DEFAULT_NOTIFICATION_RADIUS = 16;
   private final String name;
   private final int notificationRadius;
   private final Holder.Reference<GameEvent> builtInRegistryHolder;

   public GameEvent(String var1, int var2) {
      super();
      this.builtInRegistryHolder = Registry.GAME_EVENT.createIntrusiveHolder(this);
      this.name = var1;
      this.notificationRadius = var2;
   }

   public String getName() {
      return this.name;
   }

   public int getNotificationRadius() {
      return this.notificationRadius;
   }

   private static GameEvent register(String var0) {
      return register(var0, 16);
   }

   private static GameEvent register(String var0, int var1) {
      return (GameEvent)Registry.register(Registry.GAME_EVENT, (String)var0, new GameEvent(var0, var1));
   }

   public String toString() {
      return "Game Event{ " + this.name + " , " + this.notificationRadius + "}";
   }

   /** @deprecated */
   @Deprecated
   public Holder.Reference<GameEvent> builtInRegistryHolder() {
      return this.builtInRegistryHolder;
   }

   public boolean is(TagKey<GameEvent> var1) {
      return this.builtInRegistryHolder.is(var1);
   }

   public static final class Message implements Comparable<Message> {
      private final GameEvent gameEvent;
      private final Vec3 source;
      private final Context context;
      private final GameEventListener recipient;
      private final double distanceToRecipient;

      public Message(GameEvent var1, Vec3 var2, Context var3, GameEventListener var4, Vec3 var5) {
         super();
         this.gameEvent = var1;
         this.source = var2;
         this.context = var3;
         this.recipient = var4;
         this.distanceToRecipient = var2.distanceToSqr(var5);
      }

      public int compareTo(Message var1) {
         return Double.compare(this.distanceToRecipient, var1.distanceToRecipient);
      }

      public GameEvent gameEvent() {
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
      public int compareTo(Object var1) {
         return this.compareTo((Message)var1);
      }
   }

   public static record Context(@Nullable Entity a, @Nullable BlockState b) {
      @Nullable
      private final Entity sourceEntity;
      @Nullable
      private final BlockState affectedState;

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
