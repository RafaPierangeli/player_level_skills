package com.player_level_skills.item;

import com.player_level_skills.init.ItemInit; // Ajuste para o seu registro de itens
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

public class TradeInjector {

    public static void register() {

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 5, factories -> {
            factories.add((entity,level, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD, 64), // Preço: 64 Esmeraldas
                    new ItemStack(ItemInit.RARE_CANDY, 1),
                    3, // Máximo de 1 uso (ele vende apenas um e bloqueia)
                    30, // XP para o villager
                    0.05f
            ));
        });

        // 🌟 Adiciona no VENDEDOR AMBULANTE (Wandering Trader)
        // Usando o sistema de Pool da 1.21.1
        TradeOfferHelper.registerWanderingTraderOffers(builder -> {
            builder.pool(
                    Identifier.of("player_level_skills", "rare_candy_pool"),
                    1, // O trader vai pescar 1 oferta desta piscina
                    (entity,level, random) -> new TradeOffer(
                            new TradedItem(Items.EMERALD, 32), // No Ambulante é mais barato: 32 esmeraldas
                            new ItemStack(ItemInit.RARE_CANDY, 1),
                            2, // Ele vende até 2 unidades
                            15,
                            0.05f
                    )
            );
        });
    }
}

