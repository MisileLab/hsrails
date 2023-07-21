package xyz.misilelaboratory.hsrails

import org.bukkit.Material
import org.bukkit.block.data.type.RedstoneRail
import org.bukkit.entity.Minecart
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleMoveEvent

class MinecartListener(
    private val boostBlock: Material?,
    private val hardBrakeBlock: Material?,
    private val isCheatMode: Boolean
) : Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    fun onVehicleMove(event: VehicleMoveEvent) {
        if (event.vehicle is Minecart) {
            val cart = event.vehicle as Minecart
            val cartLocation = cart.location
            val cartsWorld = cart.world
            val rail = cartsWorld.getBlockAt(cartLocation)
            val blockBelow = cartsWorld.getBlockAt(cartLocation.add(0.0, -1.0, 0.0))
            if (rail.type == Material.POWERED_RAIL) {
                if (isCheatMode || blockBelow.type == boostBlock) {
                    cart.maxSpeed = DEFAULT_SPEED_METERS_PER_TICK * HsRails.configuration.speedMultiplier
                } else {
                    cart.maxSpeed = DEFAULT_SPEED_METERS_PER_TICK
                }
                val railBlockData = rail.blockData as RedstoneRail
                if (!railBlockData.isPowered
                    && blockBelow.type == hardBrakeBlock
                ) {
                    val cartVelocity = cart.velocity
                    cartVelocity.multiply(HsRails.configuration.hardBrakeMultiplier)
                    cart.velocity = cartVelocity
                }
            }
        }
    }

    companion object {
        /**
         * Default speed, in meters per tick. A tick is 0.05 seconds, thus 0.4 * 1/0.05 = 8 m/s
         */
        private const val DEFAULT_SPEED_METERS_PER_TICK = 0.4
    }
}
