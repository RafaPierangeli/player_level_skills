package com.player_level_skills.access;

import net.minecraft.world.chunk.Chunk;

public interface PlayerDropAccess {

    void increaseKilledMobStat(Chunk chunk);

    boolean allowMobDrop();

    void resetKilledMobStat();
}
