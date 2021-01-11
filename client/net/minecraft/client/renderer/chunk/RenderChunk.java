package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class RenderChunk {
   private World field_178588_d;
   private final RenderGlobal field_178589_e;
   public static int field_178592_a;
   private BlockPos field_178586_f;
   public CompiledChunk field_178590_b;
   private final ReentrantLock field_178587_g;
   private final ReentrantLock field_178598_h;
   private ChunkCompileTaskGenerator field_178599_i;
   private final Set<TileEntity> field_181056_j;
   private final int field_178596_j;
   private final FloatBuffer field_178597_k;
   private final VertexBuffer[] field_178594_l;
   public AxisAlignedBB field_178591_c;
   private int field_178595_m;
   private boolean field_178593_n;
   private EnumMap<EnumFacing, BlockPos> field_181702_p;

   public RenderChunk(World var1, RenderGlobal var2, BlockPos var3, int var4) {
      super();
      this.field_178590_b = CompiledChunk.field_178502_a;
      this.field_178587_g = new ReentrantLock();
      this.field_178598_h = new ReentrantLock();
      this.field_178599_i = null;
      this.field_181056_j = Sets.newHashSet();
      this.field_178597_k = GLAllocation.func_74529_h(16);
      this.field_178594_l = new VertexBuffer[EnumWorldBlockLayer.values().length];
      this.field_178595_m = -1;
      this.field_178593_n = true;
      this.field_181702_p = Maps.newEnumMap(EnumFacing.class);
      this.field_178588_d = var1;
      this.field_178589_e = var2;
      this.field_178596_j = var4;
      if (!var3.equals(this.func_178568_j())) {
         this.func_178576_a(var3);
      }

      if (OpenGlHelper.func_176075_f()) {
         for(int var5 = 0; var5 < EnumWorldBlockLayer.values().length; ++var5) {
            this.field_178594_l[var5] = new VertexBuffer(DefaultVertexFormats.field_176600_a);
         }
      }

   }

   public boolean func_178577_a(int var1) {
      if (this.field_178595_m == var1) {
         return false;
      } else {
         this.field_178595_m = var1;
         return true;
      }
   }

   public VertexBuffer func_178565_b(int var1) {
      return this.field_178594_l[var1];
   }

   public void func_178576_a(BlockPos var1) {
      this.func_178585_h();
      this.field_178586_f = var1;
      this.field_178591_c = new AxisAlignedBB(var1, var1.func_177982_a(16, 16, 16));
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing var5 = var2[var4];
         this.field_181702_p.put(var5, var1.func_177967_a(var5, 16));
      }

      this.func_178567_n();
   }

   public void func_178570_a(float var1, float var2, float var3, ChunkCompileTaskGenerator var4) {
      CompiledChunk var5 = var4.func_178544_c();
      if (var5.func_178487_c() != null && !var5.func_178491_b(EnumWorldBlockLayer.TRANSLUCENT)) {
         this.func_178573_a(var4.func_178545_d().func_179038_a(EnumWorldBlockLayer.TRANSLUCENT), this.field_178586_f);
         var4.func_178545_d().func_179038_a(EnumWorldBlockLayer.TRANSLUCENT).func_178993_a(var5.func_178487_c());
         this.func_178584_a(EnumWorldBlockLayer.TRANSLUCENT, var1, var2, var3, var4.func_178545_d().func_179038_a(EnumWorldBlockLayer.TRANSLUCENT), var5);
      }
   }

   public void func_178581_b(float var1, float var2, float var3, ChunkCompileTaskGenerator var4) {
      CompiledChunk var5 = new CompiledChunk();
      boolean var6 = true;
      BlockPos var7 = this.field_178586_f;
      BlockPos var8 = var7.func_177982_a(15, 15, 15);
      var4.func_178540_f().lock();

      RegionRenderCache var9;
      label231: {
         try {
            if (var4.func_178546_a() == ChunkCompileTaskGenerator.Status.COMPILING) {
               var9 = new RegionRenderCache(this.field_178588_d, var7.func_177982_a(-1, -1, -1), var8.func_177982_a(1, 1, 1), 1);
               var4.func_178543_a(var5);
               break label231;
            }
         } finally {
            var4.func_178540_f().unlock();
         }

         return;
      }

      VisGraph var10 = new VisGraph();
      HashSet var11 = Sets.newHashSet();
      if (!var9.func_72806_N()) {
         ++field_178592_a;
         boolean[] var12 = new boolean[EnumWorldBlockLayer.values().length];
         BlockRendererDispatcher var13 = Minecraft.func_71410_x().func_175602_ab();
         Iterator var14 = BlockPos.func_177975_b(var7, var8).iterator();

         while(var14.hasNext()) {
            BlockPos.MutableBlockPos var15 = (BlockPos.MutableBlockPos)var14.next();
            IBlockState var16 = var9.func_180495_p(var15);
            Block var17 = var16.func_177230_c();
            if (var17.func_149662_c()) {
               var10.func_178606_a(var15);
            }

            if (var17.func_149716_u()) {
               TileEntity var18 = var9.func_175625_s(new BlockPos(var15));
               TileEntitySpecialRenderer var19 = TileEntityRendererDispatcher.field_147556_a.func_147547_b(var18);
               if (var18 != null && var19 != null) {
                  var5.func_178490_a(var18);
                  if (var19.func_181055_a()) {
                     var11.add(var18);
                  }
               }
            }

            EnumWorldBlockLayer var34 = var17.func_180664_k();
            int var35 = var34.ordinal();
            if (var17.func_149645_b() != -1) {
               WorldRenderer var20 = var4.func_178545_d().func_179039_a(var35);
               if (!var5.func_178492_d(var34)) {
                  var5.func_178493_c(var34);
                  this.func_178573_a(var20, var7);
               }

               var12[var35] |= var13.func_175018_a(var16, var15, var9, var20);
            }
         }

         EnumWorldBlockLayer[] var30 = EnumWorldBlockLayer.values();
         int var31 = var30.length;

         for(int var32 = 0; var32 < var31; ++var32) {
            EnumWorldBlockLayer var33 = var30[var32];
            if (var12[var33.ordinal()]) {
               var5.func_178486_a(var33);
            }

            if (var5.func_178492_d(var33)) {
               this.func_178584_a(var33, var1, var2, var3, var4.func_178545_d().func_179038_a(var33), var5);
            }
         }
      }

      var5.func_178488_a(var10.func_178607_a());
      this.field_178587_g.lock();

      try {
         HashSet var28 = Sets.newHashSet(var11);
         HashSet var29 = Sets.newHashSet(this.field_181056_j);
         var28.removeAll(this.field_181056_j);
         var29.removeAll(var11);
         this.field_181056_j.clear();
         this.field_181056_j.addAll(var11);
         this.field_178589_e.func_181023_a(var29, var28);
      } finally {
         this.field_178587_g.unlock();
      }

   }

   protected void func_178578_b() {
      this.field_178587_g.lock();

      try {
         if (this.field_178599_i != null && this.field_178599_i.func_178546_a() != ChunkCompileTaskGenerator.Status.DONE) {
            this.field_178599_i.func_178542_e();
            this.field_178599_i = null;
         }
      } finally {
         this.field_178587_g.unlock();
      }

   }

   public ReentrantLock func_178579_c() {
      return this.field_178587_g;
   }

   public ChunkCompileTaskGenerator func_178574_d() {
      this.field_178587_g.lock();

      ChunkCompileTaskGenerator var1;
      try {
         this.func_178578_b();
         this.field_178599_i = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.REBUILD_CHUNK);
         var1 = this.field_178599_i;
      } finally {
         this.field_178587_g.unlock();
      }

      return var1;
   }

   public ChunkCompileTaskGenerator func_178582_e() {
      this.field_178587_g.lock();

      ChunkCompileTaskGenerator var1;
      try {
         if (this.field_178599_i != null && this.field_178599_i.func_178546_a() == ChunkCompileTaskGenerator.Status.PENDING) {
            var1 = null;
            return var1;
         }

         if (this.field_178599_i != null && this.field_178599_i.func_178546_a() != ChunkCompileTaskGenerator.Status.DONE) {
            this.field_178599_i.func_178542_e();
            this.field_178599_i = null;
         }

         this.field_178599_i = new ChunkCompileTaskGenerator(this, ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY);
         this.field_178599_i.func_178543_a(this.field_178590_b);
         var1 = this.field_178599_i;
      } finally {
         this.field_178587_g.unlock();
      }

      return var1;
   }

   private void func_178573_a(WorldRenderer var1, BlockPos var2) {
      var1.func_181668_a(7, DefaultVertexFormats.field_176600_a);
      var1.func_178969_c((double)(-var2.func_177958_n()), (double)(-var2.func_177956_o()), (double)(-var2.func_177952_p()));
   }

   private void func_178584_a(EnumWorldBlockLayer var1, float var2, float var3, float var4, WorldRenderer var5, CompiledChunk var6) {
      if (var1 == EnumWorldBlockLayer.TRANSLUCENT && !var6.func_178491_b(var1)) {
         var5.func_181674_a(var2, var3, var4);
         var6.func_178494_a(var5.func_181672_a());
      }

      var5.func_178977_d();
   }

   private void func_178567_n() {
      GlStateManager.func_179094_E();
      GlStateManager.func_179096_D();
      float var1 = 1.000001F;
      GlStateManager.func_179109_b(-8.0F, -8.0F, -8.0F);
      GlStateManager.func_179152_a(var1, var1, var1);
      GlStateManager.func_179109_b(8.0F, 8.0F, 8.0F);
      GlStateManager.func_179111_a(2982, this.field_178597_k);
      GlStateManager.func_179121_F();
   }

   public void func_178572_f() {
      GlStateManager.func_179110_a(this.field_178597_k);
   }

   public CompiledChunk func_178571_g() {
      return this.field_178590_b;
   }

   public void func_178580_a(CompiledChunk var1) {
      this.field_178598_h.lock();

      try {
         this.field_178590_b = var1;
      } finally {
         this.field_178598_h.unlock();
      }

   }

   public void func_178585_h() {
      this.func_178578_b();
      this.field_178590_b = CompiledChunk.field_178502_a;
   }

   public void func_178566_a() {
      this.func_178585_h();
      this.field_178588_d = null;

      for(int var1 = 0; var1 < EnumWorldBlockLayer.values().length; ++var1) {
         if (this.field_178594_l[var1] != null) {
            this.field_178594_l[var1].func_177362_c();
         }
      }

   }

   public BlockPos func_178568_j() {
      return this.field_178586_f;
   }

   public void func_178575_a(boolean var1) {
      this.field_178593_n = var1;
   }

   public boolean func_178569_m() {
      return this.field_178593_n;
   }

   public BlockPos func_181701_a(EnumFacing var1) {
      return (BlockPos)this.field_181702_p.get(var1);
   }
}
