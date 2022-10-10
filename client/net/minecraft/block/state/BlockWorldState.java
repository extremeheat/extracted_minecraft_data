package net.minecraft.block.state;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;

public class BlockWorldState {
   private final IWorldReaderBase field_177515_a;
   private final BlockPos field_177513_b;
   private final boolean field_181628_c;
   private IBlockState field_177514_c;
   private TileEntity field_177511_d;
   private boolean field_177512_e;

   public BlockWorldState(IWorldReaderBase var1, BlockPos var2, boolean var3) {
      super();
      this.field_177515_a = var1;
      this.field_177513_b = var2;
      this.field_181628_c = var3;
   }

   public IBlockState func_177509_a() {
      if (this.field_177514_c == null && (this.field_181628_c || this.field_177515_a.func_175667_e(this.field_177513_b))) {
         this.field_177514_c = this.field_177515_a.func_180495_p(this.field_177513_b);
      }

      return this.field_177514_c;
   }

   @Nullable
   public TileEntity func_177507_b() {
      if (this.field_177511_d == null && !this.field_177512_e) {
         this.field_177511_d = this.field_177515_a.func_175625_s(this.field_177513_b);
         this.field_177512_e = true;
      }

      return this.field_177511_d;
   }

   public IWorldReaderBase func_196960_c() {
      return this.field_177515_a;
   }

   public BlockPos func_177508_d() {
      return this.field_177513_b;
   }

   public static Predicate<BlockWorldState> func_177510_a(Predicate<IBlockState> var0) {
      return (var1) -> {
         return var1 != null && var0.test(var1.func_177509_a());
      };
   }
}
