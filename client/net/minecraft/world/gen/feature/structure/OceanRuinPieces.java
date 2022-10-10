package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityDrowned;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class OceanRuinPieces {
   private static final ResourceLocation[] field_204058_G = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
   private static final ResourceLocation[] field_204059_H = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
   private static final ResourceLocation[] field_204053_B = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
   private static final ResourceLocation[] field_204061_J = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
   private static final ResourceLocation[] field_204062_K = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
   private static final ResourceLocation[] field_204066_O = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
   private static final ResourceLocation[] field_204070_S = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
   private static final ResourceLocation[] field_204049_ab = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

   public static void func_204046_a() {
      StructureIO.func_143031_a(OceanRuinPieces.Piece.class, "ORP");
   }

   private static ResourceLocation func_204042_a(Random var0) {
      return field_204058_G[var0.nextInt(field_204058_G.length)];
   }

   private static ResourceLocation func_204043_b(Random var0) {
      return field_204049_ab[var0.nextInt(field_204049_ab.length)];
   }

   public static void func_204041_a(TemplateManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4, OceanRuinConfig var5) {
      boolean var6 = var4.nextFloat() <= var5.field_204032_b;
      float var7 = var6 ? 0.9F : 0.8F;
      func_204045_a(var0, var1, var2, var3, var4, var5, var6, var7);
      if (var6 && var4.nextFloat() <= var5.field_204033_c) {
         func_204047_a(var0, var4, var2, var1, var5, var3);
      }

   }

   private static void func_204047_a(TemplateManager var0, Random var1, Rotation var2, BlockPos var3, OceanRuinConfig var4, List<StructurePiece> var5) {
      int var6 = var3.func_177958_n();
      int var7 = var3.func_177952_p();
      BlockPos var8 = Template.func_207669_a(new BlockPos(15, 0, 15), Mirror.NONE, var2, new BlockPos(0, 0, 0)).func_177982_a(var6, 0, var7);
      MutableBoundingBox var9 = MutableBoundingBox.func_175899_a(var6, 0, var7, var8.func_177958_n(), 0, var8.func_177952_p());
      BlockPos var10 = new BlockPos(Math.min(var6, var8.func_177958_n()), 0, Math.min(var7, var8.func_177952_p()));
      List var11 = func_204044_a(var1, var10.func_177958_n(), var10.func_177952_p());
      int var12 = MathHelper.func_76136_a(var1, 4, 8);

      for(int var13 = 0; var13 < var12; ++var13) {
         if (!var11.isEmpty()) {
            int var14 = var1.nextInt(var11.size());
            BlockPos var15 = (BlockPos)var11.remove(var14);
            int var16 = var15.func_177958_n();
            int var17 = var15.func_177952_p();
            Rotation var18 = Rotation.values()[var1.nextInt(Rotation.values().length)];
            BlockPos var19 = Template.func_207669_a(new BlockPos(5, 0, 6), Mirror.NONE, var18, new BlockPos(0, 0, 0)).func_177982_a(var16, 0, var17);
            MutableBoundingBox var20 = MutableBoundingBox.func_175899_a(var16, 0, var17, var19.func_177958_n(), 0, var19.func_177952_p());
            if (!var20.func_78884_a(var9)) {
               func_204045_a(var0, var15, var18, var5, var1, var4, false, 0.8F);
            }
         }
      }

   }

   private static List<BlockPos> func_204044_a(Random var0, int var1, int var2) {
      ArrayList var3 = Lists.newArrayList();
      var3.add(new BlockPos(var1 - 16 + MathHelper.func_76136_a(var0, 1, 8), 90, var2 + 16 + MathHelper.func_76136_a(var0, 1, 7)));
      var3.add(new BlockPos(var1 - 16 + MathHelper.func_76136_a(var0, 1, 8), 90, var2 + MathHelper.func_76136_a(var0, 1, 7)));
      var3.add(new BlockPos(var1 - 16 + MathHelper.func_76136_a(var0, 1, 8), 90, var2 - 16 + MathHelper.func_76136_a(var0, 4, 8)));
      var3.add(new BlockPos(var1 + MathHelper.func_76136_a(var0, 1, 7), 90, var2 + 16 + MathHelper.func_76136_a(var0, 1, 7)));
      var3.add(new BlockPos(var1 + MathHelper.func_76136_a(var0, 1, 7), 90, var2 - 16 + MathHelper.func_76136_a(var0, 4, 6)));
      var3.add(new BlockPos(var1 + 16 + MathHelper.func_76136_a(var0, 1, 7), 90, var2 + 16 + MathHelper.func_76136_a(var0, 3, 8)));
      var3.add(new BlockPos(var1 + 16 + MathHelper.func_76136_a(var0, 1, 7), 90, var2 + MathHelper.func_76136_a(var0, 1, 7)));
      var3.add(new BlockPos(var1 + 16 + MathHelper.func_76136_a(var0, 1, 7), 90, var2 - 16 + MathHelper.func_76136_a(var0, 4, 8)));
      return var3;
   }

   private static void func_204045_a(TemplateManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4, OceanRuinConfig var5, boolean var6, float var7) {
      if (var5.field_204031_a == OceanRuinStructure.Type.WARM) {
         ResourceLocation var8 = var6 ? func_204043_b(var4) : func_204042_a(var4);
         var3.add(new OceanRuinPieces.Piece(var0, var8, var1, var2, var7, var5.field_204031_a, var6));
      } else if (var5.field_204031_a == OceanRuinStructure.Type.COLD) {
         ResourceLocation[] var12 = var6 ? field_204062_K : field_204059_H;
         ResourceLocation[] var9 = var6 ? field_204070_S : field_204053_B;
         ResourceLocation[] var10 = var6 ? field_204066_O : field_204061_J;
         int var11 = var4.nextInt(var12.length);
         var3.add(new OceanRuinPieces.Piece(var0, var12[var11], var1, var2, var7, var5.field_204031_a, var6));
         var3.add(new OceanRuinPieces.Piece(var0, var9[var11], var1, var2, 0.7F, var5.field_204031_a, var6));
         var3.add(new OceanRuinPieces.Piece(var0, var10[var11], var1, var2, 0.5F, var5.field_204031_a, var6));
      }

   }

   public static class Piece extends TemplateStructurePiece {
      private OceanRuinStructure.Type field_204036_d;
      private float field_204037_e;
      private ResourceLocation field_204038_f;
      private Rotation field_204039_g;
      private boolean field_204040_h;

      public Piece() {
         super();
      }

      public Piece(TemplateManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, float var5, OceanRuinStructure.Type var6, boolean var7) {
         super(0);
         this.field_204038_f = var2;
         this.field_186178_c = var3;
         this.field_204039_g = var4;
         this.field_204037_e = var5;
         this.field_204036_d = var6;
         this.field_204040_h = var7;
         this.func_204034_a(var1);
      }

      private void func_204034_a(TemplateManager var1) {
         Template var2 = var1.func_200220_a(this.field_204038_f);
         PlacementSettings var3 = (new PlacementSettings()).func_186220_a(this.field_204039_g).func_186214_a(Mirror.NONE).func_186225_a(Blocks.field_150350_a);
         this.func_186173_a(var2, this.field_186178_c, var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74778_a("Template", this.field_204038_f.toString());
         var1.func_74778_a("Rot", this.field_204039_g.name());
         var1.func_74776_a("Integrity", this.field_204037_e);
         var1.func_74778_a("BiomeType", this.field_204036_d.toString());
         var1.func_74757_a("IsLarge", this.field_204040_h);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_204038_f = new ResourceLocation(var1.func_74779_i("Template"));
         this.field_204039_g = Rotation.valueOf(var1.func_74779_i("Rot"));
         this.field_204037_e = var1.func_74760_g("Integrity");
         this.field_204036_d = OceanRuinStructure.Type.valueOf(var1.func_74779_i("BiomeType"));
         this.field_204040_h = var1.func_74767_n("IsLarge");
         this.func_204034_a(var2);
      }

      protected void func_186175_a(String var1, BlockPos var2, IWorld var3, Random var4, MutableBoundingBox var5) {
         if ("chest".equals(var1)) {
            var3.func_180501_a(var2, (IBlockState)Blocks.field_150486_ae.func_176223_P().func_206870_a(BlockChest.field_204511_c, var3.func_204610_c(var2).func_206884_a(FluidTags.field_206959_a)), 2);
            TileEntity var6 = var3.func_175625_s(var2);
            if (var6 instanceof TileEntityChest) {
               ((TileEntityChest)var6).func_189404_a(this.field_204040_h ? LootTableList.field_204115_q : LootTableList.field_204114_p, var4.nextLong());
            }
         } else if ("drowned".equals(var1)) {
            EntityDrowned var7 = new EntityDrowned(var3.func_201672_e());
            var7.func_110163_bv();
            var7.func_174828_a(var2, 0.0F, 0.0F);
            var7.func_204210_a(var3.func_175649_E(var2), (IEntityLivingData)null, (NBTTagCompound)null);
            var3.func_72838_d(var7);
            if (var2.func_177956_o() > var3.func_181545_F()) {
               var3.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 2);
            } else {
               var3.func_180501_a(var2, Blocks.field_150355_j.func_176223_P(), 2);
            }
         }

      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         this.field_186177_b.func_189946_a(this.field_204037_e);
         int var5 = var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, this.field_186178_c.func_177958_n(), this.field_186178_c.func_177952_p());
         this.field_186178_c = new BlockPos(this.field_186178_c.func_177958_n(), var5, this.field_186178_c.func_177952_p());
         BlockPos var6 = Template.func_207669_a(new BlockPos(this.field_186176_a.func_186259_a().func_177958_n() - 1, 0, this.field_186176_a.func_186259_a().func_177952_p() - 1), Mirror.NONE, this.field_204039_g, new BlockPos(0, 0, 0)).func_177971_a(this.field_186178_c);
         this.field_186178_c = new BlockPos(this.field_186178_c.func_177958_n(), this.func_204035_a(this.field_186178_c, var1, var6), this.field_186178_c.func_177952_p());
         return super.func_74875_a(var1, var2, var3, var4);
      }

      private int func_204035_a(BlockPos var1, IBlockReader var2, BlockPos var3) {
         int var4 = var1.func_177956_o();
         int var5 = 512;
         int var6 = var4 - 1;
         int var7 = 0;
         Iterator var8 = BlockPos.func_177980_a(var1, var3).iterator();

         while(var8.hasNext()) {
            BlockPos var9 = (BlockPos)var8.next();
            int var10 = var9.func_177958_n();
            int var11 = var9.func_177952_p();
            int var12 = var1.func_177956_o() - 1;
            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos(var10, var12, var11);
            IBlockState var14 = var2.func_180495_p(var13);

            for(IFluidState var15 = var2.func_204610_c(var13); (var14.func_196958_f() || var15.func_206884_a(FluidTags.field_206959_a) || var14.func_177230_c().func_203417_a(BlockTags.field_205213_E)) && var12 > 1; var15 = var2.func_204610_c(var13)) {
               --var12;
               var13.func_181079_c(var10, var12, var11);
               var14 = var2.func_180495_p(var13);
            }

            var5 = Math.min(var5, var12);
            if (var12 < var6 - 2) {
               ++var7;
            }
         }

         int var16 = Math.abs(var1.func_177958_n() - var3.func_177958_n());
         if (var6 - var5 > 2 && var7 > var16 - 2) {
            var4 = var5 + 1;
         }

         return var4;
      }
   }
}
