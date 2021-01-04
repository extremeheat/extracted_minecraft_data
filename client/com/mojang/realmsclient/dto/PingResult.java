package com.mojang.realmsclient.dto;

import java.util.ArrayList;
import java.util.List;

public class PingResult extends ValueObject {
   public List<RegionPingResult> pingResults = new ArrayList();
   public List<Long> worldIds = new ArrayList();

   public PingResult() {
      super();
   }
}
