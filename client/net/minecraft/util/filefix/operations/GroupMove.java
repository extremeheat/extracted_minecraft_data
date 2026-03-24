package net.minecraft.util.filefix.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.minecraft.util.worldupdate.UpgradeProgress;

public record GroupMove(Map<String, String> fromTo, List<Move> fixers) implements FileFixOperation {
   public GroupMove {
      super();
   }

   public void fix(final Path baseDirectory, final UpgradeProgress upgradeProgress) throws IOException {
      for(Map.Entry<String, String> entry : this.fromTo.entrySet()) {
         for(Move moveOperation : this.fixers) {
            Move relative = moveOperation.relative((String)entry.getKey(), (String)entry.getValue());
            relative.fix(baseDirectory, upgradeProgress);
         }
      }

   }
}
