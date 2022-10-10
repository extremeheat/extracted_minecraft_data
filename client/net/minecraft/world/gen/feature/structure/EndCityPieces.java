package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class EndCityPieces {
   private static final PlacementSettings field_186202_b = (new PlacementSettings()).func_186222_a(true);
   private static final PlacementSettings field_186203_c;
   private static final EndCityPieces.IGenerator field_186204_d;
   private static final List<Tuple<Rotation, BlockPos>> field_186205_e;
   private static final EndCityPieces.IGenerator field_186206_f;
   private static final EndCityPieces.IGenerator field_186207_g;
   private static final List<Tuple<Rotation, BlockPos>> field_186208_h;
   private static final EndCityPieces.IGenerator field_186209_i;

   public static void func_186200_a() {
      StructureIO.func_143031_a(EndCityPieces.CityTemplate.class, "ECP");
   }

   private static EndCityPieces.CityTemplate func_191090_b(TemplateManager var0, EndCityPieces.CityTemplate var1, BlockPos var2, String var3, Rotation var4, boolean var5) {
      EndCityPieces.CityTemplate var6 = new EndCityPieces.CityTemplate(var0, var3, var1.field_186178_c, var4, var5);
      BlockPos var7 = var1.field_186176_a.func_186262_a(var1.field_186177_b, var2, var6.field_186177_b, BlockPos.field_177992_a);
      var6.func_181138_a(var7.func_177958_n(), var7.func_177956_o(), var7.func_177952_p());
      return var6;
   }

   public static void func_191087_a(TemplateManager var0, BlockPos var1, Rotation var2, List<StructurePiece> var3, Random var4) {
      field_186209_i.func_186184_a();
      field_186204_d.func_186184_a();
      field_186207_g.func_186184_a();
      field_186206_f.func_186184_a();
      EndCityPieces.CityTemplate var5 = func_189935_b(var3, new EndCityPieces.CityTemplate(var0, "base_floor", var1, var2, true));
      var5 = func_189935_b(var3, func_191090_b(var0, var5, new BlockPos(-1, 0, -1), "second_floor_1", var2, false));
      var5 = func_189935_b(var3, func_191090_b(var0, var5, new BlockPos(-1, 4, -1), "third_floor_1", var2, false));
      var5 = func_189935_b(var3, func_191090_b(var0, var5, new BlockPos(-1, 8, -1), "third_roof", var2, true));
      func_191088_b(var0, field_186206_f, 1, var5, (BlockPos)null, var3, var4);
   }

   private static EndCityPieces.CityTemplate func_189935_b(List<StructurePiece> var0, EndCityPieces.CityTemplate var1) {
      var0.add(var1);
      return var1;
   }

   private static boolean func_191088_b(TemplateManager var0, EndCityPieces.IGenerator var1, int var2, EndCityPieces.CityTemplate var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
      if (var2 > 8) {
         return false;
      } else {
         ArrayList var7 = Lists.newArrayList();
         if (var1.func_191086_a(var0, var2, var3, var4, var7, var6)) {
            boolean var8 = false;
            int var9 = var6.nextInt();
            Iterator var10 = var7.iterator();

            while(var10.hasNext()) {
               StructurePiece var11 = (StructurePiece)var10.next();
               var11.field_74886_g = var9;
               StructurePiece var12 = StructurePiece.func_74883_a(var5, var11.func_74874_b());
               if (var12 != null && var12.field_74886_g != var3.field_74886_g) {
                  var8 = true;
                  break;
               }
            }

            if (!var8) {
               var5.addAll(var7);
               return true;
            }
         }

         return false;
      }
   }

   static {
      field_186203_c = (new PlacementSettings()).func_186222_a(true).func_186225_a(Blocks.field_150350_a);
      field_186204_d = new EndCityPieces.IGenerator() {
         public void func_186184_a() {
         }

         public boolean func_191086_a(TemplateManager var1, int var2, EndCityPieces.CityTemplate var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            if (var2 > 8) {
               return false;
            } else {
               Rotation var7 = var3.field_186177_b.func_186215_c();
               EndCityPieces.CityTemplate var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var3, var4, "base_floor", var7, true));
               int var9 = var6.nextInt(3);
               if (var9 == 0) {
                  EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 4, -1), "base_roof", var7, true));
               } else if (var9 == 1) {
                  var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
                  var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 8, -1), "second_roof", var7, false));
                  EndCityPieces.func_191088_b(var1, EndCityPieces.field_186206_f, var2 + 1, var8, (BlockPos)null, var5, var6);
               } else if (var9 == 2) {
                  var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
                  var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 4, -1), "third_floor_2", var7, false));
                  var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 8, -1), "third_roof", var7, true));
                  EndCityPieces.func_191088_b(var1, EndCityPieces.field_186206_f, var2 + 1, var8, (BlockPos)null, var5, var6);
               }

               return true;
            }
         }
      };
      field_186205_e = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(1, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
      field_186206_f = new EndCityPieces.IGenerator() {
         public void func_186184_a() {
         }

         public boolean func_191086_a(TemplateManager var1, int var2, EndCityPieces.CityTemplate var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            Rotation var7 = var3.field_186177_b.func_186215_c();
            EndCityPieces.CityTemplate var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var3, new BlockPos(3 + var6.nextInt(2), -3, 3 + var6.nextInt(2)), "tower_base", var7, true));
            var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(0, 7, 0), "tower_piece", var7, true));
            EndCityPieces.CityTemplate var9 = var6.nextInt(3) == 0 ? var8 : null;
            int var10 = 1 + var6.nextInt(3);

            for(int var11 = 0; var11 < var10; ++var11) {
               var8 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(0, 4, 0), "tower_piece", var7, true));
               if (var11 < var10 - 1 && var6.nextBoolean()) {
                  var9 = var8;
               }
            }

            if (var9 != null) {
               Iterator var14 = EndCityPieces.field_186205_e.iterator();

               while(var14.hasNext()) {
                  Tuple var12 = (Tuple)var14.next();
                  if (var6.nextBoolean()) {
                     EndCityPieces.CityTemplate var13 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var9, (BlockPos)var12.func_76340_b(), "bridge_end", var7.func_185830_a((Rotation)var12.func_76341_a()), true));
                     EndCityPieces.func_191088_b(var1, EndCityPieces.field_186207_g, var2 + 1, var13, (BlockPos)null, var5, var6);
                  }
               }

               EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
            } else {
               if (var2 != 7) {
                  return EndCityPieces.func_191088_b(var1, EndCityPieces.field_186209_i, var2 + 1, var8, (BlockPos)null, var5, var6);
               }

               EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
            }

            return true;
         }
      };
      field_186207_g = new EndCityPieces.IGenerator() {
         public boolean field_186186_a;

         public void func_186184_a() {
            this.field_186186_a = false;
         }

         public boolean func_191086_a(TemplateManager var1, int var2, EndCityPieces.CityTemplate var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            Rotation var7 = var3.field_186177_b.func_186215_c();
            int var8 = var6.nextInt(4) + 1;
            EndCityPieces.CityTemplate var9 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var3, new BlockPos(0, 0, -4), "bridge_piece", var7, true));
            var9.field_74886_g = -1;
            byte var10 = 0;

            for(int var11 = 0; var11 < var8; ++var11) {
               if (var6.nextBoolean()) {
                  var9 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var9, new BlockPos(0, var10, -4), "bridge_piece", var7, true));
                  var10 = 0;
               } else {
                  if (var6.nextBoolean()) {
                     var9 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var9, new BlockPos(0, var10, -4), "bridge_steep_stairs", var7, true));
                  } else {
                     var9 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var9, new BlockPos(0, var10, -8), "bridge_gentle_stairs", var7, true));
                  }

                  var10 = 4;
               }
            }

            if (!this.field_186186_a && var6.nextInt(10 - var2) == 0) {
               EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var9, new BlockPos(-8 + var6.nextInt(8), var10, -70 + var6.nextInt(10)), "ship", var7, true));
               this.field_186186_a = true;
            } else if (!EndCityPieces.func_191088_b(var1, EndCityPieces.field_186204_d, var2 + 1, var9, new BlockPos(-3, var10 + 1, -11), var5, var6)) {
               return false;
            }

            var9 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var9, new BlockPos(4, var10, 0), "bridge_end", var7.func_185830_a(Rotation.CLOCKWISE_180), true));
            var9.field_74886_g = -1;
            return true;
         }
      };
      field_186208_h = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(4, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
      field_186209_i = new EndCityPieces.IGenerator() {
         public void func_186184_a() {
         }

         public boolean func_191086_a(TemplateManager var1, int var2, EndCityPieces.CityTemplate var3, BlockPos var4, List<StructurePiece> var5, Random var6) {
            Rotation var8 = var3.field_186177_b.func_186215_c();
            EndCityPieces.CityTemplate var7 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var3, new BlockPos(-3, 4, -3), "fat_tower_base", var8, true));
            var7 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var7, new BlockPos(0, 4, 0), "fat_tower_middle", var8, true));

            for(int var9 = 0; var9 < 2 && var6.nextInt(3) != 0; ++var9) {
               var7 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var7, new BlockPos(0, 8, 0), "fat_tower_middle", var8, true));
               Iterator var10 = EndCityPieces.field_186208_h.iterator();

               while(var10.hasNext()) {
                  Tuple var11 = (Tuple)var10.next();
                  if (var6.nextBoolean()) {
                     EndCityPieces.CityTemplate var12 = EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var7, (BlockPos)var11.func_76340_b(), "bridge_end", var8.func_185830_a((Rotation)var11.func_76341_a()), true));
                     EndCityPieces.func_191088_b(var1, EndCityPieces.field_186207_g, var2 + 1, var12, (BlockPos)null, var5, var6);
                  }
               }
            }

            EndCityPieces.func_189935_b(var5, EndCityPieces.func_191090_b(var1, var7, new BlockPos(-2, 8, -2), "fat_tower_top", var8, true));
            return true;
         }
      };
   }

   interface IGenerator {
      void func_186184_a();

      boolean func_191086_a(TemplateManager var1, int var2, EndCityPieces.CityTemplate var3, BlockPos var4, List<StructurePiece> var5, Random var6);
   }

   public static class CityTemplate extends TemplateStructurePiece {
      private String field_186181_d;
      private Rotation field_186182_e;
      private boolean field_186183_f;

      public CityTemplate() {
         super();
      }

      public CityTemplate(TemplateManager var1, String var2, BlockPos var3, Rotation var4, boolean var5) {
         super(0);
         this.field_186181_d = var2;
         this.field_186178_c = var3;
         this.field_186182_e = var4;
         this.field_186183_f = var5;
         this.func_191085_a(var1);
      }

      private void func_191085_a(TemplateManager var1) {
         Template var2 = var1.func_200220_a(new ResourceLocation("end_city/" + this.field_186181_d));
         PlacementSettings var3 = (this.field_186183_f ? EndCityPieces.field_186202_b : EndCityPieces.field_186203_c).func_186217_a().func_186220_a(this.field_186182_e);
         this.func_186173_a(var2, this.field_186178_c, var3);
      }

      protected void func_143012_a(NBTTagCompound var1) {
         super.func_143012_a(var1);
         var1.func_74778_a("Template", this.field_186181_d);
         var1.func_74778_a("Rot", this.field_186182_e.name());
         var1.func_74757_a("OW", this.field_186183_f);
      }

      protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
         super.func_143011_b(var1, var2);
         this.field_186181_d = var1.func_74779_i("Template");
         this.field_186182_e = Rotation.valueOf(var1.func_74779_i("Rot"));
         this.field_186183_f = var1.func_74767_n("OW");
         this.func_191085_a(var2);
      }

      protected void func_186175_a(String var1, BlockPos var2, IWorld var3, Random var4, MutableBoundingBox var5) {
         if (var1.startsWith("Chest")) {
            BlockPos var6 = var2.func_177977_b();
            if (var5.func_175898_b(var6)) {
               TileEntityLockableLoot.func_195479_a(var3, var4, var6, LootTableList.field_186421_c);
            }
         } else if (var1.startsWith("Sentry")) {
            EntityShulker var7 = new EntityShulker(var3.func_201672_e());
            var7.func_70107_b((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D);
            var7.func_184694_g(var2);
            var3.func_72838_d(var7);
         } else if (var1.startsWith("Elytra")) {
            EntityItemFrame var8 = new EntityItemFrame(var3.func_201672_e(), var2, this.field_186182_e.func_185831_a(EnumFacing.SOUTH));
            var8.func_82334_a(new ItemStack(Items.field_185160_cR));
            var3.func_72838_d(var8);
         }

      }
   }
}
