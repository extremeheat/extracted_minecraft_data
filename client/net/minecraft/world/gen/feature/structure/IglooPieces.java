package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class IglooPieces {
   private static final ResourceLocation field_202592_e = new ResourceLocation("igloo/top");
   private static final ResourceLocation field_202593_f = new ResourceLocation("igloo/middle");
   private static final ResourceLocation field_202594_g = new ResourceLocation("igloo/bottom");
   private static final Map<ResourceLocation, BlockPos> field_207621_d;
   private static final Map<ResourceLocation, BlockPos> field_207622_e;

   public static void func_202591_ae_() {
      StructureIO.func_143031_a(IglooPieces.Piece.class, "Iglu");
   }

   public static void func_207617_a(TemplateManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4, IglooConfig var5) {
      if (var4.nextDouble() < 0.5D) {
         int var6 = var4.nextInt(8) + 4;
         var3.add(new IglooPieces.Piece(var0, field_202594_g, var1, var2, var6 * 3));

         for(int var7 = 0; var7 < var6 - 1; ++var7) {
            var3.add(new IglooPieces.Piece(var0, field_202593_f, var1, var2, var7 * 3));
         }
      }

      var3.add(new IglooPieces.Piece(var0, field_202592_e, var1, var2, 0));
   }

   static {
      field_207621_d = ImmutableMap.of(field_202592_e, new BlockPos(3, 5, 5), field_202593_f, new BlockPos(1, 3, 1), field_202594_g, new BlockPos(3, 6, 7));
      field_207622_e = ImmutableMap.of(field_202592_e, new BlockPos(0, 0, 0), field_202593_f, new BlockPos(2, -3, 4), field_202594_g, new BlockPos(0, -3, -2));
   }

   public static class Piece extends TemplateStructurePiece {
      private ResourceLocation field_207615_d;
      private Rotation field_207616_e;

      public Piece() {
         super();
      }

      public Piece(TemplateManager var1, ResourceLocation var2, BlockPos var3, Rotation var4, int var5) {
         super(0);
         this.field_207615_d = var2;
         BlockPos var6 = (BlockPos)IglooPieces.field_207622_e.get(var2);
         this.field_186178_c = var3.func_177982_a(var6.func_177958_n(), var6.func_177956_o() - var5, var6.func_177952_p());
         this.field_207616_e = var4;
         this.func_207614_a(var1);
      }

      private void func_207614_a(TemplateManager var1) {
         Template var2 = var1.func_200220_a(this.field_207615_d);
         PlacementSettings var3 = (new PlacementSettings()).func_186220_a(this.field_207616_e).func_186214_a(Mirror.NONE).func_207665_a((BlockPos)IglooPieces.field_207621_d.get(this.field_207615_d));
         this.func_186173_a(var2, this.field_186178_c, var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74778_a("Template", this.field_207615_d.toString());
         var1.func_74778_a("Rot", this.field_207616_e.name());
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_207615_d = new ResourceLocation(var1.func_74779_i("Template"));
         this.field_207616_e = Rotation.valueOf(var1.func_74779_i("Rot"));
         this.func_207614_a(var2);
      }

      protected void func_186175_a(String var1, BlockPos var2, IWorld var3, Random var4, MutableBoundingBox var5) {
         if ("chest".equals(var1)) {
            var3.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 3);
            TileEntity var6 = var3.func_175625_s(var2.func_177977_b());
            if (var6 instanceof TileEntityChest) {
               ((TileEntityChest)var6).func_189404_a(LootTableList.field_186431_m, var4.nextLong());
            }

         }
      }

      public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         PlacementSettings var5 = (new PlacementSettings()).func_186220_a(this.field_207616_e).func_186214_a(Mirror.NONE).func_207665_a((BlockPos)IglooPieces.field_207621_d.get(this.field_207615_d));
         BlockPos var6 = (BlockPos)IglooPieces.field_207622_e.get(this.field_207615_d);
         BlockPos var7 = this.field_186178_c.func_177971_a(Template.func_186266_a(var5, new BlockPos(3 - var6.func_177958_n(), 0, 0 - var6.func_177952_p())));
         int var8 = var1.func_201676_a(Heightmap.Type.WORLD_SURFACE_WG, var7.func_177958_n(), var7.func_177952_p());
         BlockPos var9 = this.field_186178_c;
         this.field_186178_c = this.field_186178_c.func_177982_a(0, var8 - 90 - 1, 0);
         boolean var10 = super.func_74875_a(var1, var2, var3, var4);
         if (this.field_207615_d.equals(IglooPieces.field_202592_e)) {
            BlockPos var11 = this.field_186178_c.func_177971_a(Template.func_186266_a(var5, new BlockPos(3, 0, 5)));
            IBlockState var12 = var1.func_180495_p(var11.func_177977_b());
            if (!var12.func_196958_f() && var12.func_177230_c() != Blocks.field_150468_ap) {
               var1.func_180501_a(var11, Blocks.field_196604_cC.func_176223_P(), 3);
            }
         }

         this.field_186178_c = var9;
         return var10;
      }
   }
}
