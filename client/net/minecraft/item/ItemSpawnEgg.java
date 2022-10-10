package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowingFluid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemSpawnEgg extends Item {
   private static final Map<EntityType<?>, ItemSpawnEgg> field_195987_b = Maps.newIdentityHashMap();
   private final int field_195988_c;
   private final int field_195989_d;
   private final EntityType<?> field_200890_d;

   public ItemSpawnEgg(EntityType<?> var1, int var2, int var3, Item.Properties var4) {
      super(var4);
      this.field_200890_d = var1;
      this.field_195988_c = var2;
      this.field_195989_d = var3;
      field_195987_b.put(var1, this);
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      World var2 = var1.func_195991_k();
      if (var2.field_72995_K) {
         return EnumActionResult.SUCCESS;
      } else {
         ItemStack var3 = var1.func_195996_i();
         BlockPos var4 = var1.func_195995_a();
         EnumFacing var5 = var1.func_196000_l();
         IBlockState var6 = var2.func_180495_p(var4);
         Block var7 = var6.func_177230_c();
         if (var7 == Blocks.field_150474_ac) {
            TileEntity var8 = var2.func_175625_s(var4);
            if (var8 instanceof TileEntityMobSpawner) {
               MobSpawnerBaseLogic var12 = ((TileEntityMobSpawner)var8).func_145881_a();
               EntityType var10 = this.func_208076_b(var3.func_77978_p());
               if (var10 != null) {
                  var12.func_200876_a(var10);
                  var8.func_70296_d();
                  var2.func_184138_a(var4, var6, var6, 3);
               }

               var3.func_190918_g(1);
               return EnumActionResult.SUCCESS;
            }
         }

         BlockPos var11;
         if (var6.func_196952_d(var2, var4).func_197766_b()) {
            var11 = var4;
         } else {
            var11 = var4.func_177972_a(var5);
         }

         EntityType var9 = this.func_208076_b(var3.func_77978_p());
         if (var9 == null || var9.func_208049_a(var2, var3, var1.func_195999_j(), var11, true, !Objects.equals(var4, var11) && var5 == EnumFacing.UP) != null) {
            var3.func_190918_g(1);
         }

         return EnumActionResult.SUCCESS;
      }
   }

   public ActionResult<ItemStack> func_77659_a(World var1, EntityPlayer var2, EnumHand var3) {
      ItemStack var4 = var2.func_184586_b(var3);
      if (var1.field_72995_K) {
         return new ActionResult(EnumActionResult.PASS, var4);
      } else {
         RayTraceResult var5 = this.func_77621_a(var1, var2, true);
         if (var5 != null && var5.field_72313_a == RayTraceResult.Type.BLOCK) {
            BlockPos var6 = var5.func_178782_a();
            if (!(var1.func_180495_p(var6).func_177230_c() instanceof BlockFlowingFluid)) {
               return new ActionResult(EnumActionResult.PASS, var4);
            } else if (var1.func_175660_a(var2, var6) && var2.func_175151_a(var6, var5.field_178784_b, var4)) {
               EntityType var7 = this.func_208076_b(var4.func_77978_p());
               if (var7 != null && var7.func_208049_a(var1, var4, var2, var6, false, false) != null) {
                  if (!var2.field_71075_bZ.field_75098_d) {
                     var4.func_190918_g(1);
                  }

                  var2.func_71029_a(StatList.field_75929_E.func_199076_b(this));
                  return new ActionResult(EnumActionResult.SUCCESS, var4);
               } else {
                  return new ActionResult(EnumActionResult.PASS, var4);
               }
            } else {
               return new ActionResult(EnumActionResult.FAIL, var4);
            }
         } else {
            return new ActionResult(EnumActionResult.PASS, var4);
         }
      }
   }

   public boolean func_208077_a(@Nullable NBTTagCompound var1, EntityType<?> var2) {
      return Objects.equals(this.func_208076_b(var1), var2);
   }

   public int func_195983_a(int var1) {
      return var1 == 0 ? this.field_195988_c : this.field_195989_d;
   }

   public static ItemSpawnEgg func_200889_b(@Nullable EntityType<?> var0) {
      return (ItemSpawnEgg)field_195987_b.get(var0);
   }

   public static Iterable<ItemSpawnEgg> func_195985_g() {
      return Iterables.unmodifiableIterable(field_195987_b.values());
   }

   @Nullable
   public EntityType<?> func_208076_b(@Nullable NBTTagCompound var1) {
      if (var1 != null && var1.func_150297_b("EntityTag", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("EntityTag");
         if (var2.func_150297_b("id", 8)) {
            return EntityType.func_200713_a(var2.func_74779_i("id"));
         }
      }

      return this.field_200890_d;
   }
}
