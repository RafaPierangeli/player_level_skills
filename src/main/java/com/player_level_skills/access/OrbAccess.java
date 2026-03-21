package com.player_level_skills.access;


import com.player_level_skills.network.packet.OrbPacket;

public interface OrbAccess {

    void onLevelExperienceOrbSpawn(OrbPacket packet);
}
