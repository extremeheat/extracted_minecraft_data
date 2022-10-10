package net.minecraft.block;

import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCarvedPumpkin extends BlockHorizontal {
   public static final DirectionProperty field_196359_a;
   private BlockPattern field_196361_b;
   private BlockPattern field_196362_c;
   private BlockPattern field_196363_y;
   private BlockPattern field_196364_z;
   private static final Predicate<IBlockState> field_196360_A;

   protected BlockCarvedPumpkin(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196359_a, EnumFacing.NORTH));
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         this.func_196358_b(var2, var3);
      }
   }

   public boolean func_196354_a(IWorldReaderBase var1, BlockPos var2) {
      return this.func_196353_d().func_177681_a(var1, var2) != null || this.func_196356_f().func_177681_a(var1, var2) != null;
   }

   private void func_196358_b(World var1, BlockPos var2) {
      BlockPattern.PatternHelper var3 = this.func_196355_e().func_177681_a(var1, var2);
      int var4;
      Iterator var6;
      EntityPlayerMP var7;
      BlockWorldState var8;
      int var14;
      int var15;
      if (var3 != null) {
         for(var4 = 0; var4 < this.func_196355_e().func_177685_b(); ++var4) {
            BlockWorldState var5 = var3.func_177670_a(0, var4, 0);
            var1.func_180501_a(var5.func_177508_d(), Blocks.field_150350_a.func_176223_P(), 2);
         }

         EntitySnowman var9 = new EntitySnowman(var1);
         BlockPos var10 = var3.func_177670_a(0, 2, 0).func_177508_d();
         var9.func_70012_b((double)var10.func_177958_n() + 0.5D, (double)var10.func_177956_o() + 0.05D, (double)var10.func_177952_p() + 0.5D, 0.0F, 0.0F);
         var1.func_72838_d(var9);
         var6 = var1.func_72872_a(EntityPlayerMP.class, var9.func_174813_aQ().func_186662_g(5.0D)).iterator();

         while(var6.hasNext()) {
            var7 = (EntityPlayerMP)var6.next();
            CriteriaTriggers.field_192133_m.func_192229_a(var7, var9);
         }

         var14 = Block.func_196246_j(Blocks.field_196604_cC.func_176223_P());
         var1.func_175718_b(2001, var10, var14);
         var1.func_175718_b(2001, var10.func_177984_a(), var14);

         for(var15 = 0; var15 < this.func_196355_e().func_177685_b(); ++var15) {
            var8 = var3.func_177670_a(0, var15, 0);
            var1.func_195592_c(var8.func_177508_d(), Blocks.field_150350_a);
         }
      } else {
         var3 = this.func_196357_g().func_177681_a(var1, var2);
         if (var3 != null) {
            for(var4 = 0; var4 < this.func_196357_g().func_177684_c(); ++var4) {
               for(int var12 = 0; var12 < this.func_196357_g().func_177685_b(); ++var12) {
                  var1.func_180501_a(var3.func_177670_a(var4, var12, 0).func_177508_d(), Blocks.field_150350_a.func_176223_P(), 2);
               }
            }

            BlockPos var11 = var3.func_177670_a(1, 2, 0).func_177508_d();
            EntityIronGolem var13 = new EntityIronGolem(var1);
            var13.func_70849_f(true);
            var13.func_70012_b((double)var11.func_177958_n() + 0.5D, (double)var11.func_177956_o() + 0.05D, (double)var11.func_177952_p() + 0.5D, 0.0F, 0.0F);
            var1.func_72838_d(var13);
            var6 = var1.func_72872_a(EntityPlayerMP.class, var13.func_174813_aQ().func_186662_g(5.0D)).iterator();

            while(var6.hasNext()) {
               var7 = (EntityPlayerMP)var6.next();
               CriteriaTriggers.field_192133_m.func_192229_a(var7, var13);
            }

            for(var14 = 0; var14 < 120; ++var14) {
               var1.func_195594_a(Particles.field_197593_D, (double)var11.func_177958_n() + var1.field_73012_v.nextDouble(), (double)var11.func_177956_o() + var1.field_73012_v.nextDouble() * 3.9D, (double)var11.func_177952_p() + var1.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

            for(var14 = 0; var14 < this.func_196357_g().func_177684_c(); ++var14) {
               for(var15 = 0; var15 < this.func_196357_g().func_177685_b(); ++var15) {
                  var8 = var3.func_177670_a(var14, var15, 0);
                  var1.func_195592_c(var8.func_177508_d(), Blocks.field_150350_a);
               }
            }
         }
      }

   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_196359_a, var1.func_195992_f().func_176734_d());
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196359_a);
   }

   protected BlockPattern func_196353_d() {
      if (this.field_196361_b == null) {
         this.field_196361_b = FactoryBlockPattern.func_177660_a().func_177659_a(" ", "#", "#").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_196604_cC))).func_177661_b();
      }

      return this.field_196361_b;
   }

   protected BlockPattern func_196355_e() {
      if (this.field_196362_c == null) {
         this.field_196362_c = FactoryBlockPattern.func_177660_a().func_177659_a("^", "#", "#").func_177662_a('^', BlockWorldState.func_177510_a(field_196360_A)).func_177662_a('#', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_196604_cC))).func_177661_b();
      }

      return this.field_196362_c;
   }

   protected BlockPattern func_196356_f() {
      if (this.field_196363_y == null) {
         this.field_196363_y = FactoryBlockPattern.func_177660_a().func_177659_a("~ ~", "###", "~#~").func_177662_a('#', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150339_S))).func_177662_a('~', BlockWorldState.func_177510_a(BlockMaterialMatcher.func_189886_a(Material.field_151579_a))).func_177661_b();
      }

      return this.field_196363_y;
   }

   protected BlockPattern func_196357_g() {
      if (this.field_196364_z == null) {
         this.field_196364_z = FactoryBlockPattern.func_177660_a().func_177659_a("~^~", "###", "~#~").func_177662_a('^', BlockWorldState.func_177510_a(field_196360_A)).func_177662_a('#', BlockWorldState.func_177510_a(BlockStateMatcher.func_177638_a(Blocks.field_150339_S))).func_177662_a('~', BlockWorldState.func_177510_a(BlockMaterialMatcher.func_189886_a(Material.field_151579_a))).func_177661_b();
      }

      return this.field_196364_z;
   }

   static {
      field_196359_a = BlockHorizontal.field_185512_D;
      field_196360_A = (var0) -> {
         return var0 != null && (var0.func_177230_c() == Blocks.field_196625_cS || var0.func_177230_c() == Blocks.field_196628_cT);
      };
   }
}
