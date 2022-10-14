package dev.dediamondpro.skyguide.map.navigation

import dev.dediamondpro.skyguide.map.Island
import dev.dediamondpro.skyguide.map.SkyblockMap
import dev.dediamondpro.skyguide.map.poi.DestinationPoi
import dev.dediamondpro.skyguide.map.poi.Portal
import dev.dediamondpro.skyguide.utils.RenderUtils
import gg.essential.universal.UChat
import gg.essential.universal.wrappers.UPlayer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import kotlin.math.pow
import kotlin.math.sqrt

class NavigationHandler {

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return
        val dest = destinationPio.destination ?: return
        val distance = sqrt(
            (dest.x - UPlayer.getPosX()).pow(2.0) + if (dest.y == null) 0.0 else (dest.y - UPlayer.getPosY()).pow(
                2.0
            ) + (dest.z - UPlayer.getPosZ()).pow(2.0)
        )
        if (distance <= 5) actions.clear()
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        (actions[SkyblockMap.getCurrentIsland() ?: return] ?: return).drawAction(event.partialTicks)
    }

    companion object {
        private val actions = mutableMapOf<Island, NavigationAction>()
        private var destinationPio = DestinationPoi(null)

        fun navigateTo(destination: Destination) {
            actions.clear()
            val currentIsland = SkyblockMap.getCurrentIsland() ?: return
            if (currentIsland != destination.island) {
                val route = findRouteToIsland(
                    destination.island.zone ?: return,
                    currentIsland,
                    mutableListOf()
                )
                if (route == null) {
                    UChat.chat("Could not find a route!")
                    return
                }
                for (step in route) actions[step.key] = PortalAction(step.value, destination)
            }
            actions[destination.island] = DestinationAction(destination)
            destinationPio.destination = destination
        }

        private fun findRouteToIsland(
            destination: String,
            currentIsland: Island,
            visitedIslands: MutableList<String>
        ): MutableMap<Island, Portal>? {
            visitedIslands.add(currentIsland.zone!!)
            for (portal in currentIsland.portals) {
                if (portal.destination == null || visitedIslands.contains(portal.destination)) continue
                if (portal.destination == destination) return mutableMapOf(currentIsland to portal)
                val currentWorld = SkyblockMap.getCurrentWorld() ?: continue
                val destinationIsland = currentWorld[portal.destination] ?: continue
                val path = findRouteToIsland(destination, destinationIsland, visitedIslands) ?: continue
                path[currentIsland] = portal
                return path
            }
            return null
        }
    }
}