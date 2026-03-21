package com.player_level_skills.network.packet;

import com.player_level_skills.Player_level_skills;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public record PlayerSkillSyncPacket(List<Integer> playerSkillIds, List<Integer> playerSkillLevels) implements CustomPayload {

    public static final CustomPayload.Id<PlayerSkillSyncPacket> PACKET_ID = new CustomPayload.Id<>(Player_level_skills.identifierOf("player_skill_sync_packet"));

    public static final PacketCodec<RegistryByteBuf, PlayerSkillSyncPacket> PACKET_CODEC = PacketCodec.of((value, buf) -> {
        buf.writeCollection(value.playerSkillIds, PacketByteBuf::writeInt);
        buf.writeCollection(value.playerSkillLevels, PacketByteBuf::writeInt);
    }, buf -> new PlayerSkillSyncPacket(buf.readList(PacketByteBuf::readInt), buf.readList(PacketByteBuf::readInt)));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

}