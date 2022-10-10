package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.gen.Heightmap;

public class EntitySpawnPlacementRegistry {
   private static final Map<EntityType<?>, EntitySpawnPlacementRegistry.Entry> field_209347_a = Maps.newHashMap();

   private static void func_209343_a(EntityType<?> var0, EntitySpawnPlacementRegistry.SpawnPlacementType var1, Heightmap.Type var2) {
      func_209346_a(var0, var1, var2, (Tag)null);
   }

   private static void func_209346_a(EntityType<?> var0, EntitySpawnPlacementRegistry.SpawnPlacementType var1, Heightmap.Type var2, @Nullable Tag<Block> var3) {
      field_209347_a.put(var0, new EntitySpawnPlacementRegistry.Entry(var2, var1, var3));
   }

   @Nullable
   public static EntitySpawnPlacementRegistry.SpawnPlacementType func_209344_a(EntityType<? extends EntityLiving> var0) {
      EntitySpawnPlacementRegistry.Entry var1 = (EntitySpawnPlacementRegistry.Entry)field_209347_a.get(var0);
      return var1 == null ? null : var1.field_209340_b;
   }

   public static Heightmap.Type func_209342_b(@Nullable EntityType<? extends EntityLiving> var0) {
      EntitySpawnPlacementRegistry.Entry var1 = (EntitySpawnPlacementRegistry.Entry)field_209347_a.get(var0);
      return var1 == null ? Heightmap.Type.MOTION_BLOCKING_NO_LEAVES : var1.field_209339_a;
   }

   public static boolean func_209345_a(EntityType<? extends EntityLiving> var0, IBlockState var1) {
      EntitySpawnPlacementRegistry.Entry var2 = (EntitySpawnPlacementRegistry.Entry)field_209347_a.get(var0);
      if (var2 == null) {
         return false;
      } else {
         return var2.field_209341_c != null && var1.func_203425_a(var2.field_209341_c);
      }
   }

   static {
      func_209343_a(EntityType.field_203780_j, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_205137_n, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_204724_o, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200761_A, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_203779_Z, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_203778_ae, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200749_ao, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_204262_at, EntitySpawnPlacementRegistry.SpawnPlacementType.IN_WATER, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209346_a(EntityType.field_200781_U, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, BlockTags.field_206952_E);
      func_209346_a(EntityType.field_200783_W, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING, BlockTags.field_206952_E);
      func_209346_a(EntityType.field_200786_Z, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlockTags.field_205213_E);
      func_209343_a(EntityType.field_200791_e, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200792_f, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200794_h, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200795_i, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200796_j, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200797_k, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200798_l, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200803_q, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200804_r, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200802_p, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200811_y, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200812_z, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200762_B, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200763_C, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200769_I, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200771_K, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200780_T, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200779_S, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200784_X, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200736_ab, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200737_ac, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200740_af, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200741_ag, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200742_ah, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200743_ai, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200745_ak, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200748_an, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200750_ap, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_203099_aq, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200756_av, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200757_aw, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200759_ay, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200760_az, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200722_aA, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200724_aC, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200725_aD, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200726_aE, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200785_Y, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
      func_209343_a(EntityType.field_200727_aF, EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
   }

   public static enum SpawnPlacementType {
      ON_GROUND,
      IN_WATER;

      private SpawnPlacementType() {
      }
   }

   static class Entry {
      private final Heightmap.Type field_209339_a;
      private final EntitySpawnPlacementRegistry.SpawnPlacementType field_209340_b;
      @Nullable
      private final Tag<Block> field_209341_c;

      public Entry(Heightmap.Type var1, EntitySpawnPlacementRegistry.SpawnPlacementType var2, @Nullable Tag<Block> var3) {
         super();
         this.field_209339_a = var1;
         this.field_209340_b = var2;
         this.field_209341_c = var3;
      }
   }
}
